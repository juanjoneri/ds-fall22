public class CommandParser {

  private Inventory inventory;

  public CommandParser(Inventory inventory) {
    this.inventory = inventory;
  }

  public String parseBuffer(byte[] msgData) {
    String msg = new String(msgData);
    String[] tokens = msg.split(" ");
    String returnData = "fail";
    System.out.print(msg);

    if (tokens[0].equals("setmode")) {
      System.out.println("setmode hit");
      // TODO: set the mode of communication for sending commands to the server
      // and display the name of the protocol that will be used in future
    } else if (tokens[0].equals("purchase")) {

      inventory.purchase(tokens[1], tokens[2], Integer.parseInt(tokens[3].trim()));
      System.out.println(inventory);
      returnData = "You purchased : " + tokens[2] + "!!"; // todo add logic for different messages

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

    return returnData;
  }
}
