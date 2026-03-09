## Architecture Overview

This document describes the high-level architecture of the TA Recruitment System web application.

The main design goals are:

- Keep the system **simple, modular, and extensible**.
- Respect the module constraints (JSP/Servlet + text files, no database, no heavy frameworks).
- Support collaborative development by clearly separating responsibilities.

---

### 1. Layers and Responsibilities

The application is organised into several logical layers:

1. **Presentation Layer (View)**
   - Technology: **JSP pages** under `src/main/webapp/`.
   - Responsibility:
     - Render HTML views for TA, MO, and Admin.
     - Display data passed from controllers.
     - Provide forms for user input (login, registration, job posting, applications).

2. **Controller Layer**
   - Technology: **Servlets** in `src/main/java/edu/bupt/ta/controller/`.
   - Responsibility:
     - Handle HTTP requests and responses.
     - Perform basic input validation.
     - Call service layer methods.
     - Decide which JSP to forward/redirect to.

3. **Service / Business Logic Layer**
   - Location: `src/main/java/edu/bupt/ta/service/`.
   - Responsibility:
     - Implement business rules (e.g. who can apply, status transitions, workload checks).
     - Orchestrate calls between controllers and file storage.

4. **File Storage / Repository Layer**
   - Location: `src/main/java/edu/bupt/ta/storage/`.
   - Responsibility:
     - Read and write data from/to CSV/JSON/TXT files in the `data/` directory.
     - Provide simple APIs to services, such as:
       - `List<Job> loadJobs()`
       - `void saveApplications(List<Application> apps)`
     - Hide file formats from upper layers.

5. **Domain Model**
   - Location: `src/main/java/edu/bupt/ta/model/`.
   - Responsibility:
     - Define core entities: `User`, `Job`, `Application`, etc.
     - Keep them as simple POJOs (fields + getters/setters, minimal logic).

---

### 2. Data Storage Design (Text Files)

All persistent data is stored under the project’s `data/` directory.  
Example files (initial samples are included):

- `data/ta_users.csv`
  - Fields: `id,name,email,role,year,major,status`
  - Represents TA applicant accounts and profile data.

- `data/jobs.csv`
  - Fields: `job_id,title,module_code,organiser,min_year,max_year,hours,status`
  - Represents TA job postings created by Module Organisers.

- `data/applications.csv`
  - Fields: `application_id,user_id,job_id,status,submitted_at,notes`
  - Represents which TA applied to which job and the current status.

The storage layer will provide helper methods to:

- Parse CSV lines into domain objects.
- Write updated objects back to files.
- Handle basic error cases (missing file, invalid record) in a controlled way.

---

### 3. Web Application Configuration

The web application is configured using the standard deployment descriptor:

- `src/main/webapp/WEB-INF/web.xml`

Key responsibilities of `web.xml`:

- Define servlet classes and their URL mappings (e.g. `/hello`, `/jobs`, `/apply`).
- Configure welcome file list (e.g. `index.jsp`).
- Optionally configure filters (e.g. simple authentication/authorisation checks) if needed.

Tomcat automatically deploys the generated `.war` file into its `webapps/` directory.

---

### 4. Future Extensions (Optional)

If time allows, the architecture can be extended in a controlled way:

- Add simple **filter-based authentication** (Servlet filters) to protect MO/Admin pages.
- Introduce a **lightweight validation utility** for common input checks.
- Extend storage layer to support different text formats (e.g. JSON) while keeping upper layers unchanged.

Any extension should remain consistent with the module’s constraints and keep the overall design understandable for all team members.

