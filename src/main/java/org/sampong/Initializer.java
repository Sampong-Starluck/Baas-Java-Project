package org.sampong;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.sampong.controller.UserController;
import org.sampong.plugins.DatabaseConfig;

import java.util.Scanner;

public class Initializer {
    private static Session session;

    private static UserController userController = new UserController();
    private static Scanner scanner = new Scanner(System.in);

    public Initializer(UserController userController, Scanner scanner) {
        Initializer.userController = userController;
        Initializer.scanner = scanner;
    }

    public static void interactMenu() {
        System.out.println("""
                Interactive menu:
                1. User.
                2. Account.
                3. Transfer
                """.trim());
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1 -> handleUser();
            case 2 -> handleAccount();
            case 3 -> handleTransfer();
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void handleUser() {
        // Implement user-related logic here
        System.out.println("User menu selected.");
        System.out.println("\n\n");
        System.out.println("""
                1. Get user by ID.
                2. Get all users.
                3. Create new user.
                4. Delete user.
                5. Update user.
                6. Return to main menu
                """.trim());
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1 -> {
                System.out.println("Enter user ID: ");
                Long id = scanner.nextLong();
                var user = userController.getUserById(id);
                System.out.println(user.toString());
            }
            case 2 -> {
                var user = userController.getAllUsers();
                System.out.println(user.toString());
            }
        }

    }

    private static void handleAccount() {
        // Implement account-related logic here
        System.out.println("Account menu selected.");
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
                System.out.println("Session closed on shutdown.");
            }
            DatabaseConfig.shutdown();
        }));

        // Print HikariCP pool stats
        DatabaseConfig.printConnectionPoolStats();

        System.out.println("Session is open.");
        System.out.println("\n\n");
        while (true) {
            interactMenu();
        }
    }

//    public static void startUp() {
//        System.out.println("Initializing...");
//        // Open session at startup
//        session = DatabaseConfig.getSessionFactory().openSession();
//
//        // Register shutdown hook to close session, SessionFactory, and DataSource
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            if (session != null && session.isOpen()) {
//                session.close();
//                System.out.println("Session closed on shutdown.");
//            }
//            DatabaseConfig.shutdown();
//        }));
//
//        // Print HikariCP pool stats
//        DatabaseConfig.printConnectionPoolStats();
//        // Keep the program running (simulate long-running process)
//        try {
//            System.out.println("Session is open.");
//            Thread.sleep(Long.MAX_VALUE);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//        System.out.println("\n\n");
//        while (true) {
//             interactMenu();
//        }
//    }

}
