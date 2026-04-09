# Iteration 2 Requirement List

This document reorganises the backlog in `ProductBacklog2_group53.xlsx` into implementation modules for the 12 April 2026 intermediate assessment.

Purpose:
- provide a requirement checklist for acceptance review;
- show which backlog items are already implemented, partially implemented, or not implemented;
- serve as the direct input for the Iteration 2 test document.

Status legend:
- `Implemented`: the core flow exists in code and can be demonstrated.
- `Partial`: part of the story is implemented, but some acceptance criteria are still missing.
- `Not Implemented`: no end-to-end implementation yet.

## Module 1. Authentication and Session Control

| ID | User Story | Priority | Sprint | Current Status | Acceptance Focus | Current Implementation Notes | Gap for Testing / Next Work |
| --- | --- | --- | --- | --- | --- | --- | --- |
| US21 | User Login | very high | 1 | Implemented | Valid login, invalid login handling, role-based routing, session kept during current session | Login with username, password, and role is implemented. Users are redirected to TA/MO/Admin dashboards after successful login. Invalid credentials and wrong role selection show error messages. | Need test cases for role mismatch, inactive account, and direct access to protected pages without login. |
| US22 | User Logout | medium | 2 | Implemented | Logout button ends the session, redirects to login page, protected pages cannot be accessed afterwards | Logout servlet invalidates session and redirects to `/home`. Protected TA/MO/Admin pages check session user and role. | Need test cases for logout from all three roles and back-button/direct URL behaviour after logout. |

## Module 2. TA Profile and CV Management

| ID | User Story | Priority | Sprint | Current Status | Acceptance Focus | Current Implementation Notes | Gap for Testing / Next Work |
| --- | --- | --- | --- | --- | --- | --- | --- |
| US01 | Create Applicant Profile | very high | 1 | Partial | Create applicant profile with personal/academic info, required-field validation, save and reopen | TA registration supports username, password, name, email, year, major, and skills. Saved data is written to CSV and can be reopened later through profile pages. | Backlog expects fuller profile content, including availability at initial profile creation and stricter required-field checks. |
| US02 | Edit Applicant Profile | high | 2 | Implemented | Update saved profile, save correctly, do not lose existing data | TA profile edit page supports name, email, year, major, skills, and availability updates. Data is saved back to CSV. | Need regression tests to confirm existing profile data is preserved after multiple edits. |
| US03 | Upload CV | very high | 1 | Not Implemented | Upload local CV, restrict to PDF/DOC, show uploaded file, replace file, validation on invalid input | No upload servlet, no multipart handling, no file storage, and no CV metadata persistence currently exist. | This remains a clear gap for acceptance. Test document should mark it as not yet available and exclude it from pass cases. |

## Module 3. TA Job Discovery and Application Workflow

| ID | User Story | Priority | Sprint | Current Status | Acceptance Focus | Current Implementation Notes | Gap for Testing / Next Work |
| --- | --- | --- | --- | --- | --- | --- | --- |
| US04 | Browse Available Jobs | very high | 1 | Implemented | Show open jobs, display key fields, exclude closed jobs from default list | TA can browse open jobs. Job cards display title, module code, organiser, hours, year range, required skills, and match score. Closed jobs are excluded by the open-job query. | Need demo tests for normal browsing and empty list behaviour if all jobs are closed. |
| US05 | Filter and Search Jobs | medium | 2 | Not Implemented | Filter by module/activity/type/keyword and show empty-state message when needed | No backend filtering or searching logic is implemented for TA job browsing. | This story should be listed as pending in Iteration 2 scope. |
| US06 | View Job Details | very high | 1 | Partial | Open detailed job information before applying | Core job information is visible in the job card, but there is no dedicated job detail page. | Need to treat current implementation as lightweight inline detail, not full detailed-page support. |
| US07 | Submit Application | very high | 1 | Partial | Submit application with profile/CV/rule checks, prevent invalid application, confirm submission, make record visible to TA and MO | TA can submit an application and the record is stored in `applications.csv`. Duplicate application to the same job is blocked. Submitted applications are visible to TA and MO. | Missing checks include CV upload prerequisite, complete-profile validation, max 3 applications rule, closed/deadline validation, and workload constraints. |
| US08 | View Application Status | very high | 1 | Partial | Open My Applications page and show job, date, status, update state consistently | TA can open the applications page and view current application status records. | Current page does not fully show latest status update time or a full link back to detailed job information. |
| US09 | Withdraw Application | medium | 2 | Not Implemented | Withdraw pending or under-review application and mark it clearly | No TA withdrawal function exists. | Keep as planned work after intermediate assessment. |
| US10 | View Selection Result and Assignment Details | high | 2 | Partial | See selected result and assignment information | TA can see changed application status when MO updates it. | No dedicated assignment-detail view exists; current implementation only exposes status-level feedback. |
| US11 | Acknowledge Selection Result | medium | 2 | Not Implemented | Selected TA acknowledges result and system records acknowledgement | No acknowledgement function exists. | Planned future extension only. |

