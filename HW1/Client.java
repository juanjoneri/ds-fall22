import java.util.Scanner;
import java.net.*;
import java.io.*;


public class Client {

  public static void sendUdpCommand(int port, String command) throws UnknownHostException, SocketException, IOException
  {
    String hostname;
    //int port = 2018;
    int len = 1024;
    DatagramPacket sPacket, rPacket;
    hostname = "localhost";

    try
    {
      InetAddress ia = InetAddress.getByName(hostname);
      DatagramSocket dataSocket = new DatagramSocket();
      //BufferedReader stdinp = new BufferedReader(new InputStreamReader(System.in));
      //while (true)
      {
        try
        {
          String echoline = command;
          //String echoline = stdinp.readLine();
          //if (echoline.equals("done")) break;
          byte[] buffer = new byte[echoline.length()];
          buffer = echoline.getBytes();
          sPacket = new DatagramPacket(buffer, buffer.length, ia, port);
          dataSocket.send(sPacket);
          byte[] rbuf = new byte[1024];
          DatagramPacket returnPacket = new DatagramPacket(rbuf, 1024);
          dataSocket.receive(returnPacket);
          String msg = new String(returnPacket.getData());
          System.out.print(msg);
          //byte[] rbuffer = new byte[len];
          //rPacket = new DatagramPacket(rbuffer, rbuffer.length);
          //dataSocket.send(rPacket);
          //String retstring = new String(rPacket.getData(), 0, rPacket.getLength());
          //System.out.println(retstring);
        }
        catch (IOException e)
        {
          System.err.println(e);
        }
      }// while
    }
    catch (UnknownHostException e)
    {
      System.err.println(e);
    }
    catch (SocketException se)
    {
      System.err.println(se);
    }
  }

  public static void main (String[] args) throws UnknownHostException, SocketException, IOException {
    String hostAddress;
    int tcpPort;
    int udpPort;

    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <hostAddress>: the address of the server");
      System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(3) <udpPort>: the port number for UDP connection");
      System.exit(-1);
    }

    hostAddress = args[0];
    tcpPort = Integer.parseInt(args[1]);
    udpPort = Integer.parseInt(args[2]);

    Scanner sc = new Scanner(System.in);
    while(sc.hasNextLine()) {
      String cmd = sc.nextLine();
      String[] tokens = cmd.split(" ");

      if (tokens[0].equals("setmode")) {
        // TODO: set the mode of communication for sending commands to the server 
        // and display the name of the protocol that will be used in future
      }
      else if (tokens[0].equals("purchase")) {
        System.out.println("CLIENT: Purchase if hit!");
        System.out.println(cmd);
        sendUdpCommand(udpPort, cmd);
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
      } else if (tokens[0].equals("cancel")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
      } else if (tokens[0].equals("search")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
      } else if (tokens[0].equals("list")) {
        // TODO: send appropriate command to the server and display the
        // appropriate responses form the server
      } else {
        System.out.println("ERROR: No such command");
      }
    }
  }
}
