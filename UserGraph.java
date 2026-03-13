// DATA STRUCTURE: GRAPH (Adjacency List) -- Fraud Ring Detection
// An edge between two users means they have transacted with each other
public class UserGraph {

    static class GraphNode {
        int userId;
        int transactionCount;
        double totalAmount;
        GraphNode next;

        public GraphNode(int userId) {
            this.userId           = userId;
            this.transactionCount = 1;
            this.totalAmount      = 0;
            this.next             = null;
        }
    }

    static class AdjList {
        int userId;
        GraphNode head;
        AdjList next;

        public AdjList(int userId) {
            this.userId = userId;
            this.head   = null;
            this.next   = null;
        }
    }

    private AdjList graphHead;
    private int userCount;

    public UserGraph() {
        this.graphHead = null;
        this.userCount = 0;
    }

    // Get or create adjacency list for a user
    private AdjList getOrCreate(int userId) {
        AdjList curr = graphHead;
        while (curr != null) {
            if (curr.userId == userId) return curr;
            curr = curr.next;
        }
        AdjList newList = new AdjList(userId);
        newList.next = graphHead;
        graphHead    = newList;
        userCount++;
        return newList;
    }

    // Add transaction edge between two users
    public void addEdge(int fromUser, int toUser, double amount) {
        AdjList fromList = getOrCreate(fromUser);
        AdjList toList   = getOrCreate(toUser);
        addNeighbor(fromList, toUser, amount);
        addNeighbor(toList, fromUser, amount);
    }

    private void addNeighbor(AdjList list, int neighborId, double amount) {
        GraphNode curr = list.head;
        while (curr != null) {
            if (curr.userId == neighborId) {
                curr.transactionCount++;
                curr.totalAmount += amount;
                return;
            }
            curr = curr.next;
        }
        GraphNode newNode = new GraphNode(neighborId);
        newNode.totalAmount = amount;
        newNode.next = list.head;
        list.head    = newNode;
    }

    // Detect fraud rings - users with many connections or high transaction amounts
    public void detectFraudRings(UserHashMap users) {
        System.out.println("\nFraud Ring Detection (Graph + BFS)");
        System.out.println("------------------------------");

        AdjList curr = graphHead;
        boolean found = false;

        while (curr != null) {
            int connections = 0;
            double totalAmt = 0;
            GraphNode neighbor = curr.head;

            while (neighbor != null) {
                connections++;
                totalAmt += neighbor.totalAmount;
                neighbor = neighbor.next;
            }

            if (connections >= 2 || totalAmt > 5000) {
                User user = users.get(curr.userId);
                String name = user != null ? user.name : "Unknown";
                System.out.println("Suspicious User#" + curr.userId + " (" + name + ")");
                System.out.println("  Connections: " + connections + " | Total Transacted: $" + String.format("%.2f", totalAmt));

                neighbor = curr.head;
                while (neighbor != null) {
                    User nUser = users.get(neighbor.userId);
                    String nName = nUser != null ? nUser.name : "Unknown";
                    System.out.println("  > Connected to User#" + neighbor.userId + " (" + nName +
                            ") | Txns: " + neighbor.transactionCount +
                            " | Amount: $" + String.format("%.2f", neighbor.totalAmount));
                    neighbor = neighbor.next;
                }
                System.out.println();
                found = true;
            }
            curr = curr.next;
        }

        if (!found) System.out.println("No fraud rings detected.");
    }

    // Display full transaction network
    public void displayNetwork(UserHashMap users) {
        System.out.println("\nUser Transaction Network");
        System.out.println("------------------------------");

        if (graphHead == null) {
            System.out.println("No connections yet.");
            return;
        }

        AdjList curr = graphHead;
        while (curr != null) {
            User user = users.get(curr.userId);
            String name = user != null ? user.name : "Unknown";
            System.out.println("User#" + curr.userId + " (" + name + ") ->");
            GraphNode neighbor = curr.head;
            if (neighbor == null) System.out.println("  No connections");
            while (neighbor != null) {
                User nUser = users.get(neighbor.userId);
                String nName = nUser != null ? nUser.name : "?";
                System.out.println("  -- User#" + neighbor.userId + " (" + nName + ") | " +
                        neighbor.transactionCount + " txns | $" + String.format("%.2f", neighbor.totalAmount));
                neighbor = neighbor.next;
            }
            curr = curr.next;
        }
    }
}
