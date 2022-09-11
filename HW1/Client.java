import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

  private static final int BUFFER_LENGTH = 1024;
  
  private Constants.Protocol protocol = Constants.Protocol.UDP; // TODO: set to udp
  
  private final InetAddress host;

  private final int tcpPort;
  private final int udpPort;

  public Client(String hostAddress, int hostTcpPort, int tcpPort, int udpPort) throws Exception {
    host = InetAddress.getByName(hostAddress);
    this.tcpPort = tcpPort;
    this.udpPort = udpPort;

    establishConnection(hostTcpPort);
  }

  public void setProtocol(Constants.Protocol protocol) {
    // TODO: Send message to server to request switching to new protocol
    this.protocol = protocol;
  }

  public String send(String command) throws Exception {
    System.out.println("Requested to send " + command);
    if (protocol.equals(Constants.Protocol.UDP)) {
      return sendUdp(command, udpPort);
    }
    return sendTcp(command, tcpPort);
  }

  private void establishConnection(int hostTcpPort) throws Exception {
    // TODO: use sendTcp
    sendUdp(String.format("connect %d %d", tcpPort, udpPort), hostTcpPort);
  }

  private String sendUdp(String command, int port) throws Exception {
    System.out.println(String.format("sending %s to %d", command, port));
    DatagramSocket dataSocket = new DatagramSocket();

    byte[] outBuffer = command.getBytes();
    DatagramPacket sPacket = new DatagramPacket(outBuffer, outBuffer.length, host, port);
    dataSocket.send(sPacket);

    byte[] inBuffer = new byte[BUFFER_LENGTH];
    DatagramPacket returnPacket = new DatagramPacket(inBuffer, BUFFER_LENGTH);
    dataSocket.receive(returnPacket);
    String response = new String(returnPacket.getData());
    dataSocket.close();

    return response;
  }

  private String sendTcp(String command, int port) throws Exception {
    return ""; // TODO
  }
}
