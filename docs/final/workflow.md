# TA Recruitment System — Workflow Documentation

## 1. TA Registration and Login

### Registration Flow

```
[Browser]                         [RegisterServlet]                [UserService]              [FileStorageUtil]
     │                                    │                               │                            │
     │──── POST /register ──────────────► │                               │                            │
     │   (username, password, name,       │                               │                            │
     │    email, year, major, skills,     │                               │                            │
     │    availability, personalStatement,│                               │                            │
     │    relevantCourses, projectExp,    │                               │                            │
     │    preferredRole)                  │                               │                            │
     │                                    │──── registerTa() ────────────►│                            │
     │                                    │   Check username uniqueness   │                            │
     │                                    │   Generate next U{NNN} ID     │                            │
     │                                    │   Normalize skills: comma→|   │                            │
     │                                    │   Calculate summaryStatus     │                            │
     │                                    │   Set cvStatus = MISSING      │──── saveUsers() ──────────►│
     │                                    │                               │    (writes ta_users.csv)    │
     │                                    │◄─── return User ───────────   │                            │
     │                                    │                               │                            │
     │◄─── redirect /ta/dashboard ──────  │ (set session: currentUser)    │                            │
     │                                    │                               │                            │
```

### Login Flow

```
[Browser]                         [LoginServlet]                   [UserService]              [FileStorageUtil]
     │                                   │                               │                            │
     │──── POST /login ────────────────► │                               │                            │
     │   (username, password, role)      │                               │                            │
     │                                   │──── authenticate() ─────────►│                            │
     │                                   │   Load all users from CSV    │──── loadUsers() ─────────►│
     │                                   │   Filter by username+password │                            │
     │                                   │◄─── return User ───────────│                            │
     │                                   │                               │                            │
     │                                   │ Validate:                     │                            │
     │                                   │   • User must exist           │                            │
     │                                   │   • Role must match selected  │                            │
     │                                   │   • Status must be ACTIVE     │                            │
     │                                   │                               │                            │
     │                                   │──── LogService.log(LOGIN) ──► │                            │
     │                                   │                               │                            │
     │◄─── redirect to role dashboard ── │ (set session: currentUser)    │                            │
     │                                   │                               │                            │
     │   TA  → /ta/dashboard             │                               │                            │
     │   MO  → /mo/dashboard             │                               │                            │
     │   ADMIN → /admin/dashboard        │                               │                            │
```

---

## 2. TA Creates / Updates Profile

```
[Browser]                         [TaProfileServlet]              [UserService]              [FileStorageUtil]
     │                                   │                               │                            │
     │──── GET /ta/profile ─────────────►│                               │                            │
     │                                   │──── findById(userId) ───────► │                            │
     │                                   │   Check hasUploadedCv()       │                            │
     │                                   │◄─── return User ───────────   │                            │
     │                                   │──── calculateSummaryStatus()─►│                            │
     │                                   │◄─── return status ─────────   │                            │
     │◄─── forward /ta/profile.jsp ───── │                               │                            │
     │                                   │                               │                            │
     │──── POST /ta/profile ────────────►│                               │                            │
     │   (name, email, year, major,      │                               │                            │
     │    skills, availability,          │                               │                            │
     │    personalStatement,             │                               │                            │
     │    relevantCourses,               │                               │                            │
     │    projectExperience,             │                               │                            │
     │    preferredRole)                 │                               │                            │
     │                                   │──── updateProfile() ─────────►│                            │
     │                                   │   Update 10 fields in memory  │                            │
     │                                   │   Recalculate summaryStatus   │──── saveUsers() ──────────►│
     │                                   │   Recalculate cvStatus        │                            │
     │                                   │◄─── return updated User ────│                            │
     │                                   │                               │                            │
     │                                   │ (update session: currentUser) │                            │
     │◄─── redirect /ta/profile ──────── │                               │                            │
```

The profile form is split into three sections: **Basic Profile** (name, email, year, major), **Skills & Availability** (skills, availability, preferred role), and **Candidate Summary Builder** (personal statement, relevant courses, project experience). The summary status is recalculated after each save and displayed on the profile page as `SUMMARY_COMPLETE`, `BASIC_COMPLETE`, or `INCOMPLETE`.

---

## 3. TA Uploads or Deletes CV

