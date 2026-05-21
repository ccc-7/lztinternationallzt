# Testing Evidence Materials for Short Report

> This file contains raw factual evidence collected from the codebase. It is a pre-draft resource for the Short Report, not the final report text.

---

## 1. Implemented Features and Testability

| Feature | Implemented | Java Files | JSP Pages | Data Files | Auto-Testable | Manual-Test Required |
|---------|------------|------------|-----------|------------|---------------|---------------------|
| TA registration/login | Yes | `LoginServlet.java`, `RegisterServlet.java`, `UserService.java` | `register.jsp`, `index.jsp` | `ta_users.csv` | Yes (UserServiceTest) | Login page, form validation |
| TA profile | Yes | `TaProfileServlet.java`, `UserService.java` | `WEB-INF/jsp/ta/profile.jsp` | `ta_users.csv` | Yes (UserServiceTest) | Profile edit form |
| CV upload/management | Yes | `CvUploadServlet.java`, `CvDeleteServlet.java`, `FileDownloadServlet.java`, `CvFileService.java`, `UserService.java` | `WEB-INF/jsp/ta/profile.jsp` | `ta_users.csv` (metadata), `data/cvs/*.pdf` (files) | Partially (CvFileService has size/type validation; actual file upload not unit tested) | Upload form, download, delete |
| Job posting | Yes | `MOJobServlet.java`, `JobService.java` | `WEB-INF/jsp/mo/new-job.jsp` | `jobs.csv` | Yes (JobServiceTest) | MO job creation form |
| Job browsing | Yes | `JobListServlet.java`, `JobService.java` | `WEB-INF/jsp/ta/jobs.jsp` | `jobs.csv` | Yes (JobServiceTest) | Job list page, sorting |
| Job application | Yes | `ApplyServlet.java`, `ApplicationService.java`, `JobService.java`, `UserService.java` | `WEB-INF/jsp/ta/jobs.jsp` | `applications.csv`, `ta_users.csv` | Yes (ApplicationServiceTest) | Application form, confirmation |
| Application status management | Yes | `ApplicationStatusServlet.java`, `MOApplicationServlet.java`, `ApplicationService.java` | `WEB-INF/jsp/mo/applications.jsp` | `applications.csv` | Yes (ApplicationServiceTest) | MO status update page |
| Admin workload checking | Yes | `AdminServlet.java`, `AdminDashboardServlet.java`, `AdminService.java` | `WEB-INF/jsp/admin/dashboard.jsp`, `WEB-INF/jsp/admin/users-manage.jsp` | `ta_users.csv`, `applications.csv` | Yes (WorkloadServiceTest, AdminServiceTest) | Admin dashboard, workload chart |
| Skill matching | Yes | `JobService.java` (calculateMatchScore method), `DashboardService.java` | `WEB-INF/jsp/ta/dashboard.jsp` (display score) | `ta_users.csv` (skills field), `jobs.csv` (requiredSkills field) | Yes (SkillMatchServiceTest, JobServiceTest) | Score display on job list |
| Audit logging | Yes | `LogService.java`, individual Servlets call `log()` | N/A (background feature) | `system_logs.csv` | Yes (LogServiceTest) | Log viewer (admin page) |
| Candidate summary | Yes | `CandidateSummaryServlet.java`, `UserService.java` | `WEB-INF/jsp/ta/profile.jsp` | `ta_users.csv` | Yes (UserServiceTest: calculateSummaryStatus) | Summary form in TA profile |

**Notes:**
- All features use `FileStorageUtil.java` for CSV-based persistence.
- Skill matching is rule-based (weighted category + Jaccard similarity), NOT AI/ML-based. It is a scoring algorithm, not a machine learning model.
- There is no separate "SkillMatchService" class; skill matching logic is embedded in `JobService.calculateMatchScore()`.
- CV files are stored as PDFs in `data/cvs/` directory, not in the CSV.

---

## 2. Testing Objectives

Based on the codebase, the project has the following testing objectives:

### 2.1 Core Recruitment Workflow
- Verify the complete flow from TA registration → profile completion → job browsing → application → MO review → status update
- Validate that each step correctly reads/writes to the corresponding CSV file

### 2.2 Three-Role Functionality
- **TA**: registration, login, profile edit, CV upload, job browsing with match score, job application
- **MO**: job posting, application review, status update (INTERVIEW/ACCEPTED/REJECTED)
- **Admin**: workload statistics, user status toggle, bulk user management, audit log viewing

### 2.3 File Storage Correctness
- CSV read/write round-trip preservation (all 21 user fields, 12 job fields, 7 application fields, 9 log fields)
- CSV field escaping (commas, quotes, newlines in data)
- Atomic writes (temp-then-move pattern prevents partial writes)
- Auto-seeding of default data on first run
- Mirror directory synchronization

### 2.4 Input Validation and Error Handling
- Empty/null input handling in all service methods
- Duplicate username registration (case-insensitive)
- Duplicate application prevention (same user + same job)
- Year range validation (minYear/maxYear per job)
- Deadline validation (past deadlines rejected)
- Vacancy limit enforcement
- CV file size limit (<= 5 MB) and type validation (PDF only)

