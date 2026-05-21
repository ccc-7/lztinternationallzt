# Final Testing Document

| Item | Value |
|------|-------|
| **Project** | TA Recruitment System |
| **Task** | TEST-01 (B-05) |
| **Version** | Final / Iteration 5 |
| **Date** | 2026-05-21 |
| **Base URL** | `http://localhost:8080/ta-webapp/` |
| **Test type** | Manual functional + negative testing |
| **Owner** | Team B (framework); Team A may supplement TA/CV screenshots |

---

## 1. Purpose

This document defines the **final acceptance test suite** for the TA Recruitment System. It covers core **normal flows** and **exception flows** required for demonstration and grading.

Each case includes: **Test ID**, **Purpose**, **Precondition**, **Steps**, **Expected Result**, **Actual Result**, **Status**, and **Screenshot Evidence**.

> **Note:** Fill in **Actual Result**, **Status**, and attach screenshots during execution. Use `Pass` / `Fail` / `Blocked` / `To be executed` for Status.

---

## 2. Test Environment

### 2.1 Prerequisites

- Java 17+, Maven 3.x, Apache Tomcat 10+ (Jakarta EE)
- Build: `mvn clean package` → deploy `target/ta-webapp.war`
- Browser: Chrome / Edge (latest)

### 2.2 Default Accounts

| Username | Password | Role | User ID | Notes |
|----------|----------|------|---------|-------|
| `seele` | `123456` | TA | U001 | Has profile + applications; use for duplicate/expired-job negative tests |
| `luna` | `123456` | TA | U002 | Good for profile/CV/upload/apply happy path |
| `kevin` | `123456` | TA | U003 | Alternate TA |
| `mo1` | `123456` | MO | U004 | Dr.Wang — owns J001, J003 |
| `mo2` | `123456` | MO | U005 | Dr.Liu — owns J002, J004 |
| `admin` | `123456` | ADMIN | U006 | System admin |

### 2.3 Default Data (seed)

- Jobs **J001–J004** are `OPEN`; default deadline **2026-06-30** (see `data/jobs.csv`)
- Sample applications exist for **U001** (seele); **luna (U002)** typically has no applications — suitable for TC-07

### 2.4 Screenshot Convention

Save evidence under:

```text
docs/screenshots/final/
  TC-01-ta-login.png
  TC-04-ta-upload-cv.png
  ...
```

Team A may add TA/CV-specific captures; Team B owns the document structure and non-CV cases.

### 2.5 Status Legend

| Status | Meaning |
|--------|---------|
| To be executed | Not run yet |
| Pass | Matches expected result |
| Fail | Does not match expected result |
| Blocked | Cannot run (environment/data issue) |

---

## 3. Test Case Summary

| Test ID | Title | Type |
|---------|-------|------|
| TC-01 | TA login | Normal |
| TC-02 | TA save profile | Normal |
| TC-03 | TA preview candidate summary | Normal |
| TC-04 | TA upload PDF CV | Normal |
| TC-05 | TA replace PDF CV | Normal |
| TC-06 | TA delete PDF CV | Normal |
| TC-07 | TA apply valid job | Normal |
| TC-08 | TA cannot apply expired job | Exception |
| TC-09 | TA cannot apply duplicate job | Exception |
| TC-10 | MO view Candidate Summary | Normal |
| TC-11 | MO view uploaded CV | Normal |
| TC-12 | MO update application status | Normal |
| TC-13 | Admin view workload | Normal |
| TC-14 | `/admin/stats` does not break | Normal |
| TC-15 | Invalid PDF upload rejected | Exception |
| TC-16 | TA withdraw pending application | Normal |
| TC-17 | TA delete any application | Normal |
| TC-18 | Context menu shows correct options | Normal |

---

## 4. Test Cases

---

### TC-01 — TA Login

| Field | Content |
|-------|---------|
| **Test ID** | TC-01 |
| **Purpose** | Verify a registered TA can authenticate and reach the TA dashboard. |
| **Precondition** | Application deployed; default user `luna` / `123456` exists in `data/ta_users.csv`. User is logged out. |
| **Steps** | 1. Open `{baseURL}/home`.<br>2. Enter username `luna`, password `123456`.<br>3. Click **Log in**. |
| **Expected Result** | Redirect to `/ta/dashboard`; top bar shows username `luna`; no error flash message. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-01-ta-login.png` |

---

### TC-02 — TA Save Profile

| Field | Content |
|-------|---------|
| **Test ID** | TC-02 |
| **Purpose** | Verify TA can update structured profile fields and persist them. |
| **Precondition** | Logged in as TA (`luna`). |
| **Steps** | 1. Go to **My Profile** (`/ta/profile`).<br>2. Change **Major**, **Skills**, or **Personal Statement**.<br>3. Click **Save Profile**.<br>4. Refresh the page. |
| **Expected Result** | Success flash message; changed fields remain after refresh; `data/ta_users.csv` row for U002 updated. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-02-ta-save-profile.png` |

