# Iteration2 System Design (Version 1)

## 1. Document Purpose

This document records:
- understanding of the coursework requirements from the project PDFs,
- the current architecture and implementation status in ta-webapp,
- requirement-to-implementation alignment for the current baseline version.

Date: 2026-03-30

## 2. Requirement Understanding (From PDF)

### 2.1 Mandatory technical constraints
From the coursework handout:
- The system must be a Java standalone app or lightweight Java Servlet/JSP web app.
- Data storage must use text files (CSV/JSON/XML/TXT), not a database.
- The project should deliver a complete prototype and implement selected core functions first.

Current project status against constraints:
- Servlet/JSP web app: satisfied.
- Text-file storage (CSV): satisfied.
- No database: satisfied.

### 2.2 High-level business requirements (handout suggested core functions)
- TA can create applicant profile.
- TA can upload CV.
- TA can find available jobs.
- TA can apply for jobs.
- TA can check application status.
- MO can post jobs.
- MO can select applicants.
- Admin can check TA overall workload.

### 2.3 Team-defined MVP direction (Iteration1 report)
From the team report, the MVP focuses on end-to-end recruitment flow first:
- account/login and role access,
- TA browse/apply/status,
- MO post/review/decision,
- Admin workload summary.

The report also mentions constraints/targets such as:
- max 3 applications per TA,
- max 3 accepted positions per TA,
- CV upload support (PDF/DOC),
- clear status visibility.

## 3. Current System Architecture

The current implementation uses layered MVC-style design.

### 3.1 Layers
- Controller layer: Servlet endpoints and session/role checks.
- Service layer: business logic and rule execution.
- Model layer: User, Job, Application domain objects and enums.
- Storage layer: CSV read/write and basic ID generation.
- View layer: JSP pages for TA/MO/Admin workflows.

### 3.2 Key package structure
- src/main/java/edu/bupt/ta/controller
- src/main/java/edu/bupt/ta/service
- src/main/java/edu/bupt/ta/model
- src/main/java/edu/bupt/ta/storage
- src/main/webapp/WEB-INF/jsp
- data/*.csv

### 3.3 Runtime and deployment
- Java 17, Maven WAR packaging, Jakarta Servlet/JSP + JSTL.
- Deploy to Tomcat.
- Home entry redirects to /home.

## 4. Implemented Functionality (Current Baseline)

### 4.1 Authentication and role routing
Implemented:
- Login by username/password with selected role check.
- Session currentUser creation.
- Role-based redirection to TA/MO/Admin dashboards.
- Logout and session invalidation.

### 4.2 TA side
Implemented:
- TA registration (username/password/profile fields).
- TA dashboard with profile summary and dashboard metrics.
- Browse open job list.
- Submit application.
- View own application status list.

Partially implemented:
- Matching recommendation is present as rule-based skill overlap percentage.
- Search/filter controls exist in TA dashboard UI, but no backend filtering query is implemented.

Not implemented yet:
- CV upload (no upload endpoint/form/field persistence for CV file path).
- Profile edit after registration.
- Rule: TA max 3 applications.

### 4.3 MO side
Implemented:
- MO dashboard (job count, pending count, accepted count).
- Create new job posting.
- View applications.
- Update application status through MO actions (accept/reject buttons).

Partially implemented:
- Application status enum includes INTERVIEW, but current MO page action exposes only ACCEPTED/REJECTED buttons.

Not implemented yet:
- Edit/close posted job.
- Restrict list to only applications of MO-owned jobs (current page displays all applications).
- Rule: max 3 accepted positions per TA.

### 4.4 Admin side
Implemented:
- Admin dashboard showing workload overview table.
- Workload currently calculated as number of applications per TA user.

Partially implemented:
- The handout/report expectation of workload balancing may require accepted-assignment or hour-based view; current version is application-count based.

### 4.5 AI-related capability
Implemented (lightweight):
- Skill matching score based on overlap ratio between TA skills and required job skills.

Not implemented:
- Missing skill identification.
- Workload balancing recommendations.
- Explainable recommendation details beyond simple score.

## 5. Endpoint and Page Mapping (Implemented)

### 5.1 Main endpoints
- /home
- /login
- /logout
- /register
- /ta/dashboard
- /jobs
- /apply
- /applications
- /mo/dashboard
- /mo/jobs/new
- /mo/applications
- /mo/applications/update
- /admin/dashboard

### 5.2 Main JSP pages
- WEB-INF/jsp/home.jsp
- WEB-INF/jsp/register.jsp
- WEB-INF/jsp/ta/dashboard.jsp
- WEB-INF/jsp/ta/jobs.jsp
- WEB-INF/jsp/ta/applications.jsp
- WEB-INF/jsp/mo/dashboard.jsp
- WEB-INF/jsp/mo/new-job.jsp
- WEB-INF/jsp/mo/applications.jsp
- WEB-INF/jsp/admin/dashboard.jsp

## 6. Data and Persistence Design (Current)

CSV files:
- data/ta_users.csv
- data/jobs.csv
- data/applications.csv

Current behavior:
- Storage utility initializes headers and default test users.
- IDs are generated as Uxxx/Jxxx/Axxx by scanning existing max numeric suffix.
- Read/write logic is encapsulated in FileStorageUtil.
- No relational database and no ORM.

## 7. Requirement Alignment Summary

### 7.1 Core requirement alignment
- TA create profile: implemented (via registration).
- TA upload CV: not implemented.
- TA find jobs: implemented.
- TA apply for jobs: implemented.
- TA check status: implemented.
- MO post jobs: implemented.
- MO select applicants: implemented (basic accept/reject flow).
- Admin check TA workload: implemented (application-count summary).

### 7.2 Iteration status interpretation
Current version is a valid baseline MVP for the main recruitment flow and matches most core handout items.
Primary gaps for next iteration are CV upload, hard business limits (3-apply / 3-accept), and stronger MO/Admin management capabilities.

## 8. Scope Boundary for This Version

This document describes only the current implemented architecture and features in code.
It does not change source code, data schema, or deployment configuration.
