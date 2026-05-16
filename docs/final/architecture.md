# TA Recruitment System — Architecture Documentation

## 1. System Overview

The TA Recruitment System (TA_SYS) is a web application built on **Jakarta EE (Servlet/JSP)** that manages the lifecycle of Teaching Assistant (TA) recruitment. It enables three roles — **TA** (candidate), **MO** (Module Organiser), and **Admin** (system administrator) — to post jobs, submit applications, review candidates, and perform system administration.

The system follows a traditional **MVC pattern** adapted for the Servlet/JSP stack, with a clear separation between the web presentation layer (JSP), the request handling layer (Servlet Controllers), the business logic layer (Service), and the data persistence layer (FileStorageUtil backed by CSV files).

---

## 2. Why Java Servlet/JSP

Java Servlet/JSP was chosen as the foundational technology for this project for several practical reasons:

**Lightweight and self-contained.** Unlike Spring Boot, which brings a large framework footprint with auto-configuration, embedded servers, and extensive annotation-driven magic, Servlet/JSP allows every component to be understood in plain Java. A `HttpServlet` subclass is just an ordinary Java class that overrides `doGet` and `doPost`. This makes the request lifecycle, session management, and forwarding/redirecting mechanics fully transparent to the developer.

**Direct HTTP control.** With Servlets, every aspect of the HTTP response is handled explicitly — setting content types, headers, status codes, and writing the response body. There is no abstraction layer obscuring what happens at the network level.

**JSP for view composition.** JSP works naturally with JSTL and JSTL functions (`fn:split`, `fn:length`), enabling clean iteration and conditional rendering within the HTML templates without resorting to scriptlets. The project uses JSP fragment includes (`.jspf` files) for shared header/footer/flash components, mirroring a layout system.

**Jakarta EE 6.0.** The project uses `jakarta.servlet-api` version 6.0, which is the standard post-Java EE 8 migration path. This keeps the project on a modern, maintained API while avoiding the heavier Spring ecosystem.

---

## 3. Why CSV/TXT File Storage

The system stores all structured data in **CSV files** rather than a relational database. This design choice serves the project's specific context:

**No external database dependency.** A CSV-based storage means the application is fully self-contained. There is no need to install, configure, or run a separate database server (MySQL, PostgreSQL, etc.). The data directory (`data/`) alongside the source code can be committed to version control, making the project portable and easy to set up in any environment with a Tomcat 10+ container.

**Suited for single-server, low-concurrency workloads.** This is a course project with a small user base. The I/O load is minimal, and a file-based store eliminates the complexity of connection pooling, ORM configuration, and SQL schema migrations. All persistence logic is concentrated in a single class — `FileStorageUtil`.

**Transparency and inspectability.** CSV files are human-readable and can be opened in any spreadsheet editor, which is helpful during development and debugging. The data format is immediately visible without querying a database.

**Atomic writes and synchronization.** `FileStorageUtil` implements a single-process synchronization mechanism using a static `IO_LOCK` object (`synchronized (IO_LOCK)`), ensuring that concurrent requests within the same Tomcat instance do not corrupt the CSV files. It also uses a temp-file-then-atomic-move pattern to prevent partial writes.

**Trade-off note.** CSV storage is not designed for high-concurrency or transactional workloads. If the application were to scale to many simultaneous users or require complex multi-table queries, a relational database would be necessary.

---

## 4. Architecture — Frontend JSP, Servlet Controller, Service, Model, File Storage

### 4.1 Request-Response Flow

