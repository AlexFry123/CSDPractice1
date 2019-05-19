import javax.crypto.Cipher;
import java.security.Key;
import java.util.Arrays;

public class MessageReciever {

    private boolean firstCRC;
    private boolean secondCRC;
    private boolean messageCorrect;
    private Cipher decrCipher;
    private Key decrKey;
    private String message;

    MessageReciever(byte[] packet, Key key, Cipher cipher, String startMessage){
        this.decrCipher = cipher;
        this.decrKey = key;
        this.message = startMessage;
        this.firstCRC = checkCRC(packet,0,14);
        this.secondCRC = checkCRC(packet,18,packet.length-4);
        this.messageCorrect = messageIsCorrect(packet);
    }

    private boolean checkCRC(byte[] packet, int start, int finish){
        boolean res = true;
        byte[] tmpBytesArray = new byte[finish-start];
        for(int i = start; i<finish; i++){
            tmpBytesArray[i-start] = packet[i];
        }
        CRC16 tmpCrc = new CRC16(tmpBytesArray);
        byte[] packageSrc = convertToBigEndianInt(tmpCrc.getCrc(), (byte)4);
        for(int i = finish; i<finish+4; i++){
            if(packageSrc[i-finish]!=packet[i])
                res = false;
        }
        return res;
    }


    private byte[] convertToBigEndianInt(int data, byte length){
        byte[] res = new byte[length];
        for(int i = length-1, j = 0; i >= 0; i--, j++){
            res[j] = (byte)(data >> (i*8));
        }
        return res;
    }

    private boolean messageIsCorrect(byte[] packet){
        String res = "";
        try {
            decrCipher.init(Cipher.DECRYPT_MODE, decrKey, decrCipher.getParameters());
            res = new String(decrCipher.doFinal(Arrays.copyOfRange(packet, 26, packet.length-4)));
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return message.equals(res);
    }

    public boolean packageIsCorrect()
    {
        return firstCRC && secondCRC && messageCorrect;
    }

}
