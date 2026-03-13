import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static FraudDetector detector = new FraudDetector();

    public static void main(String[] args) {
        System.out.println("\nFraud Detection System");
        System.out.println("Sample data loaded: 20 users, 24 transactions.\n");

        while (true) {
            mainMenu();
        }
    }

    private static void mainMenu() {
        System.out.println("\nFraud Detection System - Main Menu");
        System.out.println("1.  Add New User");
        System.out.println("2.  Add Transaction");
        System.out.println("3.  View All Transactions");
        System.out.println("4.  View Flagged Transactions");
        System.out.println("5.  View Fraud Alerts");
        System.out.println("6.  View All Users");
        System.out.println("7.  Search User");
        System.out.println("8.  View User History");
        System.out.println("9.  Detect Fraud Rings");
        System.out.println("10. View Transaction Network");
        System.out.println("11. View Sliding Window");
        System.out.println("12. Unsuspend User");
        System.out.println("13. Delete User");
        System.out.println("14. Exit");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:  addUser();            break;
            case 2:  addTransaction();     break;
            case 3:  detector.getHistory().displayAll(); break;
            case 4:  detector.getHistory().displayFlagged(); break;
            case 5:  detector.getAlertHeap().displayAlerts(); break;
            case 6:  detector.getUsers().displayAll(); break;
            case 7:  searchUser();         break;
            case 8:  viewUserHistory();    break;
            case 9:  detector.getUserGraph().detectFraudRings(detector.getUsers()); break;
            case 10: detector.getUserGraph().displayNetwork(detector.getUsers()); break;
            case 11: viewSlidingWindow();  break;
            case 12: unsuspendUser();      break;
            case 13: deleteUser();         break;
            case 14:
                System.out.println("Exiting... Goodbye!");
                System.exit(0);
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void addUser() {
        System.out.println("\nAdd New User");
        System.out.print("User ID   : ");
        int userId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Name      : ");
        String name = scanner.nextLine();
        System.out.print("Email     : ");
        String email = scanner.nextLine();
        System.out.print("Balance   : $");
        double balance = scanner.nextDouble();

        if (detector.addUser(userId, name, email, balance)) {
            System.out.println("User added successfully!");
        } else {
            System.out.println("User ID already exists.");
        }
    }

    private static void addTransaction() {
        System.out.println("\nNew Transaction");
        System.out.print("Sender User ID : ");
        int userId = scanner.nextInt();

        System.out.print("Amount ($)     : ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("Type: 1. Purchase   2. Transfer   3. Withdrawal");
        System.out.print("Choice         : ");
        int typeChoice = scanner.nextInt();
        String type = typeChoice == 1 ? "purchase" : typeChoice == 2 ? "transfer" : "withdrawal";
        scanner.nextLine();

        System.out.print("Location       : ");
        String location = scanner.nextLine();

        int receiverId = 0;
        if (type.equals("transfer")) {
            System.out.print("Receiver User ID: ");
            receiverId = scanner.nextInt();
        }

        System.out.println("\nProcessing transaction...");
        detector.processTransaction(userId, amount, type, location, receiverId);
    }

    private static void searchUser() {
        System.out.print("\nEnter User ID to search: ");
        int userId = scanner.nextInt();
        User user = detector.getUsers().get(userId);
        if (user == null) {
            System.out.println("User not found.");
        } else {
            System.out.println("User found:");
            System.out.println(user);
        }
    }

    private static void viewUserHistory() {
        System.out.print("\nEnter User ID: ");
        int userId = scanner.nextInt();
        detector.getHistory().displayUserHistory(userId);
    }

    private static void viewSlidingWindow() {
        System.out.print("\nEnter User ID to view recent transactions: ");
        int userId = scanner.nextInt();
        SlidingWindow window = detector.getUserWindows().get(userId);
        if (window == null || window.getSize() == 0) {
            System.out.println("No recent transactions for this user.");
        } else {
            System.out.println("\nSliding Window - Last 5 Transactions");
            window.display();
            System.out.println("Window Total       : $" + String.format("%.2f", window.getWindowTotal()));
            System.out.println("Rapid Transactions : " + (window.isRapidTransactions() ? "YES - Warning" : "No"));
            System.out.println("Location Hopping   : " + (window.isLocationHopping()   ? "YES - Warning" : "No"));
        }
    }

    private static void unsuspendUser() {
        System.out.print("\nEnter User ID to unsuspend: ");
        int userId = scanner.nextInt();
        User user = detector.getUsers().get(userId);
        if (user == null) {
            System.out.println("User not found.");
        } else if (!user.isSuspended) {
            System.out.println("User is not suspended.");
        } else {
            user.isSuspended = false;
            System.out.println("User " + userId + " (" + user.name + ") has been unsuspended.");
        }
    }

    private static void deleteUser() {
        System.out.print("\nEnter User ID to delete: ");
        int userId = scanner.nextInt();
        if (detector.deleteUser(userId)) {
            System.out.println("User " + userId + " deleted successfully.");
        } else {
            System.out.println("User not found.");
        }
    }
}
