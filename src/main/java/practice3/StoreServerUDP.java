package practice3;
import homework.Decriptor;
import homework.Message;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

class StoreServerUDP implements Runnable {
    public static void main(String args[]) {
        SecretKey key = new SecretKeySpec(new byte[]{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, "AES");
        Message.setKey(key);
        new Thread(new StoreServerUDP()).start();
    }

    @Override
    public void run() {
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(9876);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        while(true)
        {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                serverSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int len = bigEndianToInt(receiveData,10);
            Decriptor d=new Decriptor(Arrays.copyOfRange(receiveData,18,18+len));
            byte[] mess = d.decrypt(d.getMessage());
            String resMess = new String(Arrays.copyOfRange(mess,8,mess.length));
            System.out.println("SERVER DECRYPTED: " + resMess);
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            sendData = resMess.getBytes();
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, port);
            try {
                serverSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int bigEndianToInt(byte[] arr, int from){
        int res = 0;
        for(int i = 0; i<4; i++){
            res = ((res << i*8) | arr[from+i]);
        }
        return res;
    }
}
