import static java.util.stream.Collectors.toList;

import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Transaction {

    private ConcurrentHashMap<String, Integer> items = new ConcurrentHashMap<>();
    private boolean canceled = false;
    private final String username;

    public Transaction() {
        this.username = "admin";
    }

    public Transaction(String username, String itemName, int quantity) {
        this.username = username;
        purchase(itemName, quantity);
    }

    public List<String> list() {
        return items.entrySet().stream()
                .sorted(ConcurrentHashMap.Entry.comparingByKey())
                .map(e -> String.format("%s %d", e.getKey(), e.getValue()))
                .collect(toList());
    }

    public String singleItem() {
        ConcurrentHashMap.Entry<String, Integer> e = items.entrySet().iterator().next();
        return String.format("%s, %d", e.getKey(), -e.getValue());
    }

    public boolean isValid() {
        return items.values().stream().allMatch(q -> q >= 0);
    }

    public String getUsername() {
        return username;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        this.canceled = true;
    }

    public void purchase(String itemName, int quantity) {
        addItems(itemName, -quantity);
    }

    public void addItems(String itemName, int quantity) {
        items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
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
}
