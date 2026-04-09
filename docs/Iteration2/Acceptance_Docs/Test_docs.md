# Iteration 2 Test Document

Date: 2026-04-09

This document is based on:
- `requirements_list.md`
- `Iteration2_summary.md`

Its purpose is to support the 12 April 2026 intermediate assessment by defining:
- what is in scope for testing;
- which backlog stories are currently testable;
- which test cases should be demonstrated during acceptance;
- which items are known gaps and should be treated as deferred rather than failed core flow.

## 1. Test Objective

The objective of Iteration 2 testing is to verify that the current prototype can support the main recruitment workflow end to end:

- user authentication and role-based access;
- TA registration and profile maintenance;
- TA browsing jobs and submitting applications;
- MO posting jobs and updating applicant decisions;
- Admin viewing workload-style statistics and management pages.

This test document does not assume that all backlog stories are fully complete. Instead, it separates:
- implemented stories that should pass;
- partial stories where the implemented part should be tested;
- not implemented stories that should be recorded as deferred work.

## 2. Test Scope

### 2.1 In-scope for Iteration 2 acceptance

- login and logout;
- role-based session protection;
- TA registration;
- TA profile editing;
- TA job browsing;
- TA application submission;
- TA application-status viewing;
- MO job creation;
- MO applicant-list viewing;
- MO decision update;
- Admin dashboard and management entry pages;
- CSV persistence of users, jobs, applications, and logs.

### 2.2 Partial-scope items

These items can only be partially tested because the implementation is incomplete:

- applicant-profile completeness rules;
- job details beyond job cards;
- application constraints such as max 3 applications and deadline validation;
- MO-side job lifecycle management;
- admin workload fairness logic.

### 2.3 Out-of-scope / deferred items

These stories are currently not implemented and should not be treated as required passing cases for the intermediate assessment:

- CV upload;
- job filter/search for TA;
- withdraw application;
- acknowledge selection result;
- workload-record filtering;
- workload alerts.

## 3. Test Strategy

The test strategy for Iteration 2 is manual functional and integration testing.

### 3.1 Testing approach

- Functional testing:
  - verify each core user-visible feature behaves as expected.
- Negative testing:
  - verify incorrect input or invalid access is handled safely.
- Integration testing:
  - verify data created by one role is visible and usable by another role.
- Persistence testing:
  - verify important actions are written into CSV files and remain visible after page reload.

### 3.2 Acceptance principle

- A story marked `Implemented` in `requirements_list.md` should have at least one positive test and one relevant negative/access-control test where appropriate.
- A story marked `Partial` should be tested only for the implemented behaviour, and the missing acceptance criteria should be noted explicitly.
- A story marked `Not Implemented` should appear in the traceability table as `Deferred`, not as a failed core case.

## 4. Test Environment

Recommended environment for test execution:

| Item | Value |
| --- | --- |
| OS | Windows |
| JDK | 17 |
| Build Tool | Maven |
| Web Container | Apache Tomcat 10 |
| Application Type | Java Servlet/JSP web application |
| Data Storage | CSV files in `data/` |
| Browser | Chrome / Edge |

Build command:

```bash
mvn clean package
```

Deployment method:

- deploy generated WAR to Tomcat;
- start Tomcat;
- open the application in browser through the configured context path.

Recommended demo accounts already present in the system:

| Role | Username | Password |
| --- | --- | --- |
| TA | `seele` | `123456` |
| MO | `mo1` | `123456` |
| Admin | `admin` | `123456` |

## 5. Requirement Traceability Matrix

