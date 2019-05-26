package homework;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Processor {

    private LinkedBlockingQueue<Message> processQueue;
    private static LinkedBlockingQueue<Message> encryptQueue;
    private boolean finish;

    static{
        encryptQueue = new LinkedBlockingQueue<>();
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public Processor(LinkedBlockingQueue<Message> procQueue){
        this.finish = false;
        this.processQueue = procQueue;
        for(int i=0; i<2; i++) {
            Thread thr = new Thread(() -> {
                while(finish!=true)   {
                    try { Message tmp = processQueue.poll(2000, TimeUnit.MICROSECONDS);
                        if(tmp!=null) {
                            process(new Message("Ok", tmp.getC_type(), tmp.getB_user_id()));}
                    } catch (Exception ex) {
                        System.out.println("Processor thread exception");
                        ex.printStackTrace();
                    }}});
            thr.setName("processor"+i);
            thr.start();
        }
    }

    private void process(Message message){
        encryptQueue.add(message);
    }

//    @Override
//    public void run(){
//        while(finish!=true) {
//            try {
//                Message tmp = processQueue.poll(2000, TimeUnit.MICROSECONDS);
//                if(tmp!=null) {
//                    process(new Message("Ok", tmp.getC_type(), tmp.getB_user_id()));
//                }
//            } catch (Exception ex) {
//                System.out.println("Processor error");
//                process(new Message("Error", 0, 0));
//                ex.printStackTrace();
//            }
//        }
//    }

    public LinkedBlockingQueue<Message> getEncryptQueue() {
        return encryptQueue;
    }

}
