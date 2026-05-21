## Iteration 0 / First Meeting Summary

### 1. Meeting Overview

- **Date**: 2026-03-09
- **Attendees**: Zhu Siyuan, Chen Taiyu
- **Recorder**: Zhu Siyuan
- **Main Objectives**:
  - Align understanding of course project requirements (TA Recruitment System);
  - Discuss and determine preliminary development approach and tech stack;
  - Clarify Git repository structure and branch management approach;
  - List todos before the first iteration.

---

### 2. Project Goals and Scope Consensus

- System type determined: **Lightweight JSP/Servlet Web Application**, deployed on local Tomcat.
- Data storage: Using only **text files (CSV/JSON)**, no database.
- Main user roles: **TA, Module Organiser (MO), Admin**.
- Initial goals:
  - Complete basic flow for TA registration / login / browse jobs;
  - Establish a clear, scalable project structure for subsequent iterative development;
  - Lay the foundation for subsequent requirement analysis, prototype design, and multiple iteration development.

---

### 3. Completed Items

**(1) Code and Environment**

- Completed internal discussion on technical approach, confirmed **JDK 17 + Tomcat 10 + Maven + JSP/Servlet + text file storage** lightweight Web architecture.
- Main members have completed local development environment setup:
  - Installed and configured JDK 17 (`JAVA_HOME` and `Path`);
  - Installed and configured Tomcat 10 (`CATALINA_HOME` and running verification);
  - Installed and configured Maven (`MAVEN_HOME` and `mvn -v` verification).
- Created Maven Web project skeleton in `ta-webapp` directory:
  - Directory structure includes `src/main/java`, `src/main/webapp`, `data`, `docs`, etc.;
  - Added sample CSV data files in `data/` for subsequent file read/write logic implementation.
- Created and initialized GitHub repository:
  - Pushed `ta-webapp` to remote `master` branch as team's unified code baseline;
  - Recorded environment configuration steps and project structure in `README.md`.

**(2) Process and Standards**

- In the first team meeting, discussed and initially agreed on the following software engineering process:
  - Adopt iterative / Agile development approach, each iteration should have a demonstrable version;
  - All members submit code through Git branches + Pull Requests, no direct development on `master`;
  - Requirements, design, testing, and other documents are centrally maintained in `docs/` directory;
  - Before major milestones (each assessment), complete branch merges and version tagging.

---

### 4. Subsequent Todos (Next Phase Work)

**4.1 Requirements and Analysis Phase — Clarify User Stories and Product Scope**

- Complete first systematic requirement analysis:
  - Through Story Writing Workshop, write user stories around three types: TA / MO / Admin;
  - Add acceptance criteria, priorities, and estimated workload for each user story;
  - Enter organized user stories into the Product Backlog Excel required by the course.
- Clarify system boundaries and core feature set:
  - List "minimum viable features for Iteration 1" (e.g., registration, login, browse jobs);
  - Mark optional advanced features (e.g., simple "smart matching") as candidates for subsequent iterations.

**4.2 Prototype Design Phase — Form Discussable Interface Blueprint**

- Based on requirement analysis results, complete low/mid-fidelity prototypes for main pages:
  - TA perspective: Register / Login / View jobs / View application status;
  - MO perspective: Post jobs / View applicants / Update application status;
  - Admin perspective: View overall jobs and workload overview.
- Collect initial feedback from team and instructor/teaching assistants, and make 1-2 rounds of lightweight adjustments to the prototype.

**4.3 Investigation and Feedback (Fact-Finding) — Collect Real Usage Requirements**

- Select appropriate research methods (questionnaire, interview, small-scale trial, etc.) to get opinions from potential users/classmates;
- Organize research results into brief conclusions to support priority and scope determination in Product Backlog;
- Add "requirement sources" and "assumptions" in `docs/requirements.md` to enhance requirement traceability.

**4.4 Iteration 1 Development Preparation — Prepare for Code Phase Task Splitting and Assignment**

- Add key modules and call relationships needed for Iteration 1 in `docs/architecture.md` (simple architecture diagram or text description is acceptable);
- Based on determined user stories, split Iteration 1 tasks into assignable development tasks:
  - UI development (JSP);
  - Controller layer (Servlet);
  - File storage layer (CSV read/write);
  - Basic testing and verification.
- Assign at least 1 clear task to each member, and map to Git feature branch naming (e.g., `sy/login-basic-flow`), to prepare for the next code phase.

## Iteration 1 / Second Meeting Summary

### 1. Meeting Overview

- **Date**: 2026-03-13
- **Attendees**: Zhu Siyuan, Chen Taiyu, Liu Zeteng
- **Recorder**: Chen Taiyu
- **Main Objectives**:
  - Clarified Phase 1 task requirements
  - Discussed and determined next direction and work allocation;
  - Communicated with instructor, understood specific requirements for backlog.

### 2. Project Goals

- Started working on:
  - Product backlog
  - Prototype
  - Brief report

### 3. Completed Items

- Product backlog
- Draft of brief report
- Identified optimization directions for both documents