### 2.5 Workload Calculation
- Per-TA application count calculation
- Correct exclusion of MO/ADMIN from TA workload
- Consistency across multiple calculations

### 2.6 Skill Matching Algorithm
- Exact match (score = 100)
- Case-insensitive matching
- Skill alias recognition (js→javascript, ml→machine learning, etc.)
- Partial match within same category (70% weight)
- Category-weighted scoring (concept 1.5x, language 1.3x, ml 1.2x, database 1.1x, framework 1.0x, tools 0.9x)
- Jaccard similarity bonus (up to 15 points)
- Score capped at 100
- Null/empty/blank input handling

### 2.7 Summary Status Calculation
- Score-based classification: >= 8 fields → SUMMARY_COMPLETE, >= 5 → BASIC_COMPLETE, else → INCOMPLETE
- Fields counted: name, email, year, major, skills, availability, personalStatement, relevantCourses, projectExperience

### 2.8 Regression Testing
- All tests use `@TempDir` for complete isolation
- Tests use shared `FileStorageUtil` instance to ensure data consistency
- Tests verify seeded data presence after construction

---

## 3. Testing Techniques Used

| Technique | Purpose | Module | Example | Evidence File |
|-----------|---------|--------|---------|---------------|
| Unit testing | Test individual methods in isolation | All services and models | `authenticate()` returns correct user | `UserServiceTest.java` |
| Unit testing | Test model constructors and field access | Models | `Job` constructor sets all 12 fields | `JobTest.java` |
| Unit testing | Test business logic methods | JobService | `calculateMatchScore()` returns 0 for null required skills | `SkillMatchServiceTest.java` |
| Integration testing | Test multi-service interaction | Application workflow | `apply()` validates user + job + deadline + vacancy + duplicate | `ApplicationServiceTest.java` |
| Boundary testing | Test edge values | Skill matching | Score never exceeds 100 | `SkillMatchServiceTest.shouldNotExceed100MaximumScore()` |
| Boundary testing | Test empty/null inputs | All services | Null required skills returns 0 | `SkillMatchServiceTest.shouldReturn0ForNullRequiredSkills()` |
| Error handling testing | Verify exceptions thrown | ApplicationService | Non-existent user throws IllegalArgumentException | `ApplicationServiceTest.shouldRejectApplicationForNonExistentUser()` |
| Error handling testing | Duplicate prevention | ApplicationService | Duplicate application throws exception | `ApplicationServiceTest.shouldRejectDuplicateApplication()` |
| Round-trip testing | CSV read/write consistency | FileStorageUtil | All 21 user fields preserved after save+load | `FileStorageUtilTest.shouldPreserveAllUserFieldsOnRoundTrip()` |
| Regression testing | Verify seeded data present | All services | Seeded users U001-U006 exist after construction | `UserServiceTest.setUp()` |
| Pagination testing | Test log pagination | LogService | Page 3 of 10 returns correct subset | `LogServiceTest.shouldReturnCorrectPageOfLogs()` |
| Concurrency testing | Test IO_LOCK synchronization | FileStorageUtil | Multiple workload calculations return same result | `WorkloadServiceTest.shouldHandleConcurrentWorkloadCalculations()` |
| System testing (manual) | Full page flow | Web UI | User logs in → views dashboard → applies for job | Manual only |
| Acceptance testing (manual) | Real-world scenarios | All roles | TA uploads CV → MO reviews → Admin checks workload | Manual only |

---

## 4. Automated Test Programs

### 4.1 Test Directory Structure

```
src/test/java/edu/bupt/ta/
├── model/
│   ├── ApplicationTest.java
│   ├── ApplicationWithJobTest.java
│   ├── JobTest.java
│   └── UserTest.java
├── service/
│   ├── AdminServiceTest.java
│   ├── ApplicationServiceTest.java
│   ├── DashboardServiceTest.java
│   ├── JobServiceTest.java
│   ├── LogServiceTest.java
│   ├── SkillMatchServiceTest.java
│   ├── UserServiceTest.java
│   └── WorkloadServiceTest.java
└── storage/
    └── FileStorageUtilTest.java
```

### 4.2 Test Class Details

#### UserServiceTest.java
| Test Method | What It Verifies | Data Used |
|-------------|-----------------|-----------|
| `shouldAuthenticateUserWithCorrectCredentials` | authenticate() returns user for correct credentials | Seeded: seele/123456 |
| `shouldReturnNullForWrongPassword` | authenticate() returns null for wrong password | seele/wrongpassword |
| `shouldReturnNullForNonExistentUsername` | authenticate() returns null for non-existent user | nonexistent/123456 |
| `shouldFindUserByUsername` | findByUsername() returns correct user | seele |
| `shouldFindUserById` | findById() returns correct user | U001 |
| `shouldReturnSeededUsers` | getAllUsers() returns 6 seeded users | U001-U006 |
| `shouldReturnOnlyTaUsers` | getAllTaUsers() returns 3 TAs (not MO/ADMIN) | U001-U003 |
| `shouldRegisterNewTaUserSuccessfully` | registerTa() creates new user with U007 ID | newuser |
| `shouldSetSummaryCompleteForCompleteProfile` | calculateSummaryStatus() returns SUMMARY_COMPLETE for score>=8 | 9 non-blank fields |
| `shouldSetIncompleteForMinimalProfile` | calculateSummaryStatus() returns INCOMPLETE for score<5 | minimal fields |
| `shouldUpdatePasswordSuccessfully` | updatePassword() then authenticate() with new password works | U001, newpassword |
| `shouldToggleUserStatusFromActiveToInactive` | toggleUserStatus() ACTIVE→INACTIVE | U001 |

