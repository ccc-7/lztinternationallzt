# TA Recruitment System — Maintenance Guide

## 1. Adding a New Feature

The system follows a consistent three-layer pattern for every feature. To add a new feature, follow this template.

### Step 1 — Create the Model (if needed)

Place it under `src/main/java/edu/bupt/ta/model/`. If the entity is not a file-backed entity (e.g., a request parameter object or a display DTO), you can use a plain POJO. For example, `ApplicationWithJob` is a display DTO that combines `Application` with denormalized job fields; it has no CSV storage.

```java
// src/main/java/edu/bupt/ta/model/Notification.java
public class Notification {
    private String notificationId;
    private String userId;
    private String message;
    private String createdAt;
    // getters and setters...
}
```

### Step 2 — Create the Service

Place it under `src/main/java/edu/bupt/ta/service/`. Keep it stateless — hold a single shared instance as a field in the servlet (or pass it as a constructor argument). All CSV I/O goes through `FileStorageUtil`; do not read or write CSV files directly from a service.

```java
// src/main/java/edu/bupt/ta/service/NotificationService.java
public class NotificationService {
    public List<Notification> getByUserId(String userId) { ... }
    public void save(Notification n) { ... }
}
```

### Step 3 — Create the Servlet

Place it under `src/main/java/edu/bupt/ta/controller/`. Use the `@WebServlet` annotation to declare URL patterns. Each servlet typically handles both `doGet()` (load and display) and `doPost()` (receive form submissions).

```java
@WebServlet(urlPatterns = {"/notifications", "/notifications/delete"})
public class NotificationServlet extends HttpServlet {
    private final NotificationService service = new NotificationService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Load data, set request attributes, forward to JSP
        req.setAttribute("notifications", service.getByUserId(currentUser.getUserId()));
        req.getRequestDispatcher("/WEB-INF/jsp/ta/notifications.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) ... {
        // Call service, redirect back
    }
}
```

### Step 4 — Add Session Check (Authentication Guard)

Every servlet that requires authentication should check `session.getAttribute("currentUser")` at the top of both `doGet` and `doPost`. If null, redirect to the login page. See existing servlets like `TaDashboardServlet` for the exact pattern.

### Step 5 — Create the JSP

Place it under `src/main/webapp/WEB-INF/jsp/`. Always use `<jsp:include>` to include the shared `header.jspf` and `footer.jspf` fragments. Access request attributes set by the servlet using `${attributeName}` JSTL/EL expressions.

```jsp
<jsp:include page="/WEB-INF/jsp/common/header.jspf"/>
<!-- Page content -->
<jsp:include page="/WEB-INF/jsp/common/footer.jspf"/>
```

### Step 6 — Log the Operation

If the feature creates, updates, or deletes data, call `LogService.log()` to record it in `system_logs.csv`. See `AdminServlet` for the exact invocation pattern.

---

## 2. Adding a New Data Field

Adding a field requires changes across the entire stack. Complete all steps or the CSV file will become corrupted.

### Example: Adding a `phone` field to `ta_users.csv`

**Step 1 — Model:** Add the field to the `User` class with getter and setter.

```java
// User.java
private String phone;
public String getPhone() { return phone; }
public void setPhone(String phone) { this.phone = phone; }
```

**Step 2 — CSV Header:** Add the column name to `USERS_HEADER` in `FileStorageUtil`.

```java
private static final String USERS_HEADER =
    "userId,username,password,name,email,role,year,major,skills,status," +
    "availability,personalStatement,relevantCourses,projectExperience," +
    "preferredRole,summaryStatus,cvStoredName,cvOriginalName,cvContentType," +
    "cvUploadedAt,cvStatus,phone";   // ← new column
```

**Step 3 — CSV Parsing:** Update `parseUser()` in `FileStorageUtil` to read the new column by index. Insert the new field between existing ones, counting commas carefully.

**Step 4 — CSV Serialization:** Update `toCsv(User)` and `toCsvFields()` to include the new field in the correct column position.

**Step 5 — Service:** If the field affects computed values (like `summaryStatus`), update the relevant calculation in `UserService`.

**Step 6 — Form:** Add an `<input>` field to the registration JSP (`register.jsp`) and profile JSP (`ta/profile.jsp`).

**Step 7 — Seed Data:** Update `ensureDefaultUsers()` in `FileStorageUtil` to provide default values for the new column in all seed rows.

**Step 8 — Migrate Existing Data:** Existing rows in `ta_users.csv` will be missing the new column on the next read. In `parseUser()`, treat a missing trailing field as empty string (the current parser already does this for trailing fields), so old records degrade gracefully.

---

## 3. Adding a New Page

### Adding a Static Informational Page

Create a JSP file under `src/main/webapp/WEB-INF/jsp/`. You can link to it directly from any existing page:

```jsp
<a href="${pageContext.request.contextPath}/WEB-INF/jsp/about.jsp">About</a>
```

Or map it through a servlet for access control.

### Adding a Role-Specific Page

1. Create the JSP at `src/main/webapp/WEB-INF/jsp/{role}/new-page.jsp`.
2. Add the navigation link to the appropriate sidebar (`sidebar-ta`, `sidebar-mo`, or `sidebar-admin`) in the role's dashboard JSP.
3. If the page needs dynamic data, create a servlet and forward to the JSP from the servlet's `doGet`.

### Extending the Admin Panel