---

### TC-03 — TA Preview Candidate Summary

| Field | Content |
|-------|---------|
| **Test ID** | TC-03 |
| **Purpose** | Verify TA can open the HTML Candidate Summary generated from profile fields (separate from PDF CV). |
| **Precondition** | Logged in as TA with profile fields filled (`luna` or `seele`). |
| **Steps** | 1. Open `/ta/profile`.<br>2. Click **Preview Candidate Summary** (or **Preview Summary**).<br>3. Observe the new browser tab. |
| **Expected Result** | New tab opens `{baseURL}/files/cv-summary/{userId}`; page shows structured summary (name, year, major, skills, statement, etc.); HTTP 200, readable layout. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-03-ta-preview-summary.png` |

---

### TC-04 — TA Upload PDF CV

| Field | Content |
|-------|---------|
| **Test ID** | TC-04 |
| **Purpose** | Verify TA can upload a valid PDF CV (≤ 5 MB). |
| **Precondition** | Logged in as TA without an uploaded CV (e.g. `luna` if no `data/cvs/U002.pdf`). Valid sample PDF available. |
| **Steps** | 1. Open `/ta/profile`.<br>2. In **Original PDF CV**, choose a `.pdf` file (< 5 MB).<br>3. Click **Upload CV**.<br>4. Confirm profile section shows uploaded file name. |
| **Expected Result** | Success message; `data/cvs/U002.pdf` created; user CSV metadata (`cvStoredName`, `cvUploadedAt`) updated; **View / Replace / Delete** controls visible. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-04-ta-upload-cv.png` _(Team A may provide additional CV UI shots)_ |

---

### TC-05 — TA Replace PDF CV

| Field | Content |
|-------|---------|
| **Test ID** | TC-05 |
| **Purpose** | Verify TA can replace an existing PDF with a new one. |
| **Precondition** | Logged in as TA with an existing uploaded CV (complete TC-04 or use `seele` if `U001.pdf` exists). Second valid PDF prepared. |
| **Steps** | 1. Open `/ta/profile`.<br>2. Note current CV file name.<br>3. Select a different PDF and upload.<br>4. Refresh profile page. |
| **Expected Result** | Success message; `data/cvs/{userId}.pdf` overwritten; displayed original filename updated; MO can still open CV via `/files/cv/{userId}`. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-05-ta-replace-cv.png` |

---

### TC-06 — TA Delete PDF CV

| Field | Content |
|-------|---------|
| **Test ID** | TC-06 |
| **Purpose** | Verify TA can remove the uploaded PDF CV. |
| **Precondition** | Logged in as TA with uploaded CV. |
| **Steps** | 1. Open `/ta/profile`.<br>2. Click **Delete CV** (confirm if prompted).<br>3. Refresh page. |
| **Expected Result** | Success message; CV file removed from disk (or metadata cleared); upload form shown again; `hasUploadedCv` false unless summary alone satisfies apply rules. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-06-ta-delete-cv.png` |

---

### TC-07 — TA Apply Valid Job

| Field | Content |
|-------|---------|
| **Test ID** | TC-07 |
| **Purpose** | Verify TA can submit an application to an open, non-expired job when eligible. |
| **Precondition** | Logged in as `luna` (U002). Profile complete **or** PDF CV uploaded. Job **J004** (or another OPEN job not yet applied) has deadline in the future. U002 has fewer than 3 active (PENDING/INTERVIEW) applications. |
| **Steps** | 1. Open **Job Board** (`/jobs`).<br>2. Open a valid job card → **Apply Now**.<br>3. Submit application.<br>4. Open **Applications** (`/applications`). |
| **Expected Result** | Success flash on redirect; new row in `data/applications.csv` with status `PENDING`; application visible on TA list and MO list for job owner. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-07-ta-apply-valid-job.png` |

---

### TC-08 — TA Cannot Apply Expired Job

| Field | Content |
|-------|---------|
| **Test ID** | TC-08 |
| **Purpose** | Verify system blocks applications after the job deadline. |
| **Precondition** | Logged in as TA (`luna`). One job (e.g. **J004**) has `deadline` set to a **past date** in `data/jobs.csv` (e.g. `2020-01-01`); job remains `OPEN`. TA has not applied to that job. TA is otherwise application-ready. |
| **Steps** | 1. Edit `data/jobs.csv` (or use Admin job edit) to set J004 deadline to past date; restart app if needed.<br>2. Open `/jobs`, attempt to apply to J004.<br>3. Observe flash/error message. |
| **Expected Result** | Application rejected; error such as *"The application deadline for this job has passed."*; no new application row created for that job. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-08-ta-expired-job-blocked.png` |