## Module 4. MO Job Posting and Applicant Review

| ID | User Story | Priority | Sprint | Current Status | Acceptance Focus | Current Implementation Notes | Gap for Testing / Next Work |
| --- | --- | --- | --- | --- | --- | --- | --- |
| US12 | Create Job Post | very high | 1 | Partial | Create/publish a job with title, module/activity, requirements, hours, deadline, vacancies; validate required fields; allow visibility to TA | MO can create a new job post and save it into CSV. Published jobs appear in the TA job list. | No draft mode exists. Validation is basic and does not yet fully enforce all required business fields from backlog wording. |
| US13 | Edit or Close Job Post | high | 2 | Partial | Edit job before deadline, close post manually, prevent new applications for closed jobs | Job update and status toggle exist in admin management, but not as a full MO-owned workflow. | MO-side edit/close flow is still incomplete. Application service also does not yet enforce closed/deadline rejection on direct submit. |
| US14 | View Applicant List | very high | 1 | Partial | View applicants for a specific job with key profile summary and applicant count | MO can open applicant lists and filter by job. Dashboard shows applicant counts per job. | Applicant list does not yet show full programme/major information expected by backlog. |
| US15 | Review Applicant Details and CV | very high | 1 | Partial | Review applicant profile and CV without losing context | MO applicant list shows applicant names, IDs, status, and availability. | There is no applicant detail page and CV access is only a placeholder link because CV upload is not implemented. |
| US16 | View My Posted Jobs | high | 2 | Partial | View all jobs posted by current MO and open them for management actions | MO dashboard shows job counts and job/application statistics. | There is no dedicated `My Posted Jobs` management page limited to current MO ownership. |
| US17 | Update Applicant Decision | very high | 1 | Partial | Update applicant outcome, reflect it in MO and TA views, enforce workload constraints | MO can change application status to `ACCEPTED`, `INTERVIEW`, or `REJECTED`, and TA can see the result. | Backlog asks for selected/rejected/waitlisted plus workload-limit control. Current implementation lacks waitlist and max-3-job protection/warning. |

## Module 5. Admin Workload and Fairness Oversight

| ID | User Story | Priority | Sprint | Current Status | Acceptance Focus | Current Implementation Notes | Gap for Testing / Next Work |
| --- | --- | --- | --- | --- | --- | --- | --- |
| US18 | View TA Workload Summary | very high | 1 | Partial | View workload per TA, including assigned posts and total hours, identify overloaded cases | Admin dashboard and workload summary exist. System can count applications and show top TAs/top jobs. | Current workload is calculated mainly by application count, not by selected jobs plus total assigned hours. |
| US19 | Filter Workload Records | medium | 2 | Not Implemented | Search/filter workload records by TA or workload status | No dedicated workload filter page exists. | Planned work after baseline summary/test completion. |
| US20 | View Workload Alerts | high | 2 | Not Implemented | Flag overloaded TAs and allow admin to inspect related assignments | No automatic workload alert logic exists. | Planned extension only. |

## Module 6. Additional Implemented Support Features

These items are not the main backlog stories above, but they are already present in the current codebase and should be mentioned during acceptance because they strengthen the prototype.

| Feature | Current Status | Notes |
| --- | --- | --- |
| Role-based dashboard routing | Implemented | Separate dashboards/pages for TA, MO, and Admin are available. |
| CSV-based persistence | Implemented | Users, jobs, applications, and logs are stored in text files under `data/`. |
| Admin user management | Implemented | Admin can create users, toggle user status, and change passwords. |
| Admin job management | Implemented | Admin can create, update, toggle, and delete job posts. |
| Admin application management | Implemented | Admin can filter applications and approve/reject them. |
| System operation logs | Implemented | Login and admin actions can be recorded into `system_logs.csv`. |
| Skill-match scoring | Implemented | A rule-based match score is shown on TA job pages as a lightweight AI-assisted function. |

## Iteration 2 Acceptance Scope Recommendation

For the 12 April 2026 intermediate assessment, the most realistic acceptance scope is:

- demonstrate the end-to-end core workflow:
  - login;
  - TA registration/profile maintenance;
  - TA browse jobs and apply;
  - MO create jobs and update application decisions;
  - Admin view workload and management dashboards;
- explicitly label the following stories as partial or pending:
  - US03 Upload CV;
  - US05 Filter and Search Jobs;
  - US09 Withdraw Application;
  - US11 Acknowledge Selection Result;
  - US19 Filter Workload Records;
  - US20 View Workload Alerts;
- use this file as the baseline for test-case creation:
  - every `Implemented` story should have positive and negative test cases;
- every `Partial` story should have `implemented part` test cases plus `known gap` notes;
  - every `Not Implemented` story should be tracked as deferred work, not counted as passing acceptance tests.