import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  
  private Constants.Protocol protocol = Constants.Protocol.UDP;
  
  private final InetAddress host;

  private final int tcpPort;
  private final int udpPort;

  private Socket tcpSocket;

  public Client(String hostAddress, int hostTcpPort, int tcpPort, int udpPort) throws Exception {
    host = InetAddress.getByName(hostAddress);
    this.tcpPort = tcpPort;
    this.udpPort = udpPort;

    establishConnection(hostTcpPort);
  }

  public void setProtocol(Constants.Protocol protocol) {
    this.protocol = protocol;
  }

  public String send(String command) throws Exception {
    if (protocol.equals(Constants.Protocol.UDP)) {
      return sendUdp(command, udpPort);
    }
    return sendTcp(command);
  }

  private void establishConnection(int hostTcpPort) throws Exception {
    sendUdp(String.format("connect %d %d", tcpPort, udpPort), hostTcpPort);
  }

  private String sendUdp(String command, int port) throws Exception {
    DatagramSocket dataSocket = new DatagramSocket();
    Constants.send(command, dataSocket, host, port);
    DatagramPacket returnPacket = Constants.receive(dataSocket);
    String response = new String(returnPacket.getData());
    dataSocket.close();

    return response;
  }

  private String sendTcp(String command) throws Exception {
    tcpSocket = new Socket(host.getHostAddress(), tcpPort);
    Scanner input = new Scanner(tcpSocket.getInputStream()) ;
    PrintStream output = new PrintStream(tcpSocket.getOutputStream());
    output.println(command);
    String response = input.nextLine();
    while (input.hasNextLine()) {
      response += "\n" + input.nextLine();
    }
    output.flush();
    input.close();
    tcpSocket.close();

    return response;
  }
}
