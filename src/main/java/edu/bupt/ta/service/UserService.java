package edu.bupt.ta.service;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.storage.FileStorageUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final FileStorageUtil storage = new FileStorageUtil();

    public User authenticate(String username, String password) {
        return storage.loadUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .filter(u -> u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public User findByUsername(String username) {
        return storage.loadUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public User findById(String userId) {
        return storage.loadUsers().stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public List<User> getAllUsers() {
        return storage.loadUsers();
    }

    public List<User> getAllTaUsers() {
        List<User> all = storage.loadUsers();
        List<User> tas = new ArrayList<>();
        for (User user : all) {
            if (user.getRole() == UserRole.TA) {
                tas.add(user);
            }
        }
        return tas;
    }

    public User registerTa(String username, String password, String name, String email,
                           int year, String major, String skills) {
        List<User> users = storage.loadUsers();

        Optional<User> existed = users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();

        if (existed.isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }

        String normalizedSkills = normalizeSkills(skills);

        User user = new User();
        user.setUserId(nextUserId(users));
        user.setUsername(username.trim());
        user.setPassword(password.trim());
        user.setName(name.trim());
        user.setEmail(email.trim());
        user.setRole(UserRole.TA);
        user.setYear(year);
        user.setMajor(major == null ? "" : major.trim());
        user.setSkills(normalizedSkills);
        user.setStatus("ACTIVE");

        users.add(user);
        storage.saveUsers(users);
        return user;
    }

    public void registerUser(User user) {
        List<User> users = storage.loadUsers();

        Optional<User> existed = users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(user.getUsername()))
                .findFirst();

        if (existed.isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }

        if (user.getSkills() != null) {
            user.setSkills(normalizeSkills(user.getSkills()));
        }

        user.setUserId(nextUserId(users));
        user.setUsername(user.getUsername().trim());
        user.setPassword(user.getPassword().trim());
        if (user.getName() != null) {
            user.setName(user.getName().trim());
        }
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim());
        }
        if (user.getMajor() != null) {
            user.setMajor(user.getMajor().trim());
        }

        users.add(user);
        storage.saveUsers(users);
    }

    public void toggleUserStatus(String userId) {
        List<User> users = storage.loadUsers();
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                user.setStatus("ACTIVE".equals(user.getStatus()) ? "INACTIVE" : "ACTIVE");
                break;
            }
        }
        storage.saveUsers(users);
    }

    public void updatePassword(String userId, String newPassword) {
        List<User> users = storage.loadUsers();
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                user.setPassword(newPassword);
                break;
            }
        }
        storage.saveUsers(users);
    }

    public void saveUsersDirect(List<User> users) {
        storage.saveUsers(users);
    }

    private String nextUserId(List<User> users) {
        int max = users.stream()
                .map(User::getUserId)
                .filter(id -> id != null && id.startsWith("U"))
                .map(id -> {
                    try {
                        return Integer.parseInt(id.substring(1));
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .max(Comparator.naturalOrder())
                .orElse(0);
        return String.format("U%03d", max + 1);
    }

    private String normalizeSkills(String skills) {
        if (skills == null || skills.isBlank()) {
            return "";
        }
        return skills.replace("，", ",")
                .replace(",", "|")
                .trim();
    }
}