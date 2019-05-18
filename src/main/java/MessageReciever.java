public class MessageReciever {

    private boolean firstCRC;
    private boolean secondCRC;

    MessageReciever(byte[] packet){
        this.firstCRC = checkCRC(packet,0,14);
        this.secondCRC = checkCRC(packet,18,packet.length-4);
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

    public boolean packageIsCorrect()
    {
        System.out.println(firstCRC);
        System.out.println(secondCRC);
        return firstCRC && secondCRC;
    }

}
