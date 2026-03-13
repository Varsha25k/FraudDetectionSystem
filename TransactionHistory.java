// DATA STRUCTURE: LINKED LIST -- Full Transaction History
public class TransactionHistory {
    private Transaction head;
    private Transaction tail;
    private int size;

    public TransactionHistory() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // Add transaction to end
    public void add(Transaction txn) {
        if (head == null) {
            head = tail = txn;
        } else {
            tail.next = txn;
            tail = txn;
        }
        size++;
    }

    // Get transaction by ID
    public Transaction getById(int txnId) {
        Transaction temp = head;
        while (temp != null) {
            if (temp.transactionId == txnId) return temp;
            temp = temp.next;
        }
        return null;
    }

    // Display all transactions for a specific user
    public void displayUserHistory(int userId) {
        System.out.println("\nTransaction History - User " + userId);
        System.out.println("------------------------------");
        Transaction temp = head;
        boolean found = false;
        while (temp != null) {
            if (temp.userId == userId) {
                System.out.println(temp);
                found = true;
            }
            temp = temp.next;
        }
        if (!found) System.out.println("No transactions found for User " + userId);
    }

    // Display all transactions
    public void displayAll() {
        System.out.println("\nAll Transactions");
        System.out.println("------------------------------");
        if (head == null) {
            System.out.println("No transactions yet.");
            return;
        }
        Transaction temp = head;
        while (temp != null) {
            System.out.println(temp);
            temp = temp.next;
        }
        System.out.println("Total: " + size + " transactions");
    }

    // Display only flagged transactions
    public void displayFlagged() {
        System.out.println("\nFlagged Transactions");
        System.out.println("------------------------------");
        Transaction temp = head;
        int count = 0;
        while (temp != null) {
            if (temp.isFlagged) {
                System.out.println(temp);
                count++;
            }
            temp = temp.next;
        }
        if (count == 0) System.out.println("No flagged transactions.");
        else System.out.println("Total flagged: " + count);
    }

    public int getSize() { return size; }
    public Transaction getHead() { return head; }
}
