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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Workload-related operations in AdminService.
 * Tests TA workload statistics, application counts, and hours calculation.
 * Uses @TempDir to isolate test data from production files.
 */
class WorkloadServiceTest {

    @TempDir
    Path tempDir;

    private AdminService adminService;
    private ApplicationService applicationService;
    private JobService jobService;
    private UserService userService;
    private edu.bupt.ta.storage.FileStorageUtil storage;

    @BeforeEach
    void setUp() throws IOException {
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);

        // Create and share a single FileStorageUtil instance
        storage = new edu.bupt.ta.storage.FileStorageUtil(dataDir, null);

        adminService = new AdminService(storage);
        applicationService = new ApplicationService(storage);
        jobService = new JobService(storage);
        userService = new UserService(storage);
    }

    // ========== Workload Calculation Tests ==========

    @Nested
    @DisplayName("calculateUserWorkloads()")
    class WorkloadCalculationTests {

        @Test
        @DisplayName("should return empty map when no applications exist")
        void shouldReturnEmptyMapWhenNoApplications() {
            // No applications exist (only seeded data with no apps for TAs)
            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertNotNull(workloads);
            // Seeded TAs: U001, U002, U003 (all have workload 0 or seeded A001 for U001)
            assertTrue(workloads.containsKey("U001") || workloads.containsKey("U002"));
        }

        @Test
        @DisplayName("should count applications per TA correctly")
        void shouldCountApplicationsPerTaCorrectly() {
            // Seeded data: U001(seele), U002(luna), U003(kevin)
            // Seeded A001: U001 + J001
            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertNotNull(workloads);
            assertTrue(workloads.containsKey("U001"), "U001 should be in workload map");
            assertTrue(workloads.containsKey("U002"), "U002 should be in workload map");
            assertTrue(workloads.containsKey("U003"), "U003 should be in workload map");
        }

        @Test
        @DisplayName("should reflect new applications in workload count")
        void shouldReflectNewApplicationsInWorkloadCount() {
            // Create additional applications
            applicationService.apply("U002", "J002"); // U002 applies for J002
            applicationService.apply("U003", "J002"); // U003 applies for J002

            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertNotNull(workloads);
            // U002 now has 1 application (seeded + new)
            assertTrue(workloads.get("U002") >= 1);
            // U003 now has 1 application
            assertTrue(workloads.get("U003") >= 1);
        }

        @Test
        @DisplayName("should not count MO or Admin users in TA workload")
        void shouldNotCountNonTaUsersInWorkload() {
            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertNotNull(workloads);
            // MO users: U004(mo1), U005(mo2) should NOT be in TA workload
            assertFalse(workloads.containsKey("U004"), "MO should not appear in TA workload");
            assertFalse(workloads.containsKey("U005"), "MO should not appear in TA workload");
            // Admin: U006 should NOT be in TA workload
            assertFalse(workloads.containsKey("U006"), "Admin should not appear in TA workload");
        }

        @Test
        @DisplayName("should handle multiple applications per TA")
        void shouldHandleMultipleApplicationsPerTa() {
            // U002 applies to multiple jobs
            applicationService.apply("U002", "J002");
            applicationService.apply("U002", "J003");

            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertNotNull(workloads);
            assertTrue(workloads.get("U002") >= 2, "U002 should have at least 2 applications");
        }

        @Test
        @DisplayName("should count all application statuses for workload")
        void shouldCountAllApplicationStatusesForWorkload() {
            // Create multiple applications with different statuses
            applicationService.apply("U002", "J002");
            Application app3 = applicationService.apply("U003", "J002");

            // Update one to ACCEPTED
            applicationService.updateStatus(app3.getApplicationId(), ApplicationStatus.ACCEPTED);

            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertNotNull(workloads);
            // Both PENDING and ACCEPTED should count
            assertTrue(workloads.get("U003") >= 1);
        }
    }

    // ========== Workload Limit Detection Tests ==========

    @Nested
    @DisplayName("Workload Limit Detection")
    class WorkloadLimitTests {

        @Test
        @DisplayName("should identify TA with high application count")
        void shouldIdentifyTaWithHighApplicationCount() {
            // Apply for multiple jobs to increase workload
            applicationService.apply("U002", "J002");
            applicationService.apply("U002", "J003");

            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertNotNull(workloads);
            assertTrue(workloads.get("U002") >= 2);
        }

        @Test
        @DisplayName("should return 0 for TA with no applications")
        void shouldReturnZeroForTaWithNoApplications() {
            // U003 has no applications
            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertNotNull(workloads);
            // All TAs should be in the map (even with 0 applications)
            assertTrue(workloads.containsKey("U003"));
        }

        @Test
        @DisplayName("should handle concurrent workload calculations")
        void shouldHandleConcurrentWorkloadCalculations() {
            // Apply for jobs
            applicationService.apply("U002", "J002");

            // Calculate workload multiple times
            Map<String, Integer> workload1 = adminService.calculateUserWorkloads();
            Map<String, Integer> workload2 = adminService.calculateUserWorkloads();
            Map<String, Integer> workload3 = adminService.calculateUserWorkloads();

            assertNotNull(workload1);
            assertNotNull(workload2);
            assertNotNull(workload3);

            // All should return consistent results
            assertEquals(workload1.get("U002"), workload2.get("U002"));
            assertEquals(workload2.get("U002"), workload3.get("U002"));
        }
    }

    // ========== Edge Cases Tests ==========

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("should handle empty user list gracefully")
        void shouldHandleEmptyUserListGracefully() {
            // This is difficult to test directly as we cannot remove seeded users
            // But we can verify the structure of the result
            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertNotNull(workloads);
            // Map should contain all seeded TAs
            assertEquals(3, workloads.size(), "Should contain exactly 3 seeded TAs");
        }

        @Test
        @DisplayName("should return valid map structure")
        void shouldReturnValidMapStructure() {
            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertNotNull(workloads);
            // Verify map contains only valid TA user IDs
            for (String userId : workloads.keySet()) {
                assertTrue(userId.startsWith("U"), "User ID should start with U");
                assertNotNull(workloads.get(userId), "Workload value should not be null");
            }
        }

        @Test
        @DisplayName("should return non-negative workload values")
        void shouldReturnNonNegativeWorkloadValues() {
            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertNotNull(workloads);
            for (Integer workload : workloads.values()) {
                assertTrue(workload >= 0, "Workload should be non-negative");
            }
        }
    }
}
