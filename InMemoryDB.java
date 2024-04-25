import java.util.HashMap;
import java.util.Map;

public class InMemoryDB {
    private Map<String, Integer> hashBase;
    private Map<String, Integer> transactionTrack;
    private boolean inTransaction;

    public InMemoryDB() {
        hashBase = new HashMap<>();
        transactionTrack = new HashMap<>();
        inTransaction = false;
    }

    public Integer get(String key) {
        if (inTransaction && transactionTrack.containsKey(key)) {
            return null; // Return null since it's not committed yet
        }
        return hashBase.get(key); // Returns null if key doesn't exist
    }

    public void put(String key, Integer value) throws IllegalStateException {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress");
        }
        transactionTrack.put(key, value);
    }

    public void beginTransaction() {
        if (inTransaction) {
            throw new IllegalStateException("Transaction already in progress");
        }
        inTransaction = true;
        transactionTrack.clear();
    }

    public void commit() throws IllegalStateException {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress");
        }
        hashBase.putAll(transactionTrack);
        transactionTrack.clear();
        inTransaction = false;
    }

    public void rollback() throws IllegalStateException {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress");
        }
        transactionTrack.clear();
        inTransaction = false;
    }

    // Main method for testing the behavior
    public static void main(String[] args) {
        InMemoryDB db = new InMemoryDB();

        System.out.println(db.get("A")); // Should return null

        try {
            db.put("A", 5); // Should throw exception
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        db.beginTransaction();
        db.put("A", 5);
        System.out.println(db.get("A")); // Should return null
        db.put("A", 6);
        db.commit();
        System.out.println(db.get("A")); // Should return 6

        try {
            db.commit(); // Should throw exception
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        try {
            db.rollback(); // throws an error because there is no ongoing transaction
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        System.out.println(db.get("B")); // Should return null
        db.beginTransaction(); // starts a new transaction
        db.put("B", 10); // Set key B’s value to 10 within the transaction
        db.rollback(); // Rollback the transaction - revert any changes made to B
        System.out.println(db.get("B")); // Should return null because changes to B were rolled back

    }
}
