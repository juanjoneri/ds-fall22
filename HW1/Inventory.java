import static java.util.stream.Collectors.toList;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.List;

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

    // returns false if transactionId is not found
    public boolean cancel(int transactionId) {
        boolean validId = transactions.containsKey(transactionId);
        if (validId) {
            transactions.get(transactionId).cancel();
        }
        return validId;
    }

    // returns empty map if user has no transactions
    public List<String> search(String username) {
        return transactions.entrySet().stream()
                .filter(e -> !e.getValue().isCanceled() && e.getValue().getUsername().equals(username))
                .map(e -> String.format("%d, %s", e.getKey(), e.getValue().singleItem()))
                .collect(toList());
    }

    // returns -1 if transaction is invalid
    public int purchase(String username, String itemName, int quantity) {
        Transaction t = new Transaction(username, itemName, quantity);
        return addTransaction(t);
    }

    public List<String> list() {
        return computeInventory().list();
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
        StringBuilder name = new StringBuilder();
        name.append("Transactions:\n\n");
        transactions.entrySet().forEach(e -> name.append(String.format("(%d) %s\n", e.getKey(), e.getValue())));
        name.append("\nInventory:\n");
        name.append(computeInventory().toString());
        return name.toString();
    }

    public static void main(String[] args) throws Exception {
        Inventory inventory = new Inventory("input/inventory.txt");
        Transaction t1 = new Transaction("juan", "phone", 5);
        Transaction t2 = new Transaction("sammy", "camera", 9);
        Transaction t3 = new Transaction("sammy", "xbox", 8);

        System.out.println(inventory.list());

        inventory.addTransaction(t1);
        inventory.addTransaction(t2);
        inventory.addTransaction(t3);
        
        System.out.println(inventory.list());
        
        System.out.println(inventory.search("juan"));
        System.out.println(inventory.search("sammy"));
        inventory.cancel(2);
        System.out.println(inventory.search("sammy"));
    }
}
