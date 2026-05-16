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
        User user = storage.loadUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .filter(u -> u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
        return hydrateUser(user);
    }

    public User findByUsername(String username) {
        User user = storage.loadUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        return hydrateUser(user);
    }

    public User findById(String userId) {
        User user = storage.loadUsers().stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
        return hydrateUser(user);
    }

    public List<User> getAllUsers() {
        List<User> users = storage.loadUsers();
        for (User user : users) {
            hydrateUser(user);
        }
        return users;
    }

    public List<User> getAllTaUsers() {
        List<User> all = getAllUsers();
        List<User> tas = new ArrayList<>();
        for (User user : all) {
            if (user.getRole() == UserRole.TA) {
                tas.add(user);
            }
        }
        return tas;
    }

    public User registerTa(String username, String password, String name, String email,
                           int year, String major, String skills, String availability,
                           String personalStatement, String relevantCourses,
                           String projectExperience, String preferredRole) {
        List<User> users = storage.loadUsers();

        Optional<User> existed = users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();

        if (existed.isPresent()) {
            throw new IllegalArgumentException("existed username, please choose another one.");
        }

        User user = new User();
        user.setUserId(nextUserId(users));
        user.setUsername(username.trim());
        user.setPassword(password.trim());
        user.setName(name == null ? "" : name.trim());
        user.setEmail(email == null ? "" : email.trim());
        user.setRole(UserRole.TA);
        user.setYear(year);
        user.setMajor(major == null ? "" : major.trim());
        user.setSkills(normalizeSkills(skills));
        user.setStatus("ACTIVE");
        user.setAvailability(normalizeSingleLineText(availability));
        user.setPersonalStatement(normalizeSingleLineText(personalStatement));
        user.setRelevantCourses(normalizeListText(relevantCourses));
        user.setProjectExperience(normalizeSingleLineText(projectExperience));
        user.setPreferredRole(normalizeListText(preferredRole));
        user.setSummaryStatus(calculateSummaryStatus(user));
        user.setCvStoredName("");
        user.setCvOriginalName("");
        user.setCvContentType("");
        user.setCvUploadedAt("");
        user.setCvStatus("MISSING");

        users.add(user);
        storage.saveUsers(users);
        return hydrateUser(user);
    }

    public User updateProfile(String userId, String name, String email, int year, String major,
                              String skills, String availability, String personalStatement,
                              String relevantCourses, String projectExperience, String preferredRole) {
        List<User> users = storage.loadUsers();
        User updated = null;
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                user.setName(name == null ? "" : name.trim());
                user.setEmail(email == null ? "" : email.trim());
                user.setYear(year);
                user.setMajor(major == null ? "" : major.trim());
                user.setSkills(normalizeSkills(skills));
                user.setAvailability(normalizeSingleLineText(availability));
                user.setPersonalStatement(normalizeSingleLineText(personalStatement));
                user.setRelevantCourses(normalizeListText(relevantCourses));
                user.setProjectExperience(normalizeSingleLineText(projectExperience));
                user.setPreferredRole(normalizeListText(preferredRole));
                user.setSummaryStatus(calculateSummaryStatus(user));
                if (!notBlank(user.getCvStatus())) {
                    user.setCvStatus(hasUploadedCv(user) ? "UPLOADED" : "MISSING");
                }
                updated = user;
                break;
            }
        }
        if (updated == null) {
            throw new IllegalArgumentException("user not found");
        }
        storage.saveUsers(users);
        return hydrateUser(updated);
    }

    public User updateCvMetadata(String userId, String storedName, String originalName,
                                 String contentType, String uploadedAt) {
        List<User> users = storage.loadUsers();
        User updated = null;
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                user.setCvStoredName(normalizeSingleLineText(storedName));
                user.setCvOriginalName(normalizeSingleLineText(originalName));
                user.setCvContentType(normalizeSingleLineText(contentType));
                user.setCvUploadedAt(normalizeSingleLineText(uploadedAt));
                user.setCvStatus("UPLOADED");
                updated = user;
                break;
            }
        }
        if (updated == null) {
            throw new IllegalArgumentException("user not found");
        }
        storage.saveUsers(users);
        return hydrateUser(updated);
    }

    public User clearCvMetadata(String userId) {
        List<User> users = storage.loadUsers();
        User updated = null;
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                user.setCvStoredName("");
                user.setCvOriginalName("");
                user.setCvContentType("");
                user.setCvUploadedAt("");
                user.setCvStatus("MISSING");
                updated = user;
                break;
            }
        }
        if (updated == null) {
            throw new IllegalArgumentException("user not found");
        }
        storage.saveUsers(users);
        return hydrateUser(updated);
    }

    public void registerUser(User user) {
        List<User> users = storage.loadUsers();

        Optional<User> existed = users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(user.getUsername()))
                .findFirst();

        if (existed.isPresent()) {
            throw new IllegalArgumentException("existed username, please choose another one.");
        }

        user.setSkills(normalizeSkills(user.getSkills()));
        user.setAvailability(normalizeSingleLineText(user.getAvailability()));
        user.setPersonalStatement(normalizeSingleLineText(user.getPersonalStatement()));
        user.setRelevantCourses(normalizeListText(user.getRelevantCourses()));
        user.setProjectExperience(normalizeSingleLineText(user.getProjectExperience()));
        user.setPreferredRole(normalizeListText(user.getPreferredRole()));

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
        user.setSummaryStatus(calculateSummaryStatus(user));
        if (!notBlank(user.getCvStatus())) {
            user.setCvStatus("MISSING");
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

    public String calculateSummaryStatus(User user) {
        if (user == null) {
            return "INCOMPLETE";
        }

        int score = 0;
        if (notBlank(user.getName())) score++;
        if (notBlank(user.getEmail())) score++;
        if (user.getYear() > 0) score++;
        if (notBlank(user.getMajor())) score++;
        if (notBlank(user.getSkills())) score++;
        if (notBlank(user.getAvailability())) score++;
        if (notBlank(user.getPersonalStatement())) score++;
        if (notBlank(user.getRelevantCourses())) score++;
        if (notBlank(user.getProjectExperience())) score++;

        if (score >= 8) {
            return "SUMMARY_COMPLETE";
        }
        if (score >= 5) {
            return "BASIC_COMPLETE";
        }
        return "INCOMPLETE";
    }

    public boolean hasUploadedCv(String userId) {
        return hasUploadedCv(findById(userId));
    }

    public boolean hasUploadedCv(User user) {
        return user != null
                && notBlank(user.getCvStoredName())
                && notBlank(user.getCvOriginalName())
                && !"MISSING".equalsIgnoreCase(user.getCvStatus());
    }

    public boolean isApplicationReady(User user) {
        if (user == null) {
            return false;
        }
        return "SUMMARY_COMPLETE".equals(user.getSummaryStatus()) || hasUploadedCv(user);
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

    private User hydrateUser(User user) {
        if (user == null) {
            return null;
        }
        user.setSummaryStatus(calculateSummaryStatus(user));
        if (!notBlank(user.getCvStatus())) {
            user.setCvStatus(hasUploadedCv(user) ? "UPLOADED" : "MISSING");
        }
        return user;
    }

    private String normalizeSkills(String skills) {
        if (skills == null || skills.isBlank()) {
            return "";
        }
        return skills.replace('，', ',')
                .replace(",", "|")
                .trim();
    }

    private String normalizeSingleLineText(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.replace("\r\n", " | ")
                .replace('\n', '|')
                .replace('\r', '|')
                .replaceAll("\\s*\\|\\s*", " | ")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    private String normalizeListText(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.replace("\r\n", "|")
                .replace('\n', '|')
                .replace('\r', '|')
                .replace(",", "|")
                .replace(";", "|")
                .replaceAll("\\|{2,}", "|")
                .replaceAll("\\s*\\|\\s*", "|")
                .trim();
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
