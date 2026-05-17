<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "My Profile");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

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
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="ta-content">
            <section class="panel dashboard-intro">
                <div class="profile-hero">
                    <div>
                        <h1>Profile & Candidate Summary</h1>
                        <p>Complete your profile, build a structured candidate summary, and manage the original PDF CV that MO/Admin will review.</p>
                    </div>
                    <div class="profile-status-block">
                        <span class="summary-status-label">Summary Status</span>
                        <span class="summary-status-badge ${profileUser.summaryStatus}">
                            <c:choose>
                                <c:when test="${profileUser.summaryStatus == 'SUMMARY_COMPLETE'}">Summary Complete</c:when>
                                <c:when test="${profileUser.summaryStatus == 'BASIC_COMPLETE'}">Basic Complete</c:when>
                                <c:otherwise>Incomplete</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>
            </section>

            <section class="panel profile-form-panel<c:if test="${taProfileFieldRings}"> profile-field-rings</c:if>">
                <div class="profile-panel-head">
                    <div>
                        <h2>Candidate Summary Builder</h2>
                        <p>Summary and original PDF CV are now managed as two separate reviewer-facing artifacts.</p>
                    </div>
                    <div class="summary-preview-actions">
                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/files/cv-summary/${profileUser.userId}" target="_blank">Preview Candidate Summary</a>
                        <c:if test="${profileHasCv}">
                            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/files/cv/${profileUser.userId}" target="_blank">View Uploaded CV</a>
                        </c:if>
                    </div>
                </div>

                <form id="taProfileForm" action="${pageContext.request.contextPath}/ta/profile" method="post" class="grid-form profile-builder-form">
                    <section class="profile-section-card">
                        <div class="profile-section-header">
                            <h3>Basic Profile</h3>
                            <p>These fields support account identity, contact information, and matching logic.</p>
                        </div>
                        <div class="profile-section-grid">
                            <div>
                                <label>Username</label>
                                <input type="text" value="${profileUser.username}" disabled>
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
                                <input type="number" name="year" min="1" max="8" value="${profileUser.year}" placeholder="e.g. 2">
                            </div>

                            <div class="full-width">
                                <label>Major</label>
                                <input type="text" name="major" value="${profileUser.major}" placeholder="e.g. Computer Science">
                            </div>
                        </div>
                    </section>

                    <section class="profile-section-card">
                        <div class="profile-section-header">
                            <h3>Skills & Availability</h3>
                            <p>These fields help the system rank jobs and help MO compare applicants quickly.</p>
                        </div>
                        <div class="profile-section-grid">
                            <div>
                                <label>Availability</label>
                                <input type="text" name="availability" value="${profileUser.availability}" placeholder="e.g. Mon/Wed afternoons">
                            </div>

                            <div>
                                <label>Preferred Role</label>
                                <input type="text" name="preferredRole" value="${profileUser.preferredRole}" placeholder="e.g. Lab Support | Tutorial Support">
                            </div>

                            <div class="full-width">
                                <label>Skills</label>
                                <textarea name="skills" rows="4" placeholder="e.g. Java, SQL, Data Structures">${profileUser.skills}</textarea>
                                <p class="hint-text profile-inline-hint">Skills are normalized to `|` for CSV storage.</p>
                            </div>
                        </div>
                    </section>

                    <section class="profile-section-card">
                        <div class="profile-section-header">
                            <h3>Candidate Summary Builder</h3>
                            <p>Use these structured fields to generate a richer summary for reviewers. Line breaks are compacted for CSV compatibility.</p>
                        </div>
                        <div class="profile-section-grid">
                            <div class="full-width">
                                <label>Personal Statement</label>
                                <textarea name="personalStatement" rows="4" placeholder="Short self-introduction or motivation">${profileUser.personalStatement}</textarea>
                            </div>

                            <div class="full-width">
                                <label>Relevant Courses</label>
                                <textarea name="relevantCourses" rows="3" placeholder="Use commas, semicolons, or | to separate items">${profileUser.relevantCourses}</textarea>
                            </div>

                            <div class="full-width">
                                <label>Project / Teaching Experience</label>
                                <textarea name="projectExperience" rows="5" placeholder="One project or teaching experience per line">${profileUser.projectExperience}</textarea>
                            </div>
                        </div>
                    </section>

                    <div class="full-width form-actions profile-form-actions">
                        <button type="submit" class="btn btn-primary">Save Profile & Summary</button>
                        <a href="${pageContext.request.contextPath}/files/cv-summary/${profileUser.userId}" class="btn btn-secondary" target="_blank">Preview Summary</a>
                        <a href="${pageContext.request.contextPath}/ta/dashboard" class="btn btn-secondary" id="profileBackLink">Back</a>
                    </div>
                </form>

                <section class="profile-section-card profile-phase-note">
                    <div class="profile-section-header">
                        <h3>Original CV File</h3>
                        <p>Upload a PDF resume for MO/Admin review. This file is stored separately from your structured Candidate Summary.</p>
                    </div>
                    <div class="profile-cv-card ${profileHasCv ? 'has-file' : 'missing-file'}">
                        <div class="profile-cv-status-row">
                            <div>
                                <strong>CV Status</strong>
                                <span class="cv-status-badge ${profileHasCv ? 'uploaded' : 'missing'}">
                                    <c:choose>
                                        <c:when test="${profileHasCv}">Uploaded</c:when>
                                        <c:otherwise>Missing</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <c:if test="${profileHasCv}">
                                <a class="btn btn-secondary btn-small" href="${pageContext.request.contextPath}/files/cv/${profileUser.userId}" target="_blank">Open PDF</a>
                            </c:if>
                        </div>

                        <c:choose>
                            <c:when test="${profileHasCv}">
                                <div class="profile-cv-meta">
                                    <div><strong>Current file:</strong> ${profileUser.cvOriginalName}</div>
                                    <div><strong>Uploaded at:</strong> ${empty profileUser.cvUploadedAt ? 'Unknown' : profileUser.cvUploadedAt}</div>
                                    <div><strong>File type:</strong> ${empty profileUser.cvContentType ? 'application/pdf' : profileUser.cvContentType}</div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="profile-placeholder-box">
                                    <strong>No original CV uploaded yet</strong>
                                    <span>Upload one PDF file up to ${cvMaxSizeMb}MB. Your Candidate Summary remains available even before file upload.</span>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <form action="${pageContext.request.contextPath}/ta/profile/cv/upload" method="post" enctype="multipart/form-data" class="profile-cv-upload-form">
                            <div class="profile-cv-upload-row">
                                <input type="file" name="cvFile" accept="application/pdf,.pdf" required>
                                <button type="submit" class="btn btn-primary">${profileHasCv ? 'Replace PDF CV' : 'Upload PDF CV'}</button>
                            </div>
                            <p class="hint-text profile-inline-hint">Only PDF files are supported. Maximum size: ${cvMaxSizeMb}MB.</p>
                            <p class="hint-text profile-inline-hint">After upload, the file is stored as <code>${profileUser.userId}.pdf</code> in <code>data/cvs/</code>.</p>
                        </form>

                        <c:if test="${profileHasCv}">
                            <form action="${pageContext.request.contextPath}/ta/profile/cv/delete" method="post" class="profile-cv-delete-form" onsubmit="return confirm('Delete the currently uploaded PDF CV?');">
                                <button type="submit" class="btn btn-secondary">Delete Uploaded CV</button>
                            </form>
                        </c:if>
                    </div>
                </section>
            </section>
        </div>
    </main>
