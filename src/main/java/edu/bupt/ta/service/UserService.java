package edu.bupt.ta.service;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.storage.FileStorageUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Provides business-logic operations for user accounts and TA profiles.
 * All data access is delegated to {@link FileStorageUtil}. This class does not
 * perform any direct file I/O.
 */
public class UserService {

    private FileStorageUtil storage;
    private CvFileService cvFileService;

    /**
     * Default constructor. Uses default FileStorageUtil and CvFileService instances.
     */
    public UserService() {
        this.storage = new FileStorageUtil();
        this.cvFileService = new CvFileService(this.storage);
    }

    /**
     * Constructor with injected FileStorageUtil path. Used by tests to redirect
     * CSV I/O to a temporary directory.
     *
     * @param dataDir the data directory for CSV files
     * @param mirrorDir the mirror directory (may be null)
     */
    public UserService(java.nio.file.Path dataDir, java.nio.file.Path mirrorDir) {
        this.storage = new FileStorageUtil(dataDir, mirrorDir);
        this.cvFileService = new CvFileService(this.storage);
    }

    /**
     * Constructor with injected FileStorageUtil. Used by ApplicationService.
     *
     * @param storage the FileStorageUtil instance to use
     */
    public UserService(FileStorageUtil storage) {
        this.storage = storage;
        this.cvFileService = new CvFileService(storage);
    }

    /**
     * Sets the FileStorageUtil instance. Allows test injection.
     *
     * @param storage the FileStorageUtil to use
     */
    public void setStorage(FileStorageUtil storage) {
        this.storage = storage;
    }

