package org.sampong.views;

import org.sampong.controller.AccountController;
import org.sampong.services.AccountService;
import org.sampong.services.implement.AccountServiceImpl;

import java.util.Scanner;

public class AccountView {
    private static AccountService service = new AccountServiceImpl();
    private static final AccountController controller = new AccountController();
    private static final Scanner scanner = new Scanner(System.in);

    public AccountView() {}

    public void handleAccount(){

    }
    private String accountMenu() {
        return """
                1. Create Account.
                2. Update Account.
                3. Delete Account.
                4. Get account by id.
                5. Get all accounts.
                6. Get account by name.
                """;
    }

}
