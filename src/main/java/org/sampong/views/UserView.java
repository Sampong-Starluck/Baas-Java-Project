package org.sampong.views;

import org.sampong.controller.UserController;
import org.sampong.models.User;
import org.sampong.services.UserService;
import org.sampong.services.implement.UserServiceImpl;

import java.util.Scanner;

public class UserView {
    private static final UserController userController = new UserController();
    private static final UserService userService = new UserServiceImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public UserView(){}

    public void handleUser() {
        var running = true;
        System.out.println("\n\n");
        while (running) {
            System.out.print("\n");
            System.out.println(userMenu());
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    var user = getUserId();
                    if(user != null){
                        System.out.println("User: " + user);
                    } else {
                        System.out.println("User not found");
                    }
                }
                case 2 -> System.out.println("User" + userController.getAllUsers().toString());
                case 3 -> {
                    System.out.println("Enter user's username: ");
                    var user = userController.getUserByUsername(scanner.next());
                    if(user != null){
                        System.out.println("User: " + user);
                    } else {
                        System.out.println("User not found");
                    }
                }
                case 4 -> {
                    var user = addNewUser();
                    if(user != null){
                        System.out.println("User: " + user);
                    } else {
                        System.out.println("User can't be added");
                    }
                }
                case 5 -> {
                    System.out.println("Enter user's id: ");
                    Long id = scanner.nextLong();
                    var user = userController.deleteUser(id);
                    System.out.println("User " + user.username() + " has been deleted.");
                }
                case 6 -> {
                    var user = updateUser();
                    if(user != null){
                        System.out.println("User updated successfully:\n" + updateUser().toString());
                    } else {
                        System.out.println("User can't be updated");
                    }
                }
                case 7 -> {
                    System.out.println("Return to main menu.\n\n");
                    running = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private User getUserId(){
        System.out.println("Enter user ID: ");
        Long id = scanner.nextLong();
        return userController.getUserById(id);
    }

    private User addNewUser(){
        System.out.println("Enter user's username: ");
        var username = scanner.next();
        System.out.println("Enter user's phone number: ");
        var phoneNumber = scanner.next();
        System.out.println("Enter user's email: ");
        var email = scanner.next();
        System.out.println("Enter user's address: ");
        var address = scanner.next();

        var user = new User();
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setAddress(address);
        user.setStatus(true);
        return userController.addNewUser(user);
    }

    private User updateUser(){
        var list = userController.getAllUsers();
        System.out.println(userService.tableList(list));
        System.out.println("Enter user's id that user want to update: ");
        var userId = scanner.nextLong();
        System.out.println("Enter user's username: ");
        var username = scanner.next();
        System.out.println("Enter user's phone number: ");
        var phoneNumber = scanner.next();
        System.out.println("Enter user's email: ");
        var email = scanner.next();
        System.out.println("Enter user's address: ");
        var address = scanner.next();

        var user = userController.getUserById(userId);
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setAddress(address);
        user.setStatus(true);
        userController.updateUser(user);
        return userController.updateUser(user);
    }

    private String userMenu() {
        return """
                1. Get user by ID.
                2. Get all users.
                3. Get By username.
                4. Create new user.
                5. Delete user.
                6. Update user.
                7. Return to main menu
                """.trim();
    }
}
