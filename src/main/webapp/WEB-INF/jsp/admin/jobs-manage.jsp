<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Job Management");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<div class="layout">
    <aside class="sidebar sidebar-admin">
        <div class="sidebar-brand">
            <div class="brand-logo brand-admin">AD</div>
            <div>
                <h3>System Admin</h3>
                <p>Recruitment Suite</p>
            </div>
        </div>

        <nav class="sidebar-nav">
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/dashboard">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
                </span> Dashboard
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/applications">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                </span> Applications
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/admin/jobs">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><line x1="12" y1="12" x2="12" y2="12"/></svg>
                </span> Jobs
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/users">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                </span> Users
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/logs">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><line x1="10" y1="9" x2="8" y2="9"/></svg>
                </span> System Logs
            </a>
        </nav>
    </aside>

    <main class="content">
        <div class="topbar topbar-admin">
            <button class="sidebar-toggle">
                <svg viewBox="0 0 24 24"><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
            </button>
            <div class="topbar-title">Job Management</div>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.username}</span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="admin-content">
            <section class="panel dashboard-intro">
                <div class="intro-header">
                    <div>
                        <h1>Job Management</h1>
                        <p>Manage all TA positions in the system. Add, edit, enable/disable positions.</p>
                    </div>
                    <button class="btn btn-primary" onclick="showJobModal()">
                        <span class="btn-icon">+</span> Add Job
                    </button>
                </div>
            </section>

            <section class="panel">
                <div class="filter-bar">
                    <form method="get" action="${pageContext.request.contextPath}/admin/jobs" class="filter-form">
                        <select name="status" class="filter-select">
                            <option value="ALL">All Status</option>
                            <option value="OPEN" ${currentStatus == 'OPEN' ? 'selected' : ''}>Open</option>
                            <option value="CLOSED" ${currentStatus == 'CLOSED' ? 'selected' : ''}>Closed</option>
                        </select>

                        <input type="text" name="search" placeholder="Search job title/code..." value="${currentSearch}" class="filter-input">

                        <button type="submit" class="btn btn-primary btn-small">Filter</button>
                        <a href="${pageContext.request.contextPath}/admin/jobs" class="btn btn-secondary btn-small">Reset</a>
                    </form>
                </div>

                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                        <tr>
                            <th>Job ID</th>
                            <th>Job Title</th>
                            <th>Module Code</th>
                            <th>Vacancies</th>
                            <th>Applications</th>
                            <th>Deadline</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty jobs}">
                                <tr>
                                    <td colspan="8" class="empty-state">
                                        <div class="empty-content">
                                            <span class="empty-icon"><svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg></span>
                                            <p>No job data available</p>
                                            <button class="btn btn-primary btn-small" onclick="showJobModal()">Create First Job</button>
                                        </div>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="job" items="${jobs}">
                                    <tr data-job-id="${job.jobId}">
                                        <td><span class="job-id">${job.jobId}</span></td>
                                        <td>${job.title}</td>
                                        <td><span class="module-code">${job.moduleCode}</span></td>
                                        <td>${job.vacancies}</td>
                                        <td><span class="applicant-count">${applicantCounts[job.jobId]}</span></td>
                                        <td>${job.deadline}</td>
                                        <td>
                                            <span class="badge ${job.status}">${job.status == 'OPEN' ? 'Open' : 'Closed'}</span>
                                        </td>
                                        <td>
                                            <div class="action-buttons">
                                                <button class="btn btn-action btn-edit" onclick="editJob('${job.jobId}')" title="Edit">
                                                    <span class="btn-icon-svg">&#9998;</span>
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/jobs/toggle" method="post" class="inline-form">
                                                    <input type="hidden" name="action" value="toggle">
                                                    <input type="hidden" name="jobId" value="${job.jobId}">
                                                    <button type="submit" class="btn btn-action ${job.status == 'OPEN' ? 'btn-warning' : 'btn-success'}" title="${job.status == 'OPEN' ? 'Disable' : 'Enable'}">
                                                        ${job.status == 'OPEN' ? '&#10007;' : '&#10003;'}
                                                    </button>
                                                </form>
                                                <button class="btn btn-action btn-danger" onclick="confirmDeleteJob('${job.jobId}')" title="Delete">
                                                    <span class="btn-icon-svg">&#128465;</span>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>

