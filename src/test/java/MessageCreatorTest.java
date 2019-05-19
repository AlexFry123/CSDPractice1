import org.junit.Assert;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.util.Arrays;

public class MessageCreatorTest {

@Test
    public void checkingCorrectMessageCreation(){
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);  // Key size
            Key key1 = keyGen.generateKey();
            Key key2 = keyGen.generateKey();
            Key key3 = keyGen.generateKey();
            String startMessage1 = "test";
            String startMessage2 = "";
            String startMessage3 = "This is 3-rd test string";
            Cipher cipher1 = Cipher.getInstance("AES/CBC/PKCS5Padding");
            Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
            Cipher cipher3 = Cipher.getInstance("AES/CBC/PKCS5Padding");
            MessageCreator msgCrt1 = new MessageCreator(startMessage1, key1, cipher1);
            MessageCreator msgCrt2 = new MessageCreator(startMessage2, key2, cipher2);
            MessageCreator msgCrt3 = new MessageCreator(startMessage3, key3, cipher3);
            cipher1.init(Cipher.DECRYPT_MODE,key1, cipher1.getParameters());
            cipher2.init(Cipher.DECRYPT_MODE,key2, cipher2.getParameters());
            cipher3.init(Cipher.DECRYPT_MODE,key3, cipher3.getParameters());
            Assert.assertEquals(startMessage1, new String(cipher1.doFinal(Arrays.copyOfRange(msgCrt1.getMyPackage(), 26, msgCrt1.getMyPackage().length-4))));
            Assert.assertEquals(startMessage2, new String(cipher2.doFinal(Arrays.copyOfRange(msgCrt2.getMyPackage(), 26, msgCrt2.getMyPackage().length-4))));
            Assert.assertEquals(startMessage3, new String(cipher3.doFinal(Arrays.copyOfRange(msgCrt3.getMyPackage(), 26, msgCrt3.getMyPackage().length-4))));
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

@Test
    public void checkingCorrectMessageRecieve(){
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);  // Key size
            Key key1 = keyGen.generateKey();
            Key key2 = keyGen.generateKey();
            Key key3 = keyGen.generateKey();
            String startMessage1 = "test";
            String startMessage2 = "";
            String startMessage3 = "Testing if CRC16 are correct";
            Cipher cipher1 = Cipher.getInstance("AES/CBC/PKCS5Padding");
            Cipher cipher2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
            Cipher cipher3 = Cipher.getInstance("AES/CBC/PKCS5Padding");
            MessageCreator msgCrt1 = new MessageCreator(startMessage1, key1, cipher1);
            MessageCreator msgCrt2 = new MessageCreator(startMessage2, key2, cipher2);
            MessageCreator msgCrt3 = new MessageCreator(startMessage3, key3, cipher3);
            MessageReciever msgRcv1 = new MessageReciever(msgCrt1.getMyPackage(), key1, cipher1, startMessage1);
            MessageReciever msgRcv2 = new MessageReciever(msgCrt2.getMyPackage(), key2, cipher2, startMessage2);
            MessageReciever msgRcv3 = new MessageReciever(msgCrt3.getMyPackage(), key3, cipher3, startMessage3);
            Assert.assertTrue(msgRcv1.packageIsCorrect());
            Assert.assertTrue(msgRcv2.packageIsCorrect());
            Assert.assertTrue(msgRcv3.packageIsCorrect());
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}