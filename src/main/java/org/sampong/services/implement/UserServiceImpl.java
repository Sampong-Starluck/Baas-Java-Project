package org.sampong.services.implement;

import org.sampong.models.User;
import org.sampong.repository.UserRepository;
import org.sampong.repository.implement.UserRepositoryImpl;
import org.sampong.services.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {
    private UserRepository repository = new UserRepositoryImpl();

    public UserServiceImpl(){}

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User getUserById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("User with id "+ id +" not found"));
    }

    @Override
    public User getUserByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new RuntimeException("User with name "+ username +" not found"));
    }

    @Override
    public User addUser(User user) {
        if (user.id() != 0L){
            throw new RuntimeException("User with id "+user.id()+" already exists");
        }
        return repository.save(user);
    }

    @Override
    public User updateUser(User user) {
        if (user.id() == 0L){
            throw new RuntimeException("User with id "+user.id()+" not found");
        }
        var oldUser = getUserById(user.id());
        oldUser.setUsername(user.username());
        oldUser.setEmail(user.email());
        oldUser.setAddress(user.address());
        oldUser.setPhoneNumber(user.phoneNumber());
        return repository.update(oldUser);
    }

    @Override
    public User deleteUser(Long id) {
        var deletedUser = getUserById(id);
        deletedUser.setStatus(false);
        return repository.update(deletedUser);
    }

    @Override
    public List<User> getAllUsers() {
        return  repository.findAll();
    }
}
