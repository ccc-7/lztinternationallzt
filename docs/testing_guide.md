# TA Recruitment System - Testing Guide

This document describes how to run and understand the test suite for the TA Recruitment System (ta-webapp).

---

## 1. Test Environment Requirements

| Requirement | Version | Notes |
|------------|---------|-------|
| JDK | 17 or higher | Specified in `pom.xml` as `maven.compiler.source` and `maven.compiler.target` |
| Maven | 3.x | Tested with Maven Surefire Plugin 3.2.5 |
| Project Root | `lztinternationallzt/` | The directory containing `pom.xml` |

Verify your environment:

```bash
java -version   # Should show Java 17+
mvn -version    # Should show Maven 3.x
```

---

## 2. Running Tests

### 2.1 Run All Tests

```bash
cd lztinternationallzt
mvn clean test
```

This will:
1. Clean the `target/` directory
2. Compile the main source code
3. Compile the test source code
4. Execute all test classes
5. Generate a test report

### 2.2 Run a Single Test Class

```bash
# Run only UserServiceTest
mvn test -Dtest=UserServiceTest

# Run only JobServiceTest
mvn test -Dtest=JobServiceTest

# Run only ApplicationServiceTest
mvn test -Dtest=ApplicationServiceTest
```

### 2.3 Run a Single Test Method

```bash
# Run a specific test method in UserServiceTest
mvn test -Dtest=UserServiceTest#shouldAuthenticateUserWithCorrectCredentials

# Run a specific test method in SkillMatchServiceTest
mvn test -Dtest=SkillMatchServiceTest#shouldReturn100ForExactMatch
```

### 2.4 Run Tests with Verbose Output

```bash
# Show detailed output during test execution
mvn test -Dsurefire.useFile=false
```

---

## 3. Test Directory Structure

```
src/test/
├── java/
│   └── edu/bupt/ta/
│       ├── model/
│       │   ├── ApplicationTest.java         (10 tests)
│       │   ├── ApplicationWithJobTest.java   (14 tests)
│       │   ├── JobTest.java                 (12 tests)
│       │   └── UserTest.java                (9 tests)
│       ├── service/
│       │   ├── AdminServiceTest.java        (12 tests)
│       │   ├── ApplicationServiceTest.java  (21 tests)
│       │   ├── DashboardServiceTest.java    (10 tests)
│       │   ├── FileStorageUtilTest.java     (9 tests)
│       │   ├── JobServiceTest.java          (31 tests)
│       │   ├── LogServiceTest.java          (19 tests)
│       │   ├── SkillMatchServiceTest.java   (30 tests)
│       │   ├── UserServiceTest.java         (21 tests)
│       │   └── WorkloadServiceTest.java     (12 tests)
│       └── storage/
│           └── (placeholder for future storage tests)
└── resources/
    └── test-data/
        ├── users_test.csv          (sample user data for reference)
        ├── jobs_test.csv           (sample job data for reference)
        ├── applications_test.csv   (sample application data for reference)
        └── logs_test.csv           (sample log data for reference)
```

**Total: 219 tests** (as of last test run)

---

## 4. Test Classes Overview

### 4.1 UserServiceTest (21 tests)

**Location**: `src/test/java/edu/bupt/ta/service/UserServiceTest.java`

**Purpose**: Tests the `UserService` class which handles user authentication, registration, profile management, and application readiness.

**Key test scenarios**:
- `AuthenticationTests`: Login with correct credentials, wrong password, non-existent user
- `RegisterTests`: New user registration, duplicate username rejection, profile completeness
- `FindTests`: Find user by username and ID
- `ProfileUpdateTests`: Update user profile fields
- `PasswordTests`: Update password successfully
- `UserStatusToggleTests`: Toggle between ACTIVE/INACTIVE status
- `SummaryStatusTests`: Calculate profile completeness (INCOMPLETE/BASIC_COMPLETE/SUMMARY_COMPLETE)
- `ApplicationReadinessTests`: Check if user is eligible to apply for jobs

### 4.2 JobServiceTest (31 tests)

**Location**: `src/test/java/edu/bupt/ta/service/JobServiceTest.java`

**Purpose**: Tests the `JobService` class which handles job postings CRUD operations and the skill matching algorithm.

**Key test scenarios**:
- `MatchScoreTests`: Exact skill match, partial match, no match, skill aliases (js->javascript, ml->machine learning)
- `JobCrudTests`: Create job, update job, delete job, toggle status
- `JobQueryTests`: Get all jobs, open jobs only, filter by organiser, count operations
- `SkillNormalizationTests`: Normalize comma-separated skills to pipe-separated

### 4.3 ApplicationServiceTest (21 tests)

**Location**: `src/test/java/edu/bupt/ta/service/ApplicationServiceTest.java`

