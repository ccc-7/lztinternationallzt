# TA Recruitment System — Testing Guide

## 1. Testing Overview

This project does not include a dedicated unit test suite (`src/test/java`). There are no JUnit test classes. Instead, testing is performed by **running the application** in a Tomcat 10 container and interacting with it through a browser. The system ships with pre-seeded default data so that every feature can be exercised immediately after startup without any manual setup.

---

## 2. How to Run the Application

### Prerequisites
- Java 17 or higher
- Apache Maven 3.x
- Apache Tomcat 10 (or newer, supporting Jakarta EE 9+)
- A web browser

### Build
From the `lztinternationallzt/` directory:

```bash
mvn clean package
```

This produces `target/ta-webapp.war`.

### Deploy
1. Copy `target/ta-webapp.war` into `$CATALINA_HOME/webapps/`.
2. Start Tomcat: `$CATALINA_HOME/bin/startup.sh` (Linux/macOS) or `$CATALINA_HOME\bin\startup.bat` (Windows).
3. The application auto-deploys at `http://localhost:8080/ta-webapp/`.

Alternatively, run directly with the Maven Tomcat plugin:

```bash
mvn tomcat10:run
```

The application starts at `http://localhost:8080/ta-webapp/`.

---

## 3. Default Test Accounts

On first startup, `FileStorageUtil` automatically populates the data files with seed records if they are empty. The default accounts are:

| Username | Password | Role | User ID | Notes |
|----------|----------|------|---------|-------|
| `seele` | `123456` | TA | U001 | Full profile, CV uploaded |
| `luna` | `123456` | TA | U002 | Full profile, no CV |
| `kevin` | `123456` | TA | U003 | Full profile, no CV |
| `mo1` | `123456` | MO | U004 | Dr.Wang — created J001, J003 |
| `mo2` | `123456` | MO | U005 | Dr.Liu — created J002, J004 |
| `admin` | `123456` | ADMIN | U006 | System Admin |

---

## 4. Test Data Locations

All test data lives in the `data/` directory (resolved automatically, see `data_format.md` for path resolution details):

| File | Content |
|------|---------|
| `data/ta_users.csv` | 6 seeded users (3 TA, 2 MO, 1 Admin) |
| `data/jobs.csv` | 4 seeded jobs (J001–J004), all `OPEN` |
| `data/applications.csv` | 1 seeded application (A001: seele → J001, `PENDING`) |
| `data/system_logs.csv` | Audit log — populated as you use the system |
| `data/cvs/` | Physical PDF CV files for TAs who uploaded one |

Each time the application starts, if a CSV file contains only a header row (no data rows), the corresponding `ensureDefault*()` method re-seeds the default records. Deleting the data directory or emptying a CSV file triggers a fresh seed on the next startup.

---

## 5. Feature-by-Feature Test Cases

### 5.1 TA Registration and Login

**Test steps:**
1. Navigate to `http://localhost:8080/ta-webapp/`. Click "Register" and fill in all fields (username, password, name, email, year, major, skills, availability, personalStatement, relevantCourses, projectExperience, preferredRole). Submit.
2. Expected: Redirect to TA dashboard (`/ta/dashboard`). A new row appears in `ta_users.csv` with a new `U{NNN}` ID. `summaryStatus` is calculated as `SUMMARY_COMPLETE`, `BASIC_COMPLETE`, or `INCOMPLETE` depending on how many fields are filled.
3. Log out, then log in with the new account. Expected: Redirect to TA dashboard.
4. Try logging in with a non-existent username or wrong password. Expected: Redirect back to login with error message.
5. Login as `admin` / `123456`. Expected: Redirect to Admin dashboard.

### 5.2 TA Profile Management

**Test steps:**
1. Log in as `luna` (`123456`). Go to Profile (`/ta/profile`).
2. Check that the profile form shows all 10 editable fields. Note the current `summaryStatus`.
3. Fill in additional fields and submit. Expected: Redirect back to profile, status bar shows updated progress rings.
4. Check `ta_users.csv` for `luna`'s row — the 10 fields should reflect the new values, and `summaryStatus` should reflect the updated count.

### 5.3 TA CV Upload and Delete

**Test steps:**
1. Log in as `kevin` (`123456`). Go to Profile.
2. Upload a PDF file (≤ 5MB). Expected: Redirect to profile, a success message appears, a CV preview section becomes visible.
3. Inspect `data/cvs/U003.pdf` — the file should exist on disk. Inspect `ta_users.csv` for `kevin`'s row — `cvStoredName` = `U003.pdf`, `cvStatus` = `UPLOADED`, `cvUploadedAt` populated.
4. Try uploading a file larger than 5MB or a non-PDF file. Expected: Redirect back with an error message, no file written.
5. Click "Delete CV". Expected: `data/cvs/U003.pdf` is deleted, CSV columns cleared, `cvStatus` = `MISSING`.

### 5.4 TA Browses and Applies for Jobs