<div class="modal-overlay" id="jobModal">
    <div class="modal modal-large">
        <div class="modal-header">
            <h3 id="jobModalTitle">Add Job</h3>
            <button class="modal-close" onclick="closeJobModal()">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/jobs" method="post" id="jobForm" class="modal-form">
            <input type="hidden" name="action" id="jobFormAction" value="create">
            <input type="hidden" name="jobId" id="jobId" value="">

            <div class="form-row">
                <div class="form-group">
                    <label>Job Title <span class="required">*</span></label>
                    <input type="text" name="title" id="jobTitle" required placeholder="e.g. Software Engineering TA">
                </div>
                <div class="form-group">
                    <label>Module Code <span class="required">*</span></label>
                    <input type="text" name="moduleCode" id="jobModuleCode" required placeholder="e.g. EBU6304">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Organiser/Instructor</label>
                    <input type="text" name="organiser" id="jobOrganiser" placeholder="e.g. Dr. Wang">
                </div>
                <div class="form-group">
                    <label>Total Hours</label>
                    <input type="number" name="hours" id="jobHours" min="1" value="20">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Minimum Year</label>
                    <input type="number" name="minYear" id="jobMinYear" min="1" max="8" value="1">
                </div>
                <div class="form-group">
                    <label>Maximum Year</label>
                    <input type="number" name="maxYear" id="jobMaxYear" min="1" max="8" value="4">
                </div>
                <div class="form-group">
                    <label>Vacancies <span class="required">*</span></label>
                    <input type="number" name="vacancies" id="jobVacancies" min="1" required value="1">
                </div>
                <div class="form-group">
                    <label>Deadline</label>
                    <input type="date" name="deadline" id="jobDeadline">
                </div>
            </div>

            <div class="form-group">
                <label>Required Skills</label>
                <input type="text" name="requiredSkills" id="jobSkills" placeholder="e.g. Java, Python, Teamwork">
            </div>

            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" onclick="closeJobModal()">Cancel</button>
                <button type="submit" class="btn btn-primary" id="jobSubmitBtn">Create Job</button>
            </div>
        </form>
    </div>
</div>

<div class="modal-overlay" id="confirmModal">
    <div class="modal modal-small">
        <div class="modal-header">
            <h3>Confirm Delete</h3>
            <button class="modal-close" onclick="closeConfirmModal()">&times;</button>
        </div>
        <div class="modal-body">
            <p>Are you sure you want to delete this job? This will also clean up associated application records and cannot be recovered.</p>
        </div>
        <div class="modal-actions">
            <button type="button" class="btn btn-secondary" onclick="closeConfirmModal()">Cancel</button>
            <form action="${pageContext.request.contextPath}/admin/jobs/delete" method="post" id="deleteForm" class="inline-form">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="jobId" id="deleteJobId" value="">
                <button type="submit" class="btn btn-danger">Confirm Delete</button>
            </form>
        </div>
    </div>
</div>

<div class="toast-container" id="toastContainer"></div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

<script>
function jsStr(s) {
    if (s == null) return '';
    return String(s)
        .replace(/\\/g, '\\\\')
        .replace(/\n/g, '\\n')
        .replace(/'/g, "\\'");
}

var jobsData = {
<c:forEach var="job" items="${jobs}" varStatus="status">
    '${job.jobId}': { title: jsStr('${job.title}'), moduleCode: jsStr('${job.moduleCode}'), organiser: jsStr('${job.organiser}'),
        hours: ${job.hours}, minYear: ${job.minYear}, maxYear: ${job.maxYear}, vacancies: ${job.vacancies},
        deadline: jsStr('${job.deadline}'), requiredSkills: jsStr('${job.requiredSkills}')}${status.last ? '' : ','}
</c:forEach>
};

function showJobModal() {
    document.getElementById('jobModalTitle').textContent = 'Add Job';
    document.getElementById('jobFormAction').value = 'create';
    document.getElementById('jobId').value = '';
    document.getElementById('jobForm').reset();
    document.getElementById('jobSubmitBtn').textContent = 'Create Job';
    document.getElementById('jobModal').classList.add('active');
}

function editJob(jobId) {
    var job = jobsData[jobId];
    if (!job) return;

    document.getElementById('jobModalTitle').textContent = 'Edit Job';
    document.getElementById('jobFormAction').value = 'update';
    document.getElementById('jobId').value = jobId;
    document.getElementById('jobTitle').value = job.title || '';
    document.getElementById('jobModuleCode').value = job.moduleCode || '';
    document.getElementById('jobOrganiser').value = job.organiser || '';
    document.getElementById('jobHours').value = job.hours || 20;
    document.getElementById('jobMinYear').value = job.minYear || 1;
    document.getElementById('jobMaxYear').value = job.maxYear || 4;
    document.getElementById('jobVacancies').value = job.vacancies || 1;
    document.getElementById('jobDeadline').value = job.deadline || '';
    document.getElementById('jobSkills').value = job.requiredSkills || '';
    document.getElementById('jobSubmitBtn').textContent = 'Save Changes';
    document.getElementById('jobModal').classList.add('active');
}

function closeJobModal() {
    document.getElementById('jobModal').classList.remove('active');
}

function confirmDeleteJob(jobId) {
    document.getElementById('deleteJobId').value = jobId;
    document.getElementById('confirmModal').classList.add('active');
}

function closeConfirmModal() {
    document.getElementById('confirmModal').classList.remove('active');
}

function showToast(message, type) {
    var container = document.getElementById('toastContainer');
    var toast = document.createElement('div');
    toast.className = 'toast toast-' + (type || 'info');
    toast.innerHTML = '<span class="toast-icon">' + (type === 'success' ? '&#10003;' : type === 'error' ? '&#10007;' : '&#9432;') + '</span><span class="toast-message">' + message + '</span>';
    container.appendChild(toast);
    setTimeout(function() { toast.classList.add('show'); }, 10);
    setTimeout(function() {
        toast.classList.remove('show');
        setTimeout(function() { container.removeChild(toast); }, 300);
    }, 3000);
}

document.addEventListener('DOMContentLoaded', function() {
    var flashSuccess = '${sessionScope.flashSuccess}';
    var flashError = '${sessionScope.flashError}';
    if (flashSuccess) showToast(flashSuccess, 'success');
    if (flashError) showToast(flashError, 'error');
});
</script>
