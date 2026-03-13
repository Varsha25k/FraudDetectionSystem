import java.util.HashMap;

// Core Fraud Detection Engine
public class FraudDetector {

    private UserHashMap users;
    private TransactionHistory history;
    private FraudAlertHeap alertHeap;
    private UserGraph userGraph;
    private HashMap<Integer, SlidingWindow> userWindows;
    private int nextTxnId;

    // Fraud thresholds
    private static final double HIGH_AMOUNT_THRESHOLD   = 10000.0;
    private static final double MEDIUM_AMOUNT_THRESHOLD =  5000.0;
    private static final double LARGE_SINGLE_TXN        =  2000.0;

    public FraudDetector() {
        this.users       = new UserHashMap(20);
        this.history     = new TransactionHistory();
        this.alertHeap   = new FraudAlertHeap();
        this.userGraph   = new UserGraph();
        this.userWindows = new HashMap<>();
        this.nextTxnId   = 1001;

        loadSampleData();
    }

    // Preload sample users and transactions for demo
    private void loadSampleData() {
        // Users
        addUser(101, "Ahmed Raza",       "ahmed.raza@gmail.com",      25000.00);
        addUser(102, "Fatima Malik",     "fatima.malik@gmail.com",    18000.00);
        addUser(103, "Usman Tariq",      "usman.tariq@gmail.com",     32000.00);
        addUser(104, "Ayesha Siddiqui",  "ayesha.sid@gmail.com",      12000.00);
        addUser(105, "Bilal Khan",       "bilal.khan@gmail.com",       8500.00);
        addUser(106, "Sana Hussain",     "sana.hussain@gmail.com",    45000.00);
        addUser(107, "Zubair Ahmed",     "zubair.ahmed@gmail.com",    15000.00);
        addUser(108, "Nadia Qureshi",    "nadia.qureshi@gmail.com",   22000.00);
        addUser(109, "Hamza Sheikh",     "hamza.sheikh@gmail.com",     9000.00);
        addUser(110, "Mariam Farooq",    "mariam.farooq@gmail.com",   37000.00);
        addUser(111, "Tariq Mehmood",    "tariq.mehmood@gmail.com",   11000.00);
        addUser(112, "Hina Baig",        "hina.baig@gmail.com",       19000.00);
        addUser(113, "Shahzad Iqbal",    "shahzad.iqbal@gmail.com",   55000.00);
        addUser(114, "Rabia Noor",       "rabia.noor@gmail.com",       7000.00);
        addUser(115, "Kamran Javed",     "kamran.javed@gmail.com",    28000.00);
        addUser(116, "Imran Butt",       "imran.butt@gmail.com",      16000.00);
        addUser(117, "Zara Chaudhry",    "zara.chaudhry@gmail.com",   42000.00);
        addUser(118, "Saad Nawaz",       "saad.nawaz@gmail.com",      13000.00);
        addUser(119, "Mehwish Akhtar",   "mehwish.akhtar@gmail.com",  31000.00);
        addUser(120, "Faisal Rehman",    "faisal.rehman@gmail.com",   24000.00);

        // Normal transactions
        loadTransaction(101,  500.00, "purchase",   "Karachi",    0);
        loadTransaction(102,  300.00, "transfer",   "Lahore",   103);
        loadTransaction(103,  750.00, "purchase",   "Islamabad",  0);
        loadTransaction(104,  200.00, "withdrawal", "Karachi",    0);
        loadTransaction(105,  450.00, "purchase",   "Faisalabad", 0);
        loadTransaction(106,  600.00, "transfer",   "Lahore",   107);
        loadTransaction(107,  250.00, "purchase",   "Peshawar",   0);
        loadTransaction(108, 1000.00, "withdrawal", "Karachi",    0);
        loadTransaction(109,  350.00, "purchase",   "Multan",     0);
        loadTransaction(110,  800.00, "transfer",   "Islamabad", 111);
        loadTransaction(112,  400.00, "purchase",   "Quetta",     0);
        loadTransaction(114,  150.00, "withdrawal", "Lahore",     0);
        loadTransaction(116,  700.00, "purchase",   "Karachi",    0);
        loadTransaction(118,  550.00, "transfer",   "Faisalabad",119);
        loadTransaction(119,  900.00, "purchase",   "Islamabad",  0);
        loadTransaction(120,  300.00, "withdrawal", "Karachi",    0);
        loadTransaction(117,  650.00, "purchase",   "Lahore",     0);

        // Suspicious transactions (will trigger fraud alerts)
        loadTransaction(113, 12000.00, "purchase",   "Dubai",      0);
        loadTransaction(106,  9000.00, "transfer",   "Karachi",  113);
        loadTransaction(101,  8000.00, "withdrawal", "Lahore",     0);
        loadTransaction(115,  6000.00, "purchase",   "Islamabad",  0);
        loadTransaction(103, 11000.00, "transfer",   "Peshawar", 106);
        loadTransaction(117, 15000.00, "purchase",   "Dubai",      0);
        loadTransaction(120,  7500.00, "withdrawal", "Karachi",    0);
    }

