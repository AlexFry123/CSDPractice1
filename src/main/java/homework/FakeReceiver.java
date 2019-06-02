package homework;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class FakeReceiver implements Reciever{

    private static LinkedBlockingQueue<byte[]>  messages;

    static{
        messages = new LinkedBlockingQueue<>();
    }

    public FakeReceiver(){
    }

    public LinkedBlockingQueue<byte[]> getMessages() {
        return messages;
    }

    @Override
    public void receiveMessage() {
       for(int i = 0; i<18; i++){
           String str = generateCommand(i/3);
           messages.add(new MyPackage(new Message(str, i/3, 1)).getMyPackage());
       }
    }

    public static String generateCommand(int num){
        switch(num){
            case 0:
                return "Items quantity.";
            case 1:
                return "Take some goods";
            case 2:
                return "Put some goods.";
            case 3:
                return "Add items group.";
            case 4:
                return "Add item to group.";
            default:
                return "Put new price.";
        }
    }

}
