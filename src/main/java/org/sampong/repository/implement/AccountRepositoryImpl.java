package org.sampong.repository.implement;

import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sampong.models.Account;
import org.sampong.plugins.DatabaseConfig;
import org.sampong.repository.AccountRepository;

import java.util.List;
import java.util.Optional;

public class AccountRepositoryImpl implements AccountRepository {
    public AccountRepositoryImpl(){}

    @Override
    public Account save(Account account) {
        Transaction trx = null;
        try(Session session = DatabaseConfig.getSessionFactory().openSession()){
            trx = session.beginTransaction();
            session.persist(account);
            trx.commit();
            return account;
        } catch(Exception e){
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
            return Optional.ofNullable(account);
        } catch (NoResultException e) {
            // No user found, return empty
            return Optional.empty();
        } catch( Exception e ) {
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
        } catch( Exception e ) {
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
        }  catch( Exception e ) {
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
            return Optional.ofNullable(accountList).orElse(List.of());
        }catch( Exception e ) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to query all users");
        }
    }
}
