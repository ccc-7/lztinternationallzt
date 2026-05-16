# TA Recruitment System — Module Design Documentation

## 1. TA Module

### Functionality
The TA (Teaching Assistant) module is the candidate-facing subsystem. It enables prospective TAs to register an account, build a structured profile with skills and personal statements, upload a PDF CV, browse open job postings ranked by skill match score, submit up to 3 active applications, and track the status of their applications.

### Related Files
- `src/main/java/edu/bupt/ta/controller/TaDashboardServlet.java`
- `src/main/java/edu/bupt/ta/controller/TaProfileServlet.java`
- `src/main/java/edu/bupt/ta/controller/RegisterServlet.java`
- `src/main/java/edu/bupt/ta/controller/CvUploadServlet.java`
- `src/main/java/edu/bupt/ta/controller/CvDeleteServlet.java`
- `src/main/java/edu/bupt/ta/service/UserService.java`
- `src/main/java/edu/bupt/ta/service/CvFileService.java`
- `src/main/java/edu/bupt/ta/service/DashboardService.java`
- `src/main/webapp/WEB-INF/jsp/ta/dashboard.jsp`
- `src/main/webapp/WEB-INF/jsp/ta/profile.jsp`
- `src/main/webapp/WEB-INF/jsp/register.jsp`

### Core Classes and Methods

**`TaDashboardServlet`** — Loads dashboard data for the authenticated TA.
- `doGet()` — Collects pending/interview application count, matched job count (score ≥ 60), recent applications, top 5 jobs by applicant count. Forwards to `ta/dashboard.jsp`.

**`TaProfileServlet`** — Handles profile viewing and editing.
- `doGet()` — Loads the TA's profile from `UserService`, checks CV upload status, sets `profileHasCv` and `taProfileFieldRings` attributes, forwards to `ta/profile.jsp`.
- `doPost()` — Parses 10 form fields (name, email, year, major, skills, availability, personalStatement, relevantCourses, projectExperience, preferredRole), calls `UserService.updateProfile()`, updates session with the refreshed `User` object.

**`RegisterServlet`** — TA self-registration.
- `doPost()` — Validates username/password presence, parses year, calls `UserService.registerTa()`, stores the new user in session, redirects to TA dashboard.

**`CvUploadServlet`** — Handles PDF CV upload with `@MultipartConfig`.
- `doPost()` — Calls `CvFileService.savePdf()` to write `{userId}.pdf` to `data/cvs/`, then updates CSV metadata via `UserService.updateCvMetadata()`. Cleans up partial file on error.

**`UserService`** — Central user management.
- `registerTa(...)` — Registers a new TA with role `TA`, checks username uniqueness, normalizes skills (comma → pipe delimiter), calculates `summaryStatus`.
- `updateProfile(...)` — Updates 10 profile fields, recalculates `summaryStatus` and `cvStatus`.
- `updateCvMetadata(...)` — Updates 5 CV-related CSV columns (storedName, originalName, contentType, uploadedAt, cvStatus).
- `clearCvMetadata()` — Clears all CV columns, sets `cvStatus` to `MISSING`.
- `isApplicationReady(User)` — Returns true if `summaryStatus` is `SUMMARY_COMPLETE` OR a CV has been uploaded.
- `calculateSummaryStatus(User)` — Scores 9 fields (name, email, year, major, skills, availability, personalStatement, relevantCourses, projectExperience); returns `SUMMARY_COMPLETE` (≥8), `BASIC_COMPLETE` (≥5), or `INCOMPLETE`.

---

## 2. MO Module

### Functionality
The MO (Module Organiser) module enables faculty members to create and manage job postings for their modules, view applications submitted to their own jobs, and update application statuses (move to interview, accept, or reject).

