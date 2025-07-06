package org.sampong.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sampong.controller.AccountController;
import org.sampong.dto.AccountResponse;
import org.sampong.models.Account;
import org.sampong.models.User;
import org.sampong.services.AccountService;
import org.sampong.services.UserService;
import org.sampong.services.implement.AccountServiceImpl;
import org.sampong.services.implement.UserServiceImpl;

import java.util.Locale;
import java.util.Scanner;

public class AccountView {
    private static final AccountService service = new AccountServiceImpl();
    private static final UserService userService = new UserServiceImpl();
    private static final AccountController accountController = new AccountController();
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserView userView = new UserView();

    public AccountView() {
    }

    public void handleAccount() {
        var running = true;
        System.out.println("\n\n");
        while (running) {
            System.out.print("\n");
            System.out.println(accountMenu());
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    var savedAccount = addAccount();
                    if (savedAccount != null) {
                        System.out.println("Account created successfully: " + toJson(savedAccount));
                    } else {
                        System.out.println("Account creation failed");
                    }
                }
                case 2 -> {
                    System.out.println("Enter Account id: ");
                    Long id = scanner.nextLong();
                    var account = accountController.deleteAccount(id);
                    System.out.println("Account " + account.accountNumber() + " has been deleted.");
                }
                case 3 -> {
                    System.out.println("Enter your account id: ");
                    var account = accountController.getAccountById(scanner.nextLong());
                    if (account == null) {
                        System.out.println("Account not found");
                    } else {
                        System.out.println("Account details: " + toJson(account));
                    }
                }
                case 4 -> {
                    var accountList = accountController.getAllAccounts();
                    System.out.println(service.tableList(accountList));
                }
                case 5 -> {
                    System.out.println("Enter your account number: ");
                    var account = accountController.getByAccountNumber(scanner.nextLine());
                    if (account == null) {
                        System.out.println("Account not found");
                    } else {
                        System.out.println("Account details: " + toJson(account));
                    }
                }
                case 6 -> {
                    System.out.println("Return to main menu.\n\n");
                    running = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }

    }

    private Account addAccount() {
        var userList = userService.getAll();
        System.out.println(userService.tableList(userList));
        System.out.println("\nSelect user id for account creation: ");
        var userId = scanner.nextLong();
        var user = new User();
        double balance = 0.0;
        for (User value : userList) {
            if (value.id() == userId) {
                user = value;
                break;
            }
        }
        System.out.println("\nDo you want to add any balance to this account(y/N): ");
        var confirmation = scanner.nextLine();
        if (confirmation.equals("Y") || confirmation.equals("y")) {
            System.out.println("Enter the balance amount that you want to add: ");
            balance = scanner.nextDouble();
        }
        var account = new Account();
        account.setBalance(balance);
        account.setUser(user);
        account.setAccountName(user.username().toUpperCase(Locale.ROOT));
        account.setCurrency("USD");
        account.setStatus(true);
        return accountController.addNewAccount(account);
    }

    public String toJson(Account account) {
        var mapper = new ObjectMapper();
        try {
            var accountResponse = toResponse(account);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(accountResponse);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Failed to serialize Account to JSON\"}";
        }
    }

    public AccountResponse toResponse(Account account) {
        var user = userView.toResponse(account.user());
        return new AccountResponse(account.id(), account.accountNumber(), account.accountName(), account.balance().doubleValue(), account.currency(), user);
    }

    private String accountMenu() {
        return """
                1. Create Account.
                2. Delete Account.
                3. Get account by id.
                4. Get all accounts.
                5. Get account by account number.
                6. Return to main menu.
                """;
    }

}
