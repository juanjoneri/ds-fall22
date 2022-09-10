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
    if (protocol.equals(Constants.Protocol.UDP)) {
      return sendUdp(command);
    }
    return sendTcp(command);
  }

  private void establishConnection(int hostTcpPort) {
    // TODO: Send tcpPort and udpPort to hostName:hostTcpPort
    // Host gets the message, creates a thread to serve this client on those two ports
  }

  private String sendUdp(String command) throws Exception {
    DatagramSocket dataSocket = new DatagramSocket();

    byte[] outBuffer = command.getBytes();
    DatagramPacket sPacket = new DatagramPacket(outBuffer, outBuffer.length, host, udpPort);
    dataSocket.send(sPacket);

    byte[] inBuffer = new byte[BUFFER_LENGTH];
    DatagramPacket returnPacket = new DatagramPacket(inBuffer, BUFFER_LENGTH);
    dataSocket.receive(returnPacket);
    String response = new String(returnPacket.getData());
    dataSocket.close();

    return response;
  }

  private String sendTcp(String command) throws Exception {
    return ""; // TODO
  }
}
