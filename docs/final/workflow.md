# TA Recruitment System — Workflow Documentation

## 1. TA Registration and Login

### Registration Flow

#### Step-by-Step Process
1.  **Browser → RegisterServlet**  
    The browser sends a `POST /register` request with the following parameters:
    - `username`, `password`, `name`, `email`, `year`, `major`, `skills`, `availability`, `personalStatement`, `relevantCourses`, `projectExp`, `preferredRole`

2.  **RegisterServlet → UserService**  
    `RegisterServlet` invokes the `registerTa()` method on `UserService`.

3.  **UserService Business Logic**  
    `UserService` performs the following operations:
    - Checks the uniqueness of the provided username
    - Generates the next sequential user ID in the format `U{NNN}` (e.g., `U001`, `U002`)
    - Normalizes the `skills` field by replacing commas (`,`) with pipe symbols (`|`)
    - Calculates the `summaryStatus` for the new user
    - Sets the user's `cvStatus` to `MISSING`

4.  **UserService → FileStorageUtil**  
    `UserService` calls `saveUsers()`, which writes the new user record to the `ta_users.csv` file.

5.  **UserService → RegisterServlet**  
    `UserService` returns the created `User` object back to `RegisterServlet`.

6.  **RegisterServlet Session Setup**  
    `RegisterServlet` sets the `currentUser` attribute in the user's session.

7.  **RegisterServlet → Browser**  
    The servlet sends a redirect response to the browser, directing it to the `/ta/dashboard` page.

### Login Flow

#### Step-by-Step Process
1.  **Browser → LoginServlet**  
    The browser sends a `POST /login` request with the parameters: `username`, `password`, and `role`.

2.  **LoginServlet → UserService**  
    `LoginServlet` invokes the `authenticate()` method on `UserService`.

3.  **UserService → FileStorageUtil**  
    `UserService` calls `loadUsers()` on `FileStorageUtil`, which reads all user records from the CSV file.

4.  **UserService Processing**  
    `UserService` filters the loaded users by the provided `username` and `password`, and returns the matching `User` object to `LoginServlet`.

5.  **LoginServlet Validation**  
    `LoginServlet` performs the following validation checks on the returned user:
    - The user record must exist
    - The user's role must match the role selected during login
    - The user's account status must be `ACTIVE`

6.  **LoginServlet → LogService**  
    If validation passes, `LoginServlet` calls `LogService.log(LOGIN)` to record the login event.

7.  **Session Setup & Redirect**
    - `LoginServlet` sets the `currentUser` attribute in the user's session.
    - It then redirects the browser to the appropriate dashboard based on the user's role:
      - TA → `/ta/dashboard`
      - MO → `/mo/dashboard`
      - ADMIN → `/admin/dashboard`

---

## 2. TA Creates / Updates Profile

### Profile Flow
#### GET /ta/profile (Load Profile)
1.  **Browser → TaProfileServlet**  
    The browser sends a `GET /ta/profile` request to load the profile page.

2.  **TaProfileServlet → UserService**  
    - `TaProfileServlet` calls `findById(userId)` on `UserService` to fetch the current user’s data.
    - It also checks if the user has uploaded a CV via `hasUploadedCv()`.

3.  **UserService → TaProfileServlet**  
    `UserService` returns the `User` object. `TaProfileServlet` then calls `calculateSummaryStatus()` to retrieve the user’s profile completion status.

4.  **TaProfileServlet → Browser**  
    The servlet forwards the request to `/ta/profile.jsp`, rendering the profile form with the loaded user data and completion status.

---

#### POST /ta/profile (Update Profile)
1.  **Browser → TaProfileServlet**  
    The browser sends a `POST /ta/profile` request with updated profile fields:
    - `name`, `email`, `year`, `major`, `skills`, `availability`, `personalStatement`, `relevantCourses`, `projectExperience`, `preferredRole`

2.  **TaProfileServlet → UserService**  
    `TaProfileServlet` calls `updateProfile()` on `UserService` with the new data.

3.  **UserService Processing**  
    - Updates the 10 profile fields in memory.
    - Recalculates `summaryStatus` (profile completion level).
    - Recalculates `cvStatus` (CV upload status).

4.  **UserService → FileStorageUtil**  
    `UserService` calls `saveUsers()` on `FileStorageUtil`, which writes the updated user record back to the CSV file.

5.  **UserService → TaProfileServlet**  
    `UserService` returns the updated `User` object.

6.  **TaProfileServlet → Browser**  
    - The servlet updates the `currentUser` attribute in the user’s session.
    - It redirects the browser back to `/ta/profile` to reflect the updated data.

---

#### Additional Notes
The profile form is split into three sections:
- **Basic Profile**: `name`, `email`, `year`, `major`
- **Skills & Availability**: `skills`, `availability`, `preferred role`
- **Candidate Summary Builder**: `personal statement`, `relevant courses`, `project experience`

The `summaryStatus` is recalculated after each save and displayed on the profile page as one of three states: `SUMMARY_COMPLETE`, `BASIC_COMPLETE`, or `INCOMPLETE`.

---

