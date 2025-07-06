package org.sampong.repository.implement;

import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sampong.models.TransactionRecord;
import org.sampong.plugins.DatabaseConfig;
import org.sampong.repository.TransactionRecordRepository;

import java.util.List;
import java.util.Optional;

public class TransactionRecordRepositoryImpl implements TransactionRecordRepository {

    public TransactionRecordRepositoryImpl() {
    }

    @Override
    public TransactionRecord save(TransactionRecord transactionRecord) {
        Transaction trx = null;
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            trx = session.beginTransaction();
            session.persist(transactionRecord);
            trx.commit();
            return transactionRecord;
        } catch (Exception e) {
            if (trx != null && trx.getStatus().canRollback()) {
                try {
                    trx.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to save transactionRecord", e);
        }
    }

    @Override
    public Optional<TransactionRecord> findById(Long id) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            var query = "select u from TransactionRecord u where u.id = :id and u.status = true";
            TransactionRecord trx = session.createQuery(query, TransactionRecord.class).setParameter("id", id).getSingleResult();
            return Optional.of(trx);
        } catch (NoResultException e) {
            // No user found, return empty
            return Optional.empty();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to query trx", e);
        }
    }

    @Override
    public Optional<TransactionRecord> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public TransactionRecord update(TransactionRecord transactionRecord) {
        return null;
    }

    @Override
    public List<TransactionRecord> findAll() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            var transactionRecord = "select u from TransactionRecord u where u.status = true order by u.transactionTimeDate desc";
            List<TransactionRecord> transactionRecordList = session.createQuery(transactionRecord, TransactionRecord.class).getResultList();
            return Optional.of(transactionRecordList).orElse(List.of());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to query all transaction record");
        }
    }

    @Override
    public List<TransactionRecord> findAllTrxBySenderId(Long userId) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            var transactionRecord = "select u from TransactionRecord u where u.fromUserId = :id and u.status = true";
            List<TransactionRecord> transactionRecordList = session.createQuery(transactionRecord, TransactionRecord.class).setParameter("id", userId).getResultList();
            return Optional.of(transactionRecordList).orElse(List.of());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to query all transaction record");
        }
    }

    @Override
    public List<TransactionRecord> findAllTrxByReceiverId(Long userId) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            var transactionRecord = "select u from TransactionRecord u where u.toUserId = :id and u.status = true";
            List<TransactionRecord> transactionRecordList = session.createQuery(transactionRecord, TransactionRecord.class).setParameter("id", userId).getResultList();
            return Optional.of(transactionRecordList).orElse(List.of());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to query all transaction record");
        }
    }
}
