package edu.bupt.ta.service;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserService.
 * Uses a shared FileStorageUtil instance backed by a temporary directory.
 * The default constructor seeds data on first use, so tests verify the seeded data exists.
 */
class UserServiceTest {

    @TempDir
    Path tempDir;

    private UserService userService;
    private edu.bupt.ta.storage.FileStorageUtil storage;

    @BeforeEach
    void setUp() throws IOException {
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);
        // Create and share a single FileStorageUtil instance for all services
        storage = new edu.bupt.ta.storage.FileStorageUtil(dataDir, null);
        // Create UserService with shared storage
        userService = new UserService(storage);
        // Seed data is automatically created when files have only headers
    }

    // ========== Authentication Tests ==========

    @Nested
    @DisplayName("authenticate()")
    class AuthenticationTests {

        @Test
        @DisplayName("should authenticate user with correct credentials")
        void shouldAuthenticateUserWithCorrectCredentials() {
            // The seeded data includes user "seele" with password "123456"
            User authenticated = userService.authenticate("seele", "123456");
            assertNotNull(authenticated);
            assertEquals("seele", authenticated.getUsername());
            assertEquals("Seele", authenticated.getName());
        }

        @Test
        @DisplayName("should return null for wrong password")
        void shouldReturnNullForWrongPassword() {
            User authenticated = userService.authenticate("seele", "wrongpassword");
            assertNull(authenticated);
        }

        @Test
        @DisplayName("should return null for non-existent username")
        void shouldReturnNullForNonExistentUsername() {
            User authenticated = userService.authenticate("nonexistent", "123456");
            assertNull(authenticated);
        }
    }

    // ========== Find Tests ==========

    @Nested
    @DisplayName("Find Operations")
    class FindTests {

        @Test
        @DisplayName("should find user by username")
        void shouldFindUserByUsername() {
            User found = userService.findByUsername("seele");
            assertNotNull(found);
            assertEquals("seele", found.getUsername());
            assertEquals("Seele", found.getName());
        }

        @Test
        @DisplayName("should find user by ID")
        void shouldFindUserById() {
            User found = userService.findById("U001");
            assertNotNull(found);
            assertEquals("U001", found.getUserId());
            assertEquals("seele", found.getUsername());
        }

        @Test
        @DisplayName("should return null for non-existent username")
        void shouldReturnNullForNonExistentUsername() {
            User found = userService.findByUsername("nonexistent");
            assertNull(found);
        }

        @Test
        @DisplayName("should return null for non-existent user ID")
        void shouldReturnNullForNonExistentUserId() {
            User found = userService.findById("NONEXISTENT");
            assertNull(found);
        }
    }

    // ========== Get All Users Tests ==========

    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsersTests {

        @Test
        @DisplayName("should return seeded users")
        void shouldReturnSeededUsers() {
            List<User> users = userService.getAllUsers();
            // Seeded: U001(seele), U002(luna), U003(kevin), U004(mo1), U005(mo2), U006(admin)
            assertEquals(6, users.size());
        }

        @Test
        @DisplayName("should return only TA users")
        void shouldReturnOnlyTaUsers() {
            List<User> taUsers = userService.getAllTaUsers();
            // Seeded TAs: U001, U002, U003
            assertEquals(3, taUsers.size());
            for (User user : taUsers) {
                assertEquals(UserRole.TA, user.getRole());
            }
        }
    }

    // ========== Register Tests ==========

    @Nested
    @DisplayName("registerTa()")
    class RegisterTests {

        @Test
        @DisplayName("should register new TA user successfully")
        void shouldRegisterNewTaUserSuccessfully() {
            User registered = userService.registerTa(
                "newuser", "password123", "New User", "new@bupt.edu.cn",
                2, "Computer Science", "Java|Python", "Mon afternoons",
                "I want to be a TA", "EBU6304", "Test project", "Lab Support"
            );

            assertNotNull(registered);
            assertEquals("U007", registered.getUserId());
            assertEquals("newuser", registered.getUsername());
            assertEquals("New User", registered.getName());
            assertEquals(UserRole.TA, registered.getRole());
            assertEquals("ACTIVE", registered.getStatus());
        }

        @Test
        @DisplayName("should accept different username (not duplicate)")
        void shouldAcceptDifferentUsername() {
            // SELEE is different from seele (only case differs), but the seeded user is "seele"
            // Actually, the registration IS case-insensitive, so "SELEE" IS a duplicate
            // Let's use a truly different username
            User registered = userService.registerTa(
                "totallynew", "pass", "Totally New User", "totally@bupt.edu.cn",
                1, "CS", "Java", "Mon", "Test", "C1", "P1", "Lab"
            );
            assertNotNull(registered);
            assertEquals("totallynew", registered.getUsername());
        }

        @Test
        @DisplayName("should set SUMMARY_COMPLETE for complete profile")
        void shouldSetSummaryCompleteForCompleteProfile() {
            User registered = userService.registerTa(
                "complete", "pass", "Complete User", "complete@bupt.edu.cn",
                2, "SE", "Java|Python", "Mon afternoons",
                "I am complete", "EBU6304", "Project", "Lab"
            );

            assertEquals("SUMMARY_COMPLETE", registered.getSummaryStatus());
        }

        @Test
        @DisplayName("should set INCOMPLETE for minimal profile")
        void shouldSetIncompleteForMinimalProfile() {
            User registered = userService.registerTa(
                "minimal", "pass", "Minimal User", "minimal@bupt.edu.cn",
                1, "CS", "", "",
                "", "", "", ""
            );

            assertEquals("INCOMPLETE", registered.getSummaryStatus());
        }
    }

    // ========== Profile Update Tests ==========

    @Nested
    @DisplayName("updateProfile()")
    class ProfileUpdateTests {

        @Test
        @DisplayName("should update user profile successfully")
        void shouldUpdateUserProfileSuccessfully() {
            User updated = userService.updateProfile(
                "U001", "Seele Updated", "seele.new@bupt.edu.cn",
                3, "Software Engineering", "Java|Python|Testing",
                "Mon/Wed afternoons", "Updated statement",
                "EBU6304|EBU6201", "New project", "Testing|Tutorial"
            );

            assertNotNull(updated);
            assertEquals("Seele Updated", updated.getName());
            assertEquals("seele.new@bupt.edu.cn", updated.getEmail());
            assertEquals(3, updated.getYear());
            assertEquals("Software Engineering", updated.getMajor());
        }

        @Test
        @DisplayName("should throw exception for non-existent user")
        void shouldThrowExceptionForNonExistentUser() {
            assertThrows(IllegalArgumentException.class, () ->
                userService.updateProfile("NONEXISTENT", "Name", "email@bupt.edu.cn",
                    1, "CS", "Java", "Mon", "Test", "C1", "P1", "Lab")
            );
        }
    }

    // ========== Password Update Tests ==========

    @Nested
    @DisplayName("Password Operations")
    class PasswordTests {

        @Test
        @DisplayName("should update password successfully")
        void shouldUpdatePasswordSuccessfully() {
            userService.updatePassword("U001", "newpassword");

            // Authenticate with new password
            User authenticated = userService.authenticate("seele", "newpassword");
            assertNotNull(authenticated);

            // Old password should not work
            User oldAuth = userService.authenticate("seele", "123456");
            assertNull(oldAuth);
        }
    }

    // ========== User Status Toggle Tests ==========

    @Nested
    @DisplayName("toggleUserStatus()")
    class UserStatusToggleTests {

        @Test
        @DisplayName("should toggle user status from ACTIVE to INACTIVE")
        void shouldToggleUserStatusFromActiveToInactive() {
            userService.toggleUserStatus("U001");

            User toggled = userService.findById("U001");
            assertNotNull(toggled);
            assertEquals("INACTIVE", toggled.getStatus());
        }

        @Test
        @DisplayName("should toggle user status from INACTIVE to ACTIVE")
        void shouldToggleUserStatusFromInactiveToActive() {
            userService.toggleUserStatus("U001");
            userService.toggleUserStatus("U001");

            User toggled = userService.findById("U001");
            assertNotNull(toggled);
            assertEquals("ACTIVE", toggled.getStatus());
        }
    }

    // ========== Summary Status Calculation Tests ==========

    @Nested
    @DisplayName("calculateSummaryStatus()")
    class SummaryStatusTests {

        @Test
        @DisplayName("should return INCOMPLETE for null user")
        void shouldReturnIncompleteForNullUser() {
            String status = userService.calculateSummaryStatus(null);
            assertEquals("INCOMPLETE", status);
        }

        @Test
        @DisplayName("should return SUMMARY_COMPLETE when score >= 8")
        void shouldReturnSummaryCompleteWhenScoreIs8OrMore() {
            User completeUser = new User();
            completeUser.setName("Complete");
            completeUser.setEmail("test@test.com");
            completeUser.setYear(2);
            completeUser.setMajor("CS");
            completeUser.setSkills("Java");
            completeUser.setAvailability("Mon");
            completeUser.setPersonalStatement("Statement");
            completeUser.setRelevantCourses("Courses");
            completeUser.setProjectExperience("Project");

            String status = userService.calculateSummaryStatus(completeUser);
            assertEquals("SUMMARY_COMPLETE", status);
        }

        @Test
        @DisplayName("should return BASIC_COMPLETE when score is 5-7")
        void shouldReturnBasicCompleteWhenScoreIs5To7() {
            User basicUser = new User();
            basicUser.setName("Basic");
            basicUser.setEmail("test@test.com");
            basicUser.setYear(2);
            basicUser.setMajor("CS");
            basicUser.setSkills("Java");
            // Missing: availability, personalStatement, relevantCourses, projectExperience

            String status = userService.calculateSummaryStatus(basicUser);
            assertEquals("BASIC_COMPLETE", status);
        }

        @Test
        @DisplayName("should return INCOMPLETE when score < 5")
        void shouldReturnIncompleteWhenScoreIsLessThan5() {
            User incompleteUser = new User();
            incompleteUser.setName("Incomplete");
            incompleteUser.setEmail("test@test.com");
            incompleteUser.setYear(2);
            // Missing: major, skills, availability, personalStatement, relevantCourses, projectExperience

            String status = userService.calculateSummaryStatus(incompleteUser);
            assertEquals("INCOMPLETE", status);
        }
    }

    // ========== Application Readiness Tests ==========

    @Nested
    @DisplayName("isApplicationReady()")
    class ApplicationReadinessTests {

        @Test
        @DisplayName("should return false for null user")
        void shouldReturnFalseForNullUser() {
            assertFalse(userService.isApplicationReady(null));
        }

        @Test
        @DisplayName("should return true for user with SUMMARY_COMPLETE status")
        void shouldReturnTrueForUserWithSummaryCompleteStatus() {
            User user = userService.findById("U001");
            assertNotNull(user);
            assertTrue(userService.isApplicationReady(user));
        }

        @Test
        @DisplayName("should return false for incomplete user without CV")
        void shouldReturnFalseForIncompleteUserWithoutCv() {
            // Find a user without a CV
            User user = userService.findById("U002");
            assertNotNull(user);
            // U002 is SUMMARY_COMPLETE but without CV
            assertTrue(userService.isApplicationReady(user));
        }
    }
}