| Requirement ID | Story Name | Current Status | Test Coverage Strategy | Main Test Case IDs |
| --- | --- | --- | --- | --- |
| US21 | User Login | Implemented | Full positive + negative | TC-AUTH-01, TC-AUTH-02, TC-AUTH-03, TC-AUTH-04 |
| US22 | User Logout | Implemented | Full positive + protected-page check | TC-AUTH-05 |
| US01 | Create Applicant Profile | Partial | Test implemented registration flow only | TC-TA-01, TC-TA-02 |
| US02 | Edit Applicant Profile | Implemented | Full positive persistence test | TC-TA-03 |
| US03 | Upload CV | Not Implemented | Deferred | N/A |
| US04 | Browse Available Jobs | Implemented | Full positive | TC-TA-04, TC-TA-05 |
| US05 | Filter and Search Jobs | Not Implemented | Deferred | N/A |
| US06 | View Job Details | Partial | Test job-card level detail only | TC-TA-04 |
| US07 | Submit Application | Partial | Test existing submit flow and duplicate-block rule | TC-TA-06, TC-TA-07 |
| US08 | View Application Status | Partial | Test current status-page behaviour | TC-TA-08 |
| US09 | Withdraw Application | Not Implemented | Deferred | N/A |
| US10 | View Selection Result and Assignment Details | Partial | Test status reflection after MO update | TC-INTEG-02 |
| US11 | Acknowledge Selection Result | Not Implemented | Deferred | N/A |
| US12 | Create Job Post | Partial | Test create-and-publish flow | TC-MO-01 |
| US13 | Edit or Close Job Post | Partial | Test current admin-side substitute flow only if needed | TC-ADMIN-03 |
| US14 | View Applicant List | Partial | Test current job-filtered applicant list | TC-MO-02 |
| US15 | Review Applicant Details and CV | Partial | Test visible applicant data only; CV kept as gap | TC-MO-02 |
| US16 | View My Posted Jobs | Partial | Test MO dashboard statistical visibility only | TC-MO-03 |
| US17 | Update Applicant Decision | Partial | Test interview/accepted/rejected updates | TC-MO-04, TC-MO-05, TC-INTEG-02 |
| US18 | View TA Workload Summary | Partial | Test current dashboard summary | TC-ADMIN-01 |
| US19 | Filter Workload Records | Not Implemented | Deferred | N/A |
| US20 | View Workload Alerts | Not Implemented | Deferred | N/A |

## 6. Test Data Preparation

Before executing the cases, prepare the following baseline:

- ensure the application builds and deploys successfully;
- ensure default CSV files exist under `data/`;
- ensure default users remain available:
  - `seele` for TA;
  - `mo1` for MO;
  - `admin` for Admin;
- optionally create one extra TA test account for repeated application and profile tests;
- keep at least one open job available in `jobs.csv`.

Recommended reset baseline:

- `ta_users.csv` contains at least one TA, one MO, and one Admin account;
- `jobs.csv` contains at least one open job;
- `applications.csv` starts from a known small dataset;
- `system_logs.csv` is either empty except header or preserved if log verification is part of the demo.

## 7. Detailed Test Cases

Execution status legend:
- `Planned`: test case designed but not yet recorded with actual result in this document.
- `Pass`: passed during execution.
- `Fail`: failed during execution.
- `Blocked`: cannot be executed due to missing functionality or environment issue.

### 7.1 Authentication and Access Control

| Test ID | Related Req. | Preconditions | Steps | Expected Result | Actual Result | Status |
| --- | --- | --- | --- | --- | --- | --- |
| TC-AUTH-01 | US21 | System is running; TA account exists | 1. Open login page. 2. Select `TA`. 3. Enter `seele / 123456`. 4. Submit. | Login succeeds and user is redirected to TA dashboard. Session is established. | To be executed | Planned |
| TC-AUTH-02 | US21 | System is running; MO and Admin accounts exist | 1. Log in as `mo1`. 2. Log out. 3. Log in as `admin`. | MO is redirected to MO dashboard; Admin is redirected to Admin dashboard. | To be executed | Planned |
| TC-AUTH-03 | US21 | System is running | 1. Open login page. 2. Enter valid username and wrong password. 3. Submit. | Login fails and error message is shown. User remains on login/home page. | To be executed | Planned |
| TC-AUTH-04 | US21 | System is running; TA account exists | 1. Select `MO` role. 2. Enter `seele / 123456`. 3. Submit. | Login fails due to role mismatch and error message is shown. | To be executed | Planned |
| TC-AUTH-05 | US22 | User is logged in | 1. Click logout. 2. After redirect, manually access a protected URL such as `/ta/dashboard`. | Session is invalidated. User is redirected to home/login page when trying to open protected page again. | To be executed | Planned |
| TC-ACCESS-01 | US21, US22 | No active session | 1. Open `/ta/dashboard`, `/mo/dashboard`, `/admin/dashboard` directly. | Each request is blocked and redirected to the login/home page with a message. | To be executed | Planned |

