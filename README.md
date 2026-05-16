# TA Recruitment System

A Maven-based Java Servlet/JSP web application for the TA recruitment workflow.  
The system supports three roles:

- `TA Applicant`
- `MO (Module Organiser)`
- `Admin`

The project uses **CSV files + local file storage** instead of a database, which matches the lightweight storage requirement of the coursework.

---

## 1. Current Project Status

The system has already reached a **demonstrable end-to-end version**:

- TA can register, log in, edit profile, build a structured candidate summary, upload a PDF CV, browse jobs, and apply.
- MO can create jobs, review applications, view candidate summaries, and open uploaded PDF CVs.
- Admin can manage users, jobs, applications, and logs through the admin portal.

The current CV module is now split into two clearly separated parts:

1. `Candidate Summary`
   - generated from structured TA profile fields
   - used for quick recruiter review

2. `Original PDF CV`
   - uploaded by the TA
   - stored as a local file under `data/cvs/`
   - viewable by TA, MO, and Admin with proper permission checks

---

## 2. Implemented Features

### 2.1 TA Applicant

- Account registration with multi-step form
- Login / logout
- TA dashboard
- Profile editing
- Candidate Summary builder
- Candidate Summary preview
- PDF CV upload / replace / delete
- View uploaded PDF CV
- Browse open jobs with match-score ordering
- Submit job applications
- View application history and status

### 2.2 Module Organiser

- Login / logout
- MO dashboard
- Create and publish jobs
- View applications for own jobs
- Accept / interview / reject applications
- View applicant Candidate Summary
- View applicant uploaded PDF CV when available

### 2.3 Admin

- Login / logout
- Admin dashboard
- User management
- Job management
- Application management
- System log viewing
- View TA Candidate Summary
- View uploaded TA PDF CV

### 2.4 Application Rules Already Enforced

The application flow has already been strengthened in `ApplicationService.apply()`:

- user must exist
- user must be application-ready:
  - `SUMMARY_COMPLETE`, or
  - a real uploaded PDF CV exists
- job must exist
- job must be `OPEN`
- deadline must not be expired
- accepted count must not exceed vacancies
- user year must satisfy `minYear / maxYear`
- duplicate application to the same job is blocked
- active applications are limited to `3`

This means the core **job application rule optimization has already been implemented**.

---

## 3. Tech Stack

- `Java 17`
- `Jakarta Servlet / JSP`
- `JSTL`
- `Maven`
- `Tomcat 10`
- `CSV + local file storage`

---

## 4. Project Structure

```text
ta-webapp/
├─ README.md
├─ pom.xml
├─ data/
│  ├─ ta_users.csv
│  ├─ jobs.csv
│  ├─ applications.csv
│  └─ cvs/                  # uploaded PDF CV files
├─ src/main/java/edu/bupt/ta/
│  ├─ controller/
│  ├─ model/
│  ├─ service/
│  └─ storage/
└─ src/main/webapp/
   ├─ assets/css/
   └─ WEB-INF/jsp/
      ├─ common/
      ├─ ta/
      ├─ mo/
      └─ admin/
```

---

## 5. Key Modules

### 5.1 CV-Related Backend

- `CandidateSummaryServlet`
  - route: `/files/cv-summary/{userId}`
  - displays the structured summary page

- `FileDownloadServlet`
  - route: `/files/cv/{userId}`
  - serves the uploaded original PDF CV only

- `CvUploadServlet`
  - route: `/ta/profile/cv/upload`
  - handles PDF upload / replacement

- `CvDeleteServlet`
  - route: `/ta/profile/cv/delete`
  - removes uploaded PDF CV

- `CvFileService`
  - validates and stores PDF files
  - local storage under `data/cvs/`

### 5.2 Data Storage

The project does **not** use MySQL or any other database.

- structured business data:
  - `ta_users.csv`
  - `jobs.csv`
  - `applications.csv`

- original uploaded CV files:
  - `data/cvs/*.pdf`

---

## 6. User Data Format

### 6.1 `ta_users.csv`

