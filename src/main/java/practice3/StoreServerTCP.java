package practice3;
import homework.Decriptor;
import homework.Message;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class StoreServerTCP implements Runnable {
    private int port;
    private final ByteBuffer buffer = ByteBuffer.allocate(16384);

    public StoreServerTCP(int port) {
        this.port = port;
        new Thread(this).start();
    }

    public void run() {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ServerSocket ss = ssc.socket();
            InetSocketAddress isa = new InetSocketAddress(port);
            ss.bind(isa);
            Selector selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Listening on port " + port);

            while (true) {
                int num = selector.select();
                if (num == 0) {
                    continue;
                }

                Set keys = selector.selectedKeys();
                Iterator it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {

                        Socket s = ss.accept();
//                        System.out.println("Got connection from " + s);
                        SocketChannel sc = s.getChannel();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                    } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                        SocketChannel sc = null;

                        try {
                            sc = (SocketChannel) key.channel();
                            boolean ok = processInput(sc);
                            if (!ok) {
                                key.cancel();

                                Socket s = null;
                                try {
                                    s = sc.socket();
                                    s.close();
                                } catch (IOException ie) {
                                    System.err.println("Error closing socket " + s + ": " + ie);
                                }
                            }

                        } catch (IOException ie) {
                            key.cancel();

                            try {
                                sc.close();
                            } catch (IOException ie2) {
                                System.out.println(ie2);
                            }

                            System.out.println("Closed " + sc);
                        }
                    }
                }

                keys.clear();
            }
        } catch (IOException ie) {
            System.err.println(ie);
        }
    }

    private boolean processInput(SocketChannel sc) throws IOException {
        buffer.clear();
        sc.read(buffer);
        buffer.flip();
        if (buffer.limit() == 0) {
            return false;
        }

        byte[] getData = new byte[buffer.limit()];
        for (int i = 0; i < buffer.limit(); i++) {
            getData[i]= buffer.get(i);
//            System.out.print(buffer.get(i) + " ");
        }
//        System.out.println();
        Decriptor d=new Decriptor(Arrays.copyOfRange(getData,18,getData.length-4));
        byte[] mess = d.decrypt(d.getMessage());
        System.out.println("SERVER DECRYPTED: "+ new String(Arrays.copyOfRange(mess,8,mess.length)));
        sc.write(buffer);
//        System.out.println( "Processed "+buffer.limit()+" from "+sc );

        return true;
    }

    static public void main(String args[]) throws Exception {
        int port = 2020;
        SecretKey key = new SecretKeySpec(new byte[]{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, "AES");
        Message.setKey(key);
        new StoreServerTCP(port);
    }
}
