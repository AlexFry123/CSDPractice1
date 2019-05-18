import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class MessageCreator {

    private byte[] myPackage;
    private Key encryptionKey;
    private Cipher myCipher;
    private static long numberOfMessage = 0;
    private static final byte B_SRC = 1;
    private static final int C_TYPE = 1;
    private static final int B_USER_ID = 1;

    MessageCreator(String message, Key key, Cipher cipher){
        this.myPackage = createPackage(createMessage(message, key, cipher));
    }

    private byte[] createPackage(byte[] message){
        byte[] res = new byte[22+message.length];
        res[0] = 0x13;
        res[1] = B_SRC;
        convertToBigEndianLong(numberOfMessage, (byte)8, 2, 10, res);
        numberOfMessage++;
        convertToBigEndianInt(message.length, (byte)4, 10, 14, res);
        byte[] tmp = new byte[14];
        for(int i = 0; i<14; i++) {
            tmp[i] = res[i];
        }
        CRC16 tempCrc = new CRC16(tmp);
        convertToBigEndianInt(tempCrc.getCrc(), (byte)4, 14, 18, res);
        for(int i = 18; i<message.length+18; i++){
            res[i] = message[i-18];
        }
        tmp = new byte[message.length];
        for(int i = 18; i<18+message.length; i++) {
            tmp[i-18] = res[i];
        }
        tempCrc = new CRC16(tmp);
        convertToBigEndianInt(tempCrc.getCrc(), (byte)4, 18+message.length, 22+message.length, res);
        return res;
    }

    private void convertToBigEndianLong(long data, byte length, int start, int finish, byte[] packageArr){
        byte[] res = new byte[length];
        for(int i = length-1, j = 0; i >= 0; i--, j++){
            res[j] = (byte)(data >> (i*8));
        }
        for(int i = start; i<finish; i++){
            packageArr[i] = res[i-start];
        }
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

    private void convertToBigEndianShort(short data, byte length, int start, int finish, byte[] packageArr){
        byte[] res = new byte[length];
        for(int i = length-1, j = 0; i >= 0; i--, j++){
            res[j] = (byte)(data >> (i*8));
        }
        for(int i = start; i<finish; i++){
            packageArr[i] = res[i-start];
        }
    }

    private byte[] createMessage(String message,Key key, Cipher cipher){
        this.encryptionKey = key;
        byte[] messageBytesArray = message.getBytes();
        byte[] cipherBytes = new byte[1];
        try{
//            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//            keyGen.init(128);  // Key size
//            Key key = keyGen.generateKey();

            Cipher tmpcipher = cipher;  // Transformation of the algorithm
            tmpcipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            cipherBytes = tmpcipher.doFinal(messageBytesArray);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        byte[] res = new byte[8+cipherBytes.length];
        convertToBigEndianInt(C_TYPE, (byte)4, 0,4, res);
        convertToBigEndianInt(B_USER_ID, (byte)4, 4,8, res);
        for(int i = 8; i<res.length; i++){
            res[i] = cipherBytes[i-8];
        }
        return res;
    }

    public byte[] getMyPackage() {
        return myPackage;
    }

}