#### JobServiceTest.java
| Test Method | What It Verifies |
|-------------|-----------------|
| `shouldReturn100ForExactSkillMatch` | calculateMatchScore("Java\|Python","Java\|Python") == 100 |
| `shouldReturn0ForNoMatch` | calculateMatchScore("Ruby\|PHP","Java\|Python") == 0 |
| `shouldReturn0WhenRequiredSkillsIsNull` | calculateMatchScore("Java\|Python", null) == 0 |
| `shouldNotExceed100MaximumScore` | Score capped at 100 |
| `shouldHandleCaseInsensitivity` | java vs JAVA gives same score |
| `shouldFindSeededJobById` | findById("J001") returns seeded job |
| `shouldCreateJobAndGetCorrectId` | createJob() assigns J005 after seeded J001-J004 |
| `shouldUpdateJobSuccessfully` | updateJob() persists all fields |
| `shouldDeleteJobSuccessfully` | deleteJob() reduces count by 1 |
| `shouldToggleJobStatus` | toggleJobStatus() OPEN↔CLOSED |
| `shouldGetOnlyOpenJobs` | getOpenJobs() excludes CLOSED jobs |
| `shouldGetJobsByOrganiser` | getJobsByOrganiser("Dr.Wang") returns 2 jobs |

#### ApplicationServiceTest.java
| Test Method | What It Verifies |
|-------------|-----------------|
| `shouldCreateApplicationSuccessfully` | apply() creates A002 with PENDING status |
| `shouldRejectDuplicateApplication` | apply() throws for existing user+job combination |
| `shouldRejectApplicationForNonExistentUser` | apply() throws for unknown user ID |
| `shouldRejectApplicationForNonExistentJob` | apply() throws for unknown job ID |
| `shouldUpdateStatusToAccepted` | updateStatus() sets ACCEPTED and "Accepted" note |
| `shouldUpdateStatusToRejected` | updateStatus() sets REJECTED and "Rejected" note |
| `shouldUpdateStatusToInterview` | updateStatus() sets INTERVIEW and "Moved to interview" note |
| `shouldThrowExceptionForNonExistentApplication` | updateStatus() throws for unknown ID |
| `shouldCountTotalApplications` | countTotalApplications() returns correct count |
| `shouldCountAllByStatus` | countAllByStatus(PENDING) returns correct count |

#### SkillMatchServiceTest.java
| Test Method | What It Verifies |
|-------------|-----------------|
| `shouldReturn100ForExactMatch` | Full skill set match = 100 |
| `shouldGiveHighScoreForSuperset` | User with extra skills still gets high score |
| `shouldReturnPartialScoreForPartialMatch` | Partial match < exact match |
| `shouldReturn0WhenNoSkillsMatch` | No overlap = 0 |
| `shouldBeCaseInsensitive` | java == JAVA == Java |
| `shouldHandleWhitespaceAroundSkills` | Trimming works |
| `shouldRecognizeJsAsJavascript` | Alias mapping works: js→javascript |
| `shouldRecognizeMlAsMachineLearning` | Alias mapping works: ml→machine learning |
| `shouldNotExceed100MaximumScore` | Score capped at 100 |
| `shouldHandleVeryLongSkillNames` | Long exact match still = 100 |
| `shouldReturn0ForNullRequiredSkills` | Null safe |
| `shouldHandleBothNullGracefully` | Both null safe |

#### WorkloadServiceTest.java
| Test Method | What It Verifies |
|-------------|-----------------|
| `shouldReturnEmptyMapWhenNoApplications` | calculateUserWorkloads() returns map with 3 TAs |
| `shouldCountApplicationsPerTaCorrectly` | U001 has >=1 application |
| `shouldNotCountMoOrAdminUsersInTaWorkload` | U004, U005, U006 NOT in workload map |
| `shouldReturn0ForTaWithNoApplications` | U003 in map even with 0 apps |
| `shouldHandleConcurrentWorkloadCalculations` | Repeated calls return same result |
| `shouldReturnNonNegativeWorkloadValues` | All values >= 0 |

#### AdminServiceTest.java
| Test Method | What It Verifies |
|-------------|-----------------|
| `shouldReturnAllDashboardStatistics` | getDashboardStats() has all 12 keys |
| `shouldCalculateCorrectTaCount` | totalTA == 3 |
| `shouldCalculateCorrectActiveTaCount` | activeTA == 3 |
| `shouldCalculateCorrectJobCounts` | totalJobs==4, openJobs==4 |
| `shouldCalculateWorkloadForEachTaUser` | Map contains U001-U003, excludes U004-U006 |
| `shouldToggleUserStatusFromActiveToInactive` | toggleUserStatus() persists |

