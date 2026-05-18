```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "My Profile");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<style>
html {
    overflow-y: scroll !important;
}
.profile-columns {
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-auto-rows: 1fr;
    gap: 20px;
    align-items: stretch;
}

.profile-left-column {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.profile-left-column > .panel {
    margin-bottom: 0;
}

.basic-profile-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
}

.skills-availability-grid {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.profile-right-column > .panel {
    height: 100%;
    box-sizing: border-box;
}

.summary-builder-grid {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    height: calc(100% - 80px);
}

.summary-builder-grid .form-group:nth-child(1),
.summary-builder-grid .form-group:nth-child(3) {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.summary-builder-grid .form-group:nth-child(2) {
    flex: 0 0 auto;
}

.summary-builder-grid textarea {
    flex-grow: 1;
}

input[type="text"],
input[type="email"],
input[type="number"],
textarea {
    width: 100%;
    box-sizing: border-box;
}
</style>

<div class="layout layout-ta">
    <aside class="sidebar sidebar-ta" id="sidebar">
        <div class="sidebar-brand">
            <div class="brand-logo brand-ta">TA</div>
            <div>
                <h3>TA Portal</h3>
                <p>Recruitment System</p>
            </div>
        </div>

        <nav class="sidebar-nav">
            <a class="nav-item" href="${pageContext.request.contextPath}/ta/dashboard">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
                </span> Dashboard
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/jobs">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><line x1="12" y1="12" x2="12" y2="12"/></svg>
                </span> Job Board
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/applications">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                </span> Applications
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/ta/profile">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                </span> My Profile
            </a>
        </nav>
    </aside>

    <main class="content content-ta">
        <div class="topbar topbar-ta">
            <button type="button" class="sidebar-toggle">
                <svg viewBox="0 0 24 24"><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
            </button>
            <div class="topbar-title">My Profile</div>
            <div class="topbar-right">
                <a class="user-name" href="${pageContext.request.contextPath}/ta/profile">${sessionScope.currentUser.username}</a>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="ta-content">
            <section class="panel">
                <div class="page-header profile-header">
                    <h1>Candidate Summary Builder</h1>
                    <div class="header-right">
                        <span class="summary-status status-${user.summaryStatus == 'COMPLETE' ? 'complete' : 'incomplete'}">
                            SUMMARY STATUS: ${user.summaryStatus}
                        </span>
                        <button type="button" class="btn btn-secondary" onclick="previewSummary()">Preview Candidate Summary</button>
                    </div>
                </div>
                <p class="form-help">Summary and original PDF CV are now managed as two separate reviewer-facing artifacts.</p>
            </section>

            <form class="profile-form" action="${pageContext.request.contextPath}/ta/profile" method="post" enctype="multipart/form-data">
                <div class="profile-columns">
                    <div class="profile-left-column">
                        <section class="panel">
                            <h2>Basic Profile</h2>
                            <p class="form-help">These fields support account identity, contact information, and matching logic.</p>
                            <div class="basic-profile-grid">
                                <div class="form-group">
                                    <label>Username</label>
                                    <input type="text" name="username" value="${user.username}" readonly>
                                </div>
                                <div class="form-group">
                                    <label>Full Name</label>
                                    <input type="text" name="name" value="${user.name}" placeholder="Your full name">
                                </div>
                                <div class="form-group">
                                    <label>Email</label>
                                    <input type="email" name="email" value="${user.email}" placeholder="you@example.com">
                                </div>
                                <div class="form-group">
                                    <label>Year</label>
                                    <input type="number" name="year" value="${user.year}" min="1" max="10" placeholder="0">
                                </div>
                                <div class="form-group">
                                    <label>Major</label>
                                    <input type="text" name="major" value="${user.major}" placeholder="e.g. Computer Science">
                                </div>
                            </div>
                        </section>

                        <section class="panel">
                            <h2>Skills & Availability</h2>
                            <p class="form-help">These fields help the system rank jobs and help MO compare applicants quickly.</p>
                            <div class="skills-availability-grid">
                                <div class="form-group">
                                    <label>Availability</label>
                                    <input type="text" name="availability" value="${user.availability}" placeholder="e.g. Mon/Wed afternoons">
                                </div>
                                <div class="form-group">
                                    <label>Preferred Role</label>
                                    <input type="text" name="preferredRole" value="${user.preferredRole}" placeholder="e.g. Lab Support | Tutorial Support">
                                </div>
                                <div class="form-group">
                                    <label>Skills</label>
                                    <textarea name="skills" rows="4" placeholder="e.g. Java, SQL, Data Structures">${user.skills}</textarea>
                                    <p class="form-note">Skills are normalized to '|' for CSV storage.</p>
                                </div>
                            </div>
                        </section>
                    </div>

                    <div class="profile-right-column">
                        <section class="panel">
                            <h2>Candidate Summary Builder</h2>
                            <p class="form-help">Use these structured fields to generate a richer summary for reviewers. Line breaks are compacted for CSV compatibility.</p>
                            <div class="summary-builder-grid">
                                <div class="form-group">
                                    <label>Personal Statement</label>
                                    <textarea name="personalStatement" placeholder="Short self-introduction or motivation">${user.personalStatement}</textarea>
                                </div>
                                <div class="form-group">
                                    <label>Relevant Courses</label>
                                    <input type="text" name="relevantCourses" value="${user.relevantCourses}" placeholder="Use commas, semicolons, or | to separate items">
                                </div>
                                <div class="form-group">
                                    <label>Project / Teaching Experience</label>
                                    <textarea name="projectExperience" placeholder="One project or teaching experience per line">${user.projectExperience}</textarea>
                                </div>
                            </div>
                        </section>
                    </div>
                </div>

                <section class="panel">
                    <h2>CV Upload</h2>
                    <c:choose>
                        <c:when test="${empty user.cvStoredName}">
                            <p class="form-help">Upload a PDF version of your CV. File must be smaller than 2MB.</p>
                            <div class="form-group file-upload-group">
                                <label for="cvFile" class="file-label">Choose File</label>
                                <span id="cvFileNameDisplay">No file chosen</span>
                                <input type="file" id="cvFile" name="cvFile" accept="application/pdf" onchange="updateFileNameDisplay(this)">
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="cv-status">
                                <p class="cv-status-text">CV Uploaded: <span class="cv-filename">${user.cvOriginalName}</span></p>
                                <div class="cv-actions">
                                    <a href="${pageContext.request.contextPath}/ta/cv/download" class="btn btn-secondary btn-sm">View / Download</a>
                                    <button type="submit" name="action" value="deleteCv" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete your CV?');">Delete CV</button>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </section>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Save Changes</button>
                </div>
            </form>
        </div>
    </main>
</div>

<script>
function updateFileNameDisplay(input) {
    const display = document.getElementById('cvFileNameDisplay');
    display.textContent = input.files[0]?.name || 'No file chosen';
}

function previewSummary() {
    const url = '${pageContext.request.contextPath}/ta/profile/preview';
    const form = document.querySelector('.profile-form');
    const formData = new FormData(form);

    fetch(url, {
        method: 'POST',
        body: formData
    }).then(response => {
        if (response.ok) {
            window.open(url, '_blank');
        } else {
            alert('Failed to preview summary. Please save your changes first.');
        }
    });
}
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
```