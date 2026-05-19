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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AdminService.
 * Uses a shared FileStorageUtil instance backed by a temporary directory.
 */
class AdminServiceTest {

    @TempDir
    Path tempDir;

    private AdminService adminService;
    private UserService userService;
    private ApplicationService applicationService;
    private JobService jobService;

    @BeforeEach
    void setUp() throws IOException {
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);

        // Create and share a single FileStorageUtil instance
        edu.bupt.ta.storage.FileStorageUtil storage =
            new edu.bupt.ta.storage.FileStorageUtil(dataDir, null);

        adminService = new AdminService(storage);
        userService = new UserService(storage);
        applicationService = new ApplicationService(storage);
        jobService = new JobService(storage);

        // Seeded data:
        // Users: U001(seele, TA), U002(luna, TA), U003(kevin, TA), U004(mo1, MO), U005(mo2, MO), U006(admin, ADMIN)
        // Jobs: J001, J002, J003, J004(CLOSED)
        // Applications: A001(U001, J001, PENDING)
    }

    // ========== Dashboard Stats Tests ==========

    @Nested
    @DisplayName("getDashboardStats()")
    class DashboardStatsTests {

        @Test
        @DisplayName("should return all dashboard statistics")
        void shouldReturnAllDashboardStatistics() {
            Map<String, Object> stats = adminService.getDashboardStats();

            assertNotNull(stats);
            assertTrue(stats.containsKey("totalTA"));
            assertTrue(stats.containsKey("activeTA"));
            assertTrue(stats.containsKey("totalMO"));
            assertTrue(stats.containsKey("totalApplications"));
            assertTrue(stats.containsKey("pendingApplications"));
            assertTrue(stats.containsKey("interviewApplications"));
            assertTrue(stats.containsKey("acceptedApplications"));
            assertTrue(stats.containsKey("rejectedApplications"));
            assertTrue(stats.containsKey("topJobTitles"));
            assertTrue(stats.containsKey("totalJobs"));
            assertTrue(stats.containsKey("openJobs"));
            assertTrue(stats.containsKey("topTAs"));
            assertTrue(stats.containsKey("topJobs"));
        }

        @Test
        @DisplayName("should calculate correct TA count")
        void shouldCalculateCorrectTaCount() {
            Map<String, Object> stats = adminService.getDashboardStats();
            assertEquals(3, stats.get("totalTA"));
        }

        @Test
        @DisplayName("should calculate correct active TA count")
        void shouldCalculateCorrectActiveTaCount() {
            Map<String, Object> stats = adminService.getDashboardStats();
            assertEquals(3, stats.get("activeTA"));
        }

        @Test
        @DisplayName("should calculate correct application counts")
        void shouldCalculateCorrectApplicationCounts() {
            Map<String, Object> stats = adminService.getDashboardStats();
            // Seeded: A001 (PENDING)
            assertTrue((Integer) stats.get("totalApplications") >= 1);
        }

        @Test
        @DisplayName("should calculate correct job counts")
        void shouldCalculateCorrectJobCounts() {
            Map<String, Object> stats = adminService.getDashboardStats();
            assertEquals(4, stats.get("totalJobs"));
            assertEquals(4, stats.get("openJobs")); // All 4 seeded jobs are OPEN
        }

        @Test
        @DisplayName("should return top TAs map")
        void shouldReturnTopTAsMap() {
            Map<String, Object> stats = adminService.getDashboardStats();
            @SuppressWarnings("unchecked")
            Map<String, Integer> topTAs = (Map<String, Integer>) stats.get("topTAs");
            assertNotNull(topTAs);
        }

        @Test
        @DisplayName("should return top jobs map")
        void shouldReturnTopJobsMap() {
            Map<String, Object> stats = adminService.getDashboardStats();
            @SuppressWarnings("unchecked")
            Map<String, Integer> topJobs = (Map<String, Integer>) stats.get("topJobs");
            assertNotNull(topJobs);
        }

        @Test
        @DisplayName("should count interview applications separately")
        void shouldCountInterviewApplicationsSeparately() {
            applicationService.updateStatus("A001", ApplicationStatus.INTERVIEW);
            Map<String, Object> stats = adminService.getDashboardStats();
            assertEquals(1, stats.get("interviewApplications"));
            assertEquals(0, stats.get("pendingApplications"));
        }

        @Test
        @DisplayName("should return job titles for popular jobs chart")
        void shouldReturnJobTitlesForPopularJobsChart() {
            Map<String, Object> stats = adminService.getDashboardStats();
            @SuppressWarnings("unchecked")
            Map<String, String> topJobTitles = (Map<String, String>) stats.get("topJobTitles");
            @SuppressWarnings("unchecked")
            Map<String, Integer> topJobs = (Map<String, Integer>) stats.get("topJobs");
            assertNotNull(topJobTitles);
            assertFalse(topJobs.isEmpty());
            for (String jobId : topJobs.keySet()) {
                assertTrue(topJobTitles.containsKey(jobId));
                assertFalse(topJobTitles.get(jobId).isBlank());
            }
        }
    }

    // ========== Workload Calculation Tests ==========

    @Nested
    @DisplayName("calculateUserWorkloads()")
    class WorkloadTests {

        @Test
        @DisplayName("should calculate workload for each TA user")
        void shouldCalculateWorkloadForEachTaUser() {
            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertNotNull(workloads);
            assertTrue(workloads.containsKey("U001")); // seele
            assertTrue(workloads.containsKey("U002")); // luna
            assertTrue(workloads.containsKey("U003")); // kevin
            // MOs and admin should not be included
            assertFalse(workloads.containsKey("U004")); // mo1
            assertFalse(workloads.containsKey("U005")); // mo2
            assertFalse(workloads.containsKey("U006")); // admin
        }

        @Test
        @DisplayName("should count only accepted application hours per user")
        void shouldCountOnlyAcceptedApplicationHoursPerUser() {
            Map<String, Integer> workloads = adminService.calculateUserWorkloads();
            // U001 has seeded A001 (PENDING) for J001 (20h) — pending does not count
            assertEquals(0, workloads.get("U001"));

            applicationService.updateStatus("A001", ApplicationStatus.ACCEPTED);
            workloads = adminService.calculateUserWorkloads();
            assertEquals(20, workloads.get("U001"));
        }

        @Test
        @DisplayName("should classify workload levels")
        void shouldClassifyWorkloadLevels() {
            assertEquals("Normal", AdminService.getWorkloadLevel(0));
            assertEquals("Normal", AdminService.getWorkloadLevel(20));
            assertEquals("Warning", AdminService.getWorkloadLevel(21));
            assertEquals("Warning", AdminService.getWorkloadLevel(40));
            assertEquals("Overloaded", AdminService.getWorkloadLevel(41));
        }
    }

    // ========== User Status Toggle Tests ==========

    @Nested
    @DisplayName("toggleUserStatus()")
    class UserStatusToggleTests {

        @Test
        @DisplayName("should toggle user status from ACTIVE to INACTIVE")
        void shouldToggleUserStatusFromActiveToInactive() {
            adminService.toggleUserStatus("U001");

            User toggled = userService.findById("U001");
            assertNotNull(toggled);
            assertEquals("INACTIVE", toggled.getStatus());
        }

        @Test
        @DisplayName("should toggle user status from INACTIVE to ACTIVE")
        void shouldToggleUserStatusFromInactiveToActive() {
            adminService.toggleUserStatus("U001");
            adminService.toggleUserStatus("U001");

            User toggled = userService.findById("U001");
            assertNotNull(toggled);
            assertEquals("ACTIVE", toggled.getStatus());
        }
    }

    // ========== Bulk User Save Tests ==========

    @Nested
    @DisplayName("saveUsersDirect()")
    class BulkUserSaveTests {

        @Test
        @DisplayName("should save users directly to storage")
        void shouldSaveUsersDirectlyToStorage() {
            User newUser = new User();
            newUser.setUserId("T999");
            newUser.setUsername("newuser");
            newUser.setPassword("password");
            newUser.setName("New User");
            newUser.setEmail("new@bupt.edu.cn");
            newUser.setRole(UserRole.TA);
            newUser.setYear(1);
            newUser.setMajor("CS");
            newUser.setSkills("Python");
            newUser.setStatus("ACTIVE");
            newUser.setSummaryStatus("INCOMPLETE");
            newUser.setCvStatus("MISSING");

            adminService.saveUsersDirect(List.of(newUser));

            User found = userService.findById("T999");
            assertNotNull(found);
            assertEquals("New User", found.getName());
        }
    }
}
