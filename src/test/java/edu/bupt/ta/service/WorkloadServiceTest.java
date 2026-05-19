package edu.bupt.ta.service;

import edu.bupt.ta.model.Application;
import edu.bupt.ta.model.ApplicationStatus;
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
 * Unit tests for TA workload (accepted hours) in AdminService.
 */
class WorkloadServiceTest {

    @TempDir
    Path tempDir;

    private AdminService adminService;
    private ApplicationService applicationService;

    @BeforeEach
    void setUp() throws IOException {
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);

        edu.bupt.ta.storage.FileStorageUtil storage =
            new edu.bupt.ta.storage.FileStorageUtil(dataDir, null);

        adminService = new AdminService(storage);
        applicationService = new ApplicationService(storage);
    }

    @Nested
    @DisplayName("calculateUserWorkloads()")
    class WorkloadCalculationTests {

        @Test
        @DisplayName("should include every TA with zero hours when no accepted applications")
        void shouldIncludeEveryTaWithZeroHoursWhenNoAcceptedApplications() {
            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertNotNull(workloads);
            assertEquals(3, workloads.size());
            assertEquals(0, workloads.get("U001"));
            assertEquals(0, workloads.get("U002"));
            assertEquals(0, workloads.get("U003"));
        }

        @Test
        @DisplayName("should not count MO or Admin users")
        void shouldNotCountNonTaUsersInWorkload() {
            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertFalse(workloads.containsKey("U004"));
            assertFalse(workloads.containsKey("U005"));
            assertFalse(workloads.containsKey("U006"));
        }

        @Test
        @DisplayName("should not count pending applications toward workload hours")
        void shouldNotCountPendingApplicationsTowardWorkloadHours() {
            applicationService.apply("U002", "J002");

            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertEquals(0, workloads.get("U002"));
        }

        @Test
        @DisplayName("should add job hours when application is accepted")
        void shouldAddJobHoursWhenApplicationIsAccepted() {
            Application app = applicationService.apply("U002", "J002");
            applicationService.updateStatus(app.getApplicationId(), ApplicationStatus.ACCEPTED);

            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertEquals(18, workloads.get("U002"));
        }

        @Test
        @DisplayName("should not count rejected applications toward workload hours")
        void shouldNotCountRejectedApplicationsTowardWorkloadHours() {
            Application app = applicationService.apply("U003", "J003");
            applicationService.updateStatus(app.getApplicationId(), ApplicationStatus.REJECTED);

            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertEquals(0, workloads.get("U003"));
        }

        @Test
        @DisplayName("should sum hours from multiple accepted applications")
        void shouldSumHoursFromMultipleAcceptedApplications() {
            Application app1 = applicationService.apply("U002", "J002");
            Application app2 = applicationService.apply("U002", "J003");
            applicationService.updateStatus(app1.getApplicationId(), ApplicationStatus.ACCEPTED);
            applicationService.updateStatus(app2.getApplicationId(), ApplicationStatus.ACCEPTED);

            Map<String, Integer> workloads = adminService.calculateUserWorkloads();

            assertEquals(34, workloads.get("U002"));
        }

        @Test
        @DisplayName("should increase hours after additional acceptance")
        void shouldIncreaseHoursAfterAdditionalAcceptance() {
            applicationService.updateStatus("A001", ApplicationStatus.ACCEPTED);

            Map<String, Integer> workloads = adminService.calculateUserWorkloads();
            assertEquals(20, workloads.get("U001"));

            Application app = applicationService.apply("U001", "J002");
            applicationService.updateStatus(app.getApplicationId(), ApplicationStatus.ACCEPTED);

            workloads = adminService.calculateUserWorkloads();
            assertEquals(38, workloads.get("U001"));
        }
    }

    @Nested
    @DisplayName("getWorkloadLevel()")
    class WorkloadLevelTests {

        @Test
        @DisplayName("should return Normal for 0-20 hours")
        void shouldReturnNormalForLowHours() {
            assertEquals("Normal", AdminService.getWorkloadLevel(0));
            assertEquals("Normal", AdminService.getWorkloadLevel(20));
        }

        @Test
        @DisplayName("should return Warning for 21-40 hours")
        void shouldReturnWarningForMediumHours() {
            assertEquals("Warning", AdminService.getWorkloadLevel(21));
            assertEquals("Warning", AdminService.getWorkloadLevel(40));
        }

        @Test
        @DisplayName("should return Overloaded for more than 40 hours")
        void shouldReturnOverloadedForHighHours() {
            assertEquals("Overloaded", AdminService.getWorkloadLevel(41));
        }
    }
}