### Related Files
- `src/main/java/edu/bupt/ta/controller/MODashboardServlet.java`
- `src/main/java/edu/bupt/ta/controller/MOJobServlet.java`
- `src/main/java/edu/bupt/ta/controller/MOApplicationServlet.java`
- `src/main/java/edu/bupt/ta/service/JobService.java`
- `src/main/java/edu/bupt/ta/service/ApplicationService.java`
- `src/main/java/edu/bupt/ta/service/UserService.java`
- `src/main/webapp/WEB-INF/jsp/mo/dashboard.jsp`
- `src/main/webapp/WEB-INF/jsp/mo/new-job.jsp`
- `src/main/webapp/WEB-INF/jsp/mo/applications.jsp`

### Core Classes and Methods

**`MODashboardServlet`** — MO's personal dashboard showing job and application stats.
- `doGet()` — Calls `JobService.getJobsByOrganiser(user.getDisplayName())` to filter jobs by the MO's display name, calculates pending/accepted application counts for those jobs, forwards to `mo/dashboard.jsp`.

**`MOJobServlet`** — Job creation form handler.
- `doPost()` — Reads 8 parameters (title, moduleCode, organiser, hours, minYear, maxYear, requiredSkills, deadline, vacancies), calls `JobService.createJob()`, redirects to MO dashboard.

**`MOApplicationServlet`** — Views and updates applications for MO's jobs.
- `doGet()` — Loads MO's jobs, then applications filtered by `myJobIds`. Populates applicant names via `UserService.findById()` and CV availability via `userService.hasUploadedCv()`. Forwards to `mo/applications.jsp`.
- `doPost()` — Validates the application belongs to an MO's own job, calls `ApplicationService.updateStatus()`, redirects back to the filtered application list.

---

## 3. Admin Module

### Functionality
The Admin module provides full system administration capabilities: managing all users (create, enable/disable, change passwords), managing all job postings (create, update, toggle status, delete), managing all applications (approve/reject with reasons), and viewing system operation logs.

### Related Files
- `src/main/java/edu/bupt/ta/controller/AdminDashboardServlet.java`
- `src/main/java/edu/bupt/ta/controller/AdminServlet.java`
- `src/main/java/edu/bupt/ta/service/AdminService.java`
- `src/main/java/edu/bupt/ta/service/LogService.java`
- `src/main/webapp/WEB-INF/jsp/admin/dashboard.jsp`
- `src/main/webapp/WEB-INF/jsp/admin/users-manage.jsp`
- `src/main/webapp/WEB-INF/jsp/admin/jobs-manage.jsp`
- `src/main/webapp/WEB-INF/jsp/admin/applications-manage.jsp`
- `src/main/webapp/WEB-INF/jsp/admin/logs.jsp`

### Core Classes and Methods

**`AdminDashboardServlet`** — Global statistics dashboard.
- `doGet()` — Calls `AdminService.getDashboardStats()` and `calculateUserWorkloads()`, forwards to `admin/dashboard.jsp`.

**`AdminServlet`** — Single centralized controller for all admin sub-features, mapped to 10 URL patterns.
- `showUsers()` — Filters by role (default TA) and status, supports keyword search on username/displayName/userId.
- `showJobs()` — Filters by status (OPEN/CLOSED/ALL), keyword search on title/jobId/moduleCode.
- `showApplications()` — Filters by status and jobId, keyword search on applicationId/userId/jobId; also cleans up orphaned applications referencing deleted jobs.
- `showLogs()` — Paginated log viewing (20 per page), filters by operator and operation type, keyword search on operatorName/details/targetId.
- `handleApprove()` — Sets application status to `ACCEPTED`, logs `APPROVE` operation.
- `handleReject()` — Sets application status to `REJECTED` with reason, logs `REJECT` operation.
- `handleJobAction()` — Delegates to `create/update/toggle/delete` sub-actions.
- `handleUserAction()` — Delegates to `toggle/create/changePassword` sub-actions.

**`AdminService`** — Admin-specific business logic.
- `getDashboardStats()` — Returns a `Map<String, Object>` containing: totalTA, activeTA, totalMO, totalApplications (with breakdown by status), totalJobs, openJobs, top 5 TAs by application count, top 3 jobs by applicant count.
- `calculateUserWorkloads()` — Returns a `Map<String, Integer>` of userId → total application count, for TAs only.