## 3. TA Uploads or Deletes CV

### Upload Flow

### Upload Flow

#### Step-by-Step Process
1.  **Browser → CvUploadServlet**  
    The browser sends a `POST /ta/profile/cv/upload` request with `enctype="multipart/form-data"` and the uploaded `cvFile`.

2.  **CvUploadServlet → CvFileService**  
    `CvUploadServlet` invokes `savePDF(userId, Part)` on `CvFileService`.

3.  **CvFileService Validation & Storage**  
    `CvFileService` performs the following validations and operations:
    - Validates the file size (≤ 5MB)
    - Validates the file extension
    - Validates the `Content-Type` header
    - Writes the file to `data/cvs/{userId}.pdf` using `Files.copy()`
    - Returns a `SavedCvFile` object to `CvUploadServlet`

4.  **CvUploadServlet → CvFileService**  
    `CvUploadServlet` calls `updateCvMetadata()` on `CvFileService`.

5.  **CvFileService → FileStorageUtil**  
    `CvFileService` updates the 5 CV-related columns in the user record and calls `saveUsers()` on `FileStorageUtil` to persist the changes to the CSV file.

6.  **Finalization & Redirect**
    - `CvFileService` returns the updated `User` object to `CvUploadServlet`.
    - `CvUploadServlet` updates the `currentUser` attribute in the user's session.
    - The servlet sends a redirect response to the browser, directing it back to `/ta/profile`.

### Delete Flow

### Delete Flow

#### Step-by-Step Process
1.  **Browser → CvDeleteServlet**  
    The browser sends a `POST /ta/profile/cv/delete` request to initiate the CV deletion process.

2.  **CvDeleteServlet → CvFileService**  
    `CvDeleteServlet` calls `deleteCv(storedName)` on `CvFileService` to remove the physical CV file.

3.  **CvFileService File Deletion**  
    `CvFileService` executes `Files.deleteIfExists()` to delete the stored PDF file from disk, then returns a boolean indicating the deletion success to `CvDeleteServlet`.

4.  **CvDeleteServlet → CvFileService**  
    `CvDeleteServlet` calls `clearCvMetadata()` on `CvFileService` to update the user’s profile record.

5.  **CvFileService Metadata Update**  
    `CvFileService` performs the following operations:
    - Sets `cvStoredName` to an empty string (`""`)
    - Sets `cvStatus` to `MISSING`
    - Calls `saveUsers()` on `FileStorageUtil` to persist the changes to the CSV file

6.  **Finalization & Redirect**
    - `CvFileService` returns the updated `User` object to `CvDeleteServlet`.
    - `CvDeleteServlet` redirects the browser back to `/ta/profile`.

---

#### Additional Notes
The TA's structured profile (stored in CSV) and the original PDF file (stored on disk) are managed as two separate artifacts. The profile remains accessible even without an uploaded CV.

---

## 4. TA Applies for a Job

### Job List & Application Flow

#### GET /jobs (View Open Jobs)
1.  **Browser → JobListServlet**  
    The browser sends a `GET /jobs` request (valid TA session required).

2.  **JobListServlet → JobService**  
    `JobListServlet` calls `getOpenJobsForUser()` on `JobService`.

3.  **JobService Processing**  
    - Loads all open jobs
    - Calculates a `matchScore` (0–100) for each job by comparing the TA’s skills against the job’s required skills
    - Sorts jobs by `matchScore` in descending order
    - Returns the sorted `List<Job>` to `JobListServlet`

4.  **JobListServlet → Browser**  
    The servlet forwards the request to `/ta/jobs.jsp`, where jobs are displayed with their calculated match scores. Jobs with a score ≥ 60 are highlighted as “matched positions” on the TA dashboard.

---

#### POST /apply (Submit Job Application)
1.  **Browser → JobListServlet**  
    The browser sends a `POST /apply` request with the `jobId` parameter.

2.  **JobListServlet → ApplicationService**  
    `JobListServlet` calls `ApplicationService.apply()`.

3.  **Application Validation (9 Checks)**
    The service performs the following validations before creating the application:
    1.  The user exists
    2.  The user is application-ready
    3.  The job exists
    4.  The job is in `OPEN` status
    5.  The user’s year is within the job’s `[min, max]` range
    6.  The job’s application deadline has not passed
    7.  The job’s vacancy limit has not been exceeded
    8.  The user has not already applied for this job (no duplicate applications)
    9.  The user has no more than 3 active applications

4.  **Application Creation & Persistence**
    - Generates the next sequential application ID in the format `A{NNN}`
    - Sets the application status to `PENDING`
    - Copies the user’s availability into the application record
    - Saves the new application to `applications.csv`

5.  **JobListServlet → Browser**  
    The servlet redirects the browser back to `/jobs`.

On the job listing page, each open job displays a match score (0–100) calculated by comparing the TA's skills against the job's required skills. Jobs with a score ≥ 60 are highlighted as "matched positions" on the TA dashboard.

---

## 5. MO Publishes a Job

### MO Job Creation Flow

#### GET /mo/jobs/new (Display Job Creation Form)
1.  **Browser → MOJobServlet**  
    The browser sends a `GET /mo/jobs/new` request (valid MO session required).

