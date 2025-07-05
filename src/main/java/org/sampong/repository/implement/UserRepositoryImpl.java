package org.sampong.repository.implement;

import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sampong.models.User;
import org.sampong.plugins.DatabaseConfig;
import org.sampong.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    public UserRepositoryImpl(){}

    @Override
    public User save(User user) {
        Transaction transaction = null;
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return user;
        }  catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            var query = "select u from User u where u.id = :id and status = true";
            User user = session.createQuery(query, User.class).setParameter("id", id).getSingleResult();
            return Optional.ofNullable(user);
        }catch (NoResultException e) {
            // No user found, return empty
            return Optional.empty();
        } catch( Exception e ) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to query user", e);
        }
    }

    @Override
    public Optional<User> findByName(String username) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            var query = "select u from User u where u.username = :id and status = true";
            User user = session.createQuery(query, User.class).setParameter("id", username).getSingleResult();
            return Optional.ofNullable(user);
        }catch (NoResultException e) {
            // No user found, return empty
            return Optional.empty();
        } catch( Exception e ) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to query user", e);
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            return user;
        }  catch( Exception e ) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            var users = "select u from User u where u.status = true";
            List<User> usersList = session.createQuery(users, User.class).getResultList();
            return Optional.ofNullable(usersList).orElse(List.of());
        }catch( Exception e ) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to query all users");
        }
    }
}