---

### TC-09 — TA Cannot Apply Duplicate Job

| Field | Content |
|-------|---------|
| **Test ID** | TC-09 |
| **Purpose** | Verify TA cannot submit two applications for the same job. |
| **Precondition** | Logged in as `seele` (U001). Existing application to **J001** (see `data/applications.csv` A001). |
| **Steps** | 1. Open `/jobs`.<br>2. Open job **J001** and click **Apply Now** again.<br>3. Read flash/error message. |
| **Expected Result** | Error such as *"you have already applied for this job."*; application count for J001 unchanged. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-09-ta-duplicate-blocked.png` |

---

### TC-10 — MO View Candidate Summary

| Field | Content |
|-------|---------|
| **Test ID** | TC-10 |
| **Purpose** | Verify MO can open an applicant's structured Candidate Summary. |
| **Precondition** | Logged in as `mo1` (Dr.Wang). At least one application exists for MO's jobs (e.g. seele → J001). |
| **Steps** | 1. Open `/mo/applications`.<br>2. Locate applicant row.<br>3. Click **View Summary**. |
| **Expected Result** | New tab: `/files/cv-summary/{applicantUserId}`; summary HTML displays applicant profile data; no login error. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-10-mo-view-summary.png` |

---

### TC-11 — MO View Uploaded CV

| Field | Content |
|-------|---------|
| **Test ID** | TC-11 |
| **Purpose** | Verify MO can open the applicant's original PDF CV when uploaded. |
| **Precondition** | Logged in as `mo1`. Applicant (e.g. `seele` U001) has uploaded PDF CV (`data/cvs/U001.pdf` exists). |
| **Steps** | 1. Open `/mo/applications`.<br>2. Find applicant with CV.<br>3. Click **View CV**. |
| **Expected Result** | Browser opens/displays PDF at `/files/cv/{userId}`; content type PDF; not 404. If no CV, **PDF Missing** hint shown instead of link. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-11-mo-view-cv.png` _(Team A may provide PDF viewer shot)_ |

---

### TC-12 — MO Update Application Status

| Field | Content |
|-------|---------|
| **Test ID** | TC-12 |
| **Purpose** | Verify MO can change application status (e.g. to ACCEPTED) for own jobs. |
| **Precondition** | Logged in as `mo1`. Pending application exists for MO's job (e.g. A001 U001 → J001). |
| **Steps** | 1. Open `/mo/applications`.<br>2. Click **Accept** (or **Interview** / **Reject**) on target row.<br>3. Confirm list refresh.<br>4. Log in as applicant TA and open `/applications`. |
| **Expected Result** | Status badge updated on MO page; `data/applications.csv` status field updated; TA sees new status on applications page. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-12-mo-update-status.png` |

---

### TC-13 — Admin View Workload

| Field | Content |
|-------|---------|
| **Test ID** | TC-13 |
| **Purpose** | Verify Admin dashboard shows TA workload based on **accepted** job hours (not raw application count). |
| **Precondition** | Logged in as `admin`. At least one `ACCEPTED` application exists (default: U001 → J003). |
| **Steps** | 1. Open `/admin/dashboard`.<br>2. Review **TA Workload by Accepted Hours** chart and **TA Workload Summary** table.<br>3. Optionally: MO accepts another application, refresh dashboard. |
| **Expected Result** | Chart bars and table show per-TA **total hours** from ACCEPTED jobs only; PENDING/REJECTED excluded; workload level (Normal / Warning / Overloaded) shown; after new ACCEPTED, hours increase. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-13-admin-workload.png` |

---

### TC-14 — `/admin/stats` Does Not Break

| Field | Content |
|-------|---------|
| **Test ID** | TC-14 |
| **Purpose** | Verify legacy route `/admin/stats` does not 404/500 and redirects safely. |
| **Precondition** | Logged in as `admin`. |
| **Steps** | 1. Navigate directly to `{baseURL}/admin/stats`.<br>2. Observe browser URL and page content. |
| **Expected Result** | HTTP redirect to `/admin/dashboard`; dashboard loads with charts and stats; **no** 404, **no** 500, **no** missing JSP error. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-14-admin-stats-redirect.png` |

---

### TC-15 — Invalid PDF Upload Rejected

