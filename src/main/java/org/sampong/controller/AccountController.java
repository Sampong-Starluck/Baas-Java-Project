package org.sampong.controller;

import org.sampong.models.Account;
import org.sampong.services.AccountService;
import org.sampong.services.implement.AccountServiceImpl;

import java.util.List;

public class AccountController {
    private static final AccountService accountService = new AccountServiceImpl();

    public AccountController() {}

    public static Account getAccount(Long id) {
        return accountService.getById(id);
    }

    public static Account getByAccountNumber(String name) {
        return accountService.getByName(name);
    }

    public static Account addNewAccount(Account account) {
        return accountService.addNew(account);
    }

    public static Account updateAccount(Account account) {
        return accountService.updateObject(account);
    }

    public Account deleteAccount(Long id) {
        return accountService.delete(id);
    }

    public List<Account> getAllAccounts() {
        return accountService.getAll();
    }
}
