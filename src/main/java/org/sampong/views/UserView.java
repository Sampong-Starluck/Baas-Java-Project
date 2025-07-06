package org.sampong.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sampong.controller.UserController;
import org.sampong.dto.UserResponse;
import org.sampong.models.User;
import org.sampong.services.UserService;
import org.sampong.services.implement.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class UserView {

    private static final Logger log = LoggerFactory.getLogger(UserView.class);

    private static final UserController userController = new UserController();
    private static final UserService userService = new UserServiceImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public UserView() {
    }

    public void handleUser() {
        log.info("Start ====>> handle user view.");
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
                    if (user != null) {
                        System.out.println("User: " + toJson(user));
                    } else {
                        System.out.println("User not found");
                    }
                }
                case 2 -> {
                    var userList = userController.getAllUsers();
//                    System.out.println("User: " + toJson(userList));
                    System.out.println(userService.tableList(userList));
                }
                case 3 -> {
                    System.out.println("Enter user's username: ");
                    var user = userController.getUserByUsername(scanner.next());
                    if (user != null) {
                        System.out.println("User: " + toJson(user));
                    } else {
                        System.out.println("User not found");
                    }
                }
                case 4 -> {
                    var user = addNewUser();
                    if (user != null) {
                        System.out.println("User: " + toJson(user));
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
                    if (user != null) {
                        System.out.println("User updated successfully:\n" + toJson(user));
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

    private User getUserId() {
        System.out.println("Enter user ID: ");
        Long id = scanner.nextLong();
        return userController.getUserById(id);
    }

    private User addNewUser() {
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

    private User updateUser() {
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
                7. Return to main menu.
                """.trim();
    }

    public String toJson(User user) {
        var mapper = new ObjectMapper();
        try {
            var accountResponse = toResponse(user);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(accountResponse);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Failed to serialize Account to JSON\"}";
        }
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.id(), user.username(), user.phoneNumber(), user.email(), user.address());
    }


    /*public String toJson(List<User> users) {
        var mapper = new ObjectMapper();
        try {
            // Convert each User to UserResponse
            List<UserResponse> responses = users.stream()
                    .map(user -> new UserResponse(
                            user.id(),
                            user.username(),
                            user.phoneNumber(),
                            user.email(),
                            user.address()
                    ))
                    .collect(Collectors.toList());

            // Serialize the list
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responses);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Failed to serialize User list to JSON\"}";
        }
    }*/
}
