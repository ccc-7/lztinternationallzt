package edu.bupt.ta.storage;

import edu.bupt.ta.model.Application;
import edu.bupt.ta.model.ApplicationStatus;
import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.JobStatus;
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
 * Unit tests for FileStorageUtil.
 * Uses a temporary directory to avoid polluting real data files.
 * Tests CSV parsing, serialization, and round-trip consistency.
 */
class FileStorageUtilTest {

    @TempDir
    Path tempDir;

    private FileStorageUtil storage;

    @BeforeEach
    void setUp() throws IOException {
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);
        // Use the new constructor to inject test directory
        storage = new FileStorageUtil(dataDir, null);
        // Note: constructor calls initFiles() which seeds default data
        // because the files are created fresh (header only)
    }

    // ========== User CRUD Tests ==========

    @Nested
    @DisplayName("User CRUD Operations")
    class UserCrudTests {

        @Test
        @DisplayName("should save and load users successfully")
        void shouldSaveAndLoadUsers() {
            User user = new User();
            user.setUserId("U999");
            user.setUsername("testuser");
            user.setPassword("password123");
            user.setName("Test User");
            user.setEmail("test@example.com");
            user.setRole(UserRole.TA);
            user.setYear(2);
            user.setMajor("Computer Science");
            user.setSkills("Java|Python");
            user.setStatus("ACTIVE");
            user.setAvailability("Mon/Wed afternoons");
            user.setPersonalStatement("Test statement");
            user.setRelevantCourses("EBU6304");
            user.setProjectExperience("Test project");
            user.setPreferredRole("Lab Support");
            user.setSummaryStatus("SUMMARY_COMPLETE");
            user.setCvStatus("MISSING");

            storage.saveUsers(List.of(user));
            List<User> loaded = storage.loadUsers();

            assertNotNull(loaded);
            // Should contain seeded defaults (6) + our 1 = 7
            assertTrue(loaded.size() >= 1);
            User saved = loaded.stream()
                .filter(u -> "U999".equals(u.getUserId()))
                .findFirst().orElse(null);
            assertNotNull(saved);
            assertEquals("testuser", saved.getUsername());
            assertEquals("password123", saved.getPassword());
            assertEquals("Test User", saved.getName());
            assertEquals("test@example.com", saved.getEmail());
            assertEquals(UserRole.TA, saved.getRole());
            assertEquals(2, saved.getYear());
            assertEquals("Computer Science", saved.getMajor());
            assertEquals("Java|Python", saved.getSkills());
            assertEquals("ACTIVE", saved.getStatus());
        }

        @Test
        @DisplayName("should handle multiple users correctly")
        void shouldHandleMultipleUsers() {
            User user1 = new User();
            user1.setUserId("U997");
            user1.setUsername("user1");
            user1.setPassword("pass1");
            user1.setName("User One");
            user1.setEmail("user1@example.com");
            user1.setRole(UserRole.TA);
            user1.setYear(1);
            user1.setMajor("CS");
            user1.setSkills("Java");
            user1.setStatus("ACTIVE");
            user1.setSummaryStatus("INCOMPLETE");
            user1.setCvStatus("MISSING");

            User user2 = new User();
            user2.setUserId("U998");
            user2.setUsername("user2");
            user2.setPassword("pass2");
            user2.setName("User Two");
            user2.setEmail("user2@example.com");
            user2.setRole(UserRole.MO);
            user2.setYear(0);
            user2.setMajor("Faculty");
            user2.setSkills("Teaching");
            user2.setStatus("ACTIVE");
            user2.setSummaryStatus("INCOMPLETE");
            user2.setCvStatus("MISSING");

            List<User> existing = storage.loadUsers();
            List<User> all = new java.util.ArrayList<>(existing);
            all.add(user1);
            all.add(user2);
            storage.saveUsers(all);

            List<User> loaded = storage.loadUsers();
            assertTrue(loaded.size() >= 2);
            assertTrue(loaded.stream().anyMatch(u -> "U997".equals(u.getUserId())));
            assertTrue(loaded.stream().anyMatch(u -> "U998".equals(u.getUserId())));
        }

        @Test
        @DisplayName("should preserve all user fields on round-trip")
        void shouldPreserveAllUserFieldsOnRoundTrip() {
            User user = new User();
            user.setUserId("U996");
            user.setUsername("alice");
            user.setPassword("secret");
            user.setName("Alice \"Special\" Chen");
            user.setEmail("alice@example.com");
            user.setRole(UserRole.TA);
            user.setYear(3);
            user.setMajor("SE");
            user.setSkills("Java|Python|JavaScript");
            user.setStatus("ACTIVE");
            user.setAvailability("Mon/Wed afternoons");
            user.setPersonalStatement("Statement with, comma");
            user.setRelevantCourses("EBU6304|Software Testing");
            user.setProjectExperience("Test project");
            user.setPreferredRole("Lab|Tutorial");
            user.setSummaryStatus("SUMMARY_COMPLETE");
            user.setCvStoredName("U996.pdf");
            user.setCvOriginalName("Alice_CV.pdf");
            user.setCvContentType("application/pdf");
            user.setCvUploadedAt("2026-05-01 10:00:00");
            user.setCvStatus("UPLOADED");

            List<User> existing = storage.loadUsers();
            List<User> all = new java.util.ArrayList<>(existing);
            all.add(user);
            storage.saveUsers(all);

            List<User> loaded = storage.loadUsers();
            User result = loaded.stream()
                .filter(u -> "U996".equals(u.getUserId()))
                .findFirst().orElse(null);

            assertNotNull(result);
            assertEquals("U996", result.getUserId());
            assertEquals("alice", result.getUsername());
            assertEquals("secret", result.getPassword());
            assertEquals("Alice \"Special\" Chen", result.getName());
            assertEquals("alice@example.com", result.getEmail());
            assertEquals(UserRole.TA, result.getRole());
            assertEquals(3, result.getYear());
            assertEquals("SE", result.getMajor());
            assertEquals("Java|Python|JavaScript", result.getSkills());
            assertEquals("ACTIVE", result.getStatus());
            assertEquals("Mon/Wed afternoons", result.getAvailability());
            assertEquals("Statement with, comma", result.getPersonalStatement());
            assertEquals("EBU6304|Software Testing", result.getRelevantCourses());
            assertEquals("Test project", result.getProjectExperience());
            assertEquals("Lab|Tutorial", result.getPreferredRole());
            assertEquals("SUMMARY_COMPLETE", result.getSummaryStatus());
            assertEquals("U996.pdf", result.getCvStoredName());
            assertEquals("Alice_CV.pdf", result.getCvOriginalName());
            assertEquals("application/pdf", result.getCvContentType());
            assertEquals("2026-05-01 10:00:00", result.getCvUploadedAt());
            assertEquals("UPLOADED", result.getCvStatus());
        }
    }

    // ========== Job CRUD Tests ==========

    @Nested
    @DisplayName("Job CRUD Operations")
    class JobCrudTests {

        @Test
        @DisplayName("should save and load jobs successfully")
        void shouldSaveAndLoadJobs() {
            Job job = new Job();
            job.setJobId("J999");
            job.setTitle("Software Engineering TA");
            job.setModuleCode("EBU6304");
            job.setOrganiser("Prof.Chen");
            job.setMinYear(2);
            job.setMaxYear(4);
            job.setHours(20);
            job.setStatus(JobStatus.OPEN);
            job.setRequiredSkills("Java|Teamwork");
            job.setMatchScore(95);
            job.setDeadline("2026-12-31");
            job.setVacancies(3);

            List<Job> existing = storage.loadJobs();
            List<Job> all = new java.util.ArrayList<>(existing);
            all.add(job);
            storage.saveJobs(all);

            List<Job> loaded = storage.loadJobs();
            assertNotNull(loaded);
            assertTrue(loaded.size() >= 1);

            Job saved = loaded.stream()
                .filter(j -> "J999".equals(j.getJobId()))
                .findFirst().orElse(null);
            assertNotNull(saved);
            assertEquals("Software Engineering TA", saved.getTitle());
            assertEquals("EBU6304", saved.getModuleCode());
            assertEquals("Prof.Chen", saved.getOrganiser());
            assertEquals(2, saved.getMinYear());
            assertEquals(4, saved.getMaxYear());
            assertEquals(20, saved.getHours());
            assertEquals(JobStatus.OPEN, saved.getStatus());
            assertEquals("Java|Teamwork", saved.getRequiredSkills());
            assertEquals(95, saved.getMatchScore());
            assertEquals("2026-12-31", saved.getDeadline());
            assertEquals(3, saved.getVacancies());
        }

        @Test
        @DisplayName("should handle closed job status")
        void shouldHandleClosedJobStatus() {
            Job job = new Job();
            job.setJobId("J998");
            job.setTitle("Closed Position");
            job.setModuleCode("EBU6304");
            job.setOrganiser("Prof.Chen");
            job.setMinYear(1);
            job.setMaxYear(4);
            job.setHours(10);
            job.setStatus(JobStatus.CLOSED);
            job.setRequiredSkills("Python");
            job.setMatchScore(0);
            job.setDeadline("2020-01-01");
            job.setVacancies(1);

            List<Job> existing = storage.loadJobs();
            List<Job> all = new java.util.ArrayList<>(existing);
            all.add(job);
            storage.saveJobs(all);

            List<Job> loaded = storage.loadJobs();
            Job saved = loaded.stream()
                .filter(j -> "J998".equals(j.getJobId()))
                .findFirst().orElse(null);
            assertNotNull(saved);
            assertEquals(JobStatus.CLOSED, saved.getStatus());
        }
    }

    // ========== Application CRUD Tests ==========

    @Nested
    @DisplayName("Application CRUD Operations")
    class ApplicationCrudTests {

        @Test
        @DisplayName("should save and load applications successfully")
        void shouldSaveAndLoadApplications() {
            Application app = new Application();
            app.setApplicationId("A999");
            app.setUserId("T001");
            app.setJobId("J001");
            app.setStatus(ApplicationStatus.PENDING);
            app.setSubmittedAt("2026-01-15 10:00:00");
            app.setNotes("First application");
            app.setAvailability("Mon/Wed afternoons");

            List<Application> existing = storage.loadApplications();
            List<Application> all = new java.util.ArrayList<>(existing);
            all.add(app);
            storage.saveApplications(all);

            List<Application> loaded = storage.loadApplications();
            assertNotNull(loaded);

            Application saved = loaded.stream()
                .filter(a -> "A999".equals(a.getApplicationId()))
                .findFirst().orElse(null);
            assertNotNull(saved);
            assertEquals("T001", saved.getUserId());
            assertEquals("J001", saved.getJobId());
            assertEquals(ApplicationStatus.PENDING, saved.getStatus());
            assertEquals("First application", saved.getNotes());
            assertEquals("Mon/Wed afternoons", saved.getAvailability());
        }

        @Test
        @DisplayName("should handle all application statuses")
        void shouldHandleAllApplicationStatuses() {
            Application pending = new Application("A998", "T001", "J001",
                ApplicationStatus.PENDING, "2026-01-01 10:00", "Pending app", "");
            Application interview = new Application("A997", "T001", "J002",
                ApplicationStatus.INTERVIEW, "2026-01-02 10:00", "Interview app", "");
            Application accepted = new Application("A996", "T002", "J001",
                ApplicationStatus.ACCEPTED, "2026-01-03 10:00", "Accepted app", "");
            Application rejected = new Application("A995", "T002", "J002",
                ApplicationStatus.REJECTED, "2026-01-04 10:00", "Rejected app", "");

            List<Application> existing = storage.loadApplications();
            List<Application> all = new java.util.ArrayList<>(existing);
            all.add(pending);
            all.add(interview);
            all.add(accepted);
            all.add(rejected);
            storage.saveApplications(all);

            List<Application> loaded = storage.loadApplications();
            // Should contain seeded default (1) + our 4 = at least 5
            assertTrue(loaded.size() >= 4);
            assertTrue(loaded.stream().anyMatch(a -> a.getStatus() == ApplicationStatus.PENDING));
            assertTrue(loaded.stream().anyMatch(a -> a.getStatus() == ApplicationStatus.INTERVIEW));
            assertTrue(loaded.stream().anyMatch(a -> a.getStatus() == ApplicationStatus.ACCEPTED));
            assertTrue(loaded.stream().anyMatch(a -> a.getStatus() == ApplicationStatus.REJECTED));
        }
    }

    // ========== Utility Tests ==========

    @Nested
    @DisplayName("Utility Method Tests")
    class UtilityTests {

        @Test
        @DisplayName("should return formatted timestamp")
        void shouldReturnFormattedTimestamp() {
            String timestamp = storage.nowText();
            assertNotNull(timestamp);
            assertTrue(timestamp.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        }

        @Test
        @DisplayName("should return correct base directory path")
        void shouldReturnCorrectBaseDirectoryPath() {
            Path baseDir = storage.getBaseDir();
            assertNotNull(baseDir);
            assertTrue(baseDir.startsWith(tempDir));
        }
    }
}