**Purpose**: Tests the `ApplicationService` class which handles the job application lifecycle.

**Key test scenarios**:
- `ApplyTests`: Submit application successfully, reject duplicate application, reject non-existent user/job
- `QueryTests`: Get applications by user, by job, count operations
- `StatusUpdateTests`: Update status to ACCEPTED/REJECTED/INTERVIEW, custom notes
- `FindByIdTests`: Find application by ID

### 4.4 WorkloadServiceTest (12 tests)

**Location**: `src/test/java/edu/bupt/ta/service/WorkloadServiceTest.java`

**Purpose**: Tests the workload calculation functionality in `AdminService.calculateUserWorkloads()`.

**Key test scenarios**:
- `WorkloadCalculationTests`: Count applications per TA, new applications reflected in workload
- `WorkloadLimitTests`: Identify TA with high application count, zero workload for TAs with no applications
- `EdgeCasesTests`: Empty user list handling, valid map structure, non-negative values

### 4.5 SkillMatchServiceTest (30 tests)

**Location**: `src/test/java/edu/bupt/ta/service/SkillMatchServiceTest.java`

**Purpose**: Tests the skill matching algorithm in `JobService.calculateMatchScore()`.

**Key test scenarios**:
- `ExactMatchTests`: Perfect skill match returns 100, superset skills give high score
- `PartialMatchTests`: Partial overlap returns partial score, no match returns 0
- `MissingSkillsTests`: Missing skills result in lower score
- `CaseAndWhitespaceTests`: Case-insensitive matching, whitespace handling, comma-to-pipe conversion
- `DuplicateSkillsTests`: Duplicate skills do not affect score
- `EmptyAndNullTests`: Null/empty/blank inputs handled gracefully without crashes
- `SkillAliasTests`: js->javascript, ts->typescript, ml->machine learning, springboot->spring
- `BoundaryAndEdgeCasesTests`: Score never exceeds 100, handles long skill names, special characters (C++)

### 4.6 FileStorageUtilTest (9 tests)

**Location**: `src/test/java/edu/bupt/ta/storage/FileStorageUtilTest.java`

**Purpose**: Tests the `FileStorageUtil` class which handles CSV file persistence.

**Key test scenarios**:
- `UserCrudTests`: Save and load users, handle multiple users, field preservation on round-trip
- `JobCrudTests`: Save and load jobs, handle job status
- `ApplicationCrudTests`: Save and load applications, handle all application statuses
- `UtilityTests`: Timestamp formatting, base directory path verification

### 4.7 Other Test Classes

| Class | Location | Tests | Purpose |
|-------|----------|-------|---------|
| `AdminServiceTest` | `service/` | 12 | Dashboard statistics, user workload, bulk user operations |
| `DashboardServiceTest` | `service/` | 10 | Best match messages, todo counts, matched jobs, pending counts |
| `LogServiceTest` | `service/` | 19 | Log creation, querying, search, pagination, date range filtering |
| `ApplicationTest` | `model/` | 10 | Application model constructor, fields, status management |
| `ApplicationWithJobTest` | `model/` | 14 | ApplicationWithJob view object, status conversion |
| `JobTest` | `model/` | 12 | Job model constructor, fields, status, match score |
| `UserTest` | `model/` | 9 | User model constructor, fields, role, year |

---

## 5. Test Data Isolation Mechanism

### 5.1 Primary Isolation: @TempDir

All service tests use JUnit 5's `@TempDir` annotation to create isolated temporary directories:

```java
@TempDir
Path tempDir;

@BeforeEach
void setUp() throws IOException {
    Path dataDir = tempDir.resolve("data");
    Files.createDirectories(dataDir);

    // Create FileStorageUtil with test directory
    storage = new FileStorageUtil(dataDir, null);
    service = new ServiceClass(storage);
}
```

Each test method gets its own temporary directory that is automatically cleaned up after the test completes.

### 5.2 FileStorageUtil Path Injection

The `FileStorageUtil` class supports constructor injection for testing:

```java
// Production: uses default path
FileStorageUtil storage = new FileStorageUtil();

// Testing: uses injected path
FileStorageUtil storage = new FileStorageUtil(testDataDir, null);
```

### 5.3 Service Constructor Injection

All service classes support dependency injection:

```java
// Production: uses default storage
UserService userService = new UserService();

// Testing: uses shared storage instance
UserService userService = new UserService(storage);
```

### 5.4 Avoiding Pollution of Production Data

| Mechanism | Protection |
|-----------|------------|
| `@TempDir` | Each test method gets a fresh temporary directory |
| Constructor injection | Services use injected storage, not hardcoded paths |
| No shared state | Each test class creates its own service instances |
| Automatic cleanup | JUnit automatically deletes temporary directories after each test |

### 5.5 test-data Directory

