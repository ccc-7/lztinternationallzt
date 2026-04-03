# TA Recruitment Platform MO Module Upgrade & Optimization Document

## 1. Document Review
### 1.1 Document Purpose
This document focuses on the Module Organiser (MO) user module of the recruitment platform. By combining the current MO interface (based on the provided system screenshots) with the core requirements defined in the Product Backlog (US12-US17), it identifies missing fields, workflow disconnections, and optimization directions for visual interactions. The goal is to upgrade the current basic form and table views into an efficient MO job and candidate management center that meets the required specifications.

### 1.2 Current Project Status
The current MO module provides the most basic HTML table and form interfaces (such as the Job Postings list, Review Applicants list, and Create/Edit pages), but it suffers from several severe usability and functional deficiency issues:
- **Missing Core Fields**: The current interface fails to fully implement the Backlog requirements. For example, the Job Creation page (Image 3) is missing "Deadline" and "Number of Vacancies"; the Job List (Image 1) is missing "Applicant Count"; and the Applicant List (Image 2) is missing "Availability" and "Full Profile" access.
- **Workflow Disconnections**: The Job List page lacks a direct shortcut to view the applicants for a specific position, failing to effectively link the functional modules.
- **Flat Visual Hierarchy**: The interface utilizes primitive web tables and default dropdown boxes. It lacks status visualization (e.g., Open/Closed states) and data overviews, resulting in a passive and inefficient interaction experience.

### 1.3 Optimization Objectives
- **Complete Core Functions**: Strictly follow the acceptance criteria of US12, US14, and US16 to fill in the missing key data fields across all pages.
- **Connect Module Workflows**: Establish a direct navigation path between the "Job List" and "Applicant Review" modules.
- **Enhance Data Visualization and Interaction**: Transform plain text statuses into visual badges/pills, upgrade dropdown menus, and introduce data overview cards to aid decision-making.

---

## 2. Interface Optimization Suggestions
### 2.1 Bug Fixes & Redundancy Removal
#### 2.1.1 "Create/Edit Job Post" Missing Fields Issue (Ref: Image 3 & US12):
**Current Status**: The job creation/editing form only contains Title, Module Code, Description, Requirements, and Required Hours.
**Optimization Details**:
- **Add Required Fields**: According to US12 Acceptance Criterion 1, a Date Picker for **Deadline** and a numeric input for **Number of Vacancies** must be added to the form.
- **Validation Logic**: Ensure the system performs non-empty validation for the newly added mandatory fields before the MO can click "Publish".

#### 2.1.2 "Job Postings" List Incomplete Information Issue (Ref: Image 1 & US16):
**Current Status**: The table only displays ID, Module Code, Position Title, Required Hours, and Status, which is too sparse.
**Optimization Details**: 
- **Add Column Information**: According to US16 Acceptance Criterion 2, **Deadline** and **Applicant Count** columns must be added to the table.
- **Remove Redundant Actions**: The current "View Details" and "Edit Post" occupy two separate columns. They should be merged into a single "Actions" column using icon buttons instead of text links to save horizontal space.

#### 2.1.3 "Review Applicants" List and Review Issues (Ref: Image 2 & US14, US15):
**Current Status**: The list contains ID, Name, Email, Major, CV, and Decision. It only allows viewing the CV and lacks availability information.
**Optimization Details**: 
- **Add Availability Information**: According to US14 Acceptance Criterion 2, add an **Availability** column to the table to help MOs assess scheduling.
- **Improve Profile Review Entry**: According to US15 Acceptance Criterion 2 (review full profile and CV), add a **View Profile** button next to the "CV" column, allowing MOs to view structured personal information beyond just the resume document.

### 2.2 Visual & Interaction Optimization
**Status Visualization**:
- **Job Status**: In Image 1, replace the plain text "Open" and "Closed" in the Status column with visually distinct labels (Badges/Pills), e.g., a green background for Open and a grey/red background for Closed (Aligns with US16 Acceptance Criterion 4).

**Decision Interaction Optimization**:
- **Approval Dropdown Upgrade**: In the Decision column of Image 2, the native HTML `<select>` tag is currently used. It is recommended to upgrade this to a customized dropdown with status colors, or lay them out directly as quick action buttons: [ Selected ] / [ Rejected ] / [ Waitlisted ] (Aligns with US17).

**Layout & Spacing**:
- **Card-based Design**: The current interface exposes the tables directly on the page background. It is recommended to wrap the tables in white Card components with shadows and padding to improve the professional look and visual comfort.

### 2.3 Data Statistics Enhancement
**Job Management Dashboard Upgrade**:
- Above the job list in Image 1, add **Data Cards** to display: **Total Posted Jobs**, **Active Jobs**, and **Total Applicants**. This helps MOs quickly grasp the overall recruitment progress at a glance.

### 2.4 Functional Module Integration
**Direct Access from Job to Applicants (Applicant Management Entry)**:
- **Current Disconnection**: In Image 1, an MO cannot directly navigate to view the applicants for a specific job.
- **Integration Plan**: According to US14, viewing applicants for a specific job is required. A hyperlink must be added to the newly proposed **Applicant Count** field in Image 1. Clicking this number should navigate directly to the Image 2 page and automatically filter the applicant list by that specific job (Module Code).

---

## 3. Core Optimization Directions
Based on the current screenshot status and the Product Backlog, the following are the key improvement directions required to meet the project acceptance standards:

### 3.1 Functional Module Completion (Highest Priority)
- **Current Problems**: The basic data structure fails to meet the Backlog requirements, resulting in broken business logic loops (e.g., cannot automatically or manually close jobs without a Deadline; cannot determine if recruitment is full without Vacancies).
- **Optimization Details**:
  - Mandatory addition of the `Deadline` and `Number of vacancies` fields specified in US12 (Image 3).
  - Mandatory addition of the `Applicant count` (Image 1) and `Availability` (Image 2) columns specified in US14 and US16.

### 3.2 Data Statistics and Visualization (Medium-High Priority)
- **Current Problems**: The MO interface lacks data overview capabilities; all information must be acquired by reading the table row by row.
- **Optimization Details**:
  - Add top data summary cards (Data Cards) on the job list page to intuitively display workload and recruitment progress.
  - Implement visual tags (Pills) for the Status column modification.

### 3.3 Page Interaction and Visual Design (Medium Priority)
- **Current Problems**: The interface uses the most basic native browser controls, resulting in rigid interactions and an outdated visual appearance.
- **Optimization Details**:
  - Upgrade the native Decision dropdown in Image 2 into a more user-friendly quick action button group.
  - Merge redundant action columns in Image 1, using modern Icon buttons (e.g., edit icon, detail icon) to improve page tidiness.

### 3.4 Responsive Design (Medium Priority)
- **Current Problems**: As the number of table columns increases (e.g., adding Deadline, Applicant Count), the tables may overflow or distort on smaller screens.
- **Optimization Details**:
  - Introduce responsive container support for tables (horizontal scrollbar `overflow-x: auto`), or collapse table rows into card displays in mobile views to ensure MOs can review information properly across different devices.

Note: This part is individually finished by liuzetang.