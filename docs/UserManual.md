# TA Recruitment System User Manual

## 1. Overview

The TA Recruitment System is a web application for managing teaching assistant recruitment.
It supports three roles:

- TA Applicant
- Module Organiser (MO)
- Admin

This manual explains how each role uses the system in the current released version.

---

## 2. Before You Start

### Required environment

- A web browser
- A running TA Recruitment System deployment
- A valid account for the role you want to use

### Demo accounts

- TA: `seele` / `123456`
- MO: `mo1` / `123456`
- Admin: `admin` / `123456`

### Key pages

- Home / Login: `/home`
- TA Dashboard: `/ta/dashboard`
- TA Profile: `/ta/profile`
- Job Board: `/jobs`
- TA Applications: `/applications`
- MO Dashboard: `/mo/dashboard`
- Admin Dashboard: `/admin/dashboard`

---

## 3. TA Applicant Guide

### 3.1 Log in as a TA

1. Open the home page.
2. Select the TA role.
3. Enter your username and password.
4. Click **Sign In**.
5. You will be redirected to the TA dashboard.

### 3.2 Complete or update your profile

1. Open **My Profile**.
2. Fill in your basic profile fields:
   - Full Name
   - Email
   - Year
   - Major
3. Fill in your skills and availability.
4. Complete the Candidate Summary fields:
   - Personal Statement
   - Relevant Courses
   - Project / Teaching Experience
   - Preferred Role
5. Click **Save Profile & Summary**.

### 3.3 What the profile page means

The profile page contains two separate parts:

- **Candidate Summary**
  - structured information for recruiter review
- **Original PDF CV**
  - the uploaded PDF file stored separately on disk

These two parts are intentionally separate.

### 3.4 Upload your PDF CV

1. In **My Profile**, go to the **Original PDF CV** section.
2. Click **Choose File**.
3. Select a PDF file.
4. Click **Upload PDF CV** or **Replace PDF CV**.
5. If successful, the page shows a success message.

### 3.5 View your uploaded PDF CV

After upload, click **Open PDF** in the CV section.
You can also open it directly from the link:

- `/files/cv/U001` for the demo TA account `seele`

The number depends on your user ID.

### 3.6 Delete your uploaded PDF CV

1. In **My Profile**, find the CV section.
2. Click **Delete Uploaded CV**.
3. Confirm the deletion.
4. The system removes the stored file and clears the CV metadata.

### 3.7 Browse and filter jobs

1. Open the **Job Board**.
2. Use the filter bar to search by:
   - keyword
   - module code
   - minimum match score
3. Click **Apply Filters**.
4. Click **Clear** to reset the filters.

### 3.8 Apply for a job

1. Open the **Job Board**.
2. Click a job card to view details.
3. Click **Apply Now**.
4. If eligible, the application is submitted successfully.

### 3.9 Check your application history

1. Open **Applications**.
2. Review the current application status list.
3. Monitor whether the application is `PENDING`, `INTERVIEW`, `ACCEPTED`, or `REJECTED`.

---

## 4. Module Organiser Guide

### 4.1 Log in as an MO

1. Open the home page.
2. Select the MO role.
3. Enter your credentials.
4. Click **Sign In**.
5. You will be redirected to the MO dashboard.

### 4.2 Publish a job

1. Open the MO job creation page.
2. Fill in the job details:
   - title
   - module code
   - organiser
   - minimum year
   - maximum year
   - hours
   - required skills
   - deadline
   - vacancies
3. Click **Publish**.

### 4.3 Review applications

1. Open **Applications** from the MO dashboard.
2. Select a job or application record.
3. Review the applicant information.
4. Open the Candidate Summary for structured profile details.
5. Open the original PDF CV if available.

### 4.4 Update application status

1. Open the application details.
2. Set the status to one of:
   - Accepted
   - Interview
   - Rejected
3. Save the change.

---

## 5. Admin Guide

### 5.1 Log in as Admin

1. Open the home page.
2. Select the Admin role.
3. Enter your credentials.
4. Click **Sign In**.
5. You will be redirected to the Admin dashboard.

### 5.2 View system overview

The dashboard shows a high-level summary of:

- users
- jobs
- applications
- logs
- recruitment workload indicators

### 5.3 Manage data

From the Admin pages, you can:

- view user accounts
- manage jobs
- manage applications
- inspect system logs
- open TA Candidate Summary pages
- open uploaded PDF CV files

---

## 6. UI Notes for Screenshots

If you are preparing documentation or a video, capture the following screens:

### TA screenshots

- TA login page
- TA dashboard
- My Profile showing:
  - Candidate Summary section
  - Original PDF CV section
  - CV status badge
- Upload success message
- Open PDF view
- Job Board with filters
- Application list page

### MO screenshots

- MO dashboard
- Job creation form
- Application review page
- Candidate Summary view
- Original PDF CV view

### Admin screenshots

- Admin dashboard
- User management page
- Job management page
- Application management page
- System logs page

---

## 7. Common Rules and Validation

- Upload only PDF files.
- The original PDF CV is stored separately from the Candidate Summary.
- A TA can apply only if the account is application-ready.
- Duplicate applications to the same job are blocked.
- Active applications are limited.
- Closed or expired jobs cannot receive new applications.

---

## 8. Troubleshooting

### Upload failed

Check that:

- the file is a real PDF
- the file is smaller than 5MB
- the file is not corrupted

### PDF does not open

Check that:

- the CV has actually been uploaded
- the browser is opening `/files/cv/{userId}`
- the stored file exists under `data/cvs/`

### Application failed

Check whether:

- the job is still open
- the deadline has not passed
- your profile is complete enough
- you already applied to that job

---

## 9. Final Notes

The system intentionally keeps the **Candidate Summary** and the **Original PDF CV** as two separate review artifacts.
This is the current expected behavior and should be preserved in future updates.
