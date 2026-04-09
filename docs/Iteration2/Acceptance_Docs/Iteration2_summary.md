# Iteration 2 Summary

Date: 2026-04-09

This summary is prepared for the 12 April 2026 intermediate assessment. It explains the current scope, architecture, implementation status, and known gaps of the `ta-webapp` prototype.

## 1. Project Objective and Scope

The project aims to build a lightweight Teaching Assistant Recruitment System for BUPT International School. The system is intended to replace manual recruitment steps based on forms and spreadsheets with a simple web-based workflow.

For Iteration 2, the project scope is intentionally focused on the end-to-end core recruitment flow:
- user login and role-based access;
- TA registration and profile maintenance;
- TA job browsing and application submission;
- MO job posting and applicant decision handling;
- Admin workload overview and system-level management support.

This iteration is designed as a working prototype baseline rather than a fully complete final product. Some backlog stories remain partially implemented or deferred, which is acceptable for the intermediate assessment as long as the current scope is clear and demonstrable.

## 2. Handout Requirement Summary

The coursework handout defines the following important constraints and expected business capabilities:

- the software must be implemented as either:
  - a standalone Java application, or
  - a lightweight Java Servlet/JSP web application;
- all input/output data must be stored in text files such as CSV, JSON, XML, or TXT;
- the system should address the TA recruitment workflow with features such as:
  - TA can create an applicant profile;
  - TA can upload CV;
  - TA can find available jobs;
  - TA can apply for jobs;
  - TA can check application status;
  - MO can post jobs;
  - MO can select applicants;
  - Admin can check TA overall workload;
- AI-powered enhancements may include:
  - applicant-job skill matching;
  - missing-skill identification;
  - workload balancing.

The current project satisfies the mandatory technical constraints:
- it is a Java Servlet/JSP web application;
- it uses CSV text files rather than a database.

Against the suggested business functions, the current implementation status is:

| Handout Requirement | Current Status | Notes |
| --- | --- | --- |
| TA can create applicant profile | Partial | Registration and profile editing exist, but profile completeness rules are still lightweight. |
| TA can upload CV | Not Implemented | No file upload flow exists yet. |
| TA can find available jobs | Implemented | TA can browse open jobs. |
| TA can apply for jobs | Partial | Application submission works, but not all business rules are enforced. |
| TA can check application status | Partial | Status page exists, but some detail fields are still minimal. |
| MO can post jobs | Partial | Job creation is implemented; draft/edit lifecycle is still incomplete on MO side. |
| MO can select applicants | Partial | Decision updates exist, but waitlist and workload rules are not complete. |
| Admin can check TA workload | Partial | Dashboard exists, but workload is currently simplified. |

## 3. System Architecture

The current implementation follows a lightweight layered MVC structure.

### 3.1 Layered design

- Controller layer:
  - handles HTTP requests, role checks, redirects, and page forwarding;
  - implemented with Jakarta Servlets.
- Service layer:
  - contains core business logic such as authentication, job handling, application submission, dashboard statistics, and logging.
- Model layer:
  - defines domain objects such as `User`, `Job`, `Application`, and supporting enums.
- Storage layer:
  - reads and writes CSV data files;
  - also handles default-file initialisation.
- View layer:
  - JSP pages provide the TA, MO, and Admin interfaces.

### 3.2 Main module structure

- `src/main/java/edu/bupt/ta/controller`
- `src/main/java/edu/bupt/ta/service`
- `src/main/java/edu/bupt/ta/model`
- `src/main/java/edu/bupt/ta/storage`
- `src/main/webapp/WEB-INF/jsp`
- `data/*.csv`

### 3.3 Runtime/deployment model

- Java 17
- Maven WAR packaging
- Jakarta Servlet/JSP + JSTL
- local Tomcat deployment
- CSV file persistence under `data/`

This architecture remains simple, modular, and aligned with the coursework restriction against using Spring Boot or a database.

## 4. Implemented Features by Role

### 4.1 TA-side features

Currently implemented:
- register a TA account;
- log in as a TA user;
- view TA dashboard;
- edit TA profile information;
- browse open jobs;
- view job match score;
- submit an application;
- view own applications and current statuses.

