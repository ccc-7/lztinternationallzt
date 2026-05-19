<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "My Profile");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<style>
.profile-page-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    align-items: start;
}

.profile-column {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.profile-card-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
}

.profile-card-grid .full-width {
    grid-column: 1 / -1;
}

.profile-form-panel .panel {
    margin-bottom: 0;
}

.profile-builder-head {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 16px;
    flex-wrap: wrap;
}

.profile-builder-head h1 {
    margin-bottom: 8px;
}

.profile-summary-status {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;
}

.profile-cv-card {
    border: 1px solid var(--border-color);
    border-radius: var(--radius-lg);
    background: linear-gradient(180deg, #fcfdff 0%, #f8fbff 100%);
    padding: var(--space-lg);
    display: flex;
    flex-direction: column;
    gap: var(--space-md);
}

.profile-cv-status-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 12px;
    flex-wrap: wrap;
}

.profile-cv-meta {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 12px;
    color: var(--text-secondary);
}

.profile-cv-upload-row {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;
}

.profile-cv-upload-row input[type="file"] {
    flex: 1 1 240px;
}

.profile-inline-actions {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
}

@media (max-width: 960px) {
    .profile-page-grid {
        grid-template-columns: 1fr;
    }

    .profile-cv-meta,
    .profile-card-grid {
        grid-template-columns: 1fr;
    }
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
                <span>${sessionScope.currentUser.username}</span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="ta-content">
            <section class="panel profile-form-panel<c:if test="${taProfileFieldRings}"> profile-field-rings</c:if>">
                <div class="profile-builder-head">
                    <div>
                        <h1>Candidate Summary Builder</h1>
                        <p class="form-help">Summary and original PDF CV are managed separately. Save your profile fields here, then use preview or PDF actions independently.</p>
                    </div>
                    <div class="profile-summary-status">
                        <span class="summary-status-label">Summary Status</span>
                        <span class="summary-status-badge ${profileUser.summaryStatus}">
                            <c:choose>
                                <c:when test="${profileUser.summaryStatus == 'SUMMARY_COMPLETE'}">Summary Complete</c:when>
                                <c:when test="${profileUser.summaryStatus == 'BASIC_COMPLETE'}">Basic Complete</c:when>
                                <c:otherwise>Incomplete</c:otherwise>
                            </c:choose>
                        </span>
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/files/cv-summary/${profileUser.userId}" target="_blank">Preview Candidate Summary</a>
                        <c:if test="${profileHasCv}">
                            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/files/cv/${profileUser.userId}" target="_blank">Preview Uploaded CV</a>
                        </c:if>
                    </div>
                </div>

                <form id="taProfileForm" class="grid-form" action="${pageContext.request.contextPath}/ta/profile" method="post">
                    <div class="profile-page-grid">
                        <div class="profile-column">
                            <section class="panel">
                                <h2>Basic Profile</h2>
                                <p class="form-help">These fields support account identity, contact information, and matching logic.</p>
                                <div class="profile-card-grid">
                                    <div>
                                        <label>Username</label>
                                        <input type="text" value="${profileUser.username}" readonly>
                                    </div>
                                    <div>
                                        <label>Full Name</label>
                                        <input type="text" name="name" value="${profileUser.name}" placeholder="Your full name">
                                    </div>
                                    <div>
                                        <label>Email</label>
                                        <input type="email" name="email" value="${profileUser.email}" placeholder="you@example.com">
                                    </div>
                                    <div>
                                        <label>Year</label>
                                        <input type="number" name="year" min="1" max="8" value="${profileUser.year}" placeholder="e.g. 3">
                                    </div>
                                    <div class="full-width">
                                        <label>Major</label>
                                        <input type="text" name="major" value="${profileUser.major}" placeholder="e.g. Computer Science">
                                    </div>
                                </div>
                            </section>

                            <section class="panel">
                                <h2>Skills & Availability</h2>
                                <p class="form-help">These fields help the system rank jobs and help MO compare applicants quickly.</p>
                                <div class="profile-card-grid">
                                    <div class="full-width">
                                        <label>Availability</label>
                                        <input type="text" name="availability" value="${profileUser.availability}" placeholder="e.g. Mon/Wed afternoons">
                                    </div>
                                    <div class="full-width">
                                        <label>Preferred Role</label>
                                        <input type="text" name="preferredRole" value="${profileUser.preferredRole}" placeholder="e.g. Lab Support | Tutorial Support">
                                    </div>
                                    <div class="full-width">
                                        <label>Skills</label>
                                        <textarea name="skills" rows="4" placeholder="e.g. Java, SQL, Data Structures">${profileUser.skills}</textarea>
                                        <p class="hint-text">Skills are normalized to `|` for CSV storage.</p>
                                    </div>
                                </div>
                            </section>
                        </div>

                        <div class="profile-column">
                            <section class="panel">
                                <h2>Candidate Summary Builder</h2>
                                <p class="form-help">Use these structured fields to generate a richer summary for reviewers. Line breaks are compacted for CSV compatibility.</p>
                                <div class="profile-card-grid">
                                    <div class="full-width">
                                        <label>Personal Statement</label>
                                        <textarea name="personalStatement" rows="6" placeholder="Short self-introduction or motivation">${profileUser.personalStatement}</textarea>
                                    </div>
                                    <div class="full-width">
                                        <label>Relevant Courses</label>
                                        <input type="text" name="relevantCourses" value="${profileUser.relevantCourses}" placeholder="Use commas, semicolons, or | to separate items">
                                    </div>
                                    <div class="full-width">
                                        <label>Project / Teaching Experience</label>
                                        <textarea name="projectExperience" rows="8" placeholder="One project or teaching experience per line">${profileUser.projectExperience}</textarea>
                                    </div>
                                </div>
                            </section>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Save Profile & Summary</button>
                        <a href="${pageContext.request.contextPath}/files/cv-summary/${profileUser.userId}" class="btn btn-secondary" target="_blank">Preview Summary</a>
                        <c:if test="${profileHasCv}">
                            <a href="${pageContext.request.contextPath}/files/cv/${profileUser.userId}" class="btn btn-secondary" target="_blank">Preview CV</a>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/ta/dashboard" class="btn btn-secondary">Back</a>
                    </div>
                </form>

                <section class="panel">
                    <h2>CV Upload</h2>
                    <p class="form-help">Upload a PDF version of your CV. Maximum size: ${cvMaxSizeMb}MB.</p>

                    <div class="profile-cv-card">
                        <div class="profile-cv-status-row">
                            <div>
                                <strong>CV Status</strong>
                                <span class="summary-status-badge ${profileHasCv ? 'SUMMARY_COMPLETE' : 'INCOMPLETE'}">
                                    <c:choose>
                                        <c:when test="${profileHasCv}">Uploaded</c:when>
                                        <c:otherwise>Missing</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <div class="profile-inline-actions">
                                <c:if test="${profileHasCv}">
                                    <a href="${pageContext.request.contextPath}/files/cv/${profileUser.userId}" class="btn btn-secondary btn-small" target="_blank">Preview CV</a>
                                    <a href="${pageContext.request.contextPath}/files/cv-summary/${profileUser.userId}" class="btn btn-secondary btn-small" target="_blank">Preview Profile</a>
                                </c:if>
                            </div>
                        </div>

                        <c:choose>
                            <c:when test="${profileHasCv}">
                                <div class="profile-cv-meta">
                                    <div><strong>File:</strong> ${profileUser.cvOriginalName}</div>
                                    <div><strong>Uploaded:</strong> ${profileUser.cvUploadedAt}</div>
                                    <div><strong>Type:</strong> ${profileUser.cvContentType}</div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="profile-placeholder-box">
                                    <strong>No PDF uploaded yet.</strong>
                                    <span>You can still save and preview your structured summary before uploading a PDF CV.</span>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <form action="${pageContext.request.contextPath}/ta/profile/cv/upload" method="post" enctype="multipart/form-data" class="profile-cv-upload-form">
                            <div class="profile-cv-upload-row">
                                <input type="file" name="cvFile" accept="application/pdf,.pdf" required>
                                <button type="submit" class="btn btn-primary">${profileHasCv ? 'Replace PDF CV' : 'Upload PDF CV'}</button>
                            </div>
                        </form>

                        <c:if test="${profileHasCv}">
                            <form action="${pageContext.request.contextPath}/ta/profile/cv/delete" method="post" onsubmit="return confirm('Delete the currently uploaded PDF CV?');">
                                <button type="submit" class="btn btn-secondary">Delete Uploaded CV</button>
                            </form>
                        </c:if>
                    </div>
                </section>
            </section>
        </div>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