**`LogService`** — System audit trail.
- `log(...)` — Synchronized method that creates a new `SystemLog` entry and appends it to `data/system_logs.csv`.
- `getLogsPaginated(page, pageSize)` — Returns a slice of logs sorted by `createdAt` descending.

---

## 4. Job Module

### Functionality
The Job module handles all operations on job postings: creation by MO or Admin, retrieval (all, open only, by organiser), update, deletion, status toggle (OPEN/CLOSED), and counting operations.

### Related Files
- `src/main/java/edu/bupt/ta/model/Job.java`
- `src/main/java/edu/bupt/ta/model/JobStatus.java`
- `src/main/java/edu/bupt/ta/service/JobService.java`
- `src/main/java/edu/bupt/ta/storage/FileStorageUtil.java`

### Core Classes and Methods

**`Job`** — POJO with 12 fields: jobId, title, moduleCode, organiser, minYear, maxYear, hours, status, requiredSkills, matchScore, deadline, vacancies.

**`JobService`** — All job-related business logic.
- `getAllJobs()` — Returns all jobs from CSV via `FileStorageUtil.loadJobs()`.
- `getOpenJobs()` — Filters for `status == JobStatus.OPEN`.
- `getJobsByOrganiser(organiser)` — Filters by exact match on organiser name (case-insensitive).
- `getOpenJobsForUser(user)` — Returns open jobs with `matchScore` calculated per job via `calculateMatchScore()`, sorted descending.
- `createJob(...)` — Generates next `J{NNN}` ID, sets `status=OPEN`, normalizes skills, saves via `FileStorageUtil.saveJobs()`.
- `updateJob(...)` — Updates job fields in memory and saves.
- `deleteJob(jobId)` — Removes job by ID via `removeIf`.
- `toggleJobStatus(jobId)` — Flips between `OPEN` and `CLOSED`.
- `calculateMatchScore(userSkills, requiredSkills)` — Weighted skill matching algorithm (see Section 8).

---

## 5. Application Module

### Functionality
The Application module manages the application lifecycle: TA submits an application (with validation), MO/Admin review and update status, and the system tracks application history.

### Related Files
- `src/main/java/edu/bupt/ta/model/Application.java`
- `src/main/java/edu/bupt/ta/model/ApplicationStatus.java`
- `src/main/java/edu/bupt/ta/model/ApplicationWithJob.java`
- `src/main/java/edu/bupt/ta/service/ApplicationService.java`
- `src/main/java/edu/bupt/ta/controller/ApplyServlet.java`
- `src/main/java/edu/bupt/ta/controller/ApplicationStatusServlet.java`
- `src/main/webapp/WEB-INF/jsp/ta/applications.jsp`

### Core Classes and Methods

**`Application`** — POJO with 7 fields: applicationId, userId, jobId, status, submittedAt, notes, availability.

**`ApplicationService`** — Core application logic with 9-step validation in `apply()`:
1. User must exist
2. User must be application-ready (`isApplicationReady`)
3. Job must exist
4. Job must be `OPEN`
5. User's year must be within `[minYear, maxYear]`
6. Deadline must not have passed
7. Vacancy limit not exceeded (count accepted applications)
8. No duplicate application for the same user+job pair
9. User has ≤ 3 active (PENDING or INTERVIEW) applications

- `updateStatus(applicationId, newStatus)` — Updates status and sets default notes.
- `getApplicationsByJobIds(Set<String>)` — Batch retrieval by job IDs.
- `countApplicationsByJobIdsAndStatus(...)` — Count with both job and status filter.

**`ApplicationWithJob`** — DTO combining an `Application` with denormalized job fields (title, moduleCode, organiser) for display in lists.

---

## 6. CV / Profile Module

### Functionality
The CV/Profile module separates the TA's structured profile data (stored in CSV) from the original PDF CV file (stored on disk). Both are used as reviewer-facing artifacts, but managed independently.

