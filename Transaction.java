public class Transaction {
    int transactionId;
    int userId;
    double amount;
    String type;       // "transfer", "withdrawal", "purchase"
    String location;
    long timestamp;    // System.currentTimeMillis()
    boolean isFlagged;
    String flagReason;
    Transaction next;

    public Transaction(int transactionId, int userId, double amount, String type, String location) {
        this.transactionId = transactionId;
        this.userId        = userId;
        this.amount        = amount;
        this.type          = type;
        this.location      = location;
        this.timestamp     = System.currentTimeMillis();
        this.isFlagged     = false;
        this.flagReason    = "";
        this.next          = null;
    }

    public String getTimeString() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
        return sdf.format(new java.util.Date(timestamp));
    }

    @Override
    public String toString() {
        String flag = isFlagged ? " [FLAGGED: " + flagReason + "]" : "";
        return String.format("  TXN#%04d | User:%d | $%.2f | %s | %s | %s%s",
                transactionId, userId, amount, type, location, getTimeString(), flag);
    }
}
