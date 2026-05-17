package edu.bupt.ta.service;

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
 * Unit tests for ApplicationService.
 * Uses a shared FileStorageUtil instance backed by a temporary directory.
 * Tests the application lifecycle including submission, validation, and status updates.
 */
class ApplicationServiceTest {

    @TempDir
    Path tempDir;

    private ApplicationService applicationService;
    private JobService jobService;
    private UserService userService;

    @BeforeEach
    void setUp() throws IOException {
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);

        // Create and share a single FileStorageUtil instance
        edu.bupt.ta.storage.FileStorageUtil storage =
            new edu.bupt.ta.storage.FileStorageUtil(dataDir, null);

        // All services share the same storage
        applicationService = new ApplicationService(storage);
        jobService = new JobService(storage);
        userService = new UserService(storage);

        // Seeded data is auto-created by FileStorageUtil constructor:
        // Users: U001(seele), U002(luna), U003(kevin), U004(mo1), U005(mo2), U006(admin)
        // Jobs: J001, J002, J003, J004 (J004 is CLOSED)
        // Applications: A001(U001,J001,PENDING)
    }

    // ========== Apply Tests ==========

    @Nested
    @DisplayName("apply()")
    class ApplyTests {

        @Test
        @DisplayName("should create application successfully for seeded user and job")
        void shouldCreateApplicationSuccessfully() {
            // U001 (seele) is seeded and has SUMMARY_COMPLETE profile
            // J002 is seeded and OPEN, unique for U001
            Application result = applicationService.apply("U001", "J002");

            assertNotNull(result);
            assertEquals("U001", result.getUserId());
            assertEquals("J002", result.getJobId());
            assertEquals(ApplicationStatus.PENDING, result.getStatus());
            assertNotNull(result.getSubmittedAt());
        }

        @Test
        @DisplayName("should assign auto-incremented application ID")
        void shouldAssignAutoIncrementedApplicationId() {
            // A002 uses U001+J002, A003 uses U002+J002 - unique
            Application app1 = applicationService.apply("U001", "J002");
            Application app2 = applicationService.apply("U002", "J002");

            assertNotNull(app1);
            assertNotNull(app2);
            assertNotEquals(app1.getApplicationId(), app2.getApplicationId());
        }

        @Test
        @DisplayName("should reject application for non-existent user")
        void shouldRejectApplicationForNonExistentUser() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.apply("NONEXISTENT", "J001")
            );
            assertEquals("user not found.", exception.getMessage());
        }

        @Test
        @DisplayName("should reject application for non-existent job")
        void shouldRejectApplicationForNonExistentJob() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.apply("U001", "NONEXISTENT")
            );
            assertEquals("job not found.", exception.getMessage());
        }

        @Test
        @DisplayName("should reject duplicate application (seeded A001 already exists for U001+J001)")
        void shouldRejectDuplicateApplication() {
            // Seeded A001 = U001 + J001. Try to apply again for J001 with U001
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.apply("U001", "J001")
            );
            assertTrue(exception.getMessage().contains("already applied"));
        }

        @Test
        @DisplayName("should allow different users to apply for same job")
        void shouldAllowDifferentUsersToApplyForSameJob() {
            // Seeded A001 = U001+J001. Let U002 apply for J001
            Application app = applicationService.apply("U002", "J001");
            assertNotNull(app);
            assertEquals("U002", app.getUserId());
            assertEquals("J001", app.getJobId());
        }
    }

    // ========== Query Tests ==========

    @Nested
    @DisplayName("Query Operations")
    class QueryTests {

        @Test
        @DisplayName("should get seeded applications")
        void shouldGetSeededApplications() {
            // Seeded: A001 exists
            List<Application> apps = applicationService.getAllApplications();
            assertTrue(apps.size() >= 1);
        }

        @Test
        @DisplayName("should get applications by user ID")
        void shouldGetApplicationsByUserId() {
            List<Application> apps = applicationService.getApplicationsByUserId("U001");
            assertTrue(apps.size() >= 1);
            for (Application app : apps) {
                assertEquals("U001", app.getUserId());
            }
        }

        @Test
        @DisplayName("should get applications by job ID")
        void shouldGetApplicationsByJobId() {
            List<Application> apps = applicationService.getApplicationsByJobId("J001");
            assertTrue(apps.size() >= 1);
            for (Application app : apps) {
                assertEquals("J001", app.getJobId());
            }
        }

        @Test
        @DisplayName("should get applications by job IDs set")
        void shouldGetApplicationsByJobIdsSet() {
            List<Application> apps = applicationService.getApplicationsByJobIds(
                java.util.Set.of("J001", "J002"));
            assertNotNull(apps);
        }

        @Test
        @DisplayName("should return empty list for user with no applications")
        void shouldReturnEmptyListForUserWithNoApplications() {
            List<Application> apps = applicationService.getApplicationsByUserId("NONEXISTENT");
            assertTrue(apps.isEmpty());
        }

        @Test
        @DisplayName("should count applications by job ID")
        void shouldCountApplicationsByJobId() {
            int count = applicationService.countApplicationsByJobId("J001");
            assertTrue(count >= 1);
        }

        @Test
        @DisplayName("should count user pending and interview applications")
        void shouldCountUserPendingAndInterviewApplications() {
            // Seeded user U001 has seeded application A001
            int count = applicationService.countUserPendingAndInterview("U001");
            assertTrue(count >= 1);
        }
    }

    // ========== Status Update Tests ==========

    @Nested
    @DisplayName("updateStatus()")
    class StatusUpdateTests {

        @Test
        @DisplayName("should update status to ACCEPTED for seeded application")
        void shouldUpdateStatusToAccepted() {
            // Use seeded application A001 (U001 + J001)
            applicationService.updateStatus("A001", ApplicationStatus.ACCEPTED);

            Application updated = applicationService.findById("A001");
            assertEquals(ApplicationStatus.ACCEPTED, updated.getStatus());
            assertEquals("Accepted", updated.getNotes());
        }

        @Test
        @DisplayName("should update status to REJECTED")
        void shouldUpdateStatusToRejected() {
            // U002 applies for J001 (not a duplicate since seeded is only U001+J001)
            Application app = applicationService.apply("U002", "J001");

            applicationService.updateStatus(app.getApplicationId(), ApplicationStatus.REJECTED);

            Application updated = applicationService.findById(app.getApplicationId());
            assertEquals(ApplicationStatus.REJECTED, updated.getStatus());
            assertEquals("Rejected", updated.getNotes());
        }

        @Test
        @DisplayName("should update status to INTERVIEW")
        void shouldUpdateStatusToInterview() {
            // U003 applies for J001
            Application app = applicationService.apply("U003", "J001");

            applicationService.updateStatus(app.getApplicationId(), ApplicationStatus.INTERVIEW);

            Application updated = applicationService.findById(app.getApplicationId());
            assertEquals(ApplicationStatus.INTERVIEW, updated.getStatus());
            assertEquals("Moved to interview", updated.getNotes());
        }

        @Test
        @DisplayName("should update status with custom notes")
        void shouldUpdateStatusWithCustomNotes() {
            // U003 applies for J002
            Application app = applicationService.apply("U003", "J002");

            applicationService.updateStatus(app.getApplicationId(), ApplicationStatus.ACCEPTED, "Excellent candidate");

            Application updated = applicationService.findById(app.getApplicationId());
            assertEquals(ApplicationStatus.ACCEPTED, updated.getStatus());
            assertEquals("Excellent candidate", updated.getNotes());
        }

        @Test
        @DisplayName("should throw exception for non-existent application")
        void shouldThrowExceptionForNonExistentApplication() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applicationService.updateStatus("NONEXISTENT", ApplicationStatus.ACCEPTED)
            );
            assertEquals("Application not found", exception.getMessage());
        }
    }

    // ========== Count Tests ==========

    @Nested
    @DisplayName("Count Operations")
    class CountTests {

        @Test
        @DisplayName("should count total applications")
        void shouldCountTotalApplications() {
            int total = applicationService.countTotalApplications();
            assertTrue(total >= 1); // Seeded A001 + any we created
        }

        @Test
        @DisplayName("should count all applications by status")
        void shouldCountAllApplicationsByStatus() {
            int pending = applicationService.countAllByStatus(ApplicationStatus.PENDING);
            int accepted = applicationService.countAllByStatus(ApplicationStatus.ACCEPTED);
            int rejected = applicationService.countAllByStatus(ApplicationStatus.REJECTED);
            int interview = applicationService.countAllByStatus(ApplicationStatus.INTERVIEW);

            assertTrue(pending >= 0);
            assertTrue(accepted >= 0);
            assertTrue(rejected >= 0);
            assertTrue(interview >= 0);
        }
    }

    // ========== Find By ID Tests ==========

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("should find seeded application by ID")
        void shouldFindSeededApplicationById() {
            // Seeded: A001
            Application found = applicationService.findById("A001");
            assertNotNull(found);
            assertEquals("A001", found.getApplicationId());
        }

        @Test
        @DisplayName("should return null for non-existent ID")
        void shouldReturnNullForNonExistentId() {
            Application found = applicationService.findById("NONEXISTENT");
            assertNull(found);
        }
    }
}