| Field | Content |
|-------|---------|
| **Test ID** | TC-15 |
| **Purpose** | Verify non-PDF or invalid uploads are rejected with a clear error. |
| **Precondition** | Logged in as TA (`luna`). Sample non-PDF file ready (e.g. `.docx`, `.png`, or `.txt` renamed without real PDF content). |
| **Steps** | 1. Open `/ta/profile`.<br>2. Select non-PDF file in CV upload.<br>3. Submit upload.<br>4. Read flash message. |
| **Expected Result** | Upload fails; error such as *"Only PDF files are supported for CV upload."* or content-type rejection; no new valid CV stored; previous CV unchanged if any. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-15-invalid-pdf-rejected.png` |

---

### TC-16 — TA Withdraw Pending Application

| Field | Content |
|-------|---------|
| **Test ID** | TC-16 |
| **Purpose** | Verify TA can withdraw a pending application via right-click context menu. |
| **Precondition** | Logged in as TA (`seele` or `luna`). At least one PENDING application exists. |
| **Steps** | 1. Open `/applications`.<br>2. Locate a PENDING application row.<br>3. Right-click on the row.<br>4. Click **Withdraw** from the context menu. |
| **Expected Result** | Status changes to WITHDRAWN; flash message "Application withdrawn successfully"; `data/applications.csv` updated with WITHDRAWN status and notes. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-16-ta-withdraw.png` |

---

### TC-17 — TA Delete Any Application

| Field | Content |
|-------|---------|
| **Test ID** | TC-17 |
| **Purpose** | Verify TA can delete any of their applications (regardless of status) via right-click context menu. |
| **Precondition** | Logged in as TA with at least one WITHDRAWN, ACCEPTED, or REJECTED application. |
| **Steps** | 1. Open `/applications`.<br>2. Locate a WITHDRAWN/ACCEPTED/REJECTED application row.<br>3. Right-click on the row.<br>4. Click **Delete Record**.<br>5. Confirm in the modal dialog. |
| **Expected Result** | Application removed from list; `data/applications.csv` record deleted; flash message "Application deleted successfully". |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-17-ta-delete-app.png` |

---

### TC-18 — Context Menu Shows Correct Options

| Field | Content |
|-------|---------|
| **Test ID** | TC-18 |
| **Purpose** | Verify context menu displays correct options based on application status. |
| **Precondition** | Logged in as TA with applications in different statuses (PENDING and WITHDRAWN/ACCEPTED/REJECTED). |
| **Steps** | 1. Open `/applications`.<br>2. Right-click on a PENDING application row.<br>3. Observe menu options (Withdraw + Delete Record should be visible).<br>4. Right-click on a WITHDRAWN/ACCEPTED/REJECTED application row.<br>5. Observe menu options (Only Delete Record should be visible). |
| **Expected Result** | PENDING: Both "Withdraw" and "Delete Record" visible.<br>WITHDRAWN/ACCEPTED/REJECTED: Only "Delete Record" visible. |
| **Actual Result** | _To be filled during test run_ |
| **Status** | To be executed |
| **Screenshot Evidence** | `docs/screenshots/final/TC-18-context-menu.png` |

---

## 5. Execution Record (Summary)

| Test ID | Executed by |   Date    | Status | Notes |
|---------|-------------|-----------|--------|-------|
|  TC-01  |  Yang Gang  | 2026.5.17 | passed | |
|  TC-02  |  Yang Gang  | 2026.5.17 | failed | Candidate Summary Builder & Preferred role cannot be saved |
|  TC-03  |  Yang Gang  | 2026.5.17 | passed | |
|  TC-04  |  Yang Gang  | 2026.5.17 | failed | .pdf file cannot be truly uploaded and saved |
|  TC-05  |  Yang Gang  | 2026.5.17 | failed | .pdf file cannot be truly uploaded and saved |
|  TC-06  |  Yang Gang  | 2026.5.17 | failed | .pdf file cannot be truly uploaded and saved |
|  TC-07  |  Yang Gang  | 2026.5.17 | failed | profile should be finished before applying jobs |
| TC-08 | | | To be executed | Reset job deadline after test |
| TC-09 | | | To be executed | |
| TC-10 | | | To be executed | |
| TC-11 | | | To be executed | A: PDF viewer |
| TC-12 | | | To be executed | |
| TC-13 | | | To be executed | |
| TC-14 | | | To be executed | |
| TC-15 | | | To be executed | |
| TC-16 | | | To be executed | |
| TC-17 | | | To be executed | |
| TC-18 | | | To be executed | |

---

## 6. Known Dependencies & Notes

1. **TC-08** modifies `data/jobs.csv`; restore deadline to `2026-06-30` after testing to avoid affecting other cases.
2. **Application readiness:** TA must have `SUMMARY_COMPLETE` profile **or** uploaded PDF before apply (TC-07).
3. **Charts (TC-13):** Dashboard uses CSS-only charts; no external CDN — suitable for offline demo.
4. **Related docs:** `docs/final/testing_guide.md` (environment setup), `README.md` (feature list).
5. **TC-16/17/18 (New in Iteration 5):** Application withdraw and delete features require right-click context menu interaction.

---

## 7. Sign-off

| Role | Name | Signature | Date |
|------|------|-----------|------|
| Tester (B) | | | |
| Tester (A) | | | |
| Reviewer | | | |