```
Browser HTTP Request
        │
        ▼
┌─────────────────────────────┐
│  JSP (View Layer)            │  — Receives forwarded request with attributes.
│  .jsp files in WEB-INF/jsp/ │    Renders HTML using JSTL expression language.
└─────────────────────────────┘
        │ forwards back
        ▲
        │
┌─────────────────────────────┐
│  Servlet Controller         │  — Receives HTTP request. Validates session & role.
│  (@WebServlet classes)      │    Calls Service layer. Sets request attributes.
│  edu.bupt.ta.controller      │    Forwards to JSP or redirects.
└─────────────────────────────┘
        │ delegates business logic
        ▲
        │
┌─────────────────────────────┐
│  Service Layer              │  — Contains all business rules and calculations.
│  edu.bupt.ta.service        │    Validates input. Coordinates between Services.
│                             │    Does NOT write files directly (except CvFileService).
└─────────────────────────────┘
        │ reads/writes data
        ▲
        │
┌─────────────────────────────┐
│  FileStorageUtil             │  — Single class managing all CSV I/O.
│  edu.bupt.ta.storage         │    Synchronized read/write. Atomic writes.
│                             │    Handles Model ↔ CSV line conversion.
└─────────────────────────────┘
        │
        ▼
   CSV Files in data/
   ta_users.csv / jobs.csv / applications.csv / system_logs.csv
   PDF files in data/cvs/{userId}.pdf
```

### 4.2 Role of Each Layer

**JSP (View Layer)** — Located under `src/main/webapp/WEB-INF/jsp/`. JSP files do not contain business logic. They receive data through request attributes and the session, then render HTML using JSTL tags (`<c:if>`, `<c:forEach>`, `${sessionScope.currentUser.username}`, etc.). The project uses fragment includes (`header.jspf`, `footer.jspf`, `flash.jspf`) to share common page structure across all views. Each role has its own subdirectory (`ta/`, `mo/`, `admin/`) to group related pages.

**Servlet Controller** — Every controller is a `@WebServlet` class that maps to a specific URL pattern. Controllers are thin: they extract request parameters, check the session for the logged-in user and their role, delegate to one or more Service classes, and then either forward the request to a JSP or redirect the browser. No business logic lives here.

**Service Layer** — Each Service class is responsible for a single domain. `UserService` handles authentication and profile management. `JobService` handles job CRUD and the **skill-matching scoring algorithm**. `ApplicationService` handles the 9-step application validation pipeline. `LogService` writes system audit logs. `DashboardService` aggregates statistics for the TA dashboard. `AdminService` computes global admin statistics. `CvFileService` handles physical PDF file storage separately from the CSV metadata.

**Model** — Plain Old Java Objects (POJOs) with getters and setters. No annotations, no ORM mapping. The `User` class holds 22 fields covering identity, profile, and CV metadata. Enums (`UserRole`, `ApplicationStatus`, `JobStatus`) enforce type safety for role and status values. `ApplicationWithJob` is a view-oriented DTO that combines an `Application` with its associated `Job` details for display purposes.

**FileStorageUtil** — The only class that reads from or writes to CSV files. It maintains a static `IO_LOCK` object for synchronized access, uses a temp-file-then-atomic-move pattern (`writeLinesAtomically`) to prevent corruption, supports dual-write to a mirror directory, and automatically initializes the data directory with default users and jobs on first startup if the CSV files are empty. It is accessed by Services (not by Controllers directly).

### 4.3 Data Flow Example: TA Applies for a Job

1. TA logs in via `LoginServlet` → `UserService.authenticate()` → stored in session.
2. TA browses `JobListServlet` → `JobService.getOpenJobsForUser()` → each job's `matchScore` is calculated based on skill overlap between the TA's profile and the job's required skills.
3. TA clicks "Apply" → `ApplyServlet.doPost()` → `ApplicationService.apply()` runs 9 pre-checks (profile readiness, job open, year range, deadline, vacancy limit, duplicate application, active application cap).
4. On success → new `Application` saved via `FileStorageUtil.saveApplications()` → CSV row appended.
5. MO sees the application via `MOApplicationServlet` → reviews and updates status via `ApplicationService.updateStatus()`.
6. All state-changing operations are logged via `LogService.log()` to `system_logs.csv`.
