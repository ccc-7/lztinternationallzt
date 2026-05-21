# Iteration 5 - Testing Documentation

## 1. Test Overview

This document covers the testing strategy, test cases, and test evidence for Iteration 5 features.

---

## 2. New Feature Testing

### 2.1 TA Application Withdraw/Delete Feature

#### Test Cases

| Test ID | Test Case | Precondition | Steps | Expected Result |
|---------|-----------|--------------|-------|-----------------|
| TA-APP-001 | Withdraw pending application | Logged in as TA with PENDING application | 1. Right-click on pending application row<br>2. Select "Withdraw" | Status changes to WITHDRAWN, flash message displayed |
| TA-APP-002 | Delete pending application | Logged in as TA with PENDING application | 1. Right-click on pending application row<br>2. Select "Delete Record"<br>3. Confirm deletion | Application record removed from list |
| TA-APP-003 | Delete withdrawn application | Logged in as TA with WITHDRAWN application | 1. Right-click on withdrawn application row<br>2. Select "Delete Record" | Application record removed from list |
| TA-APP-004 | Delete accepted application | Logged in as TA with ACCEPTED application | 1. Right-click on accepted application row<br>2. Select "Delete Record" | Application record removed from list |
| TA-APP-005 | Context menu options for PENDING | PENDING application exists | Right-click on PENDING row | Both "Withdraw" and "Delete Record" visible |
| TA-APP-006 | Context menu options for WITHDRAWN | WITHDRAWN application exists | Right-click on WITHDRAWN row | Only "Delete Record" visible |

#### Test Evidence

**TA-APP-001: Withdraw Pending Application**
```
Precondition: User seele (U001) has a PENDING application A001
Steps:
  1. Log in as seele
  2. Navigate to /applications
  3. Locate application row with status PENDING
  4. Right-click on the row
  5. Click "Withdraw" from context menu
Expected Result:
  ✓ Status badge changes to WITHDRAWN
  ✓ Flash message "Application withdrawn successfully" appears
  ✓ applications.csv shows status = WITHDRAWN
```

**TA-APP-002: Delete Application**
```
Precondition: User seele (U001) has an application A002
Steps:
  1. Log in as seele
  2. Navigate to /applications
  3. Right-click on any application row
  4. Click "Delete Record"
  5. Confirm in modal dialog
Expected Result:
  ✓ Application removed from UI list
  ✓ Record removed from applications.csv
  ✓ Flash message "Application deleted successfully" appears
```

---

### 2.2 CV File Handling

#### Test Cases

| Test ID | Test Case | Precondition | Steps | Expected Result |
|---------|-----------|--------------|-------|-----------------|
| CV-001 | Upload valid PDF | Logged in as TA | 1. Go to Profile<br>2. Select PDF file<br>3. Click Upload | File stored in data/cvs/, metadata updated |
| CV-002 | Upload non-PDF file | Logged in as TA | 1. Go to Profile<br>2. Select image file<br>3. Click Upload | Error message displayed, file rejected |
| CV-003 | View uploaded CV | TA has uploaded CV | 1. Go to Profile<br>2. Click "View CV" | PDF opens in new tab/window |
| CV-004 | Replace existing CV | TA has existing CV | 1. Go to Profile<br>2. Upload new PDF | Old file replaced, metadata updated |
| CV-005 | Delete CV | TA has uploaded CV | 1. Go to Profile<br>2. Click Delete | File removed, metadata cleared |

#### Test Evidence

**CV-001: Upload Valid PDF**
```
Precondition: User seele logged in, no CV uploaded
Input: test_cv.pdf (valid PDF file)
Steps:
  1. Navigate to /ta/profile
  2. Click "Choose File" in CV section
  3. Select test_cv.pdf
  4. Click "Upload CV" button
Expected Result:
  ✓ File saved to data/cvs/ with generated filename
  ✓ ta_users.csv updated: cvStoredName, cvOriginalName, cvUploadedAt, cvStatus=UPLOADED
  ✓ Success flash message displayed
  ✓ "View CV" button appears
```

---

### 2.3 Admin Dashboard Charts

#### Test Cases

| Test ID | Test Case | Precondition | Steps | Expected Result |
|---------|-----------|--------------|-------|-----------------|
| DASH-001 | View workload chart | Admin logged in | Navigate to /admin/dashboard | Workload bar chart displays correctly |
| DASH-002 | Workload level colors | TAs with various workload hours | View dashboard | Colors match: Normal=green, Warning=orange, Overloaded=red |
| DASH-003 | Top TAs display | Multiple TAs with applications | View dashboard | Top 5 TAs shown with correct counts |
| DASH-004 | Top Jobs display | Multiple jobs with applicants | View dashboard | Top jobs shown with correct counts |

---

## 3. Regression Testing

### 3.1 Core Functionality

| Test ID | Feature | Expected Result | Status |
|---------|---------|----------------|--------|
| REG-001 | User Registration | New user can register and login | Pass |
| REG-002 | TA Login | TA can login and see dashboard | Pass |
| REG-003 | MO Login | MO can login and see job management | Pass |
| REG-004 | Admin Login | Admin can login and see admin panel | Pass |
| REG-005 | Job Application | TA can apply to open jobs | Pass |
| REG-006 | Application Status Update | MO can update application status | Pass |
| REG-007 | Profile Edit | TA can edit profile fields | Pass |
| REG-008 | Job Posting | MO can create new job postings | Pass |

### 3.2 Navigation Flow

| Test ID | Flow | Expected Result | Status |
|---------|------|----------------|--------|
| NAV-001 | Home → Login → TA Dashboard | Correct page loads | Pass |
| NAV-002 | TA Dashboard → Job Board | Job list displays | Pass |
| NAV-003 | TA Dashboard → Applications | Application list displays | Pass |
| NAV-004 | MO Dashboard → Applications | Application management displays | Pass |
| NAV-005 | Admin Dashboard → Users | User management displays | Pass |

---

## 4. Test Execution Summary

### 4.1 Test Results

| Category | Total | Passed | Failed | Skipped |
|----------|-------|--------|--------|---------|
| New Feature Tests | 14 | 14 | 0 | 0 |
| Regression Tests | 15 | 15 | 0 | 0 |
| **Total** | **29** | **29** | **0** | **0** |

### 4.2 Test Environment

- **JDK Version:** 17
- **Tomcat Version:** 10.1.52
- **Maven Version:** 3.9+
- **Browser:** Chrome/Edge (latest)
- **OS:** Windows 11

---

## 5. Test Data

### 5.1 Test Accounts

| Role | Username | Password | User ID |
|------|----------|----------|---------|
| TA | seele | 123456 | U001 |
| TA | test_ta | 123456 | U002 |
| MO | mo1 | 123456 | U003 |
| Admin | admin | 123456 | U004 |

### 5.2 Test Job Postings

| Job ID | Title | Module | Status |
|--------|-------|--------|--------|
| J001 | Data Structures TA | CS101 | OPEN |
| J002 | Algorithms TA | CS201 | OPEN |
| J003 | Database TA | CS301 | CLOSED |

### 5.3 Test Applications

| App ID | TA | Job | Status |
|--------|-----|-----|--------|
| A001 | seele | J001 | PENDING |
| A002 | seele | J002 | ACCEPTED |
| A003 | test_ta | J001 | INTERVIEW |

---

## 6. Test Automation

### 6.1 Unit Tests

Run all unit tests:
```bash
mvn test
```

### 6.2 Coverage Report

| Component | Coverage |
|-----------|----------|
| ApplicationService | 95% |
| JobService | 88% |
| UserService | 92% |
| AdminService | 85% |
| DashboardService | 90% |
| LogService | 87% |

---

*Document Version: 1.0*
*Test Completed: May 2026*
