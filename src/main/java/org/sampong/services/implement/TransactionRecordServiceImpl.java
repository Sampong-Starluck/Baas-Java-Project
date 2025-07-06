package org.sampong.services.implement;

import org.sampong.dto.TrxRequest;
import org.sampong.models.TransactionRecord;
import org.sampong.models.enumerate.TransactionType;
import org.sampong.repository.AccountRepository;
import org.sampong.repository.TransactionRecordRepository;
import org.sampong.repository.implement.AccountRepositoryImpl;
import org.sampong.repository.implement.TransactionRecordRepositoryImpl;
import org.sampong.services.TransactionRecordService;
import org.sampong.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TransactionRecordServiceImpl implements TransactionRecordService {

    private static final Logger logger = LoggerFactory.getLogger(AccountRepositoryImpl.class);

    private final TransactionRecordRepository trxRecRepository = new TransactionRecordRepositoryImpl();
    private final ConcurrentLinkedQueue<TrxRequest> transferQueue = new ConcurrentLinkedQueue<>();
    //    List<TransactionRecord> trxRecList = new CopyOnWriteArrayList<>();
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private AccountRepository accountRepository = new AccountRepositoryImpl();

    public TransactionRecordServiceImpl() {
        scheduler.scheduleAtFixedRate(this::processQueue, 0, 2, TimeUnit.SECONDS);
    }

    public TransactionRecordServiceImpl(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
    }

    @Override
    public TransactionRecord getById(Long id) {
        try {
            return trxRecRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Error fetching account by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public TransactionRecord getByName(String username) {
        return null;
    }

    @Override
    public TransactionRecord addNew(TransactionRecord transactionRecord) {
        if (transactionRecord.id() != null) {
            System.err.println("User with id " + transactionRecord.id() + " already exists");
            return null;
        }
        try {
            return trxRecRepository.save(transactionRecord);
        } catch (Exception e) {
            System.err.println("Error in addNew: " + e.getMessage());
            return null;
        }
    }

    @Override
    public TransactionRecord updateObject(TransactionRecord transactionRecord) {
        return null;
    }

    @Override
    public TransactionRecord delete(Long id) {
        return null;
    }

    @Override
    public List<TransactionRecord> getAll() {
        return trxRecRepository.findAll();
    }

    @Override
    public String tableList(List<TransactionRecord> t) {
        String[] headers = {
                "ID", "From account number", "To account number", "Transaction ID", "From user ID", "To user ID", "Balance", "Transaction type", "Transaction date time"
        };

        List<String[]> rows = new ArrayList<>();
        for (TransactionRecord trxRec : t) {
            rows.add(new String[]{
                    trxRec.id() == null ? "" : trxRec.id().toString(),
                    trxRec.fromAccountNumber() == null ? "" : trxRec.fromAccountNumber(),
                    trxRec.toAccountNumber() == null ? "" : trxRec.toAccountNumber(),
                    trxRec.transactionId() == null ? "" : trxRec.transactionId(),
                    trxRec.fromUserId() == null ? "" : trxRec.fromUserId().toString(),
                    trxRec.toUserId() == null ? "" : trxRec.toUserId().toString(),
                    trxRec.amount() == null ? "" : trxRec.amount().toString(),
                    trxRec.transactionType() == null ? "" : trxRec.transactionType().toString(),
                    trxRec.transactionTimeDate() == null ? "" : trxRec.transactionTimeDate().toString(),
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

    @Override
    public void transferBalance(TrxRequest request) {
        transferQueue.add(request);
    }

    @Override
    public void withdrawBalance(TrxRequest request) {
        transferQueue.add(request);
    }

    @Override
    public void depositBalance(TrxRequest request) {
        transferQueue.add(request);
    }

    private void processQueue() {
        TrxRequest request;
        while ((request = transferQueue.poll()) != null) {
            var status = "";
            try {
                status = processRequest(request);
                System.out.println("\n\n");
                System.out.printf("Processing %s of %.2f (from %s to %s)%n",
                        request.type(), request.balance(), request.accountNumber(), request.desAccountNumber());
                Thread.sleep(Duration.ofMillis(20000));
            } catch (InterruptedException e) {
                System.out.println(status);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                System.out.println("FAILED: Exception - " + e.getMessage());
            }

        }
    }

    private String processRequest(TrxRequest request) {
        boolean success;
        String status;
        if (request.balance() == null || request.balance() <= 0) {
            status = "FAILED: Invalid amount";
        } else {
            switch (request.type()) {
                case TRANSFER -> {
                    var src = accountRepository.checkUserAndAccount(request.userId(), request.accountId(), request.accountNumber());
                    var dest = accountRepository.checkUserAndAccount(request.userId(), request.accountId(), request.accountNumber());
                    if (src == false) {
                        return "FAILED: Unknown transaction type";
                    }
                    if (dest == false) {
                        return "FAILED: Unknown transaction type";
                    }
                    // Always lock in a consistent order to avoid deadlocks
                    Object lock1 = accountRepository.getAccountLock(request.accountId());
                    Object lock2 = accountRepository.getAccountLock(request.desAccountId());
                    Object firstLock = request.accountId() < request.desAccountId() ? lock1 : lock2;
                    Object secondLock = request.accountId() < request.desAccountId() ? lock2 : lock1;
                    synchronized (firstLock) {
                        synchronized (secondLock) {
                            logger.info("trx service >> Beginning to transfer !!!");
                            success = accountRepository.transfer(request.accountId(), request.desAccountId(), request.balance());
                            logger.info("trx service >> Finished transfer !!!");
                        }
                    }
                    status = success ? "SUCCESS" : "FAILED: Transfer failed";
                }
                case WITHDRAW -> {
                    var src = accountRepository.checkUserAndAccount(request.userId(), request.accountId(), request.accountNumber());
                    if (src == false) {
                        return "FAILED: Unknown transaction type";
                    }
                    Object lock = accountRepository.getAccountLock(request.accountId());
                    synchronized (lock) {
                        success = accountRepository.withdraw(request.accountId(), request.balance());
                    }
                    status = success ? "SUCCESS" : "FAILED: Withdraw failed";
                }
                case DEPOSIT -> {
                    var src = accountRepository.checkUserAndAccount(request.userId(), request.accountId(), request.accountNumber());
                    if (src == false) {
                        return "FAILED: Unknown transaction type";
                    }
                    Object lock = accountRepository.getAccountLock(request.accountId());
                    synchronized (lock) {
                        success = accountRepository.deposit(request.accountId(), request.balance());
                    }
                    status = success ? "SUCCESS" : "FAILED: Deposit failed";
                }
                default -> status = "FAILED: Unknown transaction type";
            }
        }


        // Record transaction history
        TransactionRecord trxRec = new TransactionRecord();
        trxRec.setFromAccountId(request.accountId());
        trxRec.setFromUserId(request.userId());
        trxRec.setFromAccountNumber(request.accountNumber());
        trxRec.setAmount(request.balance());
        trxRec.setTransactionType(request.type());
        trxRec.setTransactionId(generateTrxId(trxRec));
        trxRec.setTransactionTimeDate(LocalDateTime.now());
        trxRec.setStatus(true);
        if (request.type() == TransactionType.TRANSFER) {
            trxRec.setToUserId(request.desUserId());
            trxRec.setToAccountId(request.desAccountId());
            trxRec.setToAccountNumber(request.desAccountNumber());
        }
        trxRecRepository.save(trxRec);
        return status;
    }

    /*private void transferProcess(TrxRequest trxRequest) {
        transferQueue.add(trxRequest);
        try (ExecutorService vThreadExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            while (!transferQueue.isEmpty()) {
                TrxRequest request = transferQueue.poll();
                if (request != null) {
                    vThreadExecutor.execute(() -> {
                        synchronized (request) {
                            if (request.type() == TransactionType.TRANSFER) {
                                try {
                                    System.out.printf("Processing transfer of %.2f from %s to %s...%n",
                                            request.balance(), request.accountNumber(), request.desAccountNumber());
                                    Thread.sleep(2000);

                                    var src = accountRepository.checkUserAndAccount(request.userId(), request.accountId(), request.accountNumber());
                                    var dest = accountRepository.checkUserAndAccount(request.userId(), request.accountId(), request.accountNumber());
                                    if (src == false) {
                                        return;
                                    }
                                    if (dest == false) {
                                        return;
                                    }
                                    var account = accountRepository.transfer(request.accountId(), request.desAccountId(), request.balance());
                                    if (account == false) {
                                        System.out.println("Transfer failed");
                                        return;
                                    }
                                    var trxRec = new TransactionRecord();
                                    trxRec.setFromAccountId(request.accountId());
                                    trxRec.setFromUserId(request.userId());
                                    trxRec.setFromAccountNumber(request.accountNumber());
                                    trxRec.setAmount(request.balance());
                                    trxRec.setTransactionType(TransactionType.TRANSFER);
                                    trxRec.setTransactionId(generateTrxId(trxRec));
                                    trxRec.setTransactionTimeDate(LocalDateTime.now());
                                    trxRec.setToUserId(request.userId());
                                    trxRec.setToAccountId(trxRequest.desAccountId());
                                    trxRec.setToAccountNumber(request.desAccountNumber());
                                    trxRecList.add(trxRec);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            } else if (request.type() == TransactionType.WITHDRAW) {
                                try {
                                    System.out.printf("Processing withdraw balance of %.2f from %s...%n",
                                            request.balance(), request.accountNumber());
                                    Thread.sleep(2000);
                                    var src = accountRepository.checkUserAndAccount(request.userId(), request.accountId(), request.accountNumber());
                                    if (src == false) {
                                        return;
                                    }
                                    var account = accountRepository.withdraw(request.accountId(), request.balance());
                                    if (account == false) {
                                        return;
                                    }
                                    var trxRec = new TransactionRecord();
                                    trxRec.setFromAccountId(request.accountId());
                                    trxRec.setFromUserId(request.userId());
                                    trxRec.setFromAccountNumber(request.accountNumber());
                                    trxRec.setAmount(request.balance());
                                    trxRec.setTransactionType(TransactionType.WITHDRAW);
                                    trxRec.setTransactionId(generateTrxId(trxRec));
                                    trxRec.setTransactionTimeDate(LocalDateTime.now());
                                    trxRecList.add(trxRec);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            } else if (request.type() == TransactionType.DEPOSIT) {
                                try {
                                    System.out.printf("Processing deposit balance of %.2f to %s...%n",
                                            request.balance(), request.accountNumber());
                                    Thread.sleep(2000);
                                    var src = accountRepository.checkUserAndAccount(request.userId(), request.accountId(), request.accountNumber());
                                    if (src == false) {
                                        return;
                                    }
                                    var account = accountRepository.deposit(request.accountId(), request.balance());
                                    if (account == false) {
                                        return;
                                    }
                                    var trxRec = new TransactionRecord();
                                    trxRec.setFromAccountId(request.accountId());
                                    trxRec.setFromUserId(request.userId());
                                    trxRec.setFromAccountNumber(request.accountNumber());
                                    trxRec.setAmount(request.balance());
                                    trxRec.setTransactionType(TransactionType.DEPOSIT);
                                    trxRec.setTransactionId(generateTrxId(trxRec));
                                    trxRec.setTransactionTimeDate(LocalDateTime.now());
                                    trxRecList.add(trxRec);
                                }catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                    });
                } else {
                    try {
                        Thread.sleep(100); // Avoid busy-waiting
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            vThreadExecutor.shutdown();
            vThreadExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        for (TransactionRecord rec: trxRecList) {
            addNew(rec);
        }
    }*/

    private String generateTrxId(TransactionRecord trx) {
        var dateNow = System.currentTimeMillis();
        return "trx" + "-" + dateNow + trx.fromUserId() + trx.fromAccountId();
    }
}
