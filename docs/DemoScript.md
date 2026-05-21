# TA Recruitment System Demo Script

## 1. Demo Goal

This demo script is designed for an 8–10 minute video presentation.
It shows the complete recruitment workflow across Admin, MO, and TA roles, including normal actions, error handling, and application management (withdraw/delete).

---

## 2. Recommended Demo Order

1. Admin login and dashboard review
2. MO login and job publishing
3. TA login and profile completion
4. TA uploads PDF CV
5. TA browses jobs and applies
6. TA withdraws a pending application
7. TA deletes a withdrawn/accepted application
8. MO reviews applicant summary and CV, then accepts an applicant
9. Admin checks workload/statistics changes
10. Error handling demonstrations

---

## 3. Demo Preparation

### Suggested sample accounts

- Admin: `admin` / `123456`
- MO: `mo1` / `123456`
- TA: `seele` / `123456`

### Suggested sample files

- `Seele_U001_Test_CV.pdf`
- One invalid pseudo-PDF file renamed from `.txt` for error testing

### Suggested data condition

Before recording, ensure:

- at least one open job exists
- at least one TA account is available
- the TA profile page loads normally

---

## 4. Demo Script with Timing Guidance

### Segment 1 — Admin login and dashboard overview

**Estimated time: 1 minute**

1. Open the home page.
2. Select **Admin**.
3. Log in with `admin / 123456`.
4. Show the Admin dashboard.
5. Briefly point out summary counts and management entry points.

**Talking points**

- The system supports three roles.
- Admin has a global overview of recruitment activity.

**Suggested screenshot**

- Admin dashboard

---

### Segment 2 — MO publishes a job

**Estimated time: 1 minute**

1. Log out.
2. Log in as MO using `mo1 / 123456`.
3. Open the job creation page.
4. Fill in a sample job.
5. Publish the job.

**Talking points**

- MOs create recruitment posts.
- Jobs include module code, workload, year range, skills, deadline, and vacancies.

**Suggested screenshot**

- Job creation form
- Published job in MO dashboard or job list

---

### Segment 3 — TA completes profile and Candidate Summary

**Estimated time: 2 minutes**

1. Log out.
2. Log in as TA using `seele / 123456`.
3. Open **My Profile**.
4. Fill in or review profile fields.
5. Emphasize the structured summary fields.
6. Click **Save Profile & Summary**.
7. Open the Candidate Summary preview.

**Talking points**

- The TA profile generates a structured Candidate Summary.
- The summary is not the same as the original PDF CV.

**Suggested screenshot**

- Profile page with summary status badge
- Candidate Summary preview page

---

### Segment 4 — TA uploads PDF CV

**Estimated time: 1 minute**

1. Stay on **My Profile**.
2. Choose `Seele_U001_Test_CV.pdf`.
3. Click **Upload PDF CV**.
4. Show the success message.
5. Open the uploaded PDF using **Open PDF**.

**Talking points**

- The original PDF CV is stored separately from the summary.
- Recruiters can open the original PDF when needed.

**Suggested screenshot**

- Upload success state
- Open PDF view

---

### Segment 5 — TA browses jobs and applies

**Estimated time: 1 minute**

1. Open the Job Board.
2. Show the filter bar.
3. Filter by keyword or module code if needed.
4. Open a job card.
5. Click **Apply Now**.
6. Show the application success flow.

**Talking points**

- Jobs are ranked by match score.
- Filters help the TA find relevant opportunities.

**Suggested screenshot**

- Job Board with filters
- Job detail modal

---

### Segment 6 — TA manages applications (withdraw/delete)

**Estimated time: 1 minute**

1. Open **Applications** page.
2. Locate a PENDING application.
3. Right-click on the PENDING application row.
4. Show the context menu (Withdraw + Delete Record visible).
5. Click **Withdraw**.
6. Show the status changes to WITHDRAWN.
7. Right-click on the WITHDRAWN application.
8. Show the context menu (only Delete Record visible).
9. Click **Delete Record**.
10. Confirm deletion in the modal.

**Talking points**

- TA can withdraw pending applications.
- TA can delete any of their applications regardless of status.
- Context menu options change based on application status.

**Suggested screenshot**

- Context menu for PENDING status
- Context menu for WITHDRAWN/ACCEPTED/REJECTED status
- Application list after withdraw/delete

---

### Segment 7 — MO reviews the applicant and decides

**Estimated time: 1.5 minutes**

1. Log out.
2. Log in as MO.
3. Open the application list.
4. Open the applicant’s Candidate Summary.
5. Open the applicant’s original PDF CV.
6. Update the application status to **Accepted**.

**Talking points**

- MO reviews both the structured summary and the original PDF.
- The decision workflow is separate from the TA submission workflow.

**Suggested screenshot**

- Application list
- Candidate Summary page
- Original PDF CV view

---

### Segment 8 — Admin checks workload/statistics changes

**Estimated time: 1 minute**

1. Log out.
2. Log in as Admin.
3. Open the Admin dashboard again.
4. Point out any changed counts or workload indicators.

**Talking points**

- The dashboard reflects recruitment activity.
- Accepted applications and job workload can be tracked centrally.

**Suggested screenshot**

- Admin dashboard after workflow changes

---

### Segment 9 — Error handling demo

**Estimated time: 1 minute**

Demonstrate three quick error cases:

#### A. Invalid PDF upload
1. Try uploading a renamed `.txt` file.
2. Show the validation error.

#### B. Duplicate application
1. Attempt to apply to the same job again.
2. Show the duplicate application warning.

#### C. Expired job
1. Open or reference an expired job.
2. Show that the application is blocked.

**Talking points**

- The system validates file content, application duplication, and deadline rules.
- Error handling is user-friendly and avoids server crashes.

**Suggested screenshot**

- Validation error message
- Duplicate application message
- Deadline-related block message

---

## 5. Suggested Ending

Close the demo by summarizing:

- TA can manage profile, summary, PDF CV, applications, and withdraw/delete applications.
- MO can publish jobs and review applicants.
- Admin can manage and monitor the whole system.

---

## 6. Recording Tips

- Use a stable test dataset before recording.
- Keep the browser zoom consistent.
- Keep each action short and deliberate.
- Pause briefly after success/error messages so they are visible in the video.
- Prefer one clean example per role.

---

## 7. Optional Backup Plan

If a live demo step fails, switch to one of these backup actions:

- reload the page
- reopen the dashboard
- show the relevant CSV data file
- use a second prepared account

This helps keep the recording within the 8–10 minute target.
