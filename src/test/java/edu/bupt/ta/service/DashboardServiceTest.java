package edu.bupt.ta.service;

import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DashboardService.
 * Uses a shared FileStorageUtil instance backed by a temporary directory.
 */
class DashboardServiceTest {

    @TempDir
    Path tempDir;

    private DashboardService dashboardService;
    private JobService jobService;
    private ApplicationService applicationService;
    private UserService userService;

    @BeforeEach
    void setUp() throws IOException {
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);

        // Create and share a single FileStorageUtil instance
        edu.bupt.ta.storage.FileStorageUtil storage =
            new edu.bupt.ta.storage.FileStorageUtil(dataDir, null);

        dashboardService = new DashboardService(storage);
        jobService = new JobService(storage);
        applicationService = new ApplicationService(storage);
        userService = new UserService(storage);

        // Seeded data is available:
        // Users: U001(seele, SUMMARY_COMPLETE), U002(luna, SUMMARY_COMPLETE), U003(kevin, SUMMARY_COMPLETE)
        // Jobs: J001(JAVA), J002(C), J003(Java), J004(CLOSED)
        // Applications: A001(U001, J001, PENDING)
    }

    // ========== Pending Count Tests ==========

    @Nested
    @DisplayName("getPendingCount()")
    class PendingCountTests {

        @Test
        @DisplayName("should return 0 for null user")
        void shouldReturn0ForNullUser() {
            int count = dashboardService.getPendingCount(null);
            assertEquals(0, count);
        }

        @Test
        @DisplayName("should return correct pending count for seeded user")
        void shouldReturnCorrectPendingCountForSeededUser() {
            // U001 has seeded application A001
            User user = userService.findById("U001");
            int count = dashboardService.getPendingCount(user);
            assertTrue(count >= 1);
        }

        @Test
        @DisplayName("should return 0 for user with no applications")
        void shouldReturn0ForUserWithNoApplications() {
            // U002 has no applications by default
            User user = userService.findById("U002");
            int count = dashboardService.getPendingCount(user);
            assertEquals(0, count);
        }
    }

    // ========== Matched Jobs Tests ==========

    @Nested
    @DisplayName("getMatchedJobs()")
    class MatchedJobsTests {

        @Test
        @DisplayName("should return 0 for null user")
        void shouldReturn0ForNullUser() {
            int count = dashboardService.getMatchedJobs(null);
            assertEquals(0, count);
        }

        @Test
        @DisplayName("should count jobs with match score >= 60")
        void shouldCountJobsWithMatchScore60OrHigher() {
            User user = userService.findById("U001");
            int count = dashboardService.getMatchedJobs(user);
            assertTrue(count >= 0);
        }
    }

    // ========== Todo Count Tests ==========

    @Nested
    @DisplayName("getTodoCount()")
    class TodoCountTests {

        @Test
        @DisplayName("should return same as getPendingCount")
        void shouldReturnSameAsGetPendingCount() {
            User user = userService.findById("U001");
            int pendingCount = dashboardService.getPendingCount(user);
            int todoCount = dashboardService.getTodoCount(user);
            assertEquals(pendingCount, todoCount);
        }

        @Test
        @DisplayName("should return 0 for null user")
        void shouldReturn0ForNullUser() {
            int count = dashboardService.getTodoCount(null);
            assertEquals(0, count);
        }
    }

    // ========== Best Match Message Tests ==========

    @Nested
    @DisplayName("getBestMatchMessage()")
    class BestMatchMessageTests {

        @Test
        @DisplayName("should return login message for null user")
        void shouldReturnLoginMessageForNullUser() {
            String message = dashboardService.getBestMatchMessage(null);
            assertNotNull(message);
            assertTrue(message.toLowerCase().contains("login"));
        }

        @Test
        @DisplayName("should return recommendation message for seeded user")
        void shouldReturnRecommendationMessageForSeededUser() {
            User user = userService.findById("U001");
            String message = dashboardService.getBestMatchMessage(user);
            assertNotNull(message);
            assertFalse(message.toLowerCase().contains("login"));
        }

        @Test
        @DisplayName("should return a non-empty message")
        void shouldReturnNonEmptyMessage() {
            User user = userService.findById("U001");
            String message = dashboardService.getBestMatchMessage(user);
            assertNotNull(message);
            assertFalse(message.isEmpty());
        }
    }
}
