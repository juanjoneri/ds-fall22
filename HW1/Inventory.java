import java.io.FileReader;
import java.io.BufferedReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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

    public Transaction computeInventory() {
        return Transaction.mergeTransactions(transactions.values());
    }

    public void cancelTransaction(int transactionId) {
        transactions.get(transactionId).cancel();
    }

    public int buyItem(String itemName, int quantity) {
        Transaction t = new Transaction();
        t.addItems(itemName, -quantity);
        return addTransaction(t);
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
        name.append("Transactions:");
        transactions.entrySet().forEach(e -> name.append(String.format("\n\n(%d) %s", e.getKey(), e.getValue())));
        name.append("\n\nInventory:");
        name.append(computeInventory().toString());
        return name.toString();
    }

    public static void main(String[] args) throws Exception {
        Inventory inventory = new Inventory("input/inventory.txt");
        Transaction t1 = new Transaction();
        t1.addItems("phone", -5);
        inventory.addTransaction(t1);
        Transaction t2 = new Transaction();
        t2.addItems("camera", -10);
        inventory.addTransaction(t2);
        inventory.cancelTransaction(1);
        System.out.println(inventory);
    }
}
