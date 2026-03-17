## Requirements Overview

This document summarises the main requirements for the TA Recruitment System, based on the coursework handout and additional analysis.  
It is intended to complement the official Product Backlog Excel file (user stories, priorities, estimates).

---

### 1. User Roles

- **TA (Teaching Assistant Applicant)**
  - A student who wants to apply for TA positions.
- **Module Organiser (MO)**
  - A lecturer or staff member who creates TA jobs and selects applicants.
- **Admin**
  - A staff member who oversees the overall TA workload and system configuration.

---

### 2. Core Functional Requirements (High-Level)

These are high-level requirements derived from the handout; detailed user stories will be tracked in the Product Backlog.

#### 2.1 TA (Applicant)

- TA can create and update an applicant profile (basic personal info, skills, availability).
- TA can upload or register a CV (stored as text or simple file reference).
- TA can browse/search available TA jobs.
- TA can submit applications for specific jobs.
- TA can view application history and current status.

#### 2.2 Module Organiser (MO)

- MO can create new TA job postings (module, role description, required skills, hours).
- MO can view applicants for each job.
- MO can change the status of applications (e.g. pending, accepted, rejected).
- MO can optionally add comments or reasons for decisions.

#### 2.3 Admin

- Admin can see an overview of all TA jobs and assignments.
- Admin can review each TA’s overall workload across modules.
- Admin can perform basic data maintenance tasks (e.g. reset a user, close jobs).

---

### 3. Non-Functional Requirements and Constraints

- **Technology constraints**
  - Implementation must be a **stand-alone Java application** or **lightweight Java Servlet/JSP web application**.
  - **Data storage must use plain text files** (e.g. CSV, JSON, XML, TXT), **no database**.
  - No heavy frameworks such as Spring Boot.

- **Usability**
  - Simple and clear interface suitable for students and staff.
  - Forms should validate basic input (required fields, data formats).

- **Reliability**
  - Core flows (view jobs, apply, update status) should behave predictably.
  - File operations should handle missing/invalid data gracefully where possible.

- **Maintainability**
  - Code organised into clear layers (controllers, services, file storage utilities).
  - Minimal coupling between UI (JSP) and business logic (Java classes).

---

### 4. Example User Stories (Samples)

These are sample stories; the full list should live in the Product Backlog Excel file.

- **As a TA**, I want to **register an account and log in**, so that I can apply for TA positions.
- **As a TA**, I want to **view all open TA jobs**, so that I can decide which ones suit my skills and schedule.
- **As a TA**, I want to **see the status of my applications**, so that I know whether I have been selected.
- **As a Module Organiser**, I want to **create a TA job with required skills and hours**, so that students can apply.
- **As a Module Organiser**, I want to **see a list of applicants for my job**, so that I can compare and select candidates.
- **As an Admin**, I want to **view each TA’s total assigned hours**, so that I can balance workload and avoid overload.

Each user story must have:

- Clear **acceptance criteria**.
- A **priority** (e.g. Must / Should / Could).
- An **estimate** (e.g. 1–5 story points).
- A **target iteration** (Iteration 1–4).

---

### 5. AI-Assisted Features (Optional, Lightweight)

If time allows, the team may add simple AI-inspired features, such as:

- Suggesting suitable jobs based on a TA’s skills.
- Highlighting missing skills for a given job.
- Providing a basic recommendation or ranking of applicants for an MO.

Any AI-assisted feature should:

- Be transparent and explainable (e.g. show which skills matched).
- Combine simple rule-based logic with any AI-generated suggestions.
- Be clearly documented and limited in scope to match coursework expectations.