**Test steps:**
1. Log in as `luna` (`123456`). Go to the Jobs page (`/jobs`).
2. Expected: All 4 seeded jobs appear, each with a `matchScore` (0–100). Jobs should be sorted by score descending.
3. Click "Apply" on an open job. Expected: Redirect to the jobs page, a success flash message appears, a new row added to `applications.csv` with `applicationId` = next sequential `A{NNN}`, `status` = `PENDING`.
4. Try applying to the same job again. Expected: Error message "You have already applied for this job".
5. Apply to 3 more jobs. Expected: Third application succeeds. The fourth application should fail with "You have too many active applications".
6. Log in as `kevin`. Apply to J001 (Software Engineering TA). Expected: J001's `matchScore` for kevin is likely different from luna's because their skills differ.

### 5.5 MO Publishes and Manages Jobs

**Test steps:**
1. Log in as `mo1` (`123456`) — Dr.Wang. Go to MO Dashboard (`/mo/dashboard`).
2. Expected: Only jobs created by Dr.Wang (J001, J003) appear in the jobs list.
3. Go to New Job (`/mo/jobs/new`). Fill in all fields (title, module code, hours, years, required skills, deadline, vacancies). Submit.
4. Expected: Redirect to MO dashboard. J005 appears in the list with `status` = `OPEN`. A new row with `J005` appears in `jobs.csv`.
5. Toggle J005 to `CLOSED`. Expected: `status` in CSV changes from `OPEN` to `CLOSED`. The job disappears from the TA job board.
6. Log in as `mo2` — Dr.Liu. Expected: Dr.Liu cannot see J001 or J003 (only their own J002, J004).

### 5.6 MO Reviews and Updates Applications

**Test steps:**
1. Log in as `mo1` (`123456`). Go to Applications (`/mo/applications`).
2. Expected: Only applications for J001 and J003 appear. The seeded application A001 (seele → J001) should be visible.
3. Click "View CV" next to seele. Expected: PDF opens in the browser via `/file/download?userId=U001`.
4. Click "Candidate Summary" next to seele. Expected: Structured profile view at `/candidate/summary?userId=U001`.
5. Change seele's application status to `INTERVIEW`. Submit. Expected: `applications.csv` row for A001 shows `status` = `INTERVIEW`.
6. Change to `ACCEPTED` (with a note). Submit. Expected: `status` = `ACCEPTED`, `notes` column populated.

### 5.7 Admin System Management

**Test steps:**
1. Log in as `admin` (`123456`). Go to Admin Dashboard (`/admin/dashboard`).
2. Expected: Stat cards show correct counts (3 TA, 2 MO, 1 Admin, 1 application, 4 jobs, 4 open jobs). Top TAs and Top Jobs sections are populated.
3. Go to Users (`/admin/users`). Filter by role = TA. Expected: Only seele, luna, kevin shown. Toggle luna to `INACTIVE`. Expected: luna cannot log in afterward.
4. Go to Jobs (`/admin/jobs`). Expected: All 4 jobs shown regardless of organiser. Toggle J002 to `CLOSED`. Expected: `status` = `CLOSED` in CSV. Delete J004. Expected: J004 removed from CSV; any applications referencing J004 are cleaned up.
5. Go to Applications (`/admin/applications`). Expected: All applications visible. Approve A001. Expected: `status` = `ACCEPTED`.
6. Go to Logs (`/admin/logs`). Expected: Paginated list of all operations. Verify that each login, create, update, approve, and reject operation appears as a new row in `system_logs.csv`.

### 5.8 Skill Matching Algorithm

**Test steps:**
1. Log in as `seele` (`123456`). Skills: `Java|Python|Data Structure|STM32`.
2. Browse jobs. Expected: J001 (Java|Teamwork|Documentation) shows a high match score because "Java" matches directly. J002 (C|STM32|Debugging) also shows some score because "STM32" matches directly.
3. Log in as `kevin` (`123456`). Skills: `C|STM32|Debugging`.
4. Browse jobs. Expected: J002 (C|STM32|Debugging) shows the highest score. J001 shows a lower score because kevin has no Java skill.
5. Verify that the score is not simply a count — it uses weighted categories (concepts ×1.5, languages ×1.3, ML ×1.2, database ×1.1, frameworks ×1.0, tools ×0.9) and applies alias matching (e.g., `js` matches `javascript`).

---

## 6. Expected Outputs Summary

| Test Scenario | Expected Output |
|---------------|----------------|
| Login with valid credentials | Redirect to role-specific dashboard; `system_logs.csv` gets a `LOGIN` entry |
| Login with wrong password | Redirect to login page with error message; no log entry |
| TA applies to open job | New `A{NNN}` row in `applications.csv` with `PENDING`; success flash message |
| MO closes a job | `jobs.csv` status column changes from `OPEN` to `CLOSED`; job disappears from TA board |
| TA uploads CV | `data/cvs/{userId}.pdf` written; 5 CV columns in `ta_users.csv` populated |
| TA exceeds 3 active applications | Error message: "You have too many active applications"; no new CSV row |
| Admin approves application | `applications.csv` status → `ACCEPTED`; `system_logs.csv` gets `APPROVE` entry |
| Duplicate application | Error message: "You have already applied for this job"; no duplicate CSV row |
| MO tries to see another MO's job's applications | Filtered out; only organiser's own jobs' applications shown |
| FileStorageUtil first-run | 4 CSV files created with headers + seed data rows if files are empty |
