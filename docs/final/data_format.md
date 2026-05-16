# TA Recruitment System — Data Format Documentation

## 1. Data File Locations

All data files reside under the `data/` directory in the project root, alongside the `pom.xml` file.

```
lztinternationallzt/
├── data/
│   ├── ta_users.csv        # User accounts and TA profile data
│   ├── jobs.csv            # Job posting records
│   ├── applications.csv     # Application submissions
│   ├── system_logs.csv     # System operation audit log
│   └── cvs/                # Uploaded PDF CV files
│       └── {userId}.pdf    # One file per TA (e.g. U001.pdf)
```

**Path resolution** — `FileStorageUtil` resolves the data directory in this order of precedence:
1. Java system property `ta.data.dir`
2. Environment variable `TA_DATA_DIR`
3. Auto-detected path by walking up from `user.dir` or classpath, looking for a directory containing both `pom.xml` and `data/`
4. Falls back to the relative path `data/`

A mirror directory can be configured via `ta.data.mirror.dir` / `TA_DATA_MIRROR_DIR` for backup writes.

---

## 2. CSV File Schemas

### 2.1 `ta_users.csv` — User Accounts

The file stores all user accounts (TA, MO, and Admin) in a single flat table. There are 21 columns.

| # | Column Name | Type | Description |
|----|------------|------|-------------|
| 1 | `userId` | String | Primary key. Format: `U{NNN}` (e.g., `U001`). Assigned sequentially on registration. |
| 2 | `username` | String | Login name. Unique across all roles. Case-sensitive. |
| 3 | `password` | String | Plain-text password (no hashing in current version). |
| 4 | `name` | String | Display name. May be empty for new accounts. |
| 5 | `email` | String | Contact email. May be empty. |
| 6 | `role` | String | One of: `TA`, `MO`, `ADMIN`. Defaults to `TA` on registration. |
| 7 | `year` | Integer | Academic year (1–8). `0` means unspecified. |
| 8 | `major` | String | Academic major or department. |
| 9 | `skills` | String | Pipe-delimited skill list (e.g., `Java\|Python\|SQL`). Normalized from user input (commas/semicolons converted to `\|`). |
| 10 | `status` | String | Account status: `ACTIVE` or `INACTIVE`. Inactive accounts cannot log in. |
| 11 | `availability` | String | Free-text availability description. |
| 12 | `personalStatement` | String | Candidate's personal statement or motivation. Multi-line text compacted into single-line (newlines replaced with ` \| `). |
| 13 | `relevantCourses` | String | Pipe-delimited course list. |
| 14 | `projectExperience` | String | Free-text project/teaching experience. |
| 15 | `preferredRole` | String | Pipe-delimited preferred role list. |
| 16 | `summaryStatus` | String | Profile completeness: `SUMMARY_COMPLETE` (≥8 fields filled), `BASIC_COMPLETE` (≥5), or `INCOMPLETE`. |
| 17 | `cvStoredName` | String | File name of the uploaded PDF on disk (e.g., `U001.pdf`). Empty if no CV uploaded. |
| 18 | `cvOriginalName` | String | Original file name submitted by the user. |
| 19 | `cvContentType` | String | MIME type, typically `application/pdf`. |
| 20 | `cvUploadedAt` | String | Upload timestamp in `yyyy-MM-dd HH:mm:ss` format. |
| 21 | `cvStatus` | String | `UPLOADED` or `MISSING`. |

**Sample row:**
```csv
U001,seele,123456,Seele,seele@bupt.edu.cn,TA,3,IoT,Java|Python|Data Structure|STM32,ACTIVE,Mon/Wed afternoons,Interested in supporting programming labs,EBU6304|DS|Embedded Systems,RoboCup robot project,Lab Support,SUMMARY_COMPLETE,U001.pdf,Seele_U001_Test_CV.pdf,application/pdf,2026-05-16 17:39:16,UPLOADED
```

---

### 2.2 `jobs.csv` — Job Postings

Stores all job postings. There are 12 columns.

| # | Column Name | Type | Description |
|----|------------|------|-------------|
| 1 | `jobId` | String | Primary key. Format: `J{NNN}` (e.g., `J001`). |
| 2 | `title` | String | Job title (e.g., `Software Engineering TA`). |
| 3 | `moduleCode` | String | Course module code (e.g., `EBU6304`). |
| 4 | `organiser` | String | MO's display name who created the job. Used for access control. |
| 5 | `minYear` | Integer | Minimum academic year required. Default `1`. |
| 6 | `maxYear` | Integer | Maximum academic year allowed. Default `4`. |
| 7 | `hours` | Integer | Expected weekly working hours. |
| 8 | `status` | String | `OPEN` or `CLOSED`. Determines visibility on the TA job board. |
| 9 | `requiredSkills` | String | Pipe-delimited skill requirements (e.g., `Java\|Teamwork\|Documentation`). |
| 10 | `matchScore` | Integer | Per-job match score for the current TA user (set dynamically at query time, not persisted). |
| 11 | `deadline` | String | Application deadline in `yyyy-MM-dd` format. |
| 12 | `vacancies` | Integer | Number of open positions for this job. |

**Sample row:**
```csv
J001,Software Engineering TA,EBU6304,Dr.Wang,2,4,20,OPEN,Java|Teamwork|Documentation,95,2026-05-01,3
```

---

### 2.3 `applications.csv` — Application Records

Stores TA job applications. There are 7 columns.