### 7.2 TA Registration and Profile

| Test ID | Related Req. | Preconditions | Steps | Expected Result | Actual Result | Status |
| --- | --- | --- | --- | --- | --- | --- |
| TC-TA-01 | US01 | System is running; target username does not exist | 1. Open register page. 2. Enter valid TA registration data. 3. Submit. | New TA account is created, saved to CSV, auto-login occurs, and TA dashboard opens. | To be executed | Planned |
| TC-TA-02 | US01 | System is running | 1. Open register page. 2. Leave username or password blank. 3. Submit. | Registration is blocked and validation/error message is shown. | To be executed | Planned |
| TC-TA-03 | US02 | Logged in as TA | 1. Open `My Profile`. 2. Update name/email/year/major/skills/availability. 3. Save. 4. Reload profile page. | Updated profile values remain visible after reload and persist in CSV. | To be executed | Planned |

### 7.3 TA Job Browsing and Application

| Test ID | Related Req. | Preconditions | Steps | Expected Result | Actual Result | Status |
| --- | --- | --- | --- | --- | --- | --- |
| TC-TA-04 | US04, US06 | Logged in as TA; at least one open job exists | 1. Open job list page. 2. Inspect one job card. | Open jobs are displayed. Each visible card shows title, module code, organiser, hours, year range, required skills, and match score. | To be executed | Planned |
| TC-TA-05 | US04 | At least one job is marked `CLOSED` in data | 1. Log in as TA. 2. Open job list page. | Closed jobs are not shown in the default open-job list. | To be executed | Planned |
| TC-TA-06 | US07 | Logged in as TA; target open job has not yet been applied for by this TA | 1. Open job list. 2. Click apply on one job. 3. Open `My Applications`. | Application is submitted successfully, success message is shown, and a new application record appears in TA application list. | To be executed | Planned |
| TC-TA-07 | US07 | Same TA already applied to the same job | 1. Submit the same job application again. | System blocks duplicate application and shows an error message. No duplicate CSV record is created. | To be executed | Planned |
| TC-TA-08 | US08 | TA has at least one application record | 1. Open `My Applications`. | Current application records are visible with status information. | To be executed | Planned |

### 7.4 MO Job Management and Applicant Review

| Test ID | Related Req. | Preconditions | Steps | Expected Result | Actual Result | Status |
| --- | --- | --- | --- | --- | --- | --- |
| TC-MO-01 | US12 | Logged in as MO | 1. Open new-job page. 2. Enter valid job data. 3. Submit. 4. Log in as TA and open jobs page. | New job is created successfully, stored in CSV, and visible in TA open-job list. | To be executed | Planned |
| TC-MO-02 | US14, US15 | Logged in as MO; at least one application exists | 1. Open MO applications page. 2. If available, filter by job ID. 3. Review rows in applicant list. | Applicant list is visible; current implementation shows applicant name/ID/status/availability and job context. | To be executed | Planned |
| TC-MO-03 | US16 | Logged in as MO; jobs exist | 1. Open MO dashboard. | Dashboard shows job count, application counts, and current job statistics relevant to MO workflow. | To be executed | Planned |
| TC-MO-04 | US17 | Logged in as MO; at least one pending application exists | 1. Open MO applications page. 2. Change one application to `INTERVIEW`. | Update succeeds and application status changes to `INTERVIEW`. | To be executed | Planned |
| TC-MO-05 | US17 | Logged in as MO; at least one application exists | 1. Change application status to `ACCEPTED` or `REJECTED`. 2. Refresh the applicant list. | Status update is reflected on MO page after submission. | To be executed | Planned |

### 7.5 Admin Dashboard and Management