### Upload Flow

```
[Browser]                         [CvUploadServlet]               [CvFileService]             [FileStorageUtil]
     │                                   │                               │                            │
     │──── POST /ta/profile/cv/upload ──►│                               │                            │
     │   (enctype=multipart/form-data,   │                               │                            │
     │    file: cvFile)                  │                               │                            │
     │                                   │──── savePdf(userId, Part) ───►│                            │
     │                                   │   Validate: size ≤ 5MB        │                            │
     │                                   │   Validate: file extension    │                            │
     │                                   │   Validate: Content-Type      │                            │
     │                                   │   Write to data/cvs/{userId}.pdf  (Files.copy)            │
     │                                   │◄─── return SavedCvFile ─────  │                            │
     │                                   │                               │                            │
     │                                   │──── updateCvMetadata() ─────► │                            │
     │                                   │   Update 5 CV columns in CSV  │──── saveUsers() ──────────►│
     │                                   │◄─── return updated User ────  │                            │
     │                                   │                               │                            │
     │                                   │ (update session: currentUser) │                            │
     │◄─── redirect /ta/profile ────────│                                │                            │
```

### Delete Flow

```
[Browser]                         [CvDeleteServlet]               [CvFileService]             [FileStorageUtil]
     │                                   │                               │                            │
     │──── POST /ta/profile/cv/delete ──►│                               │                            │
     │                                   │──── deleteCv(storedName) ───► │                            │
     │                                   │   Files.deleteIfExists()      │                            │
     │                                   │◄─── return boolean ────────   │                            │
     │                                   │                               │                            │
     │                                   │──── clearCvMetadata() ──────► │                            │
     │                                   │   Set cvStoredName = ""       │                            │
     │                                   │   Set cvStatus = MISSING      │──── saveUsers() ──────────►│
     │                                   │◄─── return updated User ────  │                            │
     │                                   │                               │                            │
     │◄─── redirect /ta/profile ────────│                                │                            │
```

Note: The TA's structured profile (stored in CSV) and the original PDF file (stored on disk) are managed as two separate artifacts. The profile remains accessible even without an uploaded CV.

---

## 4. TA Applies for a Job

```
[Browser]                         [JobListServlet]                [JobService]
     │                                   │                               │
     │──── GET /jobs ───────────────────►│                               │
     │   (TA session required)           │                               │
     │                                   │──── getOpenJobsForUser() ───► │
     │                                   │   Load all open jobs          │
     │                                   │   For each job:               │
     │                                   │     calculateMatchScore()     │
     │                                   │   Sort by matchScore ↓        │
     │                                   │◄─── return List<Job> ───────  │
     │                                   │                               │
     │◄─── forward /ta/jobs.jsp ─────────│                               │
     │   (jobs displayed with            │                               │
     │    match scores)                  │                               │
     │                                   │                               │
     │──── POST /apply ─────────────────►│                               │
     │   (jobId)                         │                               │
     │                                   │──── ApplicationService.apply()│
     │                                   │   Validations (9 checks):     │
     │                                   │     1. User exists            │
     │                                   │     2. User is application-ready │
     │                                   │     3. Job exists             │
     │                                   │     4. Job is OPEN            │
     │                                   │     5. Year within [min,max]  │
     │                                   │     6. Deadline not passed    │
     │                                   │     7. Vacancy not exceeded   │
     │                                   │     8. No duplicate apply     │
     │                                   │     9. ≤ 3 active apps        │
     │                                   │                               │
     │                                   │   Generate next A{NNN} ID     │
     │                                   │   Set status = PENDING        │
     │                                   │   Copy availability from user │
     │                                   │   Save to applications.csv    │
     │                                   │                               │
     │◄─── redirect /jobs ───────────────│                               │
```

On the job listing page, each open job displays a match score (0–100) calculated by comparing the TA's skills against the job's required skills. Jobs with a score ≥ 60 are highlighted as "matched positions" on the TA dashboard.

---

## 5. MO Publishes a Job