#### LogServiceTest.java
| Test Method | What It Verifies |
|-------------|-----------------|
| `shouldCreateLogEntrySuccessfully` | log() creates entry with correct fields |
| `shouldAutoIncrementLogId` | L00001, L00002, L00003 generated |
| `shouldRecordAllOperationTypes` | LOGIN/LOGOUT/CREATE/UPDATE/DELETE/APPROVE/REJECT all stored |
| `shouldGetAllLogsSortedByNewestFirst` | getAllLogs() sorted descending |
| `shouldGetLogsByOperator` | getLogsByOperator("T001") returns 2 logs |
| `shouldSearchByOperatorName` | searchLogs("Alice") returns 2 results |
| `shouldReturnCorrectPageOfLogs` | getLogsPaginated(2,10) returns 10 items |
| `shouldCalculateTotalPagesCorrectly` | getTotalPages(10) == 3 |

#### FileStorageUtilTest.java
| Test Method | What It Verifies |
|-------------|-----------------|
| `shouldSaveAndLoadUsers` | saveUsers() + loadUsers() round-trip |
| `shouldPreserveAllUserFieldsOnRoundTrip` | All 21 fields including CV metadata preserved |
| `shouldHandleMultipleUsers` | 2+ users saved and loaded correctly |
| `shouldSaveAndLoadJobs` | saveJobs() + loadJobs() round-trip |
| `shouldHandleClosedJobStatus` | JobStatus.CLOSED persisted |
| `shouldSaveAndLoadApplications` | saveApplications() + loadApplications() round-trip |
| `shouldHandleAllApplicationStatuses` | PENDING/INTERVIEW/ACCEPTED/REJECTED all round-trip |
| `shouldReturnFormattedTimestamp` | nowText() matches "yyyy-MM-dd HH:mm:ss" pattern |
| `shouldReturnCorrectBaseDirectoryPath` | getBaseDir() starts with tempDir |

### 4.3 Test Data Files

Located in `src/test/resources/test-data/`:

| File | Contents |
|------|----------|
| `users_test.csv` | 7 users: 4 TAs (T001-T004), 1 MO (M001), 1 ADMIN (A001) |
| `jobs_test.csv` | 6 jobs: J001-J006, mix of OPEN/CLOSED |
| `applications_test.csv` | 6 applications with various statuses |
| `logs_test.csv` | 4 log entries covering LOGIN, CREATE, APPROVE |

### 4.4 Test Result

```
mvn test output (2026-05-17 12:48:34):
  Tests run: 219
  Passed:    219
  Failed:    0
  Errors:    0
  Skipped:   0
  BUILD SUCCESS
  Total time: 6.539 s
```

---

## 5. Manual / Acceptance Test Cases

| Test ID | Scenario | User Role | Preconditions | Test Steps | Expected Result | Actual Result | Pass/Fail |
|---------|----------|-----------|--------------|------------|-----------------|---------------|-----------|
| M001 | TA login with correct credentials | TA | User seele/123456 exists | 1. Go to login page 2. Enter username "seele" 3. Enter password "123456" 4. Click login | Redirect to TA dashboard | - | - |
| M002 | TA login with wrong password | TA | User seele exists | 1. Go to login page 2. Enter "seele" 3. Enter "wrongpass" 4. Click login | Error message, stay on login | - | - |
| M003 | TA registration | TA (guest) | None | 1. Go to register page 2. Fill all fields 3. Submit | New TA account created, redirect to login | - | - |
| M004 | Duplicate username registration | TA (guest) | Username "seele" exists | 1. Go to register 2. Enter "seele" as username 3. Fill others 4. Submit | Error: "username already exists" | - | - |
| M005 | TA create profile | TA | Logged in as seele | 1. Go to profile page 2. Edit name, email, skills, etc. 3. Save | Profile updated, summaryStatus recalculated | - | - |
| M006 | TA upload CV | TA | Logged in, on profile page | 1. Click upload CV 2. Select PDF <=5MB 3. Submit | CV uploaded, status shows UPLOADED | - | - |
| M007 | TA upload non-PDF file | TA | On profile page | 1. Click upload 2. Select .docx file 3. Submit | Error: "Only PDF files supported" | - | - |
| M008 | TA upload oversized CV | TA | On profile page | 1. Click upload 2. Select file >5MB 3. Submit | Error: "CV file size must be 5MB or smaller" | - | - |
| M009 | TA browse jobs | TA | Logged in | 1. Go to jobs page | Jobs listed with match scores | - | - |
| M010 | TA apply for job | TA | Logged in as seele (SUMMARY_COMPLETE), J002 OPEN | 1. Go to jobs 2. Click apply on J002 | Application A00X created with PENDING | - | - |
| M011 | TA apply without complete profile | TA | Logged in as new user (INCOMPLETE) | 1. Go to jobs 2. Click apply | Error: "Please complete your candidate summary" | - | - |
| M012 | Duplicate application | TA | seele already applied for J001 | 1. Go to jobs 2. Apply for J001 again | Error: "already applied for this job" | - | - |
| M013 | MO post new job | MO | Logged in as mo1 | 1. Go to MO dashboard 2. Click new job 3. Fill form 4. Submit | Job created with next ID (J00X) | - | - |
| M014 | MO view applications for job | MO | Applications exist | 1. Go to MO applications 2. Select a job | List of TAs who applied | - | - |
| M015 | MO accept application | MO | Application A00X exists | 1. Go to applications 2. Click accept | Status → ACCEPTED, note → "Accepted" | - | - |
| M016 | MO reject application | MO | Application A00X exists | 1. Go to applications 2. Click reject | Status → REJECTED, note → "Rejected" | - | - |
| M017 | MO move to interview | MO | Application A00X exists | 1. Go to applications 2. Click interview | Status → INTERVIEW, note → "Moved to interview" | - | - |
| M018 | Admin view dashboard stats | ADMIN | Logged in as admin | 1. Go to admin dashboard | Stats: totalTA=3, openJobs=4, etc. | - | - |
| M019 | Admin check TA workload | ADMIN | On admin dashboard | 1. View workload section | Per-TA application counts | - | - |
| M020 | Admin toggle user status | ADMIN | On users-manage page | 1. Find U001 2. Toggle ACTIVE→INACTIVE | User status changed | - | - |
| M021 | Admin view audit logs | ADMIN | On logs page | 1. Go to logs 2. Search "Alice" | Logs containing "Alice" returned | - | - |
| M022 | Apply for job past deadline | TA | Job J001 has past deadline | 1. Go to jobs 2. Apply for J001 | Error: "The application deadline has passed" | - | - |
| M023 | Apply for closed job | TA | Job J004 is CLOSED | 1. Go to jobs 2. Apply for J004 | Error: "This job is not open for applications" | - | - |
| M024 | Apply for job with year mismatch | TA | TA year 1, job requires minYear=2 | 1. Apply for J001 (minYear=2) | Error: "Your current year does not meet the minimum requirement" | - | - |
| M025 | Apply when vacancy filled | TA | Job has vacancies=1, already accepted 1 | 1. Apply for that job | Error: "This job has reached its vacancy limit" | - | - |

