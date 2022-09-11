public class ServerRunner {
  public static void main(String[] args) throws Exception {

    if (args.length != 2) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <port>: the port number for incoming connections");
      System.out.println("\t(2) <file>: the file of inventory");

      System.exit(-1);
    }

    int port = Integer.parseInt(args[0]);
    String fileName = args[1];

    Inventory inventory = new Inventory(fileName);
    CommandHandler handler = new CommandHandler(inventory);

    Server server = new Server(port, handler);

    server.start();
  }
}