2.  **MOJobServlet → Browser**  
    The servlet forwards the request to `/mo/new-job.jsp`, rendering the job creation form for the Module Organiser.

---

#### POST /mo/jobs/new (Submit New Job)
1.  **Browser → MOJobServlet**  
    The browser sends a `POST /mo/jobs/new` request with the following job details:
    - `title`, `moduleCode`, `organiser`, `hours`, `minYear`, `maxYear`, `requiredSkills`, `deadline`, `vacancies`

2.  **MOJobServlet → JobService**  
    `MOJobServlet` calls `createJob()` on `JobService`.

3.  **JobService Processing**  
    `JobService` performs the following operations:
    - Generates the next sequential job ID in the format `J{NNN}`
    - Sets the job status to `OPEN`
    - Sets the `organiser` field to the MO’s display name
    - Normalizes the `requiredSkills` field (e.g., replacing commas with pipes)
    - Calls `saveJobs()` on `FileStorageUtil` to persist the new job to the CSV file

4.  **MOJobServlet → LogService**  
    `JobService` returns the created `Job` object to `MOJobServlet`, which then calls `LogService.log(CREATE)` to record the job creation event.

5.  **MOJobServlet → Browser**  
    The servlet redirects the browser back to `/mo/dashboard`.

The `organiser` field is automatically set to the MO's `displayName` from the session, ensuring that the MO can only see and manage their own job postings.

---

## 6. MO Selects an Applicant

### MO Application Review Flow

#### GET /mo/applications (View Applications)
1.  **Browser → MOApplicationServlet**  
    The browser sends a `GET /mo/applications` request (valid MO session required).

2.  **MOApplicationServlet Data Loading**  
    The servlet performs the following data preparation:
    - Loads all jobs created by the logged-in MO (filtered by organiser)
    - Loads all applications linked to those job IDs
    - Loads applicant names via `UserService.findById()`
    - Checks whether each applicant has uploaded a CV using `hasUploadedCv()`

3.  **MOApplicationServlet → Browser**  
    The servlet forwards the request to `/mo/applications.jsp`. On this page, the MO can review applicant profiles and CVs, and filter the list by specific jobs.

---

#### POST /mo/applications (Update Application Status)
1.  **Browser → MOApplicationServlet**  
    The browser sends a `POST /mo/applications` request with the parameters:
    - `applicationId`
    - `status` (one of: `INTERVIEW`, `ACCEPTED`, `REJECTED`)

2.  **MOApplicationServlet Validation**  
    The servlet validates that the application belongs to a job created by the logged-in MO, to prevent unauthorised status changes.

3.  **MOApplicationServlet → ApplicationService**  
    The servlet calls `updateStatus()` on `ApplicationService`.

4.  **ApplicationService Processing**  
    - Sets the new application status
    - Sets default notes (e.g., for interview scheduling or rejection feedback)
    - Calls `saveApplications()` on `FileStorageUtil` to persist the updated record to the CSV file

5.  **Logging & Redirect**
    - The servlet calls `LogService.log(UPDATE)` to record the status change.
    - Finally, it redirects the browser back to `/mo/applications`.

MO can only see and update applications for jobs where they are the organiser. TA profile data and CV files are available for review on this page. The MO can move an application to `INTERVIEW`, `ACCEPTED`, or `REJECTED`.

---

## 7. Admin Reviews Workload

## 7. Admin Reviews Workload

### Admin Dashboard Flow

#### GET /admin/dashboard
1.  **Browser → AdminDashboardServlet**  
    The browser sends a `GET /admin/dashboard` request (valid Admin session required).

2.  **AdminDashboardServlet → AdminService**  
    The servlet calls `getDashboardStats()` on `AdminService` to retrieve system-wide statistics:
    - Counts of `totalTA`, `activeTA`, `totalMO`, and `totalApplications` (broken down by status)
    - Counts of `totalJobs` and `openJobs`
    - Lists of the **Top 5 TAs by application count** and **Top 3 jobs by application count**

3.  **AdminService → AdminDashboardServlet**  
    `AdminService` returns the collected statistics as a `Map<String, Object>`.

4.  **AdminDashboardServlet → AdminService**  
    The servlet calls `calculateUserWorkloads()` on `AdminService` to generate workload data:
    - Iterates over each TA and counts their total number of applications
    - Returns a `Map<String, Integer>` with `userId` as the key and application count as the value

5.  **AdminDashboardServlet → Browser**  
    The servlet forwards the request to `/admin/dashboard.jsp`, where the data is rendered as:
    - Stats cards displaying the high-level system metrics
    - A workload table showing the application count per TA

The Admin dashboard displays:
- **Stat cards**: Total TA / Active TA / Total MO / Total Applications (PENDING/ACCEPTED/REJECTED) / Total Jobs / Open Jobs
- **Top TAs**: Top 5 TAs ranked by number of submitted applications
- **Top Jobs**: Top 3 jobs ranked by number of applications received
- **Workload table**: Each TA's total application count, useful for identifying high-activity candidates