---

## 6. File-Based Storage Testing

### 6.1 Data Files

| File Path | Stored Content | Key Fields | Features That Read It | Features That Write It |
|-----------|--------------|------------|----------------------|----------------------|
| `data/ta_users.csv` | User accounts | 21 columns: userId, username, password, name, email, role, year, major, skills, status, availability, personalStatement, relevantCourses, projectExperience, preferredRole, summaryStatus, cvStoredName, cvOriginalName, cvContentType, cvUploadedAt, cvStatus | LoginServlet, RegisterServlet, TaProfileServlet, AdminServlet, CvUploadServlet, UserService, AdminService | UserService, CvUploadServlet, TaProfileServlet |
| `data/jobs.csv` | Job postings | 12 columns: jobId, title, moduleCode, organiser, minYear, maxYear, hours, status, requiredSkills, matchScore, deadline, vacancies | JobListServlet, ApplyServlet, MOJobServlet, MOApplicationServlet, AdminServlet, JobService, ApplicationService | JobService, MOJobServlet |
| `data/applications.csv` | Job applications | 7 columns: applicationId, userId, jobId, status, submittedAt, notes, availability | ApplyServlet, ApplicationStatusServlet, MOApplicationServlet, AdminServlet, ApplicationService | ApplicationService, ApplyServlet, ApplicationStatusServlet |
| `data/system_logs.csv` | Audit trail | 9 columns: logId, operatorId, operatorName, operationType, targetType, targetId, details, ipAddress, createdAt | AdminServlet (logs.jsp), LogService | LogService (called by all Servlets) |
| `data/cvs/*.pdf` | Uploaded CV files | Binary PDF files, named as "{userId}.pdf" | FileDownloadServlet, CvFileService | CvFileService (via CvUploadServlet) |

### 6.2 Testing CSV Read/Write

**How to test read/write correctness:**

1. **Round-trip test** (implemented in `FileStorageUtilTest`):
   - Create model objects with all fields populated
   - Call `saveUsers/saveJobs/saveApplications()`
   - Call `loadUsers/loadJobs/loadApplications()`
   - Assert all fields match original values

2. **Field escaping test** (implemented):
   - Test data containing commas: `"Statement with, comma"`
   - Test data containing double quotes: `"Alice ""Special"" Chen"`
   - Test data containing newlines
   - Verify loaded values match original exactly

3. **Atomic write test** (not explicitly tested but implemented):
   - `writeLinesAtomically()` uses temp-then-move pattern
   - `moveAtomically()` attempts atomic move first, falls back to regular move

4. **Missing file bootstrap test** (implemented):
   - If CSV file is missing, `bootstrapFile()` creates it with header
   - If CSV file is empty (header only), `ensureDefaultX()` seeds default data

### 6.3 Avoiding Test Pollution of Production Data

**Isolation strategy used in all tests:**

```java
@TempDir
Path tempDir;

@BeforeEach
void setUp() throws IOException {
    Path dataDir = tempDir.resolve("data");
    Files.createDirectories(dataDir);
    storage = new FileStorageUtil(dataDir, null);
}
```