Admin pages are managed by `AdminServlet` using a single controller with multiple sub-actions. To add a new admin page, add a new URL pattern to the `@WebServlet` annotation, then add a case in `showPage()` to render the appropriate JSP. Do not create a separate servlet for Admin sub-pages — keep all admin routing in `AdminServlet`.

```java
@WebServlet(urlPatterns = {
    "/admin/applications",
    "/admin/reports",           // ← new
    ...
})
```

Then in `AdminServlet.doGet()`:

```java
case "/admin/reports":
    showReports(req, resp);    // forward to reports.jsp
    break;
```

---

## 4. Common Errors and Troubleshooting

### 4.1 CSV File Not Found / Data Not Persisting

**Symptoms:** New data appears on the page but disappears after restart.

**Cause:** The `data/` directory path resolution failed, and files were created in the wrong location.

**Fix:** Check where the files actually are. Set the environment variable `TA_DATA_DIR` to an absolute path before starting Tomcat:

```bash
# Linux/macOS
export TA_DATA_DIR=/full/path/to/lztinternationallzt/data

# Windows (CMD)
set TA_DATA_DIR=D:\Github_Files\TA_SYS\lztinternationallzt\data
```

Or pass it as a JVM argument:

```bash
-Dta.data.dir=D:\Github_Files\TA_SYS\lztinternationallzt\data
```

### 4.2 Empty Dashboard / No Data Loaded

**Symptoms:** Dashboard shows "0" for all counts; no jobs appear.

**Cause:** The CSV files were recreated (e.g., by a fresh build) but are empty except for headers. The seed data was not inserted because `lines.size() > 1` evaluated to false only for truly empty files — but if the file has a header plus a blank line, it may still be considered non-empty.

**Fix:** Delete the CSV files in `data/` and restart the application. `ensureDefaultUsers()`, `ensureDefaultJobs()`, and `ensureDefaultApplications()` will regenerate them from scratch.

### 4.3 NullPointerException in Servlet — currentUser is null

**Symptoms:** `NullPointerException` when calling `currentUser.getUserId()`.

**Cause:** The servlet was accessed without a valid session (user not logged in). Every servlet must check for `session.getAttribute("currentUser")` before accessing user data.

**Fix:** Add the session guard at the top of both `doGet` and `doPost`:

```java
User currentUser = (User) session.getAttribute("currentUser");
if (currentUser == null) {
    resp.sendRedirect(req.getContextPath() + "/");
    return;
}
```

### 4.4 404 on JSP After Forward

**Symptoms:** Browser shows 404 even though the JSP file exists.

**Cause:** `RequestDispatcher.forward()` paths are relative to the `WEB-INF/` directory, but the URL pattern is wrong or the file is outside `WEB-INF/`.

**Fix:** Use `/WEB-INF/jsp/` as the base path. Example:

```java
req.getRequestDispatcher("/WEB-INF/jsp/ta/dashboard.jsp").forward(req, resp);
```

Never place JSP files outside `WEB-INF/` if they need server-side data, or outside `webapp/` if they need browser access.

### 4.5 CSV Row Count Mismatch on Write

**Symptoms:** `IndexOutOfBoundsException` or rows with wrong column counts after saving.

**Cause:** A new field was added to the Model and Service but `toCsv()` in `FileStorageUtil` was not updated, so the serialized line has fewer columns than the header expects.

**Fix:** Whenever a field is added to the header constant, the corresponding `toCsv()` and `parseUser()` methods must be updated in lockstep. Verify the CSV line count matches the header column count before deploying.

### 4.6 MO Cannot See Applications

**Symptoms:** `MOApplicationServlet` shows an empty list even though applications exist for the MO's jobs.

**Cause:** The MO's `displayName` in the session does not exactly match the `organiser` field in `jobs.csv`. The comparison is case-insensitive but whitespace-sensitive. If the MO's name was edited after the job was created, the match fails.

**Fix:** The `organiser` field in `jobs.csv` is set at job creation time from `currentUser.getDisplayName()`. If the MO changes their display name, it does not retroactively update existing jobs. Edit the `organiser` column in `jobs.csv` directly to match the current display name, or recreate the job.

### 4.7 Application Stuck at PENDING

**Symptoms:** An application remains at `PENDING` and cannot be moved to `ACCEPTED`.

**Cause:** `ApplicationService.updateStatus()` checks whether the target job still exists before updating. If the job was deleted by an Admin, the update is rejected.

**Fix:** If a job must be removed but existing applications need resolution, use the Admin panel to update the application statuses before deleting the job. Deleting a job does not cascade-delete its applications — `AdminServlet` only cleans up orphaned rows referencing deleted jobs when the admin views the applications list.

---

## 5. Dependency Reference

When adding features, be aware of these constraints:

- **Jakarta EE 10 / Servlet 6.0** — Use `jakarta.servlet.*` packages, not `javax.servlet.*`.
- **No database** — All data lives in CSV files. Every service method reads from and writes to `FileStorageUtil`. Do not introduce direct file I/O in service classes.
- **Single JVM lock** — `FileStorageUtil.IO_LOCK` serializes all CSV reads and writes. This is sufficient for a single Tomcat instance but is not suitable for multi-instance deployments.
- **No authentication framework** — Session-based authentication is handled manually. Each servlet checks `session.getAttribute("currentUser")`. There is no role-based annotation or filter for access control.