| # | Column Name | Type | Description |
|----|------------|------|-------------|
| 1 | `applicationId` | String | Primary key. Format: `A{NNN}` (e.g., `A001`). |
| 2 | `userId` | String | Foreign key referencing `ta_users.csv.userId`. |
| 3 | `jobId` | String | Foreign key referencing `jobs.csv.jobId`. |
| 4 | `status` | String | `PENDING`, `INTERVIEW`, `ACCEPTED`, or `REJECTED`. |
| 5 | `submittedAt` | String | Submission timestamp in `yyyy-MM-dd HH:mm:ss` format. |
| 6 | `notes` | String | Admin/MO notes, e.g., rejection reason (`Rejected: not enough experience`). |
| 7 | `availability` | String | TA's availability at submission time (copied from `ta_users.csv`). |

**Sample row:**
```csv
A001,U001,J001,PENDING,2026-03-16 10:00:00,First application,Mon/Wed afternoons
```

---

### 2.4 `system_logs.csv` — System Audit Log

Stores a chronological record of all significant system operations. There are 9 columns.

| # | Column Name | Type | Description |
|----|------------|------|-------------|
| 1 | `logId` | String | Primary key. Format: `L{NNNNN}` (e.g., `L00001`). |
| 2 | `operatorId` | String | `userId` of the actor performing the operation. |
| 3 | `operatorName` | String | Display name of the actor (denormalized for readability). |
| 4 | `operationType` | String | Action type: `LOGIN`, `LOGOUT`, `CREATE`, `UPDATE`, `DELETE`, `APPROVE`, `REJECT`, `ENABLE`, `DISABLE`, `EXPORT`. |
| 5 | `targetType` | String | Entity type acted upon: `User`, `Job`, `Application`. |
| 6 | `targetId` | String | ID of the entity acted upon. |
| 7 | `details` | String | Human-readable description of the operation. |
| 8 | `ipAddress` | String | Client IP address from `X-Forwarded-For` or `getRemoteAddr()`. |
| 9 | `createdAt` | String | Log entry timestamp in `yyyy-MM-dd HH:mm:ss` format. |

**Sample row:**
```csv
L00001,U006,System Admin,LOGIN,User,U006,User login,127.0.0.1,2026-05-16 10:00:00
```

---

### 2.5 `data/cvs/{userId}.pdf` — Uploaded CV Files

Binary PDF files stored on the filesystem, not in CSV. The filename on disk is always `{userId}.pdf` (e.g., `U001.pdf`), regardless of the original upload name. CSV columns `cvStoredName`, `cvOriginalName`, `cvContentType`, `cvUploadedAt`, `cvStatus` in `ta_users.csv` hold the metadata.

---

## 3. Data Read/Write Flow

### 3.1 Single-Process Synchronization

All CSV read and write operations pass through `FileStorageUtil`, which holds a **static `IO_LOCK` object**. Every public method is wrapped in `synchronized (IO_LOCK)`, ensuring that concurrent HTTP requests handled by the same Tomcat instance do not interleave reads and writes.

### 3.2 Write Flow (Atomic Pattern)

```
Service calls saveUsers(List<User>)
        │
        ▼
FileStorageUtil.saveUsers() acquires IO_LOCK
        │
        ▼
Build List<String> — header row + one CSV line per entity
        │
        ▼
writeLinesAtomically(Path target, List<String> lines)
        │
        ├── Creates temp file in the same directory
        ├── Writes all lines with BufferedWriter (UTF-8)
        ├── Files.move(source, target, ATOMIC_MOVE)  ← atomic on supported OS
        │   (falls back to non-atomic move on AtomicMoveNotSupportedException)
        └── Releases IO_LOCK
        │
        ▼
syncFileToMirror() — copies the written file to mirror directory (if configured)
```

This temp-then-move pattern ensures that if the JVM crashes mid-write, the original file is never corrupted — it is either the old content or the complete new content.

### 3.3 Read Flow

```
Service calls loadUsers()
        │
        ▼
FileStorageUtil.loadUsers() acquires IO_LOCK
        │
        ├── Opens file with BufferedReader (UTF-8)
        ├── Skips first line (header)
        ├── For each data line:
        │   ├── parseCsvLine() — handles quoted commas and escaped quotes
        │   └── Maps fields to User setters
        └── Releases IO_LOCK
        │
        ▼
Returns List<User> to Service
```

### 3.4 First-Run Initialization

On application startup, `FileStorageUtil`'s static initializer calls `initFiles()`, which:
1. Creates the `data/` directory if absent
2. Bootstraps each CSV file with its header row if the file does not exist
3. Copies from mirror directory if primary is missing but mirror exists
4. Copies to mirror directory if mirror is missing but primary exists
5. Calls `ensureDefaultUsers()`, `ensureDefaultJobs()`, `ensureDefaultApplications()` if the files are empty (only one line = just the header)

This guarantees the system has seed data (3 TAs, 2 MOs, 1 Admin, 4 jobs, 1 application) on first startup.

### 3.5 Data Flow Between Services and Storage

```
ApplyServlet.doPost()
  → ApplicationService.apply()
      → UserService.isApplicationReady()      ← reads ta_users.csv
      → JobService.findById()                ← reads jobs.csv
      → FileStorageUtil.saveApplications()   ← writes applications.csv
      → LogService.log()                     ← writes system_logs.csv
```

CV file writes bypass `FileStorageUtil` entirely — `CvFileService` uses `Files.copy()` directly to `data/cvs/{userId}.pdf`, and `UserService.updateCvMetadata()` only updates the 5 CV columns in `ta_users.csv`.
