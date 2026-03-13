// DATA STRUCTURE: MAX HEAP -- High Risk Fraud Alerts (highest risk shown first)
public class FraudAlertHeap {

    static class Alert {
        int transactionId;
        int userId;
        double amount;
        int riskScore;   // 1-100
        String reason;

        public Alert(int transactionId, int userId, double amount, int riskScore, String reason) {
            this.transactionId = transactionId;
            this.userId        = userId;
            this.amount        = amount;
            this.riskScore     = riskScore;
            this.reason        = reason;
        }

        @Override
        public String toString() {
            String risk = riskScore >= 80 ? "CRITICAL" :
                          riskScore >= 60 ? "HIGH"     :
                          riskScore >= 40 ? "MEDIUM"   : "LOW";
            return String.format("  [%s | Risk: %d] TXN#%04d | User: %d | $%.2f | %s",
                    risk, riskScore, transactionId, userId, amount, reason);
        }
    }

    private Alert[] heap;
    private int size;
    private static final int MAX_SIZE = 100;

    public FraudAlertHeap() {
        this.heap = new Alert[MAX_SIZE];
        this.size = 0;
    }

    // Insert alert and bubble up
    public void insert(Alert alert) {
        if (size >= MAX_SIZE) return;
        heap[size] = alert;
        bubbleUp(size);
        size++;
    }

    private void bubbleUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap[parent].riskScore < heap[index].riskScore) {
                Alert temp = heap[parent];
                heap[parent] = heap[index];
                heap[index] = temp;
                index = parent;
            } else break;
        }
    }

    // Extract max (highest risk alert)
    public Alert extractMax() {
        if (size == 0) return null;
        Alert max = heap[0];
        heap[0] = heap[--size];
        heap[size] = null;
        bubbleDown(0);
        return max;
    }

    private void bubbleDown(int index) {
        while (true) {
            int left    = 2 * index + 1;
            int right   = 2 * index + 2;
            int largest = index;

            if (left  < size && heap[left].riskScore  > heap[largest].riskScore) largest = left;
            if (right < size && heap[right].riskScore > heap[largest].riskScore) largest = right;

            if (largest != index) {
                Alert temp = heap[largest];
                heap[largest] = heap[index];
                heap[index] = temp;
                index = largest;
            } else break;
        }
    }

    public Alert peekMax() {
        return size > 0 ? heap[0] : null;
    }

    // Display all alerts sorted by risk
    public void displayAlerts() {
        System.out.println("\nFraud Alerts (Highest Risk First)");
        System.out.println("------------------------------");
        if (size == 0) {
            System.out.println("No fraud alerts.");
            return;
        }

        // Copy heap to display without destroying it
        Alert[] copy = new Alert[size];
        for (int i = 0; i < size; i++) copy[i] = heap[i];

        // Sort by risk score descending for display
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (copy[j].riskScore < copy[j + 1].riskScore) {
                    Alert temp = copy[j];
                    copy[j] = copy[j + 1];
                    copy[j + 1] = temp;
                }
            }
        }

        for (Alert a : copy) {
            if (a != null) System.out.println(a);
        }
        System.out.println("Total alerts: " + size);
    }

    public int getSize()    { return size; }
    public boolean isEmpty() { return size == 0; }
}
