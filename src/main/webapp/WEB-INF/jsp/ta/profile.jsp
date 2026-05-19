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

.profile-summary-panel {
    margin-bottom: 20px;
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
    justify-content: center;
    flex-direction: column;
    gap: 12px;
    flex-wrap: wrap;
    min-width: 280px;
    margin-right: 28px;
}

.profile-summary-actions {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    flex-wrap: wrap;
}

.profile-summary-meta {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    flex-wrap: wrap;
}

.profile-format-hint {
    padding: 14px 16px;
    border: 1px dashed #c7d7ea;
    border-radius: var(--radius-md);
    background: #f8fbff;
    color: var(--text-secondary);
    line-height: 1.65;
}

.profile-format-hint strong {
    display: block;
    margin-bottom: 6px;
    color: var(--text-primary);
}

.profile-format-hint code {
    background: #edf4ff;
    border: 1px solid #d6e4fb;
    border-radius: 6px;
    padding: 2px 6px;
    font-size: 0.82rem;
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

.profile-role-select-shell {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.role-picker {
    position: relative;
}

.profile-role-select-helper {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    flex-wrap: wrap;
    font-size: 0.8125rem;
    color: var(--text-tertiary);
}

.role-picker-trigger {
    width: 100%;
    min-height: 44px;
    padding: 10px 14px;
    border: 1px solid var(--border-color);
    border-radius: var(--radius-md);
    background: var(--white);
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    cursor: pointer;
    text-align: left;
    transition: border-color var(--transition-fast), box-shadow var(--transition-fast);
}

.role-picker-trigger:focus,
.role-picker.open .role-picker-trigger {
    border-color: var(--enterprise-blue);
    box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.15);
    outline: none;
}

.role-picker-trigger-text {
    color: var(--text-placeholder);
}

.role-picker-trigger.has-value .role-picker-trigger-text {
    color: var(--text-primary);
}

.role-picker-arrow {
    color: var(--text-tertiary);
    transition: transform 0.2s ease;
}

.role-picker.open .role-picker-arrow {
    transform: rotate(180deg);
}

.role-picker-dropdown {
    display: none;
    position: absolute;
    top: calc(100% + 8px);
    left: 0;
    right: 0;
    z-index: 15;
    max-height: 240px;
    overflow-y: auto;
    background: var(--white);
    border: 1px solid var(--border-color);
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-elevated);
    padding: 8px;
}

.role-picker.open .role-picker-dropdown {
    display: block;
}

.role-picker-option {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px 12px;
    border-radius: 8px;
    cursor: pointer;
}

.role-picker-option:hover {
    background: var(--gray-100);
}

.role-picker-option input[type="checkbox"] {
    width: 16px;
    height: 16px;
    margin: 0;
    accent-color: var(--enterprise-blue);
    flex-shrink: 0;
}

.role-picker-option-text {
    color: var(--text-primary);
    font-size: 0.875rem;
}

