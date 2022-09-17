import java.io.FileReader;
import java.io.BufferedReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Supports operations on {@link Transaction}s such as canceling, searching,
 * listing, etc.
 */
public class Inventory {

    private ConcurrentHashMap<Integer, Transaction> transactions = new ConcurrentHashMap<>();
    private AtomicInteger nextTransactionId = new AtomicInteger(0);

    public Inventory(String filePath) throws Exception {
        FileReader fileReader = new FileReader(filePath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        Transaction initialTransaction = new Transaction();
        while (bufferedReader.ready()) {
            String[] line = bufferedReader.readLine().split(" ");
            if (line.length > 1) {
                initialTransaction.addItems(line[0], Integer.parseInt(line[1]));
            }
        }
        bufferedReader.close();
        addTransaction(initialTransaction);
    }

    public String cancel(int transactionId) {
        if (transactionId > 0 && transactions.containsKey(transactionId)) {
            transactions.get(transactionId).cancel();
            return String.format("Order %d is canceled", transactionId);
        }
        return String.format("%d not found, no such order", transactionId);
    }

    public String search(String username) {
        String result = transactions.entrySet().stream()
                .filter(e -> !e.getValue().isCanceled() && e.getValue().getUsername().equals(username))
                .map(e -> String.format("%d, %s", e.getKey(), e.getValue().singleItem()))
                .collect(Collectors.joining("\n"));

        return result.isEmpty() ? String.format("No order found for %s", username)
                : result;
    }

    public String purchase(String username, String itemName, int quantity) {
        Transaction t = new Transaction(username, itemName, quantity);
        int transactionId = addTransaction(t);
        return transactionId == -1 ? "Not Available - Not enough items"
                : String.format("You order has been placed, %d %s %s %d", transactionId, username, itemName, quantity);
    }

    public String list() {
        return computeInventory().list().stream().collect(Collectors.joining("\n"));
    }

    private Transaction computeInventory() {
        return Transaction.mergeTransactions(transactions.values());
    }

    private int addTransaction(Transaction t) {
        Transaction newInventory = Transaction.mergeTransactions(computeInventory(), t);
        if (!newInventory.isValid()) {
            return -1;
        }

        int transactionId = nextTransactionId.getAndIncrement();
        transactions.put(transactionId, t);
        return transactionId;
    }

    @Override
    public String toString() {
        return list();
    }

    public static void main(String[] args) throws Exception {
        Inventory inventory = new Inventory("input/inventory.txt");

        System.out.println(inventory.list());

        System.out.println(inventory.purchase("juan", "phone", 5));
        System.out.println(inventory.purchase("juan", "phone", 16));
        System.out.println(inventory.purchase("sammy", "camera", 9));
        System.out.println(inventory.purchase("sammy", "xbox", 8));

        System.out.println(inventory.list());

        System.out.println(inventory.search("juan"));
        System.out.println(inventory.search("sammy"));

        System.out.println(inventory.cancel(2));
        System.out.println(inventory.cancel(4));

        System.out.println(inventory.search("sammy"));
        System.out.println(inventory.search("invalidUser"));
    }
}