Currently partial:
- applicant profile creation is functional but not fully aligned with all backlog validation rules;
- job details are shown mainly through job cards rather than a dedicated detail page;
- application submission does not yet enforce all constraints, such as CV prerequisite, deadline validation, and maximum application count;
- application status view is available but still lightweight.

Not yet implemented:
- CV upload;
- application withdrawal;
- acknowledgement after selection.

### 4.2 MO-side features

Currently implemented:
- log in as MO;
- view MO dashboard;
- create a new job post;
- browse application records;
- filter applicant list by job;
- update application decision to accepted, interview, or rejected.

Currently partial:
- job-post lifecycle management is incomplete for MO-specific ownership;
- applicant review page shows limited profile context;
- CV review is only a placeholder because CV upload is not implemented;
- decision logic does not yet enforce workload limit rules or waitlist behaviour.

### 4.3 Admin-side features

Currently implemented:
- log in as Admin;
- view admin dashboard statistics;
- view workload-style summary data;
- manage applications;
- manage jobs;
- manage users;
- view system logs;
- change password and toggle user status.

Currently partial:
- workload calculation is simplified and based mainly on application counts;
- fairness/overload monitoring is not yet based on selected-job hours.

### 4.4 Shared/support features

Currently implemented:
- session-based access control;
- CSV persistence for users, jobs, applications, and logs;
- role-based page routing;
- basic flash-message feedback;
- default demo data initialisation.

## 5. Data Storage and Deployment

### 5.1 Data storage

The project uses CSV files as persistent storage:

- `data/ta_users.csv`
- `data/jobs.csv`
- `data/applications.csv`
- `data/system_logs.csv`

The storage utility is responsible for:
- ensuring files and headers exist;
- loading records into model objects;
- saving updated lists back to CSV;
- generating default demo data;
- generating timestamps and IDs.

This design directly follows the coursework requirement to use plain text storage rather than a database.

### 5.2 Build and deployment

The project is built using Maven and packaged as a WAR file.

Typical local workflow:
- run `mvn clean package`;
- deploy the generated WAR to Tomcat;
- open the application via the Tomcat context path.

At the time of writing this summary, the current project builds successfully with:

```bash
mvn -q -DskipTests package
```

This confirms that the current codebase is in a deployable baseline state for demonstration.

## 6. AI-Assisted Feature Implementation

The handout mentions AI-powered features such as skill matching, missing-skill identification, and workload balancing.

In the current iteration, the project implements a lightweight rule-based matching feature:
- each job stores required skills;
- each TA stores profile skills;
- the service layer calculates a match score based on skill overlap;
- the match score is displayed in TA job browsing views and contributes to dashboard recommendation text.

This is not a full intelligent recommendation engine, but it is a valid explainable baseline because:
- the input data is explicit;
- the scoring logic is deterministic;
- the result can be traced to overlapping skills.

Not yet implemented:
- missing-skill explanation;
- recommendation reasoning beyond overlap percentage;
- workload balancing recommendation or alerting.

## 7. Current Limitations and Next Iteration Items

The current prototype is suitable for demonstrating the core recruitment workflow, but several backlog items are still incomplete.

Main limitations:
- no CV upload and no real CV file management;
- no TA-side job filtering and searching;
- no dedicated job detail page;
- application submission does not yet enforce all business rules;
- no withdrawal or acknowledgement flow;
- MO job edit/close lifecycle is incomplete on the MO side;
- workload summary is simplified rather than based on assigned hours;
- no workload alert feature.

Recommended next iteration priorities:
- implement CV upload and file access;
- enforce key business rules for application submission and selection limits;
- strengthen MO job lifecycle management;
- improve admin workload calculation based on accepted assignments and hours;
- create requirement-traceable test cases from the acceptance requirement list.

For the 12 April 2026 acceptance, the best strategy is to present the system honestly as:
- a working servlet/JSP prototype;
- complete on the main recruitment flow;
- partially complete on several Sprint 2 usability and business-rule stories;
- ready to support a structured test document and the next development iteration.
