package org.sampong.services.implement;

import org.sampong.models.Account;
import org.sampong.repository.AccountRepository;
import org.sampong.repository.implement.AccountRepositoryImpl;
import org.sampong.services.AccountService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountServiceImpl implements AccountService {
    private static final AccountRepository repository = new AccountRepositoryImpl();

    public AccountServiceImpl() {
    }

    @Override
    public Account getById(Long id) {
        try {
            return repository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Error fetching account by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Account getByName(String username) {
        try {
            return repository.findByName(username).orElse(null);
        } catch (Exception e) {
            System.err.println("Error in getByName: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Account addNew(Account t) {
        if (t.id() != null) {
            System.err.println("User with id " + t.id() + " already exists");
            return null;
        }
        try {
            t.setAccountNumber(generateAccountNumber());
            return repository.save(t);
        } catch (Exception e) {
            System.err.println("Error in addNew: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Account updateObject(Account t) {
        if (t.id() == null || t.id() == 0L) {
            System.err.println("User with id " + t.id() + " not found");
            return null;
        }
        try {
            var oldAccount = getById(t.id());
            if (oldAccount == null || oldAccount.id() == null) {
                System.err.println("User with id " + t.id() + " not found in DB");
                return null;
            }
            oldAccount.setAccountName(t.accountName());
            oldAccount.setBalance(t.balance());
            oldAccount.setCurrency(t.currency());
            return repository.update(t);
        } catch (Exception e) {
            System.err.println("Error in updateObject: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Account delete(Long id) {
        try {
            var deleteAccount = getById(id);
            if (deleteAccount == null || deleteAccount.id() == null) {
                System.err.println("User with id " + id + " not found");
                return null;
            }
            deleteAccount.setStatus(false);
            return repository.update(deleteAccount);
        } catch (Exception e) {
            System.err.println("Error in delete: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Account> getAll() {
        return repository.findAll();
    }

    @Override
    public String tableList(List<Account> t) {
        String[] headers = {
                "ID", "Status", "Account Number", "Account Name", "Balance", "Currency", "User ID"
        };

        List<String[]> rows = new ArrayList<>();
        for (Account account : t) {
            rows.add(new String[]{
                    account.id() == null ? "" : account.id().toString(),
                    account.status() == null ? "" : account.status().toString(),
                    account.accountNumber() == null ? "" : account.accountNumber(),
                    account.accountName() == null ? "" : account.accountName(),
                    account.balance() == null ? "" : account.balance().toString(),
                    account.currency() == null ? "" : account.currency(),
                    account.user() == null ? "" : account.user().id().toString(),
            });
        }
        // Calculate max width for each column
        int[] colWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            colWidths[i] = headers[i].length();
            for (String[] row : rows) {
                if (row[i].length() > colWidths[i]) {
                    colWidths[i] = row[i].length();
                }
            }
        }
        // Build separator
        StringBuilder sep = new StringBuilder("+");
        for (int w : colWidths) {
            sep.append("-".repeat(w + 2)).append("+");
        }
        String separator = sep.toString();

        // Build table
        StringBuilder sb = new StringBuilder();
        sb.append(separator).append("\n|");
        for (int i = 0; i < headers.length; i++) {
            sb.append(" ").append(String.format("%-" + colWidths[i] + "s", headers[i])).append(" |");
        }
        sb.append("\n").append(separator);

        for (String[] row : rows) {
            sb.append("\n|");
            for (int i = 0; i < row.length; i++) {
                sb.append(" ").append(String.format("%-" + colWidths[i] + "s", row[i])).append(" |");
            }
        }
        sb.append("\n").append(separator);

        return sb.toString();
    }

    private String generateAccountNumber() {
        System.out.println("Generating account number");
        var date = new Date();
        var timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        var timeStampMIli = date.getTime();
        return "0" + timeStamp + timeStampMIli;
    }

    @Override
    public List<Account> getAccountByUserId(Long userId) {
        return repository.getAccountsByUserId(userId);
    }
}
