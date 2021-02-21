package lab;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();

        ServerSocketChannel serverCh = ServerSocketChannel.open();
        serverCh.configureBlocking(false);
        serverCh.bind(new InetSocketAddress(9000));
        serverCh.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Listen for connections");
        while (true) {
            selector.select(); // watching events
            System.out.println("got some events");
            Set<SelectionKey> keys = selector.selectedKeys();
            // do something with keys

            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                if (key.isAcceptable()) {
                    ServerSocketChannel ch = (ServerSocketChannel) key.channel();
                    SocketChannel clientCh = ch.accept();
                    clientCh.configureBlocking(false);
                    clientCh.register(selector, SelectionKey.OP_READ);
                }
//                if (key.isWritable()) {
//                    // client ready for write
//                    SocketChannel ch = (SocketChannel) key.channel();
//                    ByteBuffer buf = ByteBuffer.allocate(20);
//                    String msg = String.format("Time:%d\n", System.currentTimeMillis());
//                    buf.put(msg.getBytes());
//                    buf.flip();
//                    ch.write(buf);
//                    Thread.sleep(1000);
//                }

                if (key.isReadable()) {
                    SocketChannel ch = (SocketChannel) key.channel();
                    ByteBuffer buf = ByteBuffer.allocate(20);
                    ch.read(buf);
                    buf.flip();
                    String clientInput = new String(buf.array()).trim();
                    System.out.println(clientInput);
                    if (clientInput.contains("UPDATE")) {
                        String msg = String.format("Time:%d\n", System.currentTimeMillis());
                        buf.clear();
                        buf.put(msg.getBytes());
                        buf.flip();
                        ch.write(buf);
                    } else {
                        
                    }

                }

            }
        }
    }
}