```
[Browser]                         [MOJobServlet]                  [JobService]              [FileStorageUtil]
     │                                   │                               │                            │
     │──── GET /mo/jobs/new ───────────► │                               │                            │
     │   (MO session required)           │                               │                            │
     │◄─── forward /mo/new-job.jsp ───── │                               │                            │
     │                                   │                               │                            │
     │──── POST /mo/jobs/new ───────────►                                │                            │
     │   (title, moduleCode, organiser,  │                               │                            │
     │    hours, minYear, maxYear,       │                               │                            │
     │    requiredSkills, deadline,      │                               │                            │
     │    vacancies)                     │                               │                            │
     │                                   │──── createJob() ─────────────►│                            │
     │                                   │   Generate next J{NNN} ID     │                            │
     │                                   │   Set status = OPEN           │                            │
     │                                   │   organiser = MO's displayName│                            │
     │                                   │   Normalize requiredSkills    │──── saveJobs() ──────────► │
     │                                   │◄─── return Job ─────────────  │                            │
     │                                   │                               │                            │
     │                                   │──── LogService.log(CREATE) ──►│                            │
     │                                   │                               │                            │
     │◄─── redirect /mo/dashboard ───────│                               │                            │
```

The `organiser` field is automatically set to the MO's `displayName` from the session, ensuring that the MO can only see and manage their own job postings.

---

## 6. MO Selects an Applicant

```
[Browser]                         [MOApplicationServlet]          [ApplicationService]       [FileStorageUtil]
     │                                   │                               │                            │
     │──── GET /mo/applications ────────►│                               │                            │
     │   (MO session required)           │                               │                            │
     │                                   │   Load MO's jobs by organiser │                            │
     │                                   │   Load applications by jobIds │                            │
     │                                   │   Load applicant names via    │                            │
     │                                   │    UserService.findById()     │                            │
     │                                   │   Check CV availability via   │                            │
     │                                   │    hasUploadedCv()            │                            │
     │◄─── forward /mo/applications.jsp  │                               │                            │
     │                                   │                               │                            │
     │   (MO reviews applicant profiles  │                               │                            │
     │    and CVs; can filter by job)    │                               │                            │
     │                                   │                               │                            │
     │──── POST /mo/applications ────────►                               │                            │
     │   (applicationId, status)         │                               │                            │
     │   status ∈ {INTERVIEW, ACCEPTED,  │                               │                            │
     │            REJECTED}              │                               │                            │
     │                                   │   Validate: app belongs to    │                            │
     │                                   │    MO's own job               │                            │
     │                                   │──── updateStatus() ──────────►│──── saveApplications() ──► │
     │                                   │   Set new status              │                            │
     │                                   │   Set default notes           │                            │
     │                                   │◄─── return ─────────────────  │                            │
     │                                   │                               │                            │
     │                                   │──── LogService.log(UPDATE) ─► │                            │
     │◄─── redirect /mo/applications ─── │                               │                            │
```

MO can only see and update applications for jobs where they are the organiser. TA profile data and CV files are available for review on this page. The MO can move an application to `INTERVIEW`, `ACCEPTED`, or `REJECTED`.

---

## 7. Admin Reviews Workload

```
[Browser]                         [AdminDashboardServlet]          [AdminService]
     │                                   │                               │
     │──── GET /admin/dashboard ────────►│                               │
     │   (Admin session required)        │                               │
     │                                   │──── getDashboardStats() ──────►│
     │                                   │   Count totalTA, activeTA,     │
     │                                   │    totalMO, totalApplications  │
     │                                   │    (by status), totalJobs,    │
     │                                   │    openJobs                   │
     │                                   │   Top 5 TAs by app count      │
     │                                   │   Top 3 jobs by app count     │
     │                                   │◄─── return Map<String,Object> │
     │                                   │                               │
     │                                   │──── calculateUserWorkloads()──►│
     │                                   │   For each TA:                │
     │                                   │     count total applications  │
     │                                   │   Return Map<userId, count>   │
     │                                   │◄─── return Map<String,Integer>
     │                                   │                               │
     │◄─── forward /admin/dashboard.jsp  │                               │
     │   (Stats cards + workload table)  │                               │
```

The Admin dashboard displays:
- **Stat cards**: Total TA / Active TA / Total MO / Total Applications (PENDING/ACCEPTED/REJECTED) / Total Jobs / Open Jobs
- **Top TAs**: Top 5 TAs ranked by number of submitted applications
- **Top Jobs**: Top 3 jobs ranked by number of applications received
- **Workload table**: Each TA's total application count, useful for identifying high-activity candidates
