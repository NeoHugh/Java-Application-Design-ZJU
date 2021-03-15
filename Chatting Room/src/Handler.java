package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class Handler implements Runnable {

    final SocketChannel socketChannel;
    final SelectionKey sk;
    static final int BUFFER_SIZE = 500;
    final ExecutorService executor = Executors.newFixedThreadPool(1);
    ByteBuffer input = ByteBuffer.allocate(BUFFER_SIZE);
    ByteBuffer output = ByteBuffer.allocate(BUFFER_SIZE);
    LinkedList<String> messageQueue = new LinkedList<>();
    final ReentrantReadWriteLock rwl;
    boolean flagStart = false;
    static String hello = "hello";


    public Handler(Selector sel, SocketChannel c) throws IOException {
        rwl = new ReentrantReadWriteLock();
        socketChannel = c;
        //设置为非阻塞模式
        c.configureBlocking(false);
        //此处的0，表示不关注任何时间
        sk = socketChannel.register(sel, 0);
        //将SelectionKey绑定为本Handler 下一步有事件触发时，将调用本类的run方法
        sk.attach(this);
        //将SelectionKey标记为可读，以便读取，不可关注可写事件
        sk.interestOps(SelectionKey.OP_READ);
        sel.wakeup();
    }

    public void read() {
        if (sk.isValid()) {
            try {
                socketChannel.read(input);
                input.flip();
            } catch (IOException e) {
                closeAndRemove();
                return;
            }
            StringBuilder stringBuffer = new StringBuilder();
            while (input.hasRemaining()) {
                stringBuffer.append(input.getChar());
            }

            if (!stringBuffer.toString().equals(hello)) {
                Reactor.rwl.readLock().lock();
                for (var each : Reactor.handlerList) {
                    each.rwl.writeLock().lock();
                    each.messageQueue.add(stringBuffer.toString());
                    each.sk.interestOps(SelectionKey.OP_WRITE);
                    each.rwl.writeLock().unlock();
                }
                Reactor.rwl.readLock().unlock();
                sk.interestOps(SelectionKey.OP_WRITE);
            } else {
                //got hello message
                flagStart = true;
                sk.interestOps(SelectionKey.OP_WRITE);
            }
            input.clear();
        }
    }

    public void write() {
        if (sk.isValid()) {
            if (!flagStart) {
                rwl.readLock().lock();
                for (var each : messageQueue) {
                    if (each.getBytes(StandardCharsets.UTF_16BE).length < BUFFER_SIZE) {
                        output.clear();
                        output.put(each.getBytes(StandardCharsets.UTF_16BE));
                        try {
                            output.flip();
                            socketChannel.write(output);
                        } catch (IOException e) {
                            closeAndRemove();
                            return;
                        }
                    }
                }
                rwl.readLock().unlock();
                rwl.writeLock().lock();
                messageQueue.clear();
                rwl.writeLock().unlock();
                sk.interestOps(SelectionKey.OP_READ);
            } else {
                //reply to hello message.
                output.clear();
                output.put(hello.getBytes(StandardCharsets.UTF_16BE));
                output.flip();
                try {
                    socketChannel.write(output);
                } catch (IOException e) {
                    System.out.println(socketChannel.socket().getRemoteSocketAddress() + " disconnected");
                    try {
                        sk.channel().close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    sk.cancel();
                }
                flagStart = false;
                sk.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    public void run() {
        executor.execute(new processer());
    }

    class processer implements Runnable {
        @Override
        public void run() {
//            if (sk.isReadable()) {
//                read();
//            }
//            if (sk.isWritable()) {
//                write();
//            }
            if (!sk.isValid())
                return;
            if (sk.interestOps() == SelectionKey.OP_READ)
//            if (sk.isReadable())
                read();


            else if (sk.interestOps() == SelectionKey.OP_WRITE)
//            if (sk.isWritable())
                write();

        }
    }

    public void closeAndRemove(){
        System.out.println(socketChannel.socket().getRemoteSocketAddress() + " disconnected");
        try {
            Selector temp = sk.selector();
            sk.cancel();
            sk.channel().close();
            Reactor.rwl.writeLock().lock();
            Reactor.handlerList.remove(this);
            Reactor.rwl.writeLock().unlock();
            executor.shutdown();
            temp.wakeup();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