The `src/test/resources/test-data/` directory contains sample CSV files for reference only. These files are **not** used by the tests at runtime. Tests use:
1. Seeded data from `FileStorageUtil.ensureDefaultUsers/ensureDefaultJobs`
2. Dynamically created test data within each test method

---

## 6. Expected Test Output

### 6.1 Successful Test Run

```
[INFO] Tests run: 219, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### 6.2 Sample Test Output for a Single Test Class

```
[INFO] Tests run: 21, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.284 s
[INFO]   - in edu.bupt.ta.service.UserServiceTest$AuthenticationTests
[INFO]   - in edu.bupt.ta.service.UserServiceTest$RegisterTests
[INFO]   - in edu.bupt.ta.service.UserServiceTest$FindTests
[INFO]   - in edu.bupt.ta.service.UserServiceTest$ProfileUpdateTests
[INFO]   - in edu.bupt.ta.service.UserServiceTest$PasswordTests
[INFO]   - in edu.bupt.ta.service.UserServiceTest$UserStatusToggleTests
[INFO]   - in edu.bupt.ta.service.UserServiceTest$SummaryStatusTests
[INFO]   - in edu.bupt.ta.service.UserServiceTest$ApplicationReadinessTests
```

### 6.3 Running Specific Tests with Output

```bash
mvn test -Dtest=SkillMatchServiceTest -Dsurefire.useFile=false

# Expected output:
# SkillMatchServiceTest$ExactMatchTests > shouldReturn100ForExactMatch PASSED
# SkillMatchServiceTest$ExactMatchTests > shouldReturn100ForSingleExactMatch PASSED
# SkillMatchServiceTest$PartialMatchTests > shouldReturnPartialScoreForPartialMatch PASSED
# ...
```

### 6.4 Failed Test Example

If a test fails, you will see:

```
[ERROR] Tests run: 218, Failures: 1, Errors: 0, Skipped: 0
[ERROR] Failures: 1
[ERROR]   - edu.bupt.ta.service.UserServiceTest.authenticate() >
[ERROR]     shouldAuthenticateUserWithCorrectCredentials
[ERROR]       Expected: <User@...> but was: <null>
[INFO] BUILD FAILURE
```

---

## 7. Troubleshooting

### 7.1 Tests Fail Due to File Lock

If tests fail with "file is being used by another process":
- Ensure no other Java processes are holding file locks
- Run `mvn clean test` to ensure clean state

### 7.2 Tests Pass Locally but Fail on CI

- Ensure CI environment has JDK 17
- Check that `mvn clean test` is used (not `mvn test`)
- Verify no hardcoded paths are used

### 7.3 Slow Test Execution

- Use `-Dparallel=none` to disable parallel execution if tests interfere with each other
- Use `-DforkCount=1` to limit fork count

---

## 8. Adding New Tests

### 8.1 Test Class Template

```java
package edu.bupt.ta.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class NewServiceTest {

    @TempDir
    Path tempDir;

    private NewService newService;

    @BeforeEach
    void setUp() throws IOException {
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);

        edu.bupt.ta.storage.FileStorageUtil storage =
            new edu.bupt.ta.storage.FileStorageUtil(dataDir, null);

        newService = new NewService(storage);
    }

    @Nested
    @DisplayName("MethodName")
    class MethodNameTests {

        @Test
        @DisplayName("should do something specific")
        void shouldDoSomethingSpecific() {
            // Arrange
            // Act
            // Assert
        }
    }
}
```

### 8.2 Test Naming Convention

Use descriptive test method names:
- `shouldAuthenticateUserWithCorrectCredentials`
- `shouldRejectDuplicateApplication`
- `shouldCalculateWorkloadCorrectly`
- `shouldReturn0ForNullRequiredSkills`

### 8.3 Test Structure

Follow the AAA pattern:
1. **Arrange**: Set up test data
2. **Act**: Call the method being tested
3. **Assert**: Verify the results

---

## 9. Test Coverage Summary

| Category | Classes | Tests |
|----------|---------|-------|
| Model Tests | 4 | 45 |
| Service Tests | 9 | 174 |
| **Total** | **13** | **219** |

### Coverage by Feature

| Feature | Test Class |
|---------|------------|
| User Authentication | `UserServiceTest` |
| User Registration | `UserServiceTest` |
| Job CRUD | `JobServiceTest` |
| Skill Matching | `SkillMatchServiceTest`, `JobServiceTest` |
| Application Submission | `ApplicationServiceTest` |
| Application Status | `ApplicationServiceTest` |
| Workload Calculation | `WorkloadServiceTest` |
| Dashboard Statistics | `AdminServiceTest`, `DashboardServiceTest` |
| Audit Logging | `LogServiceTest` |
| Data Persistence | `FileStorageUtilTest` |
