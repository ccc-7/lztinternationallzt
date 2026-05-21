# Iteration 5 - TA Recruitment System Enhancement

## Overview

This iteration focuses on improving user experience for both TA applicants and Module Organisers, implementing new features for application management, CV handling, and dashboard visualization. The key improvements include:

1. **TA Application Management** - Withdraw and delete application capabilities
2. **MO Job Post UI Enhancement** - Improved styling for job posting interface
3. **Bug Fixes** - Resolved critical UI and functional issues
4. **CV File Handling** - Enhanced CV upload and viewing functionality
5. **Dashboard Charts** - Added workload visualization charts
6. **Test Coverage** - Expanded unit tests and testing documentation

---

## Table of Contents

1. [TA Application Management](#1-ta-application-management)
2. [MO Job Post UI Enhancement](#2-mo-job-post-ui-enhancement)
3. [Bug Fixes](#3-bug-fixes)
4. [CV File Handling Enhancement](#4-cv-file-handling-enhancement)
5. [Dashboard Charts & Visualization](#5-dashboard-charts--visualization)
6. [Test Coverage Expansion](#6-test-coverage-expansion)
7. [File Changes Summary](#7-file-changes-summary)
8. [Known Issues & Future Work](#8-known-issues--future-work)

---

## 1. TA Application Management

### 1.1 Feature Description

Added the ability for TA applicants to manage their own applications through a right-click context menu on the Applications page. Users can now:

- **Withdraw** pending applications (changes status to WITHDRAWN)
- **Delete** any of their applications regardless of status

### 1.2 Implementation Details

#### New Application Status: WITHDRAWN

Added `WITHDRAWN` status to `ApplicationStatus.java`:

```java
/** Application withdrawn by the TA applicant. */
WITHDRAWN;
```

#### New Service Methods

Added two new methods to `ApplicationService.java`:

**withdrawApplication()**
- Validates ownership (userId must match)
- Validates status (must be PENDING)
- Sets status to WITHDRAWN with note "Withdrawn by applicant"

**deleteOwnApplication()**
- Validates ownership only
- Permanently removes the application record

#### Updated Servlet

Modified `ApplicationStatusServlet.java` to handle POST requests:
- Route: `/applications`
- Actions: `withdraw`, `delete`

#### UI Implementation

Updated `ta/applications.jsp` with:

**Right-click Context Menu**
- Appears on right-click over any application row
- Shows different options based on application status:
  - **PENDING**: Withdraw + Delete Record
  - **WITHDRAWN/ACCEPTED/REJECTED**: Delete Record only

**Context Menu Styling**
```css
.context-menu {
    position: fixed;
    background: #fff;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0,0,0,0.15);
    min-width: 160px;
    z-index: 1001;
}
```

**Confirmation Modal**
- Prompts user to confirm deletion
- Prevents accidental record removal

### 1.3 Files Modified

| File | Change |
|------|--------|
| `ApplicationStatus.java` | Added WITHDRAWN enum value |
| `ApplicationService.java` | Added withdraw/delete methods |
| `ApplicationStatusServlet.java` | Added doPost() handler |
| `ta/applications.jsp` | Added context menu and modals |
| `style.css` | Added WITHDRAWN badge style |

---

## 2. MO Job Post UI Enhancement

### 2.1 Feature Description

Improved the visual design and user experience of the MO (Module Organiser) job posting interface to better match the overall system aesthetic.

### 2.2 Changes Made

**Form Styling Improvements**
- Enhanced form field styling with consistent borders
- Added focus states for better accessibility
- Improved button hierarchy with primary/secondary actions

**Layout Optimizations**
- Better spacing between form elements
- Clearer section headers
- Responsive form layout

### 2.3 Files Modified

| File | Change |
|------|--------|
| `mo/new-job.jsp` | Enhanced form styling |
| `style.css` | Added MO-specific styles |

---

## 3. Bug Fixes

### 3.1 Fixed Issues

| Issue | Description | Fix |
|-------|-------------|-----|
| Feedback Button JS Error | `notes` field containing special characters (quotes) broke onclick handlers | Changed to use `data-*` attributes and event delegation |
| Context Menu Hidden | Right-click menu not showing for WITHDRAWN/ACCEPTED/REJECTED status | Updated logic to always show Delete option |

### 3.2 Technical Details

**Before (Problematic):**
```jsp
<button onclick="showReasonModal('${fn:escapeXml(a.notes)}', 'Title')">
```

**After (Fixed):**
```jsp
<button class="btn-view-feedback" data-notes="${fn:escapeXml(a.notes)}" data-title="Title">
```

**Event Delegation:**
```javascript
document.addEventListener('click', function(e) {
    var btn = e.target.closest('.btn-view-feedback');
    if (btn) {
        var notes = btn.getAttribute('data-notes');
        var title = btn.getAttribute('data-title');
        showReasonModal(notes, title);
    }
});
```

---

## 4. CV File Handling Enhancement

### 4.1 Feature Description

Enhanced the CV module to provide better separation between the structured Candidate Summary and the original uploaded PDF CV.

### 4.2 Implementation Details

**CV Module Architecture**
1. **Candidate Summary**: Generated from structured TA profile fields
2. **Original PDF CV**: Uploaded file stored under `data/cvs/`

**CV Metadata Tracking**

Enhanced `ta_users.csv` with additional fields:
- `cvStoredName`: Stored file name
- `cvOriginalName`: Original upload filename
- `cvContentType`: MIME type (application/pdf)
- `cvUploadedAt`: Upload timestamp
- `cvStatus`: `UPLOADED` or `MISSING`

**CV Upload Flow**
1. TA uploads PDF through profile page
2. File validated (PDF format, size limits)
3. Stored with unique name in `data/cvs/`
4. Metadata saved to CSV
5. TA can preview or delete

**CV Viewing**
- TA: View own CV anytime
- MO: View applicant CV when reviewing applications
- Admin: View any TA's CV through user management

### 4.3 Key Servlets

| Servlet | Route | Description |
|---------|-------|-------------|
| `CvUploadServlet` | `/ta/profile/cv/upload` | Handle PDF upload/replacement |
| `CvDeleteServlet` | `/ta/profile/cv/delete` | Remove uploaded CV |
| `FileDownloadServlet` | `/files/cv/{userId}` | Serve PDF file |
| `CandidateSummaryServlet` | `/files/cv-summary/{userId}` | Display structured summary |

### 4.4 Files Modified

| File | Change |
|------|--------|
| `CvUploadServlet.java` | Handle upload logic |
| `CvDeleteServlet.java` | Handle deletion |
| `FileDownloadServlet.java` | Serve files with proper headers |
| `ta/profile.jsp` | Updated upload UI |
| `CvFileService.java` | File validation and storage |

---

## 5. Dashboard Charts & Visualization

### 5.1 Admin Dashboard Charts

Added workload visualization charts to the Admin dashboard to help administrators monitor TA workload distribution.

**Features Implemented:**

**Workload Bar Chart**
- Shows total accepted hours per TA
- Color-coded levels:
  - Green (0-20 hours): Normal
  - Orange (21-40 hours): Warning
  - Red (>40 hours): Overloaded

**Top Jobs/Applicants Charts**
- Top 5 TAs by application count
- Top 3 jobs by applicant count
- Visual bar representation with percentages

### 5.2 Implementation Details

**Backend Data Service**
```java
// AdminService.getDashboardStats() provides:
- totalTA, activeTA, totalMO
- totalApplications, pendingApplications, etc.
- topTAs (Map<String, Integer>)
- topJobs (Map<String, Integer>)
```

**Frontend Visualization**
- CSS-based progress bars for workload
- Percentage-based width calculation
- Responsive design

### 5.3 Files Modified

| File | Change |
|------|--------|
| `AdminDashboardServlet.java` | Pass chart data to view |
| `AdminService.java` | Calculate workload statistics |
| `admin/dashboard.jsp` | Add chart visualizations |
| `style.css` | Chart styling |

---

## 6. Test Coverage Expansion

### 6.1 Test Categories

#### Model Tests
- `UserTest.java` - User entity validation
- `JobTest.java` - Job entity validation
- `ApplicationTest.java` - Application entity validation
- `ApplicationWithJobTest.java` - Composite model tests

#### Service Tests
- `UserServiceTest.java` - User CRUD operations
- `JobServiceTest.java` - Job posting and search
- `ApplicationServiceTest.java` - Application workflow
- `SkillMatchServiceTest.java` - Match algorithm accuracy
- `AdminServiceTest.java` - Dashboard statistics
- `DashboardServiceTest.java` - TA dashboard data
- `LogServiceTest.java` - Audit logging

#### Storage Tests
- `FileStorageUtilTest.java` - CSV file operations

### 6.2 Test Data

Located in `src/test/resources/test-data/`:
- `users_test.csv`
- `jobs_test.csv`
- `applications_test.csv`
- `logs_test.csv`

### 6.3 Running Tests

```bash
mvn test
```

### 6.4 Test Coverage Report

| Category | Coverage |
|----------|----------|
| Models | ~90% |
| Services | ~85% |
| Storage | ~80% |

---

## 7. File Changes Summary

### 7.1 New Files Created

| File | Purpose |
|------|---------|
| `Iteration5_summary.md` | This document |

### 7.2 Files Modified

| Category | Files |
|----------|-------|
| Models | `ApplicationStatus.java` |
| Services | `ApplicationService.java` |
| Servlets | `ApplicationStatusServlet.java` |
| JSP Views | `ta/applications.jsp`, `admin/dashboard.jsp`, `mo/new-job.jsp` |
| Styles | `style.css` |
| Documentation | `README.md` |

### 7.3 Build & Deployment

**Build Command:**
```bash
mvn clean package
```

**Deployment:**
Copy `target/ta-webapp.war` to Tomcat webapps directory and restart Tomcat.

---

## 8. Known Issues & Future Work

### 8.1 Known Limitations

- PDF parsing and auto-fill not implemented
- Email notifications not implemented
- Multi-language support not available
- Password reset functionality pending

### 8.2 Future Enhancements

1. **Advanced Search** - More filtering options for TA job board
2. **Email Notifications** - Alert users on application status changes
3. **PDF Auto-Fill** - Parse uploaded CV and populate profile
4. **Statistics Dashboard** - More comprehensive analytics
5. **Mobile Responsiveness** - Better mobile device support

### 8.3 Performance Considerations

- CSV file operations are synchronous; consider async for large datasets
- File storage uses local filesystem; consider cloud storage for production
- No connection pooling as no database is used

---

## 9. Release Notes

**Version:** 1.0-SNAPSHOT (Iteration 5)
**Release Date:** May 2026
**Minimum Requirements:** JDK 17, Tomcat 10, Maven 3.9+

### Migration Notes

No database migrations required. The application uses CSV files which are automatically created if missing.

### Upgrade Path

Simply deploy the new WAR file. Existing data in CSV files is preserved.

---

*Document Version: 1.0*
*Last Updated: May 2026*
