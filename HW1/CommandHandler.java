public class CommandHandler {

  private Inventory inventory;

  public CommandHandler(Inventory inventory) {
    this.inventory = inventory;
  }

  public String handle(byte[] message) {
    String[] tokens = new String(message).trim().split(" ");
    
    switch(tokens[0]) {
      case "purchase":
        return inventory.purchase(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
      case "list":
        return inventory.list();
      case "search":
        return inventory.search(tokens[1]);
      case "cancel":
        return inventory.cancel(Integer.parseInt(tokens[1]));
      default: 
        return String.format("Invalid command %s", tokens[0]);
    }
  }
}
