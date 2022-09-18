import java.util.Scanner;

public class ClientRunner {
  public static void main(String[] args) throws Exception {
    if (args.length != 4) {
      System.out.println("ERROR: Provide 4 arguments");
      System.out.println("\t(1) <hostName>: the address of the server");
      System.out.println("\t(2) <hostPort>: the port number of the server");
      System.out.println("\t(3) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(4) <udpPort>: the port number for UDP connection");
      System.exit(-1);
    }

    String hostName = args[0];
    int hostTcpPort = Integer.parseInt(args[1]);
    int tcpPort = Integer.parseInt(args[2]);
    int udpPort = Integer.parseInt(args[3]);

    Client client = new Client(hostName, hostTcpPort, tcpPort, udpPort);

    Scanner sc = new Scanner(System.in);

    while (sc.hasNextLine()) {
      String command = sc.nextLine().trim();
      if (command.equals("setmode U")) {
        client.setProtocol(Constants.Protocol.UDP);
        System.out.println("Mode set to UDP");
        continue;
      }
      if (command.equals("setmode T")) {
        client.setProtocol(Constants.Protocol.TCP);
        System.out.println("Mode set to TCP");
        continue;
      }

      System.out.println(client.send(command));
    }
    sc.close();
  }
}