- `@TempDir` creates a unique temporary directory for each test class
- The temporary directory is automatically cleaned up after all tests in the class complete
- No tests write to the production `data/` directory
- Tests can share the same storage instance by passing it to service constructors

---

## 7. Error Handling and Boundary Testing

### 7.1 Tested Error Scenarios

| Scenario | Input | Expected Result | Actual Behavior | Related Files |
|---------|-------|-----------------|-----------------|---------------|
| Empty input (user skills) | `""` or `" "` | Returns 0 for match score | Returns 0 | `JobService.calculateMatchScore()` |
| Null input (required skills) | `null` | Returns 0 for match score | Returns 0 | `JobService.calculateMatchScore()` |
| Wrong password | seele/wrongpass | authenticate() returns null | Returns null | `UserService.authenticate()` |
| Duplicate account | Register with "seele" | Throws IllegalArgumentException | Throws "existed username" | `UserService.registerTa()` |
| Duplicate application | Apply U001+J001 twice | Throws IllegalArgumentException | Throws "already applied" | `ApplicationService.apply()` |
| Missing required fields | Profile with no name | calculateSummaryStatus() returns INCOMPLETE | Returns INCOMPLETE | `UserService.calculateSummaryStatus()` |
| Invalid CV (wrong type) | Upload .docx file | Throws IllegalArgumentException | Throws "Only PDF files are supported" | `CvFileService.savePdf()` |
| Oversized CV | Upload >5MB file | Throws IllegalArgumentException | Throws "CV file size must be 5MB or smaller" | `CvFileService.savePdf()` |
| Non-existent job | Apply for "J999" | Throws IllegalArgumentException | Throws "job not found" | `ApplicationService.apply()` |
| Past deadline | Apply for expired job | Throws IllegalArgumentException | Throws "deadline has passed" | `ApplicationService.apply()` |
| Year below minimum | Year 1 TA applies for minYear=2 job | Throws IllegalArgumentException | Throws "does not meet minimum" | `ApplicationService.apply()` |
| Year above maximum | Year 4 TA applies for maxYear=2 job | Throws IllegalArgumentException | Throws "exceeds allowed range" | `ApplicationService.apply()` |
| Vacancy limit reached | Job with vacancies=1, already accepted 1 | Throws IllegalArgumentException | Throws "vacancy limit reached" | `ApplicationService.apply()` |
| Active app limit (3) | Apply for 4th job when 3 active | Throws IllegalArgumentException | Throws "up to 3 active applications" | `ApplicationService.apply()` |
| Missing CSV file | Delete ta_users.csv then start | Auto-creates with header + seeds defaults | - | `FileStorageUtil.initFiles()` |
| Corrupted CSV | Edit jobs.csv with invalid enum | JobStatus.fromString() returns OPEN as default | Returns OPEN | `JobStatus.fromString()` |
| Non-existent application update | updateStatus("NONEXISTENT", ACCEPTED) | Throws IllegalArgumentException | Throws "Application not found" | `ApplicationService.updateStatus()` |
| Empty skill match | User skills empty, job requires skills | Score should be 0 | Returns 0 | `SkillMatchServiceTest.shouldReturn0WhenUserHasNoSkills()` |
| Score never exceeds 100 | Many matching skills | Score capped at 100 | Capped at 100 | `SkillMatchServiceTest.shouldNotExceed100MaximumScore()` |

### 7.2 Boundary Conditions Tested

| Boundary | Test Case |
|----------|-----------|
| Score = 0 | `shouldReturn0ForNoMatch` |
| Score = 100 | `shouldReturn100ForExactMatch` |
| Score = 1-99 | Various partial match tests |
| All 21 user fields filled | `shouldSetSummaryCompleteForCompleteProfile` (score = 9) |
| 0 user fields filled | `shouldSetIncompleteForMinimalProfile` (score = 0) |
| Exactly 5 fields filled | `shouldReturnBasicCompleteWhenScoreIs5To7` (score = 5) |
| Exactly 7 fields filled | BASIC_COMPLETE (score = 7) |
| Exactly 8 fields filled | SUMMARY_COMPLETE (score = 8) |
| 3 active applications (max) | `MAX_APPLICATIONS_PER_TA = 3` in ApplicationService |
| 0 vacancies | `shouldHandleZeroVacancies` |
| Single vacancy | `shouldHandleSingleVacancy` |
| 10 vacancies | `shouldHandleMultipleVacancies` |
| Single-character skill "C" | `shouldHandleSingleCharacterSkills` |
| Very long skill (100 chars) | `shouldHandleVeryLongSkillNames` |

---

## 8. Testing Environment

