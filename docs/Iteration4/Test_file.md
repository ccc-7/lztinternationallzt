# Iteration4 Test Report

## 1.Overview Summary

After detailed functional testing and evaluation, we have summarized the overall situation of Iteration 4, including the completion status of implemented features, as well as existing aesthetic and functional issues that require optimization.

Overall, this version has basically fulfilled all planned functionalities, yet there still remain several problems affecting user experience.

Therefore, this test report is compiled to sort out all pending improvement items, define their priority levels, and provide feasible revision suggestions.

## 2.System Test Defect Report

### 2.1 Aesthetic & UI Issues

| No. | Severity | Module | Defect Description | Status | Suggestion |
|-----|----------|--------|--------------------|--------|------------|
| 1 | Low | Global Header | After user login, the username in the upper right corner is too close to the "Log out" button without obvious visual separation. | Fixed √ | Adjust spacing and add visual dividing line between username and logout button. |
| 2 | Low | TA Dashboard | The blue border of TA Dashboard is overly bright and may cause eye strain. | Modified | Lower the brightness and saturation of the border color. |
| 3 | Low | Admin Dashboard | The gray border of Admin Dashboard provides poor visual experience and easily creates an illusion of system error. | Modified | Optimize border color tone to a mild neutral gray. |
| 4 | Medium | TA – My Profile | On the My Profile page, the outer frame only appears when an item is selected; no default border display. | Pending | Display permanent outer border for each item to improve recognizability. |
| 5 | Medium | TA – Job Board | "View Detail" has no visual prompt (underline / bold). The whole card area is clickable to enter details. | Pending | Remove the "View Detail" text to simplify operation flow. |
| 6 | Low | Admin Dashboard – Quick Actions | The red dot badge still appears on "Review Applications" when the quantity is zero. | Pending | Hide the red dot prompt when there are no pending applications. |

---

### 2.2 Functional & Experience Issues

| No. | Severity | Module | Defect Description | Status | Suggestion |
|-----|----------|--------|--------------------|--------|------------|
| 1 | Medium | User Registration | The pop-up reminder for "not agreeing to the Privacy Policy" is not in English. | Pending | Unify the prompt text into English version. |
| 2 | High | TA – Edit Profile | No pop-up reminder for unsaved changes when clicking Back directly after editing profile information. | Pending | Add a confirmation popup: Changes have not been saved. Are you sure to leave? |
| 3 | Medium | User Information Display | Inconsistent name display: Dashboard shows account username; Job Board and Applications show user’s real name. | Pending | Unify the displayed name logic across all pages. |
| 4 | High | Login Page | Username and password are still retained after logout, causing potential security risks. | Pending | Automatically clear account and password input content after logging out. |
| 5 | Medium | MO – Post Job | The placeholder prompt of "Application Deadline" is not switched to English. | Pending | Replace the placeholder text with standard English version. |