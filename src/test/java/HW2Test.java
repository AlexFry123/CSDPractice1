import homework.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class HW2Test {

    private LinkedBlockingQueue<byte[]> tmp = new LinkedBlockingQueue<>();

@Test
public void fakeRecieverTest() throws InterruptedException {
    FakeReceiver fkrcv = new FakeReceiver();
    fkrcv.receiveMessage();
    for(int i = 0; i<18; i++){
        String str = fkrcv.generateCommand(i/3);
        tmp.add(new MyPackage(new Message(str, i/3, 1)).getMyPackage());
    }
    while(fkrcv.getMessages().size()!=0) {
        Assert.assertTrue(Arrays.equals(fkrcv.getMessages().take(), tmp.take()));
    }
}

@Test
    public void systemTest() throws InterruptedException {
        FakeReceiver fkrcv = new FakeReceiver();
        fkrcv.receiveMessage();
        Decriptor d = new Decriptor(fkrcv.getMessages());
        Processor p = new Processor(d.getProcessQueue());
        Encriptor en = new Encriptor(p.getEncryptQueue());
//        en.getSenderQueue().poll(100, TimeUnit.MICROSECONDS);
        Sender s = new Sender(en.getSenderQueue());
        ArrayList<String> tmpArr = new ArrayList<>();
        for(int i=0; i<18; i++){
            tmpArr.add("Ok");
        }
        s.start();
        try {
            Thread.sleep(2000);
            d.setFinish(true);
            p.setFinish(true);
            en.setFinish(true);
            s.setFinish(true);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        Assert.assertTrue(Arrays.equals(s.getMessage().toArray(),tmpArr.toArray()));
    }

}