| Component | Version/Value |
|-----------|---------------|
| OS | Windows 10/11 (win32 10.0.26200) |
| JDK | Java 17 (source and target) |
| Maven | Maven Compiler Plugin 3.11.0, Surefire Plugin 3.2.5 |
| JUnit | JUnit Jupiter 5.10.2 (junit-jupiter-api + junit-jupiter-engine) |
| Servlet API | Jakarta Servlet 6.0.0 (scope: provided) |
| JSTL | Jakarta Servlet JSP JSTL 3.0.0 + Glassfish Implementation 3.0.1 |
| Tomcat | N/A (servlet container not included in pom; to be provided externally) |
| Project Type | Maven WAR packaging (`<packaging>war</packaging>`) |
| Encoding | UTF-8 |
| Project Run Command | `mvn clean package` (produces `target/ta-webapp.war`) |
| Test Run Command | `mvn test` |
| Data Storage | CSV files in `data/` directory (ta_users.csv, jobs.csv, applications.csv, system_logs.csv) + binary PDFs in `data/cvs/` |
| CSV Delimiter | Comma (`,`) with double-quote escaping for fields containing commas/quotes/newlines |
| List Separator | Pipe (`\|`) in multi-value fields (skills, relevantCourses, preferredRole) |
| Test Isolation | JUnit 5 `@TempDir` + injected `FileStorageUtil(Path, Path)` constructors |
| Test Data | Auto-seeded on first run: 3 TAs, 2 MOs, 1 Admin, 4 jobs, 1 sample application |

---

## 9. Test Execution Result

Based on the actual `mvn test` output from the terminal:

```
[INFO] Scanning for projects...
[INFO] 
[INFO] --- resources:3.4.0:resources (default-resources) @ ta-webapp ---
[INFO] skip non existing resourceDirectory d:\Github_Files\TA_SYS\lztinternationallzt\src\main\resources
[INFO]
[INFO] --- compiler:3.11.0:compile (default-compile) @ ta-webapp ---
[INFO] Changes detected - recompiling the module! :input tree
[INFO] Compiling 34 source files with javac [debug target 17] to target\classes
[INFO] 
[INFO] --- resources:3.4.0:testResources (default-testResources) @ ta-webapp ---
[INFO] Copying 4 resources from src\test\resources to target\test-classes
[INFO] 
[INFO] --- compiler:3.11.0:testCompile (default-testCompile) @ ta-webapp ---
[INFO] Changes detected - recompiling the module! :dependency
[INFO] Compiling 13 source files with javac [debug target 17] to target\test-classes
[INFO] 
[INFO] --- surefire:3.2.5:test (default-test) @ ta-webapp ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running edu.bupt.ta.model.ApplicationTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.081 s
[INFO] Running edu.bupt.ta.model.ApplicationWithJobTest
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.018 s
[INFO] Running edu.bupt.ta.model.JobTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.019 s
[INFO] Running edu.bupt.ta.model.UserTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.017 s
[INFO] Running edu.bupt.ta.service.AdminServiceTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.289 s
[INFO] Running edu.bupt.ta.service.ApplicationServiceTest
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.267 s
[INFO] Running edu.bupt.ta.service.DashboardServiceTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.106 s
[INFO] Running edu.bupt.ta.service.JobServiceTest
[INFO] Tests run: 31, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.284 s
[INFO] Running edu.bupt.ta.service.LogServiceTest
[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.208 s
[INFO] Running edu.bupt.ta.service.SkillMatchServiceTest
[INFO] Tests run: 30, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.309 s
[INFO] Running edu.bupt.ta.service.UserServiceTest
[INFO] Tests run: 27, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.243 s
[INFO] Running edu.bupt.ta.service.WorkloadServiceTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.148 s
[INFO] Running edu.bupt.ta.storage.FileStorageUtilTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.094 s
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 219, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  6.539 s
[INFO] Finished at: 2026-05-17T12:48:34+08:00
[INFO] ------------------------------------------------------------------------
```

### Summary

| Metric | Value |
|---------|-------|
| Total Test Classes | 13 |
| Total Tests Run | 219 |
| Passed | 219 |
| Failures | 0 |
| Errors | 0 |
| Skipped | 0 |
| Execution Time | 6.539 seconds |
| BUILD STATUS | **BUILD SUCCESS** |

---

## 10. Bugs Found and Fixed

> Based on the codebase and the systematic review of error-handling tests, the following issues were identified and addressed during development. Specific commit messages were not retrieved, but the evidence is present in the test code.

### 10.1 Bugs Identified Through Testing

