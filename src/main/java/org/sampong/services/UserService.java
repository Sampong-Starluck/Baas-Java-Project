package org.sampong.services;

import org.sampong.models.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);
    User getUserByUsername(String username);
    User addUser(User user);
    User updateUser(User user);
    User deleteUser(Long id);
    List<User> getAllUsers();
}
