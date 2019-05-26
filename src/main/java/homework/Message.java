package homework;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.util.Arrays;

public class Message {

    private byte[] message;
    private int c_type;
    private int b_user_id;
    private static Key key;
    private static Cipher cipher;

    static
    {
        try
        {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);  // Key size

            cipher = Cipher.getInstance("AES");
            key = keyGen.generateKey();
        }
        catch(Exception e)
        {
            e.printStackTrace();;
        }

    }

    public Message(String message, int type, int userId){
        this.c_type = type;
        this.b_user_id = userId;
        this.message = createMessage(message);
    }

    public Message(byte[] mess){
        this.message = mess;
        this.c_type = 1;
        this.b_user_id = 1;
    }

    private byte[] createMessage(String message) {
        byte[] decrMessage = message.getBytes();
        byte[] res = new byte[decrMessage.length+8];
        convertToBigEndianInt(c_type, (byte)4, 0,4, res);
        convertToBigEndianInt(b_user_id, (byte)4, 0,4, res);
        for(int i = 8; i<res.length; i++){
            res[i] = decrMessage[i-8];
        }
        return res;
    }

    public byte[] encryptMessage(){
        byte[] encr = new byte[1];
        try{
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encr = cipher.doFinal(Arrays.copyOfRange(message, 8, message.length));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        byte[] res = new byte[8+encr.length];
        convertToBigEndianInt(c_type, (byte)4, 0,4, res);
        convertToBigEndianInt(b_user_id, (byte)4, 0,4, res);
        for(int i = 8; i<res.length; i++){
            res[i] = encr[i-8];
        }
        return res;
    }

    public byte[] decryptMessage(){
        byte[] encr = new byte[1];
        try{
            cipher.init(Cipher.DECRYPT_MODE, key, cipher.getParameters());
            encr = cipher.doFinal(Arrays.copyOfRange(message, 8, message.length));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        byte[] res = new byte[8+encr.length];
        convertToBigEndianInt(c_type, (byte)4, 0,4, res);
        convertToBigEndianInt(b_user_id, (byte)4, 0,4, res);
        for(int i = 8; i<res.length; i++){
            res[i] = encr[i-8];
        }
        return res;
    }

    private void convertToBigEndianInt(int data, byte length, int start, int finish, byte[] packageArr){
        byte[] res = new byte[length];
        for(int i = length-1, j = 0; i >= 0; i--, j++){
            res[j] = (byte)(data >> (i*8));
        }
        for(int i = start; i<finish; i++){
            packageArr[i] = res[i-start];
        }
    }

    public byte[] getMessage() {
        return message;
    }

    public int getC_type() {
        return c_type;
    }

    public int getB_user_id() {
        return b_user_id;
    }

}
