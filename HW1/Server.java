public class Server {
  public static void main(String[] args) throws Exception {

    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(2) <udpPort>: the port number for UDP connection");
      System.out.println("\t(3) <file>: the file of inventory");

      System.exit(-1);
    }

    int tcpPort = Integer.parseInt(args[0]);
    int udpPort = Integer.parseInt(args[1]);
    String fileName = args[2];

    // TODO: getters and setters for inventory map, how do we make this thread safe?

    // udpServer(udpPort);
    Inventory inventory = new Inventory(fileName);
    inventory.purchase("sammy", "xbox", 3);
    System.out.println(inventory);

    CommandParser serverParser = new CommandParser(inventory);

    UDPServer server = new UDPServer(udpPort, serverParser);
    server.start();
  }
}
