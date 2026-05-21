# Team 53 - Prototype and Report Part 1
## Part A: Prototype Page Wireframe Structure

### 1. Prototype Overview

This prototype is designed for the **TA Recruitment System**, focusing on three core user types:

* Teaching Assistant Applicants (Teaching Assistant, TA)
* Course Instructors (Module Organiser, MO)
* System Administrators (Admin)

The goal of this prototype is to demonstrate the core workflows of the system in a clear and user-friendly manner. It covers major user paths, as well as important system states such as success messages, validation error messages, and empty state pages.

The design principle is to keep the system **simple, modular, and easy to understand**. Since this is an early-stage prototype, the focus is on **functional completeness** and **usability** rather than visual aesthetics.

---

## 2. Common Entry Pages

### P01. Login Page

**Purpose:**
Provides a unified system entry point for three types of users.

**Main Components:**

* System title: TA Recruitment System
* Role selection: TA / MO / Admin
* Username or student/staff ID input field
* Password input field
* Login button
* Registration link for TA users
* Error message display area

**Main Interactions:**

* User selects a role and logs in
* System redirects to the home page for the corresponding role
* If login fails, an error message is displayed

---

## 3. TA User Flow Pages

### P02. TA Home Page

**Purpose:**
Provides TA users with an overview of their current status and quick access to main tasks.

**Main Components:**

* Sidebar navigation:
  * Profile
  * CV (Resume)
  * Jobs
  * My Applications
* Profile completeness indicator
* Resume upload status
* Quick action buttons:
  * Complete profile
  * Upload resume
  * Browse jobs
* Overview cards:
  * Profile status
  * Resume status
  * Number of applied jobs

**Design Notes:**
This page should guide the TA on what to do next.

---

### P03. Create / Edit Profile Page

**Purpose:**
Allows TAs to fill in and maintain the information required for applications.

**Main Fields:**

* Full name
* Student ID
* Major/Program name
* Year
* Skills
* Available hours
* Previous TA experience
* Contact email

**Buttons:**

* Save
* Cancel

**System States:**

* Error message for missing required fields
* Error message for invalid email format
* "Profile saved successfully" message

---

### P04. Resume Upload Page

**Purpose:**
Allows TAs to upload and manage their resumes.

**Main Components:**

* Current resume upload status
* Upload button
* Replace resume option
* File name display area
* Save / Confirm button
* Return to home button

**System States:**

* Error message for no file selected
* Error message for unsupported file type
* "Resume uploaded successfully" message

---

### P05. Job List Page

**Purpose:**
Allows TAs to browse currently available positions.

**Main Components:**

* Search bar
* Filter options
* Job card list

**Each Job Card Contains:**

* Job title
* Course or activity name
* Working hours
* Deadline
* Status
* View details button

**System States:**

* "No matching jobs found"

---

### P06. Job Details Page

**Purpose:**
Helps TAs understand job information before applying.

**Main Components:**

* Job title
* Job description
* Required skills
* Working hours
* Deadline
* Module organiser information
* Apply button
* Return to job list button

**Rules:**

* If profile is incomplete, display a warning
* If resume is not uploaded, the apply action should be blocked with a clear message

---

### P07. Application Confirmation Page

**Purpose:**
Confirmation before formally submitting an application.

**Main Components:**

* Job information summary
* Applicant information summary
* Resume attached indicator
* Confirm application button
* Cancel button

**System States:**

* "Application submitted successfully"
* Error message for missing profile/resume

---

### P08. My Applications Page

**Purpose:**
Allows TAs to track the status of submitted applications.

**Main Components:**

* Application records table or list

**Each Record Contains:**

* Job title
* Application date
* Current status
* View details action

**Status Types:**

* Submitted
* Waitlisted
* Rejected
* Selected

**Design Notes:**
Status labels should be consistent and easy to understand.

---

## 4. MO User Flow Pages

### P09. MO Home Page

**Purpose:**
Provides MOs with an overview of recruitment-related activities.

**Main Components:**

* Sidebar navigation:
  * Post jobs
  * My jobs
  * Applicants
* Overview cards:
  * Number of currently active jobs
  * Total number of applicants
  * Number of pending decisions
* Quick action buttons:
  * Create new job

---

### P10. Create Job Page

**Purpose:**
Allows MOs to publish new positions.

**Main Fields:**

* Job title
* Course / Activity name
* Job description
* Required skills
* Working hours
* Deadline
* Number of positions

**Buttons:**

* Save as draft
* Publish

**Validation Rules:**

* Required fields cannot be empty
* Deadline must be valid

**System States:**

* "Job published successfully"
* Error message for missing required fields

---

### P11. Job List Page

**Purpose:**
Allows MOs to view all jobs they have created.

**Main Components:**

* List of published jobs

**Each Job Record Contains:**

* Job title
* Status
* Number of applicants
* View applicants button
* Edit button
* Close job button

---

### P12. Applicant List Page

**Purpose:**
Allows MOs to view all applicants for a specific job.

