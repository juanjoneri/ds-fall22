import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Constants {
    public enum Protocol {
        UDP, TCP;
    }

    public static int BUFFER_LENGTH = 1024;

    public static DatagramPacket receive(DatagramSocket socket) throws Exception {
        byte[] buffer = new byte[Constants.BUFFER_LENGTH];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return packet;

    }

    public static void send(String message, DatagramSocket socket, DatagramPacket dataPacket) throws Exception {
        Constants.send(message, socket, dataPacket.getAddress(), dataPacket.getPort());
    }

    public static void send(String message, DatagramSocket socket, InetAddress address, int port) throws Exception {
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), address, port);
        socket.send(packet);
    }

    public static void ack(DatagramSocket socket, DatagramPacket dataPacket) throws Exception {
        send("OK", socket, dataPacket);
    }
}