| Test ID | Related Req. | Preconditions | Steps | Expected Result | Actual Result | Status |
| --- | --- | --- | --- | --- | --- | --- |
| TC-ADMIN-01 | US18 | Logged in as Admin | 1. Open Admin dashboard. | Dashboard loads successfully and shows current workload-style summary, total TA/MO counts, application counts, and job counts. | To be executed | Planned |
| TC-ADMIN-02 | Additional Support Feature | Logged in as Admin; at least one non-admin user exists | 1. Open user-management page. 2. Toggle one user's status. 3. Try logging in with that user if disabled. | User status is updated and disabled account cannot log in successfully. | To be executed | Planned |
| TC-ADMIN-03 | US13 / Additional Support Feature | Logged in as Admin; at least one job exists | 1. Open job-management page. 2. Toggle one job status or update one job. | Admin action succeeds and the job record is updated in CSV and reflected in UI. | To be executed | Planned |
| TC-ADMIN-04 | Additional Support Feature | Logged in as Admin | 1. Open logs page after performing login/admin operations. | System logs page is accessible and recorded operations are visible if log-triggering actions were performed. | To be executed | Planned |

### 7.6 Cross-Role Integration Cases

| Test ID | Related Req. | Preconditions | Steps | Expected Result | Actual Result | Status |
| --- | --- | --- | --- | --- | --- | --- |
| TC-INTEG-01 | US07, US14 | TA and MO accounts exist; TA submits application to an open job | 1. Log in as TA and submit an application. 2. Log out. 3. Log in as MO. 4. Open applications page. | The newly submitted application is visible to MO. | To be executed | Planned |
| TC-INTEG-02 | US10, US17 | One TA application exists | 1. MO updates one application to `ACCEPTED` or `REJECTED`. 2. Log out from MO. 3. Log in as the TA. 4. Open `My Applications`. | TA can see the updated application status. | To be executed | Planned |
| TC-INTEG-03 | US12, US04 | MO can create jobs; TA can browse jobs | 1. MO creates a new open job. 2. TA opens job list. | The new job is visible in the TA job list. | To be executed | Planned |

## 8. Negative and Boundary Coverage Summary

The following risk-focused checks should be included during execution:

- invalid password during login;
- role mismatch during login;
- direct access to protected URLs without session;
- blank required registration fields;
- duplicate application to the same job;
- disabled user cannot log in;
- closed jobs do not appear in TA browsing list.

Known but not yet fully testable rules:

- max 3 applications per TA;
- max 3 accepted jobs per TA;
- deadline-based application rejection;
- CV upload format validation;
- workload alerts.

These should be recorded as implementation gaps rather than current failed test obligations.

## 9. Deferred Stories for Later Iterations

The following stories are intentionally deferred in test execution because they are not yet implemented end to end:

| Requirement ID | Story Name | Reason |
| --- | --- | --- |
| US03 | Upload CV | No upload flow, file storage, or file retrieval endpoint exists. |
| US05 | Filter and Search Jobs | No TA-side search/filter implementation exists. |
| US09 | Withdraw Application | No withdrawal action exists. |
| US11 | Acknowledge Selection Result | No acknowledgement workflow exists. |
| US19 | Filter Workload Records | No dedicated workload filtering page exists. |
| US20 | View Workload Alerts | No overload alert logic exists. |

## 10. Execution Recording Template

During actual acceptance rehearsal or formal test execution, the `Actual Result` and `Status` columns in Section 7 should be filled in.

Recommended execution order for demo rehearsal:

1. Authentication:
   - TC-AUTH-01
   - TC-AUTH-05
2. TA flow:
   - TC-TA-03
   - TC-TA-04
   - TC-TA-06
   - TC-TA-08
3. MO flow:
   - TC-MO-01
   - TC-MO-02
   - TC-MO-04
4. Integration flow:
   - TC-INTEG-02
5. Admin flow:
   - TC-ADMIN-01
   - TC-ADMIN-02
   - TC-ADMIN-04

This order is recommended because it demonstrates:
- one complete business chain;
- multi-role interaction;
- persistence and management capability;
- realistic intermediate-assessment progress.

## 11. Current Testing Conclusion

Based on the current implementation and requirement analysis:

- the project is ready for structured manual testing of the main recruitment workflow;
- the strongest acceptance path is the cross-role workflow from TA application to MO decision to Admin overview;
- the test document should be used as both:
  - an execution checklist for rehearsal;
  - evidence that the team understands implemented scope versus deferred scope.

The next step after this document is to execute the planned cases and fill in:
- actual result;
- pass/fail status;
- screenshots or brief evidence notes where needed.
