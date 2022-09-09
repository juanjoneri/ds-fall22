import java.io.*;
import java.net.*;
import java.util.*;

import Inventory.*;

public class Server {
  public static void main (String[] args) throws FileNotFoundException, IOException{
    int tcpPort;
    int udpPort;
    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(2) <udpPort>: the port number for UDP connection");
      System.out.println("\t(3) <file>: the file of inventory");

      System.exit(-1);
    }
    tcpPort = Integer.parseInt(args[0]);
    udpPort = Integer.parseInt(args[1]);
    String fileName = args[2];

    // TODO: getters and setters for inventory map, how do we make this thread safe?




    //udpServer(udpPort);
    Inventory inventory = new Inventory(fileName);
    inventory.buyItem("xbox", 3);
    inventory.printMap();

    CommandParser serverParser = new CommandParser(inventory);


    UDPServer server = new UDPServer(udpPort, serverParser);
    server.start();
  }
}
