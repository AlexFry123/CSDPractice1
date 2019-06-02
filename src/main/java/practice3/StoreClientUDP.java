package practice3;
import homework.FakeReceiver;
import homework.Message;
import homework.MyPackage;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.Random;

class StoreClientUDP implements Runnable {


    public static void main(String args[]) throws Exception {
        SecretKey key = new SecretKeySpec(new byte[]{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, "AES");
        Message.setKey(key);
        for(int i = 0; i<10; i++) {
             new Thread(new StoreClientUDP()).start();
        }
    }

    @Override
    public void run() {

        DatagramSocket clientSocket = null;
        try{
            clientSocket = new DatagramSocket();
        } catch(SocketException se)
        {
            se.printStackTrace();
        }
        InetAddress IPAddress = null;
        try{
            IPAddress = InetAddress.getLocalHost();
        }catch (UnknownHostException uhe){
            uhe.printStackTrace();
        }
        byte[] receiveData = new byte[1024];
        Random rand = new Random();
        int a = rand.nextInt(6);
        String tmp = FakeReceiver.generateCommand(a);
        MyPackage pak = new MyPackage(new Message(tmp, 1, 1));
        System.out.println("Client send: "+ tmp);
        //byte[] toSend = msg.sendMessageToServer(1, sKey);
//                System.out.println("CLIENT SEND: " + Arrays.toString(pak.getMyPackage()));
        byte sendData[] = new byte[pak.getMyPackage().length];

        for (int i = 0; i < pak.getMyPackage().length; ++i) {
            sendData[i] = pak.getMyPackage()[i];
        }
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            clientSocket.setSoTimeout(1000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while(true) {
            try {
                clientSocket.receive(receivePacket);
                break;
            } catch (SocketTimeoutException ste) {
                System.out.println("Not Received");
                try {
                    clientSocket.send(sendPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String modifiedSentence = new String(receivePacket.getData());
        System.out.println("FROM SERVER:" + modifiedSentence);
        clientSocket.close();
    }
}
