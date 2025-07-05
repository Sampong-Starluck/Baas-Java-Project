package org.sampong.repository;

import org.sampong.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    User update(User user);
    List<User> findAll();
}
