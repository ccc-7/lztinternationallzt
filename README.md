# TA Recruitment System

A Maven-based Java web application for managing the Teaching Assistant (TA) recruitment workflow using Jakarta Servlet/JSP and CSV file storage.

---

## Table of Contents

- [Overview](#1-overview)
- [System Roles](#2-system-roles)
- [Current Project Status](#3-current-project-status)
- [Implemented Features](#4-implemented-features)
- [Tech Stack](#5-tech-stack)
- [Project Structure](#6-project-structure)
- [Key Modules](#7-key-modules)
- [Data Storage](#8-data-storage)
- [User Data Format](#9-user-data-format)
- [Skill Match Algorithm](#10-skill-match-algorithm)
- [Application Rules](#11-application-rules)
- [Default Test Accounts](#12-default-test-accounts)
- [Build and Run](#13-build-and-run)
  - [Prerequisites](#131-prerequisites)
  - [Build](#132-build)
  - [Deploy to Tomcat](#133-deploy-to-tomcat)
  - [Access the Application](#134-access-the-application)
  - [Runtime Data Directory](#135-runtime-data-directory)
- [Running Tests](#14-running-tests)
- [Demo Workflow](#15-demo-workflow)
- [Current Limitations](#16-current-limitations)
- [Known Next-Step Priorities](#17-known-next-step-priorities)
- [Notes](#18-notes)

---

## 1. Overview

The TA Recruitment System is a web-based application that streamlines the process of recruiting and managing Teaching Assistants. It provides role-specific interfaces for applicants, module organizers, and administrators.

---

## 2. System Roles

| Role | Description |
|------|-------------|
| `TA Applicant` | Students applying for Teaching Assistant positions |
| `MO (Module Organiser)` | Faculty members who create job postings and review applications |
| `Admin` | System administrators who manage users, jobs, and system settings |

---

## 3. Current Project Status

The system has reached a **demonstrable end-to-end version** with the following capabilities:

- TA can register, log in, edit profile, build a structured candidate summary, upload a PDF CV, browse jobs with match-score ordering, and submit applications.
- MO can create jobs, review applications, view candidate summaries, and open uploaded PDF CVs.
- Admin can manage users, jobs, applications, and view system logs through the admin portal.
- Admin dashboard provides workload statistics based on accepted job hours.
- Comprehensive skill-match algorithm ranks jobs based on TA profile skills.
- Full unit test coverage for services and models.

### CV Module Architecture

The CV module is split into two clearly separated parts:

1. **Candidate Summary**
   - Generated from structured TA profile fields
   - Used for quick recruiter review

2. **Original PDF CV**
   - Uploaded by the TA
   - Stored as a local file under `data/cvs/`
   - Viewable by TA, MO, and Admin with proper permission checks

---

## 4. Implemented Features

### 4.1 TA Applicant

- Account registration with multi-step form
- Login / logout with session management
- TA dashboard with personalized job recommendations
- Profile editing with structured fields
- Candidate Summary builder and preview
- PDF CV upload / replace / delete
- View uploaded PDF CV
- Browse open jobs with match-score ordering
- Job search with keyword, module code, and minimum match score filters
- Submit job applications
- View application history and status
- Withdraw or delete pending applications (right-click context menu)

### 4.2 Module Organiser (MO)

- Login / logout
- MO dashboard with job overview
- Create and publish job postings
- View applications for own jobs
- Accept / interview / reject applications
- View applicant Candidate Summary
- View applicant uploaded PDF CV when available

### 4.3 Admin

- Login / logout
- Admin dashboard with comprehensive statistics
- User management (view, toggle status)
- Job management (view all jobs)
- Application management (view all applications)
- System log viewing with pagination and search
- Workload statistics per TA based on accepted jobs
- View TA Candidate Summary
- View uploaded TA PDF CV

### 4.4 Application Rules (Enforced in `ApplicationService.apply()`)

- User must exist
- User must be application-ready:
  - `SUMMARY_COMPLETE` status, OR
  - A real uploaded PDF CV exists
- Job must exist
- Job must be `OPEN`
- Deadline must not be expired
- Accepted count must not exceed vacancies
- User year must satisfy `minYear / maxYear` constraints
- Duplicate application to the same job is blocked
- Active applications are limited to `3` per TA

### 4.5 Application Statuses

| Status | Description |
|--------|-------------|
| `PENDING` | Application submitted and awaiting review |
| `INTERVIEW` | Application shortlisted; an interview is scheduled |
| `ACCEPTED` | Application approved; TA has been selected |
| `REJECTED` | Application declined or rejected by MO/Admin |
| `WITHDRAWN` | Application withdrawn by the TA applicant |

---

## 5. Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java 17 |
| Web Framework | Jakarta Servlet / JSP |
| Tag Library | Jakarta Standard Tag Library (JSTL) |
| Build Tool | Maven 3.9+ |
| Application Server | Tomcat 10 |
| Storage | CSV files + local file storage |
| Testing | JUnit 5 |
| CSS | Custom stylesheet |

---

## 6. Project Structure

```text
ta-webapp/
├── README.md
├── pom.xml
├── data/
│   ├── ta_users.csv           # User accounts and profiles
│   ├── jobs.csv               # Job postings
│   ├── applications.csv       # Job applications
│   ├── system_logs.csv        # Audit logs
│   └── cvs/                   # Uploaded PDF CV files
├── src/main/java/edu/bupt/ta/
│   ├── controller/            # HTTP request handlers
│   │   ├── AdminDashboardServlet.java
│   │   ├── AdminServlet.java
│   │   ├── ApplyServlet.java
│   │   ├── ApplicationStatusServlet.java
│   │   ├── CandidateSummaryServlet.java
│   │   ├── CvDeleteServlet.java
│   │   ├── CvUploadServlet.java
│   │   ├── FileDownloadServlet.java
│   │   ├── HomeServlet.java
│   │   ├── JobListServlet.java
│   │   ├── LoginServlet.java
│   │   ├── LogoutServlet.java
│   │   ├── MOApplicationServlet.java
│   │   ├── MODashboardServlet.java
│   │   ├── MOJobServlet.java
│   │   ├── RegisterServlet.java
│   │   ├── TaDashboardServlet.java
│   │   └── TaProfileServlet.java
│   ├── model/                 # Data models
│   │   ├── Application.java
│   │   ├── ApplicationStatus.java
│   │   ├── ApplicationWithJob.java
│   │   ├── Job.java
│   │   ├── JobStatus.java
│   │   ├── SystemLog.java
│   │   ├── User.java
│   │   └── UserRole.java
│   ├── service/               # Business logic
│   │   ├── AdminService.java
│   │   ├── ApplicationService.java
│   │   ├── CvFileService.java
│   │   ├── DashboardService.java
│   │   ├── JobService.java
│   │   ├── LogService.java
│   │   └── UserService.java
│   └── storage/               # Data persistence
│       └── FileStorageUtil.java
├── src/main/webapp/
│   ├── assets/
│   │   ├── css/style.css
│   │   └── js/app.js
│   ├── WEB-INF/
│   │   ├── web.xml
│   │   └── jsp/
│   │       ├── common/
│   │       │   ├── cv-view.jsp
│   │       │   ├── footer.jspf
│   │       │   ├── header.jspf
│   │       │   └── flash.jspf
│   │       ├── ta/
│   │       │   ├── applications.jsp
│   │       │   ├── dashboard.jsp
│   │       │   ├── jobs.jsp
│   │       │   └── profile.jsp
│   │       ├── mo/
│   │       │   ├── applications.jsp
│   │       │   ├── dashboard.jsp
│   │       │   └── new-job.jsp
│   │       ├── admin/
│   │       │   ├── applications-manage.jsp
│   │       │   ├── dashboard.jsp
│   │       │   ├── jobs-manage.jsp
│   │       │   ├── logs.jsp
│   │       │   └── users-manage.jsp
│   │       ├── home.jsp
│   │       └── register.jsp
│   └── index.jsp
└── src/test/
    ├── java/edu/bupt/ta/
    │   ├── model/              # Model unit tests
    │   ├── service/            # Service unit tests
    │   └── storage/            # Storage unit tests
    └── resources/
        └── test-data/         # Test data files
```

---

## 7. Key Modules

### 7.1 CV-Related Backend

| Servlet | Route | Description |
|---------|-------|-------------|
| `CandidateSummaryServlet` | `/files/cv-summary/{userId}` | Displays the structured summary page |
| `FileDownloadServlet` | `/files/cv/{userId}` | Serves the uploaded original PDF CV only |
| `CvUploadServlet` | `/ta/profile/cv/upload` | Handles PDF upload / replacement |
| `CvDeleteServlet` | `/ta/profile/cv/delete` | Removes uploaded PDF CV |
| `CvFileService` | - | Validates and stores PDF files under `data/cvs/` |

### 7.2 Application Flow

| Servlet | Route | Description |
|---------|-------|-------------|
| `ApplyServlet` | `/apply` | Handles job application submission |
| `ApplicationStatusServlet` | `/ta/application/status` | Updates application status (accept/reject/interview) |
| `MOApplicationServlet` | `/mo/applications` | MO reviews and manages applications |
| `ApplicationService` | - | Business logic for applications |

### 7.3 Dashboard & Statistics

| Servlet | Route | Description |
|---------|-------|-------------|
| `TaDashboardServlet` | `/ta/dashboard` | TA dashboard with pending counts and job recommendations |
| `MODashboardServlet` | `/mo/dashboard` | MO dashboard with job and application overview |
| `AdminDashboardServlet` | `/admin/dashboard` | Admin dashboard with workload statistics and metrics |
| `DashboardService` | - | Dashboard data aggregation |
| `AdminService` | - | Workload calculation and dashboard statistics |

### 7.4 Data Storage

| Service | File | Description |
|---------|------|-------------|
| `UserService` | `ta_users.csv` | User account management |
| `JobService` | `jobs.csv` | Job posting management |
| `ApplicationService` | `applications.csv` | Application tracking |
| `LogService` | `system_logs.csv` | Audit logging |
| `FileStorageUtil` | - | Core CSV file operations |

---

## 8. Data Storage

The project does **not** use MySQL or any other database. All data is stored in CSV files:

| File | Purpose |
|------|---------|
| `data/ta_users.csv` | User accounts, profiles, and CV metadata |
| `data/jobs.csv` | Job postings |
| `data/applications.csv` | Job applications |
| `data/system_logs.csv` | System audit logs |
| `data/cvs/*.pdf` | Uploaded PDF CV files |

---

## 9. User Data Format

### 9.1 `ta_users.csv` Fields

| Field | Description |
|-------|-------------|
| `userId` | Unique user identifier (e.g., "U001") |
| `username` | Login username |
| `password` | Hashed password |
| `name` | Display name |
| `email` | Email address |
| `role` | `TA`, `MO`, or `ADMIN` |
| `year` | Academic year |
| `major` | Major/Department |
| `skills` | Pipe-separated skill list |
| `status` | `ACTIVE` or `INACTIVE` |
| `availability` | Weekly availability hours |
| `personalStatement` | Personal statement text |
| `relevantCourses` | Relevant coursework |
| `projectExperience` | Project experience description |
| `preferredRole` | Preferred TA role |
| `summaryStatus` | `INCOMPLETE`, `SUMMARY_COMPLETE`, or `CV_UPLOADED` |
| `cvStoredName` | Stored file name in `data/cvs/` |
| `cvOriginalName` | Original upload filename |
| `cvContentType` | MIME type (e.g., `application/pdf`) |
| `cvUploadedAt` | Upload timestamp |
| `cvStatus` | `MISSING` or `UPLOADED` |

### 9.2 CV Metadata Meaning

- `cvStoredName`: Stored file name in `data/cvs/`
- `cvOriginalName`: Original file name uploaded by the TA
- `cvContentType`: Expected to be `application/pdf`
- `cvUploadedAt`: Upload timestamp
- `cvStatus`: `UPLOADED` or `MISSING`

---

## 10. Skill Match Algorithm

The `JobService.calculateMatchScore()` method implements a sophisticated skill matching algorithm:

### Match Types (in priority order)

1. **Direct match**: User skill exactly equals required skill
2. **Alias match**: User skill matches a canonical alias of the required skill
3. **Partial match**: Skills in the same category share a substring (70% weight)
4. **Reverse alias match**: Required skill matches a canonical alias (80% weight)

### Category Weights

| Category | Weight | Examples |
|----------|--------|----------|
| `concept` | 1.5 | Data Structures, Algorithms, OOP, Design Patterns |
| `language` | 1.3 | Java, Python, JavaScript, C++, TypeScript |
| `ml` | 1.2 | Machine Learning, TensorFlow, PyTorch, NLP |
| `database` | 1.1 | MySQL, PostgreSQL, MongoDB, Redis |
| `framework` | 1.0 | Spring, React, Django, Node.js |
| `tools` | 0.9 | Git, Docker, Kubernetes, AWS |

### Jaccard Bonus

Up to 15 points bonus for overlap between user and required skill sets.

---

## 11. Application Rules

The `ApplicationService.apply()` method enforces the following rules:

| Rule | Description |
|------|-------------|
| User existence | User must exist in the system |
| Application readiness | User must have `SUMMARY_COMPLETE` status OR uploaded PDF CV |
| Job existence | Job must exist |
| Job status | Job must be `OPEN` |
| Year requirement | User year must satisfy `minYear / maxYear` |
| Deadline | Application deadline must not be expired |
| Vacancy limit | Accepted count must not exceed vacancies |
| Duplicate check | Cannot apply to the same job twice |
| Active limit | Maximum 3 active (PENDING or INTERVIEW) applications per TA |

---

## 12. Default Test Accounts

| Role | Username | Password | Description |
|------|----------|----------|-------------|
| TA | `seele` | `123456` | TA applicant for demo |
| MO | `mo1` | `123456` | Module organiser for demo |
| Admin | `admin` | `123456` | System administrator |

---

## 13. Build and Run

### 13.1 Prerequisites

- **JDK 17** or higher
- **Maven 3.9+**
- **Apache Tomcat 10** (or compatible Jakarta EE 10 servlet container)

### 13.2 Build

Navigate to the project root directory and run:

```powershell
mvn clean package
```

Expected output:
- `BUILD SUCCESS`
- Generated WAR file: `target/ta-webapp.war`

### 13.3 Deploy to Tomcat

Copy the WAR file to Tomcat's webapps directory:

```powershell
copy target\ta-webapp.war "D:\apache-tomcat-10.1.52\webapps\"
```

Start Tomcat:

```powershell
cd D:\apache-tomcat-10.1.52\bin
.\startup.bat
```

> **Note**: Replace `D:\apache-tomcat-10.1.52` with your actual Tomcat installation path.

### 13.4 Access the Application

After starting Tomcat, access the application at:

- `http://localhost:8080/ta-webapp/`
- `http://localhost:8080/ta-webapp/home`

### 13.5 Runtime Data Directory

By default, the application uses the local `data/` directory in the working directory.

#### External Data Directory

The application supports external runtime data directories via system properties:

| Property | Description |
|----------|-------------|
| `-Dta.data.dir` | Primary data directory for runtime files |
| `-Dta.data.mirror.dir` | Mirror directory (keeps repo `data/` synced) |

#### Example (Windows PowerShell)

```powershell
$env:CATALINA_OPTS='-Dta.data.dir=D:\apache-tomcat-10.1.52\ta-data -Dta.data.mirror.dir=C:\Users\username\Documents\ta-webapp\data'
cd D:\apache-tomcat-10.1.52\bin
.\startup.bat
```

#### Example (Linux/macOS Bash)

```bash
export CATALINA_OPTS='-Dta.data.dir=/opt/tomcat/ta-data -Dta.data.mirror.dir=/home/user/ta-webapp/data'
cd /opt/tomcat/bin
./startup.sh
```

---

## 14. Running Tests

Execute unit tests with Maven:

```powershell
mvn test
```

Test coverage includes:
- Model tests (User, Job, Application, ApplicationWithJob)
- Service tests (UserService, JobService, ApplicationService, AdminService, DashboardService, LogService)
- Skill match algorithm tests
- File storage utility tests

---

## 15. Demo Workflow

### TA Demo

1. Log in as `seele`
2. Open **My Profile**
3. Edit structured profile fields (skills, courses, experience)
4. Preview **Candidate Summary**
5. Upload a PDF CV
6. Open uploaded PDF
7. Browse **Job Listings** and view match scores
8. Submit an application
9. Right-click on a pending application to **Withdraw** or **Delete Record**

### MO Demo

1. Log in as `mo1`
2. Open **Dashboard** to view job overview
3. Create a new job posting
4. Open **Applications** to review submissions
5. Click **View Summary** for each applicant
6. Click **View CV** for applicants with uploaded PDFs
7. Accept / move to interview / reject applications

### Admin Demo

1. Log in as `admin`
2. Inspect **Dashboard** with statistics and workload charts
3. Review **Users** management
4. Review **Jobs** management
5. Review **Applications** management
6. View **System Logs** with search and pagination
7. Open TA summaries and uploaded CVs

---

## 16. Current Limitations

### Not Yet Implemented

- Advanced TA job search UI with more filters (basic filtering is implemented)
- PDF parsing and auto-fill back into profile
- Email notification system
- Multi-language support
- Password reset functionality
- Password reset functionality

---

## 17. Known Next-Step Priorities

Recommended next iteration order:

1. Improve Profile UI and overall TA-facing experience
2. Add advanced TA job search / filtering
3. Implement PDF parsing for auto-fill (optional enhancement)
4. Add email notification system
5. Improve Admin workload statistics visualization
6. Add password reset functionality
7. Update user manual / demo script / testing notes

---

## 18. Notes

- The current design intentionally keeps `Candidate Summary` and `Original PDF CV` separate.
- CSV remains the main structured storage format.
- PDF parsing is **not required** for the current stable version and should be treated as an optional enhancement.
- All public service methods are designed for dependency injection to support unit testing.
- System logs track user actions for audit purposes.
