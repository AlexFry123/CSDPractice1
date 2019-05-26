package homework;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Decriptor {

    private LinkedBlockingQueue<byte[]> decrQueue;
    private static LinkedBlockingQueue<Message> processQueue;

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public boolean isFinish() {
        return finish;
    }

    private boolean finish;

    static {
        processQueue = new LinkedBlockingQueue<>();
    }

    public Decriptor(LinkedBlockingQueue<byte[]> queue){
        this.decrQueue = queue;
        this.finish = false;
        for(int i=0; i<2; i++) {
            Thread thr = new Thread(() -> {
            while(finish!=true)   {
            try { byte[] tmp = decrQueue.poll(2000, TimeUnit.MICROSECONDS);
                if(tmp!=null) {
                    decript(tmp);}
            } catch (Exception ex) {
                System.out.println("Decriptor thread exception");
                ex.printStackTrace();
            }}});
            thr.setName("decriptor"+i);
            thr.start();
        }
    }

    private void decript(byte[] message){
        byte[] arr = Arrays.copyOfRange(message,18, message.length-4);
        Message tmp = new Message(arr);
        processQueue.add(new Message(tmp.decryptMessage()));
    }

//    @Override
//    public void run(){
//        while(finish!=true) {
//            try {
//                byte[] tmp = decrQueue.poll(2000, TimeUnit.MICROSECONDS);
//                if(tmp!=null) {
//                    decript(tmp);
//                }
//            } catch (Exception ex) {
//                System.out.println("Decriptor thread exception");
//                ex.printStackTrace();
//            }
//        }
//    }

    public LinkedBlockingQueue<Message> getProcessQueue() {
        return processQueue;
    }

}
