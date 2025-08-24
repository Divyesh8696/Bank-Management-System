import java.io.*;
import java.util.*;

class Account implements Serializable {
    private String name;
    private int pin;
    private long balance;
    private List<String> history;

    public Account(String name, int pin, long balance) {
        this.name = name;
        this.pin = pin;
        this.balance = balance;
        this.history = new ArrayList<>();
        history.add("Account created with balance: " + balance);
    }

    public boolean validatePin(int enteredPin) {
        return this.pin == enteredPin;
    }

    public String getName() {
        return name;
    }

    public long getBalance() {
        return balance;
    }

    public void deposit(long amount) {
        balance += amount;
        history.add("Deposited: " + amount + " | Balance: " + balance);
    }

    public boolean withdraw(long amount) {
        if (amount % 100 != 0) {
            System.out.println("Enter amount in multiples of 100.");
            return false;
        }
        if (amount > (balance - 2000)) {
            System.out.println("Insufficient Balance.");
            return false;
        }
        balance -= amount;
        history.add("Withdrawn: " + amount + " | Balance: " + balance);
        return true;
    }

    public void printHistory() {
        System.out.println("\nTransaction History:");
        for (String h : history) {
            System.out.println(h);
        }
    }
}

public class ATMSystem {
    private static final String FILE_NAME = "accounts.dat";
    private static Map<Integer, Account> accounts = new HashMap<>();
    private static Account currentAccount = null;

    public static void main(String[] args) {
        loadAccounts();
        Scanner sc = new Scanner(System.in);

        int choice;
        do {
            System.out.println("\n------------ ATM Menu ------------");
            System.out.println("1. Login");
            System.out.println("2. Create New Account");
            System.out.println("3. Quit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    login(sc);
                    break;
                case 2:
                    createAccount(sc);
                    break;
                case 3:
                    System.out.println("Thank you for using ATM.");
                    saveAccounts();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 3);
    }

    private static void login(Scanner sc) {
        System.out.print("Enter your PIN: ");
        int pin = sc.nextInt();

        Account acc = accounts.get(pin);
        if (acc == null) {
            System.out.println("Invalid PIN.");
            return;
        }

        currentAccount = acc;
        System.out.println("Welcome, " + acc.getName());

        int choice;
        do {
            System.out.println("\n------------ Account Menu ------------");
            System.out.println("1. Check Balance");
            System.out.println("2. Withdraw Cash");
            System.out.println("3. Deposit Cash");
            System.out.println("4. Transaction History");
            System.out.println("5. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Your Balance: " + acc.getBalance());
                    break;
                case 2:
                    System.out.print("Enter amount to withdraw: ");
                    long wAmt = sc.nextLong();
                    acc.withdraw(wAmt);
                    break;
                case 3:
                    System.out.print("Enter amount to deposit: ");
                    long dAmt = sc.nextLong();
                    acc.deposit(dAmt);
                    break;
                case 4:
                    acc.printHistory();
                    break;
                case 5:
                    System.out.println("Logged out.");
                    saveAccounts();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 5);

        currentAccount = null;
    }

    private static void createAccount(Scanner sc) {
        System.out.print("Enter new name: ");
        String name = sc.next();

        System.out.print("Enter new PIN: ");
        int newPin = sc.nextInt();

        if (accounts.containsKey(newPin)) {
            System.out.println("An account with this PIN already exists. Try another.");
            return;
        }

        System.out.print("Enter initial balance: ");
        long balance = sc.nextLong();

        accounts.put(newPin, new Account(name, newPin, balance));
        System.out.println("Account created successfully for " + name + " with PIN " + newPin);

        saveAccounts();
    }

    private static void loadAccounts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            accounts = (HashMap<Integer, Account>) ois.readObject();
        } catch (Exception e) {
            System.out.println("No saved accounts found, creating new default accounts...");
            accounts.put(2645, new Account("Divyesh", 2645, 103000));
            accounts.put(1111, new Account("John", 1111, 50000));
            accounts.put(2222, new Account("Alice", 2222, 75000));
            saveAccounts();
        }
    }

    private static void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(accounts);
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }
}
