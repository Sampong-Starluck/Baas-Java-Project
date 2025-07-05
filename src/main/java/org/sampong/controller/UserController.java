package org.sampong.controller;

import org.sampong.models.User;
import org.sampong.services.UserService;
import org.sampong.services.implement.UserServiceImpl;

import java.util.List;

public class UserController {
    private static UserService service = new UserServiceImpl();

    public UserController(){

    }
    public UserController(UserService service) {
        UserController.service = service;
    }
    public User getUserById(Long id) {
        return service.getUserById(id);
    }
    public User getUserByUsername(String username) {
        return service.getUserByUsername(username);
    }
    public User addNewUser(User user) {
        return service.addUser(user);
    }
    public User updateUser(User user) {
        return service.updateUser(user);
    }
    public User deleteUser(Long id) {
        return service.deleteUser(id);
    }
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }
}
