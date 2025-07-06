package org.sampong.views;

import org.sampong.controller.TransactionRecordController;
import org.sampong.dto.TrxRequest;
import org.sampong.models.enumerate.TransactionType;
import org.sampong.services.AccountService;
import org.sampong.services.TransactionRecordService;
import org.sampong.services.UserService;
import org.sampong.services.implement.AccountServiceImpl;
import org.sampong.services.implement.TransactionRecordServiceImpl;
import org.sampong.services.implement.UserServiceImpl;

import java.util.ArrayList;
import java.util.Scanner;

public class TransactionView {
    private static final TransactionRecordService trxRecService = new TransactionRecordServiceImpl();
    private static final TransactionRecordController trxRecController = new TransactionRecordController();
    private static final AccountService accountService = new AccountServiceImpl();
    private static final UserService userService = new UserServiceImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public TransactionView() {
    }

    public void handleTransactionRequest() {
        var running = true;
        System.out.println("\n\n");
        while (running) {
            System.out.print("\n");
            System.out.println(transactionMenu());
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    transferBalance();
                    System.out.println("Transfer completed. \n\n");
                }
                case 2 -> {
                    depositToAccount();
                    System.out.println("Deposit completed. \n\n");
                }
                case 3 -> {
                    withdrawToAccount();
                    System.out.println("Withdraw completed. \n\n");
                }
                case 4 -> {
                    System.out.println(trxRecService.tableList(trxRecController.getAllTransactionRecords()));
                }
                case 5 -> {
                    System.out.println("Return to main menu.\n\n");
                    running = false;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    private void depositToAccount() {
        var userList = new ArrayList<>(userService.getAll());
        System.out.println(userService.tableList(userList));
        System.out.println("\nSelect user id as source id: ");
        var userId = scanner.nextLong();
        var accountList = accountService.getAccountByUserId(userId);
        System.out.println(accountService.tableList(accountList));
        System.out.println("\nSelect account id as source id: ");
        var accountId = scanner.nextLong();
        System.out.println("\nEnter the balance to deposit: ");
        var balance = scanner.nextDouble();

        var account = accountList.stream().filter(i -> i.id() == accountId).findFirst().orElse(null);

        assert account != null;

        var trxRec = new TrxRequest(
                userId, account.id(), account.accountNumber(), balance,
                null, null, null, TransactionType.DEPOSIT
        );
        trxRecController.depositTransaction(trxRec);
    }

    private void withdrawToAccount() {
        var userList = new ArrayList<>(userService.getAll());
        System.out.println(userService.tableList(userList));
        System.out.println("\nSelect user id as source id: ");
        var userId = scanner.nextLong();
        var accountList = accountService.getAccountByUserId(userId);
        System.out.println(accountService.tableList(accountList));
        System.out.println("\nSelect account id as source id: ");
        var accountId = scanner.nextLong();
        System.out.println("\nEnter the balance to withdraw: ");
        var balance = scanner.nextDouble();

        var account = accountList.stream().filter(i -> i.id() == accountId).findFirst().orElse(null);

        assert account != null;

        var trxRec = new TrxRequest(
                userId, account.id(), account.accountNumber(), balance,
                null, null, null, TransactionType.WITHDRAW
        );
        trxRecController.withdrawTransaction(trxRec);
    }

    private void transferBalance() {
        var userList = new ArrayList<>(userService.getAll());
        System.out.println(userService.tableList(userList));
        System.out.println("\nSelect user id as source id: ");
        var userId = scanner.nextLong();
        var accountList = accountService.getAccountByUserId(userId);
        System.out.println(accountService.tableList(accountList));
        System.out.println("\nSelect account id as source id: ");
        var accountId = scanner.nextLong();
        System.out.println("\nEnter the balance to transfer: ");
        var balance = scanner.nextDouble();
        userList.removeIf(i -> i.id().equals(userId));
        System.out.println(userService.tableList(userList));
        System.out.println("\nSelect user id as destination id: ");
        var destUserId = scanner.nextLong();
        var destAccountList = accountService.getAccountByUserId(destUserId);
        System.out.println(accountService.tableList(destAccountList));
        var destAccId = scanner.nextLong();

        var account = accountList.stream().filter(i -> i.id() == accountId).findFirst().orElse(null);
        var destAccount = destAccountList.stream().filter(i -> i.id() == destAccId).findFirst().orElse(null);

        assert account != null;
        assert destAccount != null;

        var trxRec = new TrxRequest(
                userId, account.id(), account.accountNumber(), balance,
                destUserId, destAccount.id(), destAccount.accountNumber(), TransactionType.TRANSFER
        );
        trxRecController.transferTransaction(trxRec);
    }

    private String transactionMenu() {
        return """
                1. Transfer balance to another account.
                2. Deposit balance to account.
                3. Withdrawal balance from account.
                4. List all transactions.
                5. Return to main menu.
                """.trim();
    }
}