The current user file includes both profile fields and CV metadata.

Important fields:

- `userId`
- `username`
- `password`
- `name`
- `email`
- `role`
- `year`
- `major`
- `skills`
- `status`
- `availability`
- `personalStatement`
- `relevantCourses`
- `projectExperience`
- `preferredRole`
- `summaryStatus`
- `cvStoredName`
- `cvOriginalName`
- `cvContentType`
- `cvUploadedAt`
- `cvStatus`

### 6.2 CV Metadata Meaning

- `cvStoredName`
  - stored file name in `data/cvs/`
- `cvOriginalName`
  - original file name uploaded by the TA
- `cvContentType`
  - expected to be `application/pdf`
- `cvUploadedAt`
  - upload time
- `cvStatus`
  - `UPLOADED` or `MISSING`

---

## 7. Default Test Accounts

| Role | Username | Password |
|------|----------|----------|
| TA | `seele` | `123456` |
| MO | `mo1` | `123456` |
| Admin | `admin` | `123456` |

---

## 8. Build and Run

### 8.1 Prerequisites

- `JDK 17`
- `Maven 3.9+`
- `Tomcat 10`

### 8.2 Build

Run in the project root:

```powershell
mvn clean package
```

Expected output:

- `BUILD SUCCESS`
- generated file: `target/ta-webapp.war`

### 8.3 Deploy to Tomcat

```powershell
copy target\ta-webapp.war "D:\apache-tomcat-10.1.52\webapps\"
```

Start Tomcat:

```powershell
cd D:\apache-tomcat-10.1.52\bin
.\startup.bat
```

### 8.4 Access

- `http://localhost:8080/ta-webapp/`
- `http://localhost:8080/ta-webapp/home`

---

## 9. Runtime Data Directory

By default, the application can run directly with the local `data/` directory.

It also supports an external runtime data directory through:

- `-Dta.data.dir=...`
- `-Dta.data.mirror.dir=...`

This is useful when Tomcat should write runtime files outside the project folder while keeping the repo `data/` directory mirrored for inspection.

Example:

```powershell
$env:CATALINA_OPTS='-Dta.data.dir=D:\apache-tomcat-10.1.52\ta-data -Dta.data.mirror.dir=C:\Users\siyuen\Desktop\all_code\JavaIDEA\TA_system\ta-webapp\data'
cd D:\apache-tomcat-10.1.52\bin
.\startup.bat
```

---

## 10. Current Functional Scope and Limitations

### Already Done

- structured Candidate Summary workflow
- real PDF CV upload / replace / delete
- MO/Admin document viewing
- strengthened application rules
- CSV compatibility for profile and CV metadata

### Not Done Yet

- TA job search / filter UI
- Admin workload based on accepted job hours
- small dashboard charts / visual summaries
- `admin/stats` dead route cleanup
- PDF parsing and auto-fill back into profile
- automated tests

These are the main remaining tasks for the next iteration.

---

## 11. Recommended Demo Flow

### TA Demo

1. log in as `seele`
2. open `My Profile`
3. edit structured profile fields
4. preview Candidate Summary
5. upload a PDF CV
6. open uploaded PDF
7. browse jobs and submit an application

### MO Demo

1. log in as `mo1`
2. open applications
3. review `View Summary`
4. open `View CV` for applicants with uploaded PDFs
5. accept / interview / reject

### Admin Demo

1. log in as `admin`
2. inspect dashboard
3. review users / jobs / applications
4. open TA summaries and uploaded CVs

---

## 12. Known Next-Step Priorities

Recommended next iteration order:

1. improve Profile UI and overall TA-facing experience
2. add TA job search / filtering
3. improve Admin workload statistics
4. clean dead routes and dashboard presentation
5. update user manual / demo script / testing notes
6. only then consider PDF parsing as an optional enhancement

---

## 13. Notes

- The current design intentionally keeps `Candidate Summary` and `Original PDF CV` separate.
- CSV remains the main structured storage format.
- PDF parsing is **not required** for the current stable version and should be treated as an optional enhancement rather than a blocking task.
