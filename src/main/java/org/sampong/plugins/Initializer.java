package org.sampong.plugins;

import org.hibernate.Session;
import org.sampong.views.AccountView;
import org.sampong.views.TransactionView;
import org.sampong.views.UserView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Initializer {
    private static final Logger log = LoggerFactory.getLogger(Initializer.class);
    private static final UserView userView = new UserView();
    private static final AccountView accountView = new AccountView();
    private static final TransactionView transactionView = new TransactionView();
    private static Session session;
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
            case 3 -> transactionView.handleTransactionRequest();
            case 4 -> {
                log.info("Start ====>> Shutdown the application");
                System.out.println("\n\n Good Bye !!! \n\n");
                return false; // Signal to exit the loop
            }
            default -> log.info("Start ====>> Invalid choice.");
        }
        return true; // Continue looping
    }

    public static void runApplication() {
//        System.out.println("Initializing...");
        log.info("Start ====>> Initialize the application.");
        // Open session at startup
        session = DatabaseConfig.getSessionFactory().openSession();

        // Register shutdown hook to close session, SessionFactory, and DataSource
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (session != null && session.isOpen()) {
                session.close();
                log.info("Start ====>> Session closed on shutdown.");
            }
            DatabaseConfig.shutdown();
        }));

        // Print HikariCP pool stats
        DatabaseConfig.printConnectionPoolStats();

        log.info("Start ====>> Session is open.");
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
