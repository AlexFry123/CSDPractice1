package practice3;
import homework.FakeReceiver;
import homework.Message;
import homework.MyPackage;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.Semaphore;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class StoreClientTCP implements Runnable {
    private static Semaphore _semaphore = new Semaphore(1);
    private InetAddress host;
    private int port;
    private final ByteBuffer buffer = ByteBuffer.allocate(16384);

    private Random rand = new Random();

    public StoreClientTCP(InetAddress host, int port, int numThreads) {
        this.host = host;
        this.port = port;
        for (int i = 0; i < numThreads; ++i) {
            new Thread(this).start();
        }
    }

    public void run() {
        while(true) {
            try {
                Socket s = new Socket(host, port);
                InputStream in = s.getInputStream();
                OutputStream out = s.getOutputStream();

                while (true) {
                    try {
                        _semaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Random rand = new Random();
                    int a = rand.nextInt(6);
                    String str = FakeReceiver.generateCommand(a);
                    MyPackage pak = new MyPackage(new Message(str, 1, 1));
                    //byte[] toSend = msg.sendMessageToServer(1, sKey);
                    byte buffer[] = new byte[pak.getMyPackage().length];

                    for (int i = 0; i < pak.getMyPackage().length; ++i) {
                        buffer[i] = pak.getMyPackage()[i];
                    }
                    _semaphore.release();

                    out.write(buffer);
                    System.out.println("CLIENT SEND: " + str);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                    }
                }
            } catch (Exception ie) {
                System.out.println("Waiting server start...");
            }
        }
    }



    static public void main(String args[]) throws Exception {
        InetAddress host = InetAddress.getLocalHost();
        int port = 2020;
        int numThreads = 3;
        SecretKey key = new SecretKeySpec(new byte[]{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, "AES");
        Message.setKey(key);
        for(int i = 0; i<5; i++)
            new StoreClientTCP(host, port, 3);
    }
}
