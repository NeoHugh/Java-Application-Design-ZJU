package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Reactor implements Runnable {
    final Selector []selector=new Selector[2];
    final ServerSocketChannel serverSocketChannel;
    static final List<Handler> handlerList = new LinkedList<>();
    static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    Reactor(int port) throws IOException { //Reactor初始化
        selector[0] = Selector.open();
        //The first selects OP_ACCEPT
        selector[1] = Selector.open();
        //The second selects OP_READ/OP_WRITE
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        //非阻塞
        serverSocketChannel.configureBlocking(false);

        //分步处理,第一步,接收accept事件
        SelectionKey sk =
                serverSocketChannel.register(selector[0], SelectionKey.OP_ACCEPT);
        //attach callback object, Acceptor
        sk.attach(new Acceptor());

        //after construction, the main thread begins listening for incoming attempting connection.
    }

    public void mainLoop(){
        Thread th = new Thread(this);
        th.start();
        while (!Thread.interrupted()) {
            try {
                selector[0].select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set selected = selector[0].selectedKeys();
            for (Object o : selected) {
                //Reactor负责dispatch收到的事件
                dispatch((SelectionKey) o);
            }
            selected.clear();
        }
    }

    public void run() {
        try {
            Thread.yield();
            while (!Thread.interrupted()) {
                selector[1].select();
                Set selected = selector[1].selectedKeys();
                for (Object o : selected) {
                    //Reactor负责dispatch收到的事件
                    dispatch((SelectionKey) o);
                }
                selected.clear();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void dispatch(SelectionKey k) {
        Object r = k.attachment();
        //调用之前注册的callback对象
//        if (r instanceof Handler) {
//            Thread dispatchThread=new Thread((Handler)r);
//            dispatchThread.start();
//            //r.run();
//        }
//        else if (r instanceof Acceptor)
//            ((Acceptor)r).run();
        ((Runnable)k.attachment()).run();
//        if (r instanceof Handler) {
//            Thread dispatchThread = new Thread((Handler) r);
//            dispatchThread.start();
//            try {
//                dispatchThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//        else if (r instanceof Acceptor)
//            ((Runnable)k.attachment()).run();
    }

    // inner class
    class Acceptor implements Runnable {
        public void run() {

            try {
                SocketChannel channel = serverSocketChannel.accept();
                if (channel != null) {
                    rwl.writeLock().lock();
                    handlerList.add(new Handler(selector[1], channel));
                    rwl.writeLock().unlock();
                }
            } catch (IOException ex) { /* ... */ }

        }
    }

    public static void main(String[] args) {
        Reactor re = null;
        try {
            re = new Reactor(3167);
        } catch (IOException e) {
            e.printStackTrace();
        }
        re.mainLoop();


    }
}