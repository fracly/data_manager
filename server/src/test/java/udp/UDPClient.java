package udp;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UDPClient {
    private static DatagramSocket clientSocket = null;
    private InetSocketAddress serverAddress = null;

    public UDPClient(String host, int port) throws SocketException {
        clientSocket = new DatagramSocket( );           //创建socket
        serverAddress = new InetSocketAddress(host, port);  //绑定sever的ip与port
    }


    public void send(byte[] data) throws IOException {
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress);
            clientSocket.send(packet);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //main方法用于测试
    public static void main(String[] args) throws Exception {
        File file = new File("D:///1.jpg");
        byte[] data = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            data = baos.toByteArray();
            UDPClient client = new UDPClient("10.20.11.217", 23132);
            client.send(data);
            clientSocket.close();
            System.out.println(data.length);
            fis.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
