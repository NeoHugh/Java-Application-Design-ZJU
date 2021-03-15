package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Client {
    static int BUFFER_SIZE = 500;
    static SocketChannel socketChannel;
    static ByteBuffer writeBuffer;
    static ByteBuffer readBuffer;
    static String clientName;
    static String IP = "127.0.0.1";
    static String hello="hello";
    static class getMessage implements Runnable {
        @Override
        public void run() {
            while (true) {
                readBuffer.clear();
                try {
                    socketChannel.read(readBuffer);
                    readBuffer.flip();
                    StringBuilder stringBuffer = new StringBuilder();
                    while (readBuffer.hasRemaining()) {
                        stringBuffer.append(readBuffer.getChar());
                    }
                    if (!stringBuffer.toString().equals(hello))
                    {
                        System.out.println(stringBuffer);
                        System.out.print(">>> ");
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected, bye!");
                    return;
                }

            }
        }
    }


    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);
        System.out.println("Register by putting a nickname to represent yourself");
        clientName = s.nextLine();
        System.out.println("Input the destination IP, with a blank line meaning localhost:");
        String temp = s.nextLine();
        IP = temp.equals("") ? IP : temp;
        writeBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        try {
            socketChannel = SocketChannel.open();
        } catch (IOException e) {
            System.out.println("Failed to establish connection.");
        }
        socketChannel.connect(new InetSocketAddress(IP, 3167));
        Thread th = new Thread(new getMessage());
        System.out.println("Connected!");
        th.start();
        //send hello message first
        writeBuffer.put("hello".getBytes(StandardCharsets.UTF_16BE));
        writeBuffer.flip();
        socketChannel.write(writeBuffer);
        writeBuffer.clear();
        while (true) {
            System.out.print(">>> ");
            String meg = s.nextLine();
            writeBuffer.clear();
            if (meg.equals("bye")) {
                socketChannel.close();
                break;
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            meg = df.format(new Date()) + " User: " + clientName + " \n" + meg;
            if (meg.getBytes(StandardCharsets.UTF_16BE).length < BUFFER_SIZE) {
                writeBuffer.put(meg.getBytes(StandardCharsets.UTF_16BE));
                writeBuffer.flip();
                try {
                    socketChannel.write(writeBuffer);
                } catch (IOException exp) {
                    if (exp.getMessage().contains("reset"))
                        return;
                }
            } else {
                System.out.println("Too long a message to send. Transmission failed.");
            }

        }
    }
}