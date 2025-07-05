package org.sampong.plugins;

import org.hibernate.Session;
import org.sampong.views.AccountView;
import org.sampong.views.UserView;

import java.util.Scanner;

public class Initializer {
    private static Session session;

    private static final UserView userView = new UserView();
    private static AccountView accountView = new AccountView();
    private static Scanner scanner = new Scanner(System.in);

    public Initializer(Scanner scanner) {
        Initializer.scanner = scanner;
    }

    public static boolean interactMenu() {
        System.out.println("""
                Interactive menu:
                1. User.
                2. Account.
                3. Transfer
                4. Shutdown
                """.trim());
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1 -> userView.handleUser();
            case 2 -> accountView.handleAccount();
            case 3 -> handleTransfer();
            case 4 -> {
                System.out.println("\n\n Good Bye !!! \n\n");
                return false; // Signal to exit the loop
            }
            default -> System.out.println("Invalid choice.");
        }
        return true; // Continue looping
    }

    private static void handleTransfer() {
        // Implement transfer-related logic here
        System.out.println("Transfer menu selected.");
    }

    public static void startUp() {
        System.out.println("Initializing...");
        // Open session at startup
        session = DatabaseConfig.getSessionFactory().openSession();

        // Register shutdown hook to close session, SessionFactory, and DataSource
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (session != null && session.isOpen()) {
                session.close();
                System.out.println("\n\nSession closed on shutdown.");
            }
            DatabaseConfig.shutdown();
        }));

        // Print HikariCP pool stats
        DatabaseConfig.printConnectionPoolStats();

        System.out.println("Session is open.");
        System.out.println("\n\n");
        var running = true;
        while (true) {
            running = interactMenu();
            if (!running) {
                System.exit(0);
                break;
            }
        }
    }
}
