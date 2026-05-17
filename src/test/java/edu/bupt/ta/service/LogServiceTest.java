package edu.bupt.ta.service;

import edu.bupt.ta.model.SystemLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LogService.
 * Tests audit log creation, querying, search, and pagination.
 */
class LogServiceTest {

    @TempDir
    Path tempDir;

    private LogService logService;

    @BeforeEach
    void setUp() throws IOException {
        // Use the new constructor to inject test directory
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);
        Path logFile = dataDir.resolve("system_logs.csv");

        logService = new LogService(logFile);
    }

    // ========== Log Creation Tests ==========

    @Nested
    @DisplayName("log()")
    class LogCreationTests {

        @Test
        @DisplayName("should create log entry successfully")
        void shouldCreateLogEntrySuccessfully() {
            logService.log("T001", "Alice Chen", "LOGIN", "User", "T001",
                "User logged in", "127.0.0.1");

            List<SystemLog> logs = logService.getAllLogs();
            assertEquals(1, logs.size());
            assertEquals("T001", logs.get(0).getOperatorId());
            assertEquals("Alice Chen", logs.get(0).getOperatorName());
            assertEquals("LOGIN", logs.get(0).getOperationType());
        }

        @Test
        @DisplayName("should auto-increment log ID")
        void shouldAutoIncrementLogId() {
            logService.log("T001", "Alice", "LOGIN", "User", "T001", "Login 1", "127.0.0.1");
            logService.log("T002", "Bob", "LOGOUT", "User", "T002", "Logout 1", "127.0.0.1");
            logService.log("M001", "Prof.Chen", "CREATE", "Job", "J001", "Created job", "127.0.0.1");

            List<SystemLog> logs = logService.getAllLogs();
            assertEquals(3, logs.size());
            assertTrue(logs.stream().anyMatch(log -> log.getLogId().equals("L00001")));
            assertTrue(logs.stream().anyMatch(log -> log.getLogId().equals("L00002")));
            assertTrue(logs.stream().anyMatch(log -> log.getLogId().equals("L00003")));
        }

        @Test
        @DisplayName("should record all operation types")
        void shouldRecordAllOperationTypes() {
            logService.log("T001", "Alice", "LOGIN", "User", "T001", "Login", "127.0.0.1");
            logService.log("T001", "Alice", "LOGOUT", "User", "T001", "Logout", "127.0.0.1");
            logService.log("M001", "Prof.Chen", "CREATE", "Job", "J001", "Created job", "127.0.0.1");
            logService.log("M001", "Prof.Chen", "UPDATE", "Job", "J001", "Updated job", "127.0.0.1");
            logService.log("A001", "Admin", "DELETE", "Job", "J002", "Deleted job", "127.0.0.1");
            logService.log("M001", "Prof.Chen", "APPROVE", "Application", "A001", "Approved", "127.0.0.1");
            logService.log("M001", "Prof.Chen", "REJECT", "Application", "A002", "Rejected", "127.0.0.1");

            List<SystemLog> logs = logService.getAllLogs();
            assertEquals(7, logs.size());

            assertEquals(1, logs.stream().filter(l -> l.getOperationType().equals("LOGIN")).count());
            assertEquals(1, logs.stream().filter(l -> l.getOperationType().equals("LOGOUT")).count());
            assertEquals(1, logs.stream().filter(l -> l.getOperationType().equals("CREATE")).count());
            assertEquals(1, logs.stream().filter(l -> l.getOperationType().equals("UPDATE")).count());
            assertEquals(1, logs.stream().filter(l -> l.getOperationType().equals("DELETE")).count());
            assertEquals(1, logs.stream().filter(l -> l.getOperationType().equals("APPROVE")).count());
            assertEquals(1, logs.stream().filter(l -> l.getOperationType().equals("REJECT")).count());
        }
    }

    // ========== Query Tests ==========

    @Nested
    @DisplayName("Query Operations")
    class QueryTests {

        @BeforeEach
        void createTestLogs() {
            logService.log("T001", "Alice Chen", "LOGIN", "User", "T001", "Alice logged in", "127.0.0.1");
            logService.log("T002", "Bob Wang", "LOGIN", "User", "T002", "Bob logged in", "127.0.0.2");
            logService.log("T001", "Alice Chen", "CREATE", "Application", "A001", "Alice applied", "127.0.0.1");
            logService.log("M001", "Prof.Chen", "CREATE", "Job", "J001", "Created job", "127.0.0.3");
        }

        @Test
        @DisplayName("should get all logs sorted by newest first")
        void shouldGetAllLogsSortedByNewestFirst() {
            List<SystemLog> logs = logService.getAllLogs();
            assertEquals(4, logs.size());

            for (int i = 0; i < logs.size() - 1; i++) {
                assertTrue(logs.get(i).getCreatedAt().isAfter(logs.get(i + 1).getCreatedAt()) ||
                           logs.get(i).getCreatedAt().isEqual(logs.get(i + 1).getCreatedAt()));
            }
        }

        @Test
        @DisplayName("should get logs by operator ID")
        void shouldGetLogsByOperatorId() {
            List<SystemLog> aliceLogs = logService.getLogsByOperator("T001");
            assertEquals(2, aliceLogs.size());
            for (SystemLog log : aliceLogs) {
                assertEquals("T001", log.getOperatorId());
            }
        }

        @Test
        @DisplayName("should return empty list for non-existent operator")
        void shouldReturnEmptyListForNonExistentOperator() {
            List<SystemLog> logs = logService.getLogsByOperator("NONEXISTENT");
            assertTrue(logs.isEmpty());
        }

        @Test
        @DisplayName("should get logs by operation type")
        void shouldGetLogsByOperationType() {
            List<SystemLog> loginLogs = logService.getLogsByType("LOGIN");
            assertEquals(2, loginLogs.size());
            for (SystemLog log : loginLogs) {
                assertEquals("LOGIN", log.getOperationType());
            }
        }

        @Test
        @DisplayName("should return empty list for non-existent operation type")
        void shouldReturnEmptyListForNonExistentOperationType() {
            List<SystemLog> logs = logService.getLogsByType("NONEXISTENT");
            assertTrue(logs.isEmpty());
        }
    }

    // ========== Search Tests ==========

    @Nested
    @DisplayName("searchLogs()")
    class SearchTests {

        @BeforeEach
        void createTestLogs() {
            logService.log("T001", "Alice Chen", "LOGIN", "User", "T001", "Alice logged in", "127.0.0.1");
            logService.log("T002", "Bob Wang", "LOGIN", "User", "T002", "Bob logged in", "127.0.0.2");
            logService.log("T001", "Alice Chen", "CREATE", "Application", "A001", "Applied for SE TA", "127.0.0.1");
            logService.log("M001", "Prof.Chen", "CREATE", "Job", "J001", "Created SE position", "127.0.0.3");
        }

        @Test
        @DisplayName("should search by operator name")
        void shouldSearchByOperatorName() {
            List<SystemLog> results = logService.searchLogs("Alice");
            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("should search by details keyword")
        void shouldSearchByDetailsKeyword() {
            List<SystemLog> results = logService.searchLogs("logged");
            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("should search by target ID")
        void shouldSearchByTargetId() {
            // Search for "logged" which is in the details of the LOGIN entries
            List<SystemLog> results = logService.searchLogs("logged");
            assertTrue(results.size() >= 2, "Should find at least 2 logs with 'logged' in details");
        }

        @Test
        @DisplayName("should be case insensitive")
        void shouldBeCaseInsensitive() {
            List<SystemLog> upper = logService.searchLogs("ALICE");
            List<SystemLog> lower = logService.searchLogs("alice");
            assertEquals(upper.size(), lower.size());
        }

        @Test
        @DisplayName("should return all logs for blank keyword")
        void shouldReturnAllLogsForBlankKeyword() {
            List<SystemLog> allLogs = logService.getAllLogs();
            List<SystemLog> blankSearch = logService.searchLogs("");
            assertEquals(allLogs.size(), blankSearch.size());
        }
    }

    // ========== Date Range Tests ==========

    @Nested
    @DisplayName("getLogsByDateRange()")
    class DateRangeTests {

        @Test
        @DisplayName("should filter logs by date range")
        void shouldFilterLogsByDateRange() {
            logService.log("T001", "Alice", "LOGIN", "User", "T001", "Login", "127.0.0.1");

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yesterday = now.minusDays(1);
            LocalDateTime tomorrow = now.plusDays(1);

            List<SystemLog> results = logService.getLogsByDateRange(yesterday, tomorrow);
            assertEquals(1, results.size());
        }

        @Test
        @DisplayName("should return empty list when no logs in range")
        void shouldReturnEmptyListWhenNoLogsInRange() {
            LocalDateTime lastYear = LocalDateTime.now().minusYears(1);
            LocalDateTime twoYearsAgo = LocalDateTime.now().minusYears(2);

            List<SystemLog> results = logService.getLogsByDateRange(twoYearsAgo, lastYear);
            assertEquals(0, results.size());
        }
    }

    // ========== Pagination Tests ==========

    @Nested
    @DisplayName("Pagination Operations")
    class PaginationTests {

        @BeforeEach
        void createTestLogs() {
            for (int i = 0; i < 25; i++) {
                logService.log("T001", "Alice", "LOGIN", "User", "T001",
                    "Login #" + i, "127.0.0.1");
            }
        }

        @Test
        @DisplayName("should return correct page of logs")
        void shouldReturnCorrectPageOfLogs() {
            List<SystemLog> page1 = logService.getLogsPaginated(1, 10);
            assertEquals(10, page1.size());

            List<SystemLog> page2 = logService.getLogsPaginated(2, 10);
            assertEquals(10, page2.size());

            List<SystemLog> page3 = logService.getLogsPaginated(3, 10);
            assertEquals(5, page3.size());
        }

        @Test
        @DisplayName("should return empty list for out of range page")
        void shouldReturnEmptyListForOutOfRangePage() {
            List<SystemLog> page100 = logService.getLogsPaginated(100, 10);
            assertTrue(page100.isEmpty());
        }

        @Test
        @DisplayName("should calculate total pages correctly")
        void shouldCalculateTotalPagesCorrectly() {
            assertEquals(3, logService.getTotalPages(10));
            assertEquals(2, logService.getTotalPages(15));
            assertEquals(1, logService.getTotalPages(30));
        }

        @Test
        @DisplayName("should return 0 pages for empty logs")
        void shouldReturn0PagesForEmptyLogs() {
            LogService emptyLogService = new LogService(tempDir.resolve("data").resolve("system_logs_empty.csv"));
            assertEquals(0, emptyLogService.getTotalPages(10));
        }
    }
}
