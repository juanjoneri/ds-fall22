import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collection;

public class Transaction {

    private ConcurrentHashMap<String, Integer> items = new ConcurrentHashMap<>();
    private boolean canceled = false;

    public Transaction() {
    }

    public void addItems(String itemName, int quantity) {
        items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
    }

    public void cancel() {
        this.canceled = true;
    }

    public boolean isValid() {
        return items.values().stream().allMatch(q -> q >= 0);
    }

    public static Transaction mergeTransactions(Transaction t1, Transaction t2) {
        Collection<Transaction> transactions = new ArrayList<>();
        transactions.add(t1);
        transactions.add(t2);
        return mergeTransactions(transactions);
    }

    public static Transaction mergeTransactions(Collection<Transaction> transactions) {
        Transaction mergedTransaction = new Transaction();
        for (Transaction t : transactions) {
            if (t.canceled) {
                continue;
            }
            t.items.entrySet().forEach(entry -> mergedTransaction.addItems(entry.getKey(), entry.getValue()));
        }
        return mergedTransaction;
    }

    @Override
    public String toString() {
        StringBuilder name = new StringBuilder();
        if (canceled) {
            name.append("[CANCELED]");
        }
        items.entrySet().forEach(entry -> name.append(String.format("\n%s: %d", entry.getKey(), entry.getValue())));
        return name.toString();
    }
}