.role-picker-empty {
    padding: 12px;
    color: var(--text-tertiary);
    font-size: 0.875rem;
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
            <section class="panel profile-summary-panel">
                <div class="profile-builder-head">
                    <div>
                        <h1>Candidate Summary Builder</h1>
                        <p class="form-help">Summary and original PDF CV are managed separately. Save your profile fields here, then use preview or PDF actions independently.</p>
                    </div>
                    <div class="profile-summary-status">
                        <div class="profile-summary-meta">
                            <span class="summary-status-label">Summary Status</span>
                            <span class="summary-status-badge ${profileUser.summaryStatus}">
                                <c:choose>
                                    <c:when test="${profileUser.summaryStatus == 'SUMMARY_COMPLETE'}">Summary Complete</c:when>
                                    <c:when test="${profileUser.summaryStatus == 'BASIC_COMPLETE'}">Basic Complete</c:when>
                                    <c:otherwise>Incomplete</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="profile-summary-actions">
                            <a class="btn btn-secondary btn-small" href="${pageContext.request.contextPath}/files/cv-summary/${profileUser.userId}" target="_blank">Preview Candidate Summary</a>
                            <c:if test="${profileHasCv}">
                                <a class="btn btn-secondary btn-small" href="${pageContext.request.contextPath}/files/cv/${profileUser.userId}" target="_blank">Preview Uploaded CV</a>
                            </c:if>
                        </div>
                    </div>
                </div>
            </section>

            <section class="panel profile-form-panel<c:if test="${taProfileFieldRings}"> profile-field-rings</c:if>">
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
                                        <div class="profile-role-select-shell">
                                            <div class="role-picker" id="profilePreferredRolePicker" data-selected="${profileUser.preferredRole}">
                                                <button type="button" class="role-picker-trigger" aria-expanded="false">
                                                    <span class="role-picker-trigger-text">Select up to 3 preferred roles</span>
                                                    <span class="role-picker-arrow">▼</span>
                                                </button>
                                                <div class="role-picker-dropdown">
                                                    <c:choose>
                                                        <c:when test="${empty preferredRoleOptions}">
                                                            <div class="role-picker-empty">No positions are available yet.</div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:forEach var="roleOption" items="${preferredRoleOptions}">
                                                                <label class="role-picker-option">
                                                                    <input type="checkbox" name="preferredRoleSelection" value="${roleOption}">
                                                                    <span class="role-picker-option-text">${roleOption}</span>
                                                                </label>
                                                            </c:forEach>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                            <div class="profile-role-select-helper">
                                                <span>Select up to 3 roles from the current job list.</span>
                                                <span id="profilePreferredRoleCount">0 / 3 selected</span>
                                            </div>
                                        </div>
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
                                <p class="form-help">Use these structured fields to generate a richer summary for reviewers. Please follow the suggested format so the final summary stays tidy and recruiter-friendly.</p>
                                <div class="profile-format-hint">
                                    <strong>Suggested format</strong>
                                    <span>`Personal Statement`: 1-2 short sentences about your background, strengths, and TA motivation.</span><br>
                                    <span>`Relevant Courses`: separate items with <code>|</code> or commas, for example `Data Structures | Embedded Systems | Signals and Systems`.</span><br>
                                    <span>`Project / Teaching Experience`: one experience per line, starting with an action and a result.</span>
                                </div>
                                <div class="profile-card-grid">
                                    <div class="full-width">
                                        <label>Personal Statement</label>
                                        <textarea name="personalStatement" rows="6" placeholder="Example: Final-year IoT student interested in teaching support, embedded systems, and peer mentoring.">${profileUser.personalStatement}</textarea>
                                    </div>
                                    <div class="full-width">
                                        <label>Relevant Courses</label>
                                        <input type="text" name="relevantCourses" value="${profileUser.relevantCourses}" placeholder="Example: Data Structures | Embedded Systems | Signals and Systems">
                                    </div>
                                    <div class="full-width">
                                        <label>Project / Teaching Experience</label>
                                        <textarea name="projectExperience" rows="8" placeholder="One experience per line&#10;Example: Built a Java Servlet/JSP TA recruitment system&#10;Supported peer debugging for programming coursework&#10;Designed STM32-based monitoring demos">${profileUser.projectExperience}</textarea>
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
                        <a href="${pageContext.request.contextPath}/ta/dashboard" class="btn btn-secondary" id="profileBackLink">Back</a>
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

<script>
function setupRolePicker(shellId, counterId, maxAllowed) {
    var shell = document.getElementById(shellId);
    if (!shell) return;

    var trigger = shell.querySelector('.role-picker-trigger');
    var triggerText = shell.querySelector('.role-picker-trigger-text');
    var checkboxes = Array.from(shell.querySelectorAll('input[type="checkbox"][name="preferredRoleSelection"]'));
    var preset = (shell.getAttribute('data-selected') || '').split('|').map(function(item) { return item.trim(); }).filter(Boolean);

    checkboxes.forEach(function(box) {
        box.checked = preset.includes(box.value);
    });

    function selectedValues() {
        return checkboxes.filter(function(box) { return box.checked; }).map(function(box) { return box.value; });
    }

    function updateCounter() {
        var values = selectedValues();
        var counter = document.getElementById(counterId);
        if (counter) {
            counter.textContent = values.length + ' / ' + maxAllowed + ' selected';
        }
        if (values.length === 0) {
            trigger.classList.remove('has-value');
            triggerText.textContent = 'Select up to 3 preferred roles';
        } else {
            trigger.classList.add('has-value');
            triggerText.textContent = values.join(', ');
        }
    }

    trigger.addEventListener('click', function(e) {
        e.preventDefault();
        shell.classList.toggle('open');
        trigger.setAttribute('aria-expanded', shell.classList.contains('open') ? 'true' : 'false');
    });

    document.addEventListener('click', function(e) {
        if (!shell.contains(e.target)) {
            shell.classList.remove('open');
            trigger.setAttribute('aria-expanded', 'false');
        }
    });

    checkboxes.forEach(function(box) {
        box.addEventListener('change', function() {
            if (selectedValues().length > maxAllowed) {
                box.checked = false;
                alert('You can select up to ' + maxAllowed + ' preferred roles.');
                return;
            }
            updateCounter();
        });
    });

    updateCounter();
}

setupRolePicker('profilePreferredRolePicker', 'profilePreferredRoleCount', 3);

(function() {
    var profileForm = document.getElementById('taProfileForm');
    if (!profileForm) return;

    var skipPrompt = false;
    var initialSnapshot = snapshotProfileForm();

    function snapshotProfileForm() {
        var data = [];
        Array.from(profileForm.elements).forEach(function(field) {
            if (!field.name || field.disabled) return;
            if (field.type === 'checkbox') {
                data.push(field.name + '=' + (field.checked ? '1' : '0') + ':' + field.value);
                return;
            }
            if (field.type === 'radio') {
                if (field.checked) {
                    data.push(field.name + '=' + field.value);
                }
                return;
            }
            data.push(field.name + '=' + field.value);
        });
        return data.join('||');
    }

    function hasUnsavedChanges() {
        return snapshotProfileForm() !== initialSnapshot;
    }

    function confirmLeaveIfDirty(message) {
        if (!hasUnsavedChanges()) {
            return true;
        }
        return window.confirm(message || 'You have unsaved profile changes. Leave this page without saving?');
    }

    profileForm.addEventListener('submit', function() {
        skipPrompt = true;
    });

    profileForm.addEventListener('input', function() {
        skipPrompt = false;
    });

    profileForm.addEventListener('change', function() {
        skipPrompt = false;
    });

    window.addEventListener('beforeunload', function(event) {
        if (skipPrompt || !hasUnsavedChanges()) {
            return;
        }
        event.preventDefault();
        event.returnValue = '';
    });

    document.querySelectorAll('.sidebar-nav a, .topbar-right a, #profileBackLink').forEach(function(link) {
        link.addEventListener('click', function(event) {
            if (!confirmLeaveIfDirty()) {
                event.preventDefault();
            }
        });
    });

    document.querySelectorAll('.profile-cv-upload-form, .profile-cv-card form[action$="/ta/profile/cv/delete"]').forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!confirmLeaveIfDirty('You have unsaved profile changes. Continue without saving them first?')) {
                event.preventDefault();
                return;
            }
            skipPrompt = true;
        });
    });
})();
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