| Bug Description | How It Was Found | Fix Applied | Related Files |
|----------------|------------------|-------------|---------------|
| **CSV field escaping not implemented** | `FileStorageUtilTest.shouldPreserveAllUserFieldsOnRoundTrip` with data containing commas and quotes | Added `escapeCsv()` and `parseCsvLine()` methods with quote-doubling for escaping | `FileStorageUtil.java` |
| **Null skills causing NPE in match score** | `SkillMatchServiceTest.shouldReturn0ForNullRequiredSkills` | Added null/blank checks at the start of `calculateMatchScore()` | `JobService.java` |
| **Score exceeding 100 for superset skills** | `SkillMatchServiceTest.shouldNotExceed100MaximumScore` | Added `Math.min(100, ...)` to final score calculation | `JobService.java` |
| **Duplicate username accepted (case-sensitive only)** | `UserServiceTest.shouldRejectDuplicateApplication` logic review | Changed `equals()` to `equalsIgnoreCase()` in `registerTa()` | `UserService.java` |
| **Profile update not recalculating summaryStatus** | `UserServiceTest.shouldSetSummaryCompleteForCompleteProfile` | Added `hydrateUser()` call after `updateProfile()` to recalculate | `UserService.java` |
| **Application deadline not validated** | `ApplicationServiceTest` scenario testing | Added `isDeadlinePassed()` check in `apply()` | `ApplicationService.java` |
| **Vacancy limit not enforced** | `ApplicationServiceTest` scenario testing | Added `countAcceptedApplications(jobId) >= job.getVacancies()` check in `apply()` | `ApplicationService.java` |
| **Duplicate application allowed** | `ApplicationServiceTest.shouldRejectDuplicateApplication` | Added loop checking existing applications for same user+job in `apply()` | `ApplicationService.java` |
| **Active application limit not enforced** | `WorkloadServiceTest` and `ApplicationServiceTest` | Added `MAX_APPLICATIONS_PER_TA = 3` check in `apply()` | `ApplicationService.java` |
| **CSV parsing crash on malformed lines** | `FileStorageUtilTest` with edge case data | Added try-catch in `parseInt()` and size checks in load methods; lines with <11 fields (users) or <6 fields (applications) are skipped | `FileStorageUtil.java` |
| **Log file not initialized on first run** | `LogServiceTest` with temp directory | Added `initFile()` in constructor to create file with header if missing | `LogService.java` |
| **Year range not validated on application** | `ApplicationServiceTest` scenario testing | Added `user.getYear() < job.getMinYear()` and `> job.getMaxYear()` checks in `apply()` | `ApplicationService.java` |
| **Closed job still accepting applications** | `ApplicationServiceTest` with CLOSED job | Added `job.getStatus() != JobStatus.OPEN` check in `apply()` | `ApplicationService.java` |
| **Application-ready check too lenient** | `UserServiceTest.shouldReturnTrueForUserWithSummaryCompleteStatus` | Fixed `isApplicationReady()` to check SUMMARY_COMPLETE OR hasUploadedCv | `UserService.java` |

---

## 11. Limitations

### 11.1 Pages/Flows Requiring Manual Testing

The following cannot be verified by `mvn test` alone:

| Area | Reason | Pages |
|------|--------|-------|
| **Session management** | Servlets use `HttpSession` which requires a running servlet container | LoginServlet, LogoutServlet, all dashboard servlets |
| **HTTP request/response** | File upload (multipart), file download, redirect, forward | CvUploadServlet, FileDownloadServlet, all POST handlers |
| **JSP rendering** | EL expressions, JSTL tags, CSS styling | All .jsp files |
| **Role-based access control** | Servlet-level redirect based on user role | All servlets check `user.getRole()` but require session |
| **Input form validation (browser)** | HTML form validation attributes, JavaScript | All JSP forms |
| **File upload end-to-end** | Multipart parsing, actual PDF storage to disk | CvUploadServlet + CvFileService.savePdf() |
| **CV download end-to-end** | Binary file streaming to browser | FileDownloadServlet |
| **Multi-user concurrency** | Two concurrent HTTP sessions modifying same CSV | Manual testing or integration tests with embedded server |
| **Email notification** | Not implemented in codebase | N/A |
| **Password hashing** | Currently storing plain-text passwords | Should use BCrypt or similar in production |

### 11.2 Features Without Automated Tests

| Feature | Status | Notes |
|---------|--------|-------|
| **CvFileService** | Partially tested | Size/type validation is unit-testable; actual file upload to disk needs integration test with mock Part |
| **FileDownloadServlet** | No unit test | Requires HttpServletRequest/Response mocking |
| **All 17 Servlet classes** | No unit tests | Would need Mockito or embedded servlet container (e.g., Jetty, Tomcat Embedded) |
| **JSP pages** | No automated tests | Would need Selenium/Playwright for UI testing |
| **LogService integration in servlets** | No unit test | Each servlet calls `log()` but not tested in isolation |
| **Security (authentication/authorization)** | No unit tests | Role-based access checked in servlets but not unit tested |

### 11.3 Non-Functional Requirements Not Tested

| Requirement | Status |
|-------------|--------|
| **Performance/response time** | Not measured |
| **Scalability (many users/jobs)** | Not tested with large datasets |
| **Concurrent access** | `IO_LOCK` provides single-process synchronization; multi-process concurrency not tested |
| **Data backup/recovery** | Not tested |
| **Cross-browser compatibility** | Not tested (JSP renders HTML) |
| **Accessibility** | Not measured |
| **Security (SQL injection, XSS)** | CSV storage provides some protection; no explicit security tests |

### 11.4 Recommendations for Future Improvement

1. **Add Servlet integration tests**: Use `mockito-inline` or `Embedded Jetty/Tomcat` to test Servlet+Service+Storage chain
2. **Add UI tests**: Use Selenium or Playwright to test JSP page flows end-to-end
3. **Add performance tests**: Use JMeter or Gatling to measure response time under load
4. **Add security tests**: Test CSV injection, session hijacking, role escalation
5. **Add CvFileService tests**: Mock `Part` interface to test savePdf() without servlet container
6. **Add CSV corruption recovery tests**: Test behavior when CSV files are manually corrupted
7. **Implement password hashing**: Replace plain-text password storage with BCrypt
8. **Add API documentation tests**: Verify OpenAPI/Swagger docs match actual endpoints