**Main Components:**

* Applicant table
* Filter and sort controls
* Applicant count statistics

**Each Applicant Record Contains:**

* Name
* Major
* Available hours
* Current status
* View profile button

**System States:**

* "No applicants yet"

---

### P13. Applicant Details and Decision Page

**Purpose:**
Allows MOs to view applicant details and make hiring decisions.

**Main Components:**

* Applicant profile summary
* Resume preview or download link
* Skills summary
* Available hours summary

**Decision Buttons:**

* Accept
* Reject
* Waitlist

**System States:**

* "Decision updated successfully"

---

## 5. Admin User Flow Pages

### P14. Admin Home / Workload Overview Page

**Purpose:**
Allows administrators to monitor the TA workload distribution across the entire system.

**Main Components:**

* Search by TA name or student ID
* Filter by workload status
* Workload table

**Each Record Contains:**

* TA name
* Student ID
* Number of assigned positions
* Total hours
* Workload status

**System States:**

* "No records found"

**Design Notes:**
TAs with excessive workload should be clearly highlighted.

---

## 6. Common System State Pages

### P15. Empty State Page

**Examples:**

* No jobs yet
* No applicants yet
* No workload records yet

---

### P16. Error State Page

**Examples:**

* Please complete your profile before applying
* Please upload your resume before applying
* Required fields cannot be empty

---

### P17. Success Feedback Page

**Examples:**

* Profile saved successfully
* Resume uploaded successfully
* Application submitted successfully
* Job published successfully
* Decision updated successfully

---

## 7. Main Prototype Flows

### TA Main Flow

Login → TA Home → Create/Edit Profile → Upload Resume → Browse Jobs → View Job Details → Confirm Application → View My Applications

### MO Main Flow

Login → MO Home → Create Job → Job List → Applicant List → Applicant Details and Decision

### Admin Main Flow

Login → Admin Home / Workload Overview

---

## 8. Prototype Design Notes

The prototype design should follow these principles:

* Clear navigation
* Distinct role separation
* Simple and easy-to-understand page layout
* Visible system states
* Clear validation and error feedback
* Coverage of complete end-to-end workflows
* Support for both functional and usability evaluation

The prototype should not be viewed as a set of isolated pages, but must present complete and coherent usage paths for all three user types.

---

# Part B: Report Part 1

## 1. User Story Writing Workshop

### 1.1 Workshop Purpose

The purpose of the user story writing workshop is to transform the TA recruitment problem into a structured, prioritized product backlog. Since the project description only provides high-level requirements, the team needs to identify the main user roles of the system, clarify the project scope, and transform user needs into user stories to guide subsequent prototype design and software implementation.

This workshop focuses on:

* Identifying the main users of the system
* Clarifying the project scope
* Extracting Epics from business processes
* Writing user stories in standard format
* Defining acceptance criteria
* Assigning priorities
* Estimating workload
* Creating iteration plans

This workshop lays the foundation for the subsequent prototype and product backlog.

---

### 1.2 Identified User Roles

Based on the project description, three main user types have been identified:

**Teaching Assistant Applicants (TA):**
TAs are applicants responsible for creating profiles, uploading resumes, searching for available positions, submitting job applications, and checking application status.

**Module Organisers (MO):**
MOs are responsible for publishing TA positions, viewing applicants, and selecting suitable candidates.

**Administrators (Admin):**
Administrators are responsible for viewing the overall workload of TAs to support fair distribution and supervision management.

These three user types constitute the main users of the system and form the basis for prototype and product backlog design.

---

### 1.3 Scope Definition

In the first assessment, the project scope was intentionally limited to the **core recruitment process**.

**In Scope:**

* Applicant profile creation and editing
* Resume upload
* Job browsing and job details viewing
* Application submission
* Application status tracking
* Job posting
* Applicant viewing and decision making
* Workload overview viewing

**Out of Scope:**

* AI-based skill matching
* Automatic recommendations
* Email notifications
* Mobile app version
* Advanced analytics features
* Database integration

This scope definition ensures that the system, while feasible to implement, focuses on customer value and is suitable for incremental development.

---

### 1.4 Epics Defined in Workshop

The recruitment process is organized into the following Epics:

1. TA Profile Management
2. Job Discovery and Application
3. Application Status Tracking
4. MO Job Posting and Applicant Selection
5. Admin Workload Monitoring
6. Optional AI Support Features

These Epics are further broken down into more detailed user stories.

---

### 1.5 User Story Format

The team adopted the following standard format for all user stories:

**As a [role], I want [behavior], so that [benefit].**

This format was chosen because it keeps the focus on **user value** rather than technical implementation details.

For example:

* As a TA, I want to upload my resume so that module organisers can review my qualifications.
* As an MO, I want to publish a position so that students can apply.
* As an administrator, I want to view TA workload so that I can monitor workload distribution.

---

### 1.6 Acceptance Criteria

Each user story is accompanied by acceptance criteria so that the requirement can be verified and tested.

