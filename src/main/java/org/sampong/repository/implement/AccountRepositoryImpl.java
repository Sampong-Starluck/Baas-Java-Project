package org.sampong.repository.implement;

import jakarta.persistence.NoResultException;
import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sampong.models.Account;
import org.sampong.plugins.DatabaseConfig;
import org.sampong.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class AccountRepositoryImpl implements AccountRepository {
    private static final Logger log = LoggerFactory.getLogger(AccountRepositoryImpl.class);
    private final ConcurrentMap<Long, Object> accountLocks = new ConcurrentHashMap<>();

    public AccountRepositoryImpl() {
    }

    @Override
    public Account save(Account account) {
        Transaction trx = null;
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            trx = session.beginTransaction();
            session.persist(account);
            trx.commit();
            return account;
        } catch (Exception e) {
            if (trx != null && trx.getStatus().canRollback()) {
                try {
                    trx.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to save account", e);
        }
    }

    @Override
    public Optional<Account> findById(Long id) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            var query = "select u from Account u where u.id = :id and u.status = true";
            Account account = session.createQuery(query, Account.class).setParameter("id", id).getSingleResult();
            Hibernate.initialize(account.user());
            return Optional.of(account);
        } catch (NoResultException e) {
            // No user found, return empty
            return Optional.empty();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to query account", e);
        }
    }

    @Override
    public Optional<Account> findByName(String name) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            var query = "select u from Account u where u.accountNumber = :id and status = true";
            Account account = session.createQuery(query, Account.class).setParameter("id", name).getSingleResult();
            return Optional.ofNullable(account);
        } catch (NoResultException e) {
            // No user found, return empty
            return Optional.empty();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to query account", e);
        }
    }

    @Override
    public Account update(Account account) {
        Transaction transaction = null;
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(account);
            transaction.commit();
            return account;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to update account", e);
        }
    }

    @Override
    public List<Account> findAll() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            var account = "select u from Account u where u.status = true";
            List<Account> accountList = session.createQuery(account, Account.class).getResultList();
            accountList.stream().sorted(Comparator.comparing(Account::id)).forEach(it -> Hibernate.initialize(it.user()));
            return Optional.of(accountList).orElse(List.of());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return List.of();
        }
    }

    @Override
    public Boolean deposit(Long accId, Double amount) {
        if (amount == null || amount <= 0) return false;
        Transaction transaction = null;
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            var accountQuery = "select u from Account u where u.id = :id and u.status = true";
            var account = session.createQuery(accountQuery, Account.class)
                    .setParameter("id", accId)
                    .setLockMode(LockMode.OPTIMISTIC.toJpaLockMode())
                    .uniqueResult();
            if (account == null) return false;
            account.deposit(amount);
            session.merge(account);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean withdraw(Long accId, Double amount) {
        if (amount == null || amount <= 0) return false;
        Transaction transaction = null;
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            var accountQuery = "select u from Account u where u.id = :id and u.status = true";
            var account = session.createQuery(accountQuery, Account.class)
                    .setParameter("id", accId)
                    .setLockMode(LockMode.OPTIMISTIC.toJpaLockMode())
                    .uniqueResult();
            if (account == null) return false;
            boolean success = account.withdraw(amount);
            if (!success) {
                transaction.rollback();
                return false;
            }
            session.merge(account);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean transfer(Long srcAccId, Long destAccId, Double amount) {
        if (amount == null || amount <= 0) return false;
        Transaction transaction = null;
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            System.out.println("\n");
            log.info("account service >> Beginning to transfer !!!");
            transaction = session.beginTransaction();
            var ids = List.of(srcAccId, destAccId);
            var accountQuery = "select u from Account u where u.id in (:ids) and u.status = true";
            var accounts = session.createQuery(accountQuery, Account.class)
                    .setParameter("ids", ids)
                    .setLockMode(LockMode.PESSIMISTIC_WRITE.toJpaLockMode())
                    .getResultList();

            var accountMap = accounts.stream()
                    .collect(Collectors.toMap(Account::id, account -> account));
            var srcAccount = accountMap.get(srcAccId);
            var destAccount = accountMap.get(destAccId);

            if (srcAccount == null || destAccount == null) {
                transaction.rollback();
                return false;
            }

            boolean success = srcAccount.withdraw(amount);
            if (!success) {
                transaction.rollback();
                return false;
            }
            destAccount.deposit(amount);
            session.merge(srcAccount);
            session.merge(destAccount);
            transaction.commit();
            log.info("account service >> Finished transfer !!!");
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean checkUserAndAccount(Long userId, Long accountId, String accountNumber) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            var accountQuery = "select case when count(u) > 0 then true else false end from Account u where u.id = :accountId and u.user.id = :userId and u.accountNumber = :accountNumber and u.status = true";
            return session.createQuery(accountQuery, Boolean.class)
                    .setParameter("accountId", accountId)
                    .setParameter("userId", userId)
                    .setParameter("accountNumber", accountNumber)
                    .getSingleResult();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to query account", e);
        }
    }

    @Override
    public Object getAccountLock(Long accountId) {
        return accountLocks.computeIfAbsent(accountId, id -> new Object());
    }

    @Override
    public List<Account> getAccountsByUserId(Long userId) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            var query = "select u from Account u where u.user.id = :id and status = true";
            List<Account> accountList = session.createQuery(query, Account.class).setParameter("id", userId).getResultList();
            accountList.stream().sorted().forEach(it -> Hibernate.initialize(it.user()));
            return Optional.of(accountList).orElse(List.of());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to query all accounts", e);
        }
    }
}
