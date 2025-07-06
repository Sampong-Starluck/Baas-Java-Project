package org.sampong.services.implement;

import org.sampong.models.User;
import org.sampong.repository.UserRepository;
import org.sampong.repository.implement.UserRepositoryImpl;
import org.sampong.services.UserService;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {
    private final UserRepository repository = new UserRepositoryImpl();

    public UserServiceImpl() {
    }

    @Override
    public User getById(Long id) {
        try {
            return repository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Error fetching user by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public User getByName(String username) {
        try {
            return repository.findByName(username).orElse(null);
        } catch (Exception e) {
            System.err.println("Error in getByName: " + e.getMessage());
            return null;
        }
    }

    @Override
    public User addNew(User t) {
        if (t.id() != null) {
            System.err.println("User with id " + t.id() + " already exists");
            return null;
        }
        try {
            return repository.save(t);
        } catch (Exception e) {
            System.err.println("Error in addNew: " + e.getMessage());
            return null;
        }
    }

    @Override
    public User updateObject(User t) {
        if (t.id() == null || t.id() == 0L) {
            System.err.println("User with id " + t.id() + " not found");
            return null;
        }
        try {
            var oldUser = getById(t.id());
            if (oldUser == null || oldUser.id() == null) {
                System.err.println("User with id " + t.id() + " not found in DB");
                return null;
            }
            oldUser.setUsername(t.username());
            oldUser.setEmail(t.email());
            oldUser.setAddress(t.address());
            oldUser.setPhoneNumber(t.phoneNumber());
            return repository.update(oldUser);
        } catch (Exception e) {
            System.err.println("Error in updateObject: " + e.getMessage());
            return null;
        }
    }

    @Override
    public User delete(Long id) {
        try {
            var deletedUser = getById(id);
            if (deletedUser == null || deletedUser.id() == null) {
                System.err.println("User with id " + id + " not found");
                return null;
            }
            deletedUser.setStatus(false);
            return repository.update(deletedUser);
        } catch (Exception e) {
            System.err.println("Error in delete: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public String tableList(List<User> t) {
        String[] headers = {
                "ID", "Status", "Username", "Phone Number", "Email", "Address"
        };

        // Prepare data rows
        List<String[]> rows = new ArrayList<>();
        for (User user : t) {
            rows.add(new String[]{
                    user.id() == null ? "" : user.id().toString(),
                    user.status() == null ? "" : user.status().toString(),
                    user.username() == null ? "" : user.username(),
                    user.phoneNumber() == null ? "" : user.phoneNumber(),
                    user.email() == null ? "" : user.email(),
                    user.address() == null ? "" : user.address()
            });
        }

        // Calculate max width for each column
        int[] colWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            colWidths[i] = headers[i].length();
            for (String[] row : rows) {
                if (row[i].length() > colWidths[i]) {
                    colWidths[i] = row[i].length();
                }
            }
        }

        // Build separator
        StringBuilder sep = new StringBuilder("+");
        for (int w : colWidths) {
            sep.append("-".repeat(w + 2)).append("+");
        }
        String separator = sep.toString();

        // Build table
        StringBuilder sb = new StringBuilder();
        sb.append(separator).append("\n|");
        for (int i = 0; i < headers.length; i++) {
            sb.append(" ").append(String.format("%-" + colWidths[i] + "s", headers[i])).append(" |");
        }
        sb.append("\n").append(separator);

        for (String[] row : rows) {
            sb.append("\n|");
            for (int i = 0; i < row.length; i++) {
                sb.append(" ").append(String.format("%-" + colWidths[i] + "s", row[i])).append(" |");
            }
        }
        sb.append("\n").append(separator);

        return sb.toString();
    }
}
