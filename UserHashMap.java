// User class
class User {
    int userId;
    String name;
    String email;
    double balance;
    boolean isSuspended;
    int totalTransactions;
    double totalSpent;

    public User(int userId, String name, String email, double balance) {
        this.userId            = userId;
        this.name              = name;
        this.email             = email;
        this.balance           = balance;
        this.isSuspended       = false;
        this.totalTransactions = 0;
        this.totalSpent        = 0;
    }

    @Override
    public String toString() {
        String status = isSuspended ? "SUSPENDED" : "Active";
        return String.format("  User#%d | %s | %s | Balance: $%.2f | Txns: %d | Status: %s",
                userId, name, email, balance, totalTransactions, status);
    }
}

// DATA STRUCTURE: HASH MAP -- Fast User Lookup O(1)
public class UserHashMap {

    static class UserNode {
        int key;
        User user;
        UserNode next;

        public UserNode(int key, User user) {
            this.key  = key;
            this.user = user;
            this.next = null;
        }
    }

    private UserNode[] table;
    private int size;
    private int count;

    public UserHashMap(int size) {
        this.size  = size;
        this.table = new UserNode[size];
        this.count = 0;
    }

    private int hash(int userId) {
        return Math.abs(userId) % size;
    }

    // Add user
    public boolean put(User user) {
        if (get(user.userId) != null) return false;
        int index = hash(user.userId);
        UserNode newNode = new UserNode(user.userId, user);
        if (table[index] == null) {
            table[index] = newNode;
        } else {
            UserNode curr = table[index];
            while (curr.next != null) curr = curr.next;
            curr.next = newNode;
        }
        count++;
        return true;
    }

    // Get user by ID -- O(1) average
    public User get(int userId) {
        int index = hash(userId);
        UserNode curr = table[index];
        while (curr != null) {
            if (curr.key == userId) return curr.user;
            curr = curr.next;
        }
        return null;
    }

    // Remove user by ID
    public boolean remove(int userId) {
        int index = hash(userId);
        UserNode curr = table[index];

        if (curr == null) return false;

        if (curr.key == userId) {
            table[index] = curr.next;
            count--;
            return true;
        }

        while (curr.next != null) {
            if (curr.next.key == userId) {
                curr.next = curr.next.next;
                count--;
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    // Display all users
    public void displayAll() {
        System.out.println("\nAll Users");
        System.out.println("------------------------------");
        for (UserNode node : table) {
            UserNode curr = node;
            while (curr != null) {
                System.out.println(curr.user);
                curr = curr.next;
            }
        }
        System.out.println("Total users: " + count);
    }

    public int getCount() { return count; }

    // Get all users as array
    public User[] getAllUsers() {
        User[] users = new User[count];
        int i = 0;
        for (UserNode node : table) {
            UserNode curr = node;
            while (curr != null) {
                users[i++] = curr.user;
                curr = curr.next;
            }
        }
        return users;
    }
}