### Related Files
- `src/main/java/edu/bupt/ta/service/CvFileService.java`
- `src/main/java/edu/bupt/ta/controller/CvUploadServlet.java`
- `src/main/java/edu/bupt/ta/controller/CvDeleteServlet.java`
- `src/main/java/edu/bupt/ta/controller/FileDownloadServlet.java`
- `src/main/java/edu/bupt/ta/controller/CandidateSummaryServlet.java`
- `src/main/webapp/WEB-INF/jsp/common/cv-view.jsp`

### Core Classes and Methods

**`CvFileService`** — Physical PDF file management.
- `savePdf(userId, Part)` — Validates file is ≤ 5MB, is a PDF, writes to `data/cvs/{userId}.pdf`. Returns `SavedCvFile` with metadata (storedName, originalName, contentType, sizeBytes, path).
- `deleteCv(storedName)` — Deletes the PDF file from disk.
- `resolveExistingCvPath(storedName)` — Returns the `Path` if the file physically exists.

**`CvUploadServlet`** — Wraps `CvFileService.savePdf()` with transactional cleanup on failure.
- `doPost()` — On error, calls `cvFileService.deleteCv()` to roll back the partial file.

**`FileDownloadServlet`** — Secure PDF delivery.
- `canViewCv(currentUser, targetUser)` — TA can only view their own CV; MO and Admin can view any TA's CV.
- `doGet()` — Resolves the file path, sets `Content-Type` and `Content-Disposition: inline`, streams the file.

**`CandidateSummaryServlet`** — Renders a structured preview of the TA's profile fields.
- `doGet()` — Loads the target TA's `User` object, sets `summaryViewMode`, forwards to `common/cv-view.jsp`.

---

## 7. Workload Module

### Functionality
The workload module tracks and displays the application activity of TAs, used by the Admin dashboard to identify high-activity candidates.

### Related Files
- `src/main/java/edu/bupt/ta/service/AdminService.java`
- `src/main/java/edu/bupt/ta/controller/AdminDashboardServlet.java`

### Core Classes and Methods

**`AdminService.calculateUserWorkloads()`** — Iterates over all TAs, counts their total applications, returns `Map<userId, applicationCount>` sorted by userId. Used in the Admin dashboard to display a workload summary table alongside global statistics.

---

## 8. Skill Matching Module

### Functionality
The skill matching module scores how well a TA's profile matches an open job's requirements, enabling personalized job recommendations on the TA dashboard.

### Related Files
- `src/main/java/edu/bupt/ta/service/JobService.java` (lines 14–307)

### Core Classes and Methods

**`JobService.calculateMatchScore(userSkills, requiredSkills)`** — Weighted skill matching algorithm.

**Algorithm steps:**
1. **Tokenization** — Splits both skill strings on `|` and `,`, converts to lowercase `Set<String>`.
2. **Normalization** — Adds skill aliases (e.g., `js` → `javascript`, `springboot` → `spring`) to both sets.
3. **Category weighting** — Each skill is assigned a category (`concept` ×1.5, `language` ×1.3, `ml` ×1.2, `database` ×1.1, `framework` ×1.0, `tools` ×0.9). Concepts and languages are weighted highest.
4. **Matching** — For each required skill: direct match (100%), alias match (100%), partial match in same category (70%), reverse alias match (80%).
5. **Jaccard bonus** — Up to 15% bonus added for having relevant extra skills.
6. **Final score** — `min(100, baseScore + jaccardBonus)`, rounded to integer.

**Skill categories defined:**
- `language` — java, python, javascript, c++, etc.
- `framework` — react, spring, django, etc.
- `ml` — machine learning, tensorflow, pytorch, etc.
- `database` — mysql, postgresql, mongodb, etc.
- `tools` — git, docker, kubernetes, etc.
- `concept` — data structures, algorithms, oop, microservices, etc.

**`getOpenJobsForUser(user)`** — Calls `calculateMatchScore()` on every open job, sorts by score descending, returns the list. Called by `TaDashboardServlet` and `JobListServlet`.
