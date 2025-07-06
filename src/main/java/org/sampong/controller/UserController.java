package org.sampong.controller;

import org.sampong.models.User;
import org.sampong.services.UserService;
import org.sampong.services.implement.UserServiceImpl;

import java.util.List;

public class UserController {
    private static final UserService service = new UserServiceImpl();

    public UserController() {
    }

    public User getUserById(Long id) {
        return service.getById(id);
    }

    public User getUserByUsername(String username) {
        return service.getByName(username);
    }

    public User addNewUser(User user) {
        return service.addNew(user);
    }

    public User updateUser(User user) {
        return service.updateObject(user);
    }

    public User deleteUser(Long id) {
        return service.delete(id);
    }

    public List<User> getAllUsers() {
        return service.getAll();
    }
}