    // Silent version of processTransaction for preloading sample data
    private void loadTransaction(int userId, double amount, String type,
                                 String location, int receiverId) {
        User user = users.get(userId);
        if (user == null || user.isSuspended) return;

        Transaction txn = new Transaction(nextTxnId++, userId, amount, type, location);
        userWindows.putIfAbsent(userId, new SlidingWindow());
        SlidingWindow window = userWindows.get(userId);

        int riskScore = calculateRiskScore(txn, user, window);
        if (riskScore > 0) {
            txn.isFlagged  = true;
            txn.flagReason = getRiskReason(txn, user, window);
            alertHeap.insert(new FraudAlertHeap.Alert(
                    txn.transactionId, userId, amount, riskScore, txn.flagReason));
        }

        history.add(txn);
        window.enqueue(txn);
        user.totalTransactions++;
        user.totalSpent += amount;
        if (type.equals("withdrawal") || type.equals("purchase")) user.balance -= amount;
        if (receiverId > 0 && type.equals("transfer")) userGraph.addEdge(userId, receiverId, amount);
    }

    // Add new user
    public boolean addUser(int userId, String name, String email, double balance) {
        User newUser = new User(userId, name, email, balance);
        return users.put(newUser);
    }

    // Delete user by ID
    public boolean deleteUser(int userId) {
        return users.remove(userId);
    }

    // Process a transaction and run fraud checks
    public Transaction processTransaction(int userId, double amount, String type,
                                          String location, int receiverId) {
        User user = users.get(userId);
        if (user == null) {
            System.out.println("User not found.");
            return null;
        }
        if (user.isSuspended) {
            System.out.println("Transaction blocked! User " + userId + " is SUSPENDED.");
            return null;
        }

        Transaction txn = new Transaction(nextTxnId++, userId, amount, type, location);

        userWindows.putIfAbsent(userId, new SlidingWindow());
        SlidingWindow window = userWindows.get(userId);

        int riskScore = calculateRiskScore(txn, user, window);

        if (riskScore > 0) {
            txn.isFlagged  = true;
            txn.flagReason = getRiskReason(txn, user, window);
            FraudAlertHeap.Alert alert = new FraudAlertHeap.Alert(
                    txn.transactionId, userId, amount, riskScore, txn.flagReason);
            alertHeap.insert(alert);
            System.out.println("FRAUD ALERT! Risk Score: " + riskScore + " -- " + txn.flagReason);

            if (riskScore >= 90) {
                user.isSuspended = true;
                System.out.println("User " + userId + " AUTO-SUSPENDED due to critical risk!");
            }
        } else {
            System.out.println("Transaction approved. TXN#" + txn.transactionId);
        }

        history.add(txn);
        window.enqueue(txn);

        user.totalTransactions++;
        user.totalSpent += amount;
        if (type.equals("withdrawal") || type.equals("purchase")) {
            user.balance -= amount;
        }

        if (receiverId > 0 && type.equals("transfer")) {
            userGraph.addEdge(userId, receiverId, amount);
        }

        return txn;
    }

    // Calculate fraud risk score (0-100)
    private int calculateRiskScore(Transaction txn, User user, SlidingWindow window) {
        int score = 0;

        // Rule 1: Large amount
        if (txn.amount >= HIGH_AMOUNT_THRESHOLD)        score += 50;
        else if (txn.amount >= MEDIUM_AMOUNT_THRESHOLD) score += 30;
        else if (txn.amount >= LARGE_SINGLE_TXN)        score += 15;

        // Rule 2: Amount exceeds 90% of user balance
        if (txn.amount > user.balance * 0.9) score += 25;

        // Rule 3: Rapid transactions
        if (window.isRapidTransactions()) score += 30;

        // Rule 4: Amount spike vs recent average
        if (window.isAmountSpike(txn.amount)) score += 25;

        // Rule 5: Location hopping
        if (window.isLocationHopping()) score += 20;

        return Math.min(score, 100);
    }

    private String getRiskReason(Transaction txn, User user, SlidingWindow window) {
        StringBuilder reasons = new StringBuilder();
        if (txn.amount >= HIGH_AMOUNT_THRESHOLD)        reasons.append("Extremely large amount. ");
        else if (txn.amount >= MEDIUM_AMOUNT_THRESHOLD) reasons.append("Large amount. ");
        if (txn.amount > user.balance * 0.9)            reasons.append("Exceeds 90% of balance. ");
        if (window.isRapidTransactions())               reasons.append("Rapid transactions. ");
        if (window.isAmountSpike(txn.amount))           reasons.append("Amount spike detected. ");
        if (window.isLocationHopping())                 reasons.append("Multiple locations. ");
        return reasons.length() > 0 ? reasons.toString().trim() : "Suspicious pattern";
    }

    // Getters
    public UserHashMap getUsers()                              { return users; }
    public TransactionHistory getHistory()                     { return history; }
    public FraudAlertHeap getAlertHeap()                       { return alertHeap; }
    public UserGraph getUserGraph()                            { return userGraph; }
    public HashMap<Integer, SlidingWindow> getUserWindows()    { return userWindows; }
}
