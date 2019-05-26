package homework;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Encriptor {

    private LinkedBlockingQueue<Message> encryptQueue;
    private static LinkedBlockingQueue<byte[]> senderQueue;
    private boolean finish;

    static {
        senderQueue = new LinkedBlockingQueue<>();
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public Encriptor(LinkedBlockingQueue<Message> queue){
        this.encryptQueue = queue;
        this.finish = false;
        for(int i=0; i<2; i++) {
            Thread thr = new Thread(() -> {
                while(finish!=true)   {
                    try { Message tmp = encryptQueue.poll(2000, TimeUnit.MICROSECONDS);
                        if(tmp!=null) {
                            senderQueue.add(encrypt(tmp));}
                    } catch (Exception ex) {
                        System.out.println("Encryptor thread exception");
                        ex.printStackTrace();
                    }}});
            thr.setName("encryptor"+i);
            thr.start();
        }
    }

    private byte[] encrypt(Message message) {
        return message.encryptMessage();
    }

//    @Override
//    public void run(){
//        while(finish!=true) {
//            try {
//                Message tmp = encryptQueue.poll(2000, TimeUnit.MICROSECONDS);
//                if(tmp!=null) {
//                    senderQueue.add(encrypt(tmp));
//                }
//            } catch (Exception ex) {
//                System.out.println("Encryptor error");
//                ex.printStackTrace();
//            }
//        }
//    }

    public LinkedBlockingQueue<byte[]> getSenderQueue() {
        return senderQueue;
    }

}
