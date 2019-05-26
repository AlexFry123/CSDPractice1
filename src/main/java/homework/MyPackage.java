package homework;

public class MyPackage {

    private byte[] myPackage;
    private Message message;
    private byte b_src = 1;
    private long numberOfMessage = 1;
    private Encriptor encrypt;

    public MyPackage(Message mess){
        this.myPackage = createPackage(mess.encryptMessage());
    }

    public MyPackage(byte[] arr){
        this.myPackage = arr;
    }

    private byte[] createPackage(byte[] message){
        byte[] res = new byte[22+message.length];
        res[0] = 0x13;
        res[1] = b_src;
        convertToBigEndianLong(numberOfMessage, (byte)8, 2, 10, res);
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
        tempCrc = new CRC16(message);
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

    @Override
    public boolean equals(Object obj){
        MyPackage tmp = (MyPackage) obj;
        if(myPackage.length!= tmp.getMyPackage().length)
            return false;
        for(int i=0; i<myPackage.length; i++){
            if(myPackage[i]!=tmp.getMyPackage()[i])
                return false;
        }
        return true;
    }

    public byte[] getMyPackage() {
        return myPackage;
    }

}