</div>

<div class="modal-overlay" id="unsavedProfileModal" aria-hidden="true">
    <div class="modal modal-small" role="dialog" aria-labelledby="unsavedProfileTitle" aria-modal="true">
        <div class="modal-header">
            <h3 id="unsavedProfileTitle">Warning</h3>
            <button type="button" class="modal-close" id="unsavedProfileModalClose" aria-label="Close">&times;</button>
        </div>
        <div class="modal-body">
            <p>The current modifications have not been saved. Are you sure to go back?</p>
            <div class="modal-actions modal-actions-split">
                <button type="button" class="btn btn-primary" id="unsavedProfileConfirm">Confirm</button>
                <button type="button" class="btn btn-secondary" id="unsavedProfileCancel">Cancel</button>
            </div>
        </div>
    </div>
</div>

<script>
(function () {
    var form = document.getElementById('taProfileForm');
    var backLink = document.getElementById('profileBackLink');
    var modal = document.getElementById('unsavedProfileModal');
    if (!form || !backLink || !modal) return;

    function snapshot() {
        function v(name) {
            var el = form.querySelector('[name="' + name + '"]');
            return el ? el.value : '';
        }
        return [
            v('name'),
            v('email'),
            v('year'),
            v('major'),
            v('availability'),
            v('preferredRole'),
            v('skills'),
            v('personalStatement'),
            v('relevantCourses'),
            v('projectExperience')
        ].join('\x1e');
    }

    var initial = snapshot();
    var isDirty = false;

    function refreshDirty() {
        isDirty = snapshot() !== initial;
    }

    form.addEventListener('input', refreshDirty);
    form.addEventListener('change', refreshDirty);

    function openModal() {
        modal.classList.add('active');
        modal.setAttribute('aria-hidden', 'false');
        document.body.style.overflow = 'hidden';
    }

    function closeModal() {
        modal.classList.remove('active');
        modal.setAttribute('aria-hidden', 'true');
        document.body.style.overflow = '';
    }

    backLink.addEventListener('click', function (e) {
        refreshDirty();
        if (isDirty) {
            e.preventDefault();
            openModal();
        }
    });

    document.getElementById('unsavedProfileConfirm').addEventListener('click', function () {
        window.location.href = backLink.getAttribute('href');
    });

    document.getElementById('unsavedProfileCancel').addEventListener('click', closeModal);
    document.getElementById('unsavedProfileModalClose').addEventListener('click', closeModal);

    modal.addEventListener('click', function (e) {
        if (e.target === modal) closeModal();
    });

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && modal.classList.contains('active')) closeModal();
    });
})();
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
