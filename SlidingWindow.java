// DATA STRUCTURE: QUEUE (Sliding Window) -- Monitor Recent Transactions Per User
// Keeps last N transactions to detect rapid/suspicious patterns
public class SlidingWindow {

    static class WindowNode {
        Transaction txn;
        WindowNode next;

        public WindowNode(Transaction txn) {
            this.txn  = txn;
            this.next = null;
        }
    }

    private WindowNode front;
    private WindowNode rear;
    private int size;
    private static final int WINDOW_SIZE    = 5;     // Track last 5 transactions
    private static final long TIME_WINDOW_MS = 60000; // 1 minute window

    public SlidingWindow() {
        this.front = null;
        this.rear  = null;
        this.size  = 0;
    }

    // Enqueue new transaction
    public void enqueue(Transaction txn) {
        WindowNode newNode = new WindowNode(txn);
        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;

        // Remove oldest if over window size
        if (size > WINDOW_SIZE) {
            front = front.next;
            size--;
        }
    }

    // Check for rapid transactions (3+ txns within 1 minute)
    public boolean isRapidTransactions() {
        if (size < 3) return false;
        WindowNode curr = front;
        int count = 0;
        long now = System.currentTimeMillis();

        while (curr != null) {
            if (now - curr.txn.timestamp <= TIME_WINDOW_MS) count++;
            curr = curr.next;
        }
        return count >= 3;
    }

    // Check for amount spike (current txn 3x above window average)
    public boolean isAmountSpike(double currentAmount) {
        if (size < 2) return false;
        double total = 0;
        int count    = 0;
        WindowNode curr = front;

        while (curr != null) {
            total += curr.txn.amount;
            count++;
            curr = curr.next;
        }

        double avg = total / count;
        return currentAmount > avg * 3;
    }

    // Check for location hopping (3+ different locations in window)
    public boolean isLocationHopping() {
        if (size < 2) return false;
        java.util.HashSet<String> locations = new java.util.HashSet<>();
        WindowNode curr = front;
        while (curr != null) {
            locations.add(curr.txn.location);
            curr = curr.next;
        }
        return locations.size() >= 3;
    }

    // Get total amount in window
    public double getWindowTotal() {
        double total = 0;
        WindowNode curr = front;
        while (curr != null) {
            total += curr.txn.amount;
            curr = curr.next;
        }
        return total;
    }

    public void display() {
        System.out.println("Recent " + size + " transactions in window:");
        WindowNode curr = front;
        while (curr != null) {
            System.out.println("  " + curr.txn);
            curr = curr.next;
        }
    }

    public int getSize() { return size; }
}