For example:

**User Story:**
As a TA, I want to apply for a position so that I can be considered for hiring.

**Acceptance Criteria:**

* TA must complete profile before applying
* TA must upload resume before applying
* System must display application submission success message
* Submitted application must appear in the application records list
* MO must be able to see the application

Acceptance criteria are necessary because they make user stories more precise and help directly map requirements to prototype behavior.

---

### 1.7 Priority Ranking

The team adopted the **Must / Should / Could** priority classification method.

**Must (Must Have):**
Features essential to demonstrate the core value of the system.

**Should (Should Have):**
Features important for improving system usability and completeness, but not absolutely essential for the MVP.

**Could (Could Have):**
Optional enhancement features that can be placed in subsequent iterations.

For example:

* Profile creation, resume upload, job browsing, job application, job posting, applicant viewing, and workload overview are classified as **Must**
* Filtering features and editing features are classified as **Should**
* AI-based matching and balancing features are classified as **Could**

This ranking ensures that the first version of the system, while achievable, still clearly delivers the core recruitment process.

---

### 1.8 Workload Estimation

The team used story points to estimate user stories, with the following scale:

* 1 = Very small task
* 2 = Small task
* 3 = Medium task
* 5 = Larger multi-step task
* 8 = Complex enhancement

Using story points helps the team compare workloads between different user stories and supports sprint planning.

For example:

* View job details = 2 points
* Upload resume = 3 points
* Apply for job = 5 points
* AI skill matching = 8 points

---

### 1.9 Iteration Planning

The team divided the backlog into three levels:

**Iteration 1 / Sprint 1:**
Core MVP process

* Create applicant profile
* Upload resume
* Browse jobs
* View job details
* Apply for job
* View application status
* Post job
* View applicants
* View applicant profiles and resumes
* Accept or reject applicants
* View workload overview

**Iteration 2:**
Secondary usability and management features

* Edit profile
* Filter jobs
* Edit or close jobs
* Filter workload records

**Future Backlog:**
Optional AI enhancement features

* Skill matching suggestions
* Workload balancing suggestions

This iteration plan reflects the balance between **customer value** and **development feasibility**.

---

## 2. Fact Finding

### 2.1 Purpose

Fact finding is used to better understand the recruitment process and provide evidence for backlog design and prototype improvement. Since the project description only provides high-level guidance, additional information is needed to clarify expectations and identify more practical requirements.

---

### 2.2 Fact Finding Methods Used

The team plans to use the following methods:

**1. Document Analysis**
By analyzing the project handout, identify:

* Main user groups
* Core functions
* Project constraints
* Optional AI features

**2. Interview Questions**
Prepare a set of brief interview questions to collect perspectives from potential users or reviewers to understand their expectations, pain points, and potentially missing features in the system.

**3. Survey / Questionnaire**
Design a prototype feedback questionnaire to collect structured feedback, including:

* Whether the system objectives are clear
* Whether the workflow is easy to use
* Whether the information is complete
* Which pages or steps are confusing
* Suggestions for improvement

**4. User Feedback on Prototype**
Present the prototype to several reviewers for improvement of navigation, page structure, and potentially missing information.

---

### 2.3 Reasons for Selecting These Methods

These fact-finding methods were chosen because they are suitable for an **early-stage software engineering project**, especially when time is limited and initial requirements are incomplete.

Document analysis can provide a structured starting point.
Interviews and surveys are low-cost and practical for student teams.
Prototype feedback is particularly important because it allows the team to verify system functionality and usability before entering implementation.

---

### 2.4 Evidence to Be Collected

The team plans to collect the following evidence:

* Interview questions
* Survey questions
* Survey results
* Customer or peer feedback
* Key findings summary
* Prototype and backlog modification records based on feedback

This evidence will later be used as supporting material in the report appendices.

---

## 3. Rationale for Priority Ranking

This priority ranking is primarily based on two principles:

**1. Customer Value**
The first version of the system should prioritize supporting the basic recruitment process from application to selection.

**2. Feasibility**
The team should prioritize completing features that are more realistic, easier to prototype, and subsequently implement under project constraints.

Therefore, the team did not include AI features in the MVP. Although AI skill matching and workload balancing are considered valuable subsequent enhancements, they are not essential for demonstrating the core objectives of the system in the first phase.

The team believes that prioritizing the basic process first will make the system better in terms of clarity and project controllability.

---

## 4. Conclusion

This initial phase of the project establishes the conceptual foundation for the TA Recruitment System. Through the user story writing workshop, the team identified core users, clarified the project scope, organized the system into several Epics, and formed structured user stories with acceptance criteria, priorities, estimates, and iteration plans.

Through fact finding, the team also identified appropriate methods to collect evidence and verified the prototype through user feedback. These activities directly support the formation of a meaningful product backlog and a coherent prototype.

The result is a structured foundation for the next phase of the project—including prototype optimization, backlog refinement, and subsequent software implementation.