    /**
     * Authenticates a user by username and password.
     *
     * @param username the login name
     * @param password the plain-text password
     * @return the authenticated User, or null if credentials are invalid
     */
    public User authenticate(String username, String password) {
        User user = storage.loadUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .filter(u -> u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
        return hydrateUser(user);
    }

    /**
     * Looks up a user by username.
     *
     * @param username the login name
     * @return the User, or null if not found
     */
    public User findByUsername(String username) {
        User user = storage.loadUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        return hydrateUser(user);
    }

    /**
     * Checks whether a username already exists, ignoring case and surrounding whitespace.
     *
     * @param username the username to check
     * @return true if the username is already taken
     */
    public boolean usernameExists(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        String normalized = username.trim();
        return storage.loadUsers().stream()
                .anyMatch(u -> u.getUsername() != null && u.getUsername().equalsIgnoreCase(normalized));
    }

    /**
     * Looks up a user by their unique ID (e.g. "U001").
     *
     * @param userId the user ID
     * @return the User, or null if not found
     */
    public User findById(String userId) {
        User user = storage.loadUsers().stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
        return hydrateUser(user);
    }

    /**
     * Returns all users, with summaryStatus and cvStatus recalculated for each.
     *
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        List<User> users = storage.loadUsers();
        for (User user : users) {
            hydrateUser(user);
        }
        return users;
    }

    /**
     * Returns all users whose role is TA.
     *
     * @return a list of TA users
     */
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

    /**
     * Registers a new TA account. The username must be unique (case-insensitive check).
     * Assigns the next sequential user ID and sets the role to TA with ACTIVE status.
     * CV-related fields are initialised to empty/MISSING.
     *
     * @param username          login name (must be unique)
     * @param password          plain-text password
     * @param name              display name
     * @param email             contact email
     * @param year              academic year
     * @param major             academic major
     * @param skills            pipe-separated skill list (normalised)
     * @param availability      free-text availability description
     * @param personalStatement candidate's motivation text
     * @param relevantCourses   pipe-separated course list
     * @param projectExperience free-text project experience description
     * @param preferredRole     pipe-separated preferred role list
     * @return the newly created User
     * @throws IllegalArgumentException if the username already exists
     */
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
        user.setPreferredRole(normalizePreferredRoles(preferredRole));
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

    /**
     * Updates the editable profile fields for an existing user. Recalculates summaryStatus
     * and cvStatus after the update.
     *
     * @param userId             the ID of the user to update
     * @param name               display name
     * @param email              contact email
     * @param year               academic year
     * @param major              academic major
     * @param skills             pipe-separated skill list
     * @param availability       free-text availability
     * @param personalStatement  motivation text
     * @param relevantCourses    pipe-separated course list
     * @param projectExperience  project experience text
     * @param preferredRole      pipe-separated preferred role list
     * @return the updated User
     * @throws IllegalArgumentException if the user is not found
     */
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
                user.setPreferredRole(normalizePreferredRoles(preferredRole));
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

    /**
     * Updates the five CV-related columns in a user's record after a successful file upload.
     *
     * @param userId      the user ID
     * @param storedName  the filename stored on disk (e.g. "U001.pdf")
     * @param originalName the original filename submitted by the user
     * @param contentType the MIME type of the file
     * @param uploadedAt  the upload timestamp
     * @return the updated User
     * @throws IllegalArgumentException if the user is not found
     */
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

    /**
     * Clears the CV metadata for a user, setting cvStatus to MISSING and all related
     * columns to empty strings.
     *
     * @param userId the user ID
     * @return the updated User
     * @throws IllegalArgumentException if the user is not found
     */
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

    /**
     * Saves an externally-constructed User to the CSV. Normalises all text fields,
     * assigns a new userId, and checks for duplicate username (case-insensitive).
     *
     * @param user the User to save (userId will be overwritten with the next sequential ID)
     * @throws IllegalArgumentException if the username already exists
     */
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
        user.setPreferredRole(normalizePreferredRoles(user.getPreferredRole()));

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

    /**
     * Toggles a user's account status between ACTIVE and INACTIVE.
     *
     * @param userId the ID of the user to toggle
     */
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

    /**
     * Updates the password for a user.
     *
     * @param userId      the ID of the user
     * @param newPassword the new plain-text password
     */
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

    /**
     * Directly persists a list of users to CSV, replacing all existing records.
     * Used primarily by AdminService.
     *
     * @param users the complete user list to save
     */
    public void saveUsersDirect(List<User> users) {
        storage.saveUsers(users);
    }

    /**
     * Calculates the completeness of a TA's profile.
     * Scores the presence of 9 fields (name, email, year, major, skills, availability,
     * personalStatement, relevantCourses, projectExperience) and returns:
     * SUMMARY_COMPLETE if score &ge; 8, BASIC_COMPLETE if score &ge; 5, otherwise INCOMPLETE.
     *
     * @param user the User to evaluate (may be null)
     * @return one of SUMMARY_COMPLETE, BASIC_COMPLETE, or INCOMPLETE
     */
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

    /**
     * Checks whether the specified user has uploaded a CV, by userId lookup.
     *
     * @param userId the user ID
     * @return true if a CV file exists on disk and all CV metadata columns are populated
     */
    public boolean hasUploadedCv(String userId) {
        return hasUploadedCv(findById(userId));
    }

    /**
     * Checks whether the user has a CV file uploaded. The check verifies that cvStoredName
     * and cvOriginalName are non-blank, cvStatus is not MISSING, and the file actually
     * exists on disk.
     *
     * @param user the User to check (may be null)
     * @return true if all conditions are met
     */
    public boolean hasUploadedCv(User user) {
        return user != null
                && notBlank(user.getCvStoredName())
                && notBlank(user.getCvOriginalName())
                && !"MISSING".equalsIgnoreCase(user.getCvStatus())
                && cvFileService.resolveExistingCvPath(user.getCvStoredName()) != null;
    }

    /**
     * Determines whether a user is eligible to apply for jobs.
     * A user is application-ready if their summaryStatus is SUMMARY_COMPLETE
     * or they have uploaded a CV file.
     *
     * @param user the User to check (may be null)
     * @return true if the user meets the minimum requirements for applying
     */
    public boolean isApplicationReady(User user) {
        if (user == null) {
            return false;
        }
        return "SUMMARY_COMPLETE".equals(user.getSummaryStatus()) || hasUploadedCv(user);
    }

    /**
     * Generates the next sequential user ID (e.g. "U007").
     * Parses the numeric suffix of all existing IDs starting with 'U' and returns
     * the next integer formatted as "U{NNN}".
     */
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

    /**
     * Recalculates and updates the summaryStatus and cvStatus fields of a User
     * in memory. Does not persist the change.
     *
     * @param user the User to update (may be null, in which case null is returned)
     * @return the same User, or null if input was null
     */
    private User hydrateUser(User user) {
        if (user == null) {
            return null;
        }
        user.setSummaryStatus(calculateSummaryStatus(user));
        user.setCvStatus(hasUploadedCv(user) ? "UPLOADED" : "MISSING");
        return user;
    }

    /**
     * Normalises a skills string: replaces Chinese comma with pipe, then replaces
     * all commas with pipes, and trims whitespace.
     */
    private String normalizeSkills(String skills) {
        if (skills == null || skills.isBlank()) {
            return "";
        }
        return skills.replace('，', ',')
                .replace(",", "|")
                .trim();
    }

    /**
     * Normalises a free-text field: replaces newlines and carriage returns with " | ",
     * collapses multiple whitespace, and trims. Used for availability and personalStatement.
     */
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

    /**
     * Normalises a list-type field: replaces newlines, commas, and semicolons with
     * pipes, collapses duplicate pipes, and trims. Used for relevantCourses and preferredRole.
     */
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

    /**
     * Normalises preferred-role selections and keeps at most three unique items in
     * insertion order. This gives the UI freedom to use multi-select controls while
     * keeping CSV storage compact and predictable.
     */
    private String normalizePreferredRoles(String value) {
        String normalized = normalizeListText(value);
        if (normalized.isBlank()) {
            return "";
        }
        java.util.LinkedHashSet<String> unique = new java.util.LinkedHashSet<>();
        for (String token : normalized.split("\\|")) {
            String item = token == null ? "" : token.trim();
            if (item.isEmpty()) {
                continue;
            }
            unique.add(item);
            if (unique.size() == 3) {
                break;
            }
        }
        return String.join("|", unique);
    }

    /**
     * Returns true if the value is neither null nor blank.
     */
    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
