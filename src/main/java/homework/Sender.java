package homework;

import org.junit.Assert;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Sender extends Thread{

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public boolean isFinish() {
        return finish;
    }

    private boolean finish;
    private ArrayList<String> msgs;

    public LinkedBlockingQueue<byte[]> getSenderQueue() {
        return senderQueue;
    }

    LinkedBlockingQueue<byte[]> senderQueue;

    public Sender(LinkedBlockingQueue<byte[]> queue){
        this.senderQueue = queue;
        this.msgs = new ArrayList<>();
        this.finish = false;
    }

    public void sendMessage(byte[] mess){
        Message msg = new Message(mess);
        byte[] decr = msg.decryptMessage();
        byte[] arrByte = Arrays.copyOfRange(decr,8,msg.getMessage().length);
        byte[] strArr = new byte[2];
        for(int i =0; i<arrByte.length-1; i++){
            if(arrByte[i]==0 && arrByte[i+1]==0)
                continue;
            strArr[i] = arrByte[i];
        }
        String tmp = new String(strArr);
        msgs.add(tmp);
        System.out.println(tmp);
    }

    @Override
    public void run() {
        while(finish!=true) {
            try {
                byte[] tmp = getSenderQueue().poll(2000, TimeUnit.MILLISECONDS);
                if(tmp!=null) {
                    sendMessage(tmp);
                }
            } catch (Exception ex) {
                System.out.println("Sender error");
                ex.printStackTrace();
            }
        }
    }

    public ArrayList<String> getMessage() {
        return msgs;
    }

}
