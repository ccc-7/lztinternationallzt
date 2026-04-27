<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Application Management");
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
                <span class="nav-icon">&#9632;</span> Dashboard
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/admin/applications">
                <span class="nav-icon">&#9733;</span> Applications
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/jobs">
                <span class="nav-icon">&#9651;</span> Jobs
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/users">
                <span class="nav-icon">&#9679;</span> Users
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/logs">
                <span class="nav-icon">&#9633;</span> System Logs
            </a>
        </nav>
    </aside>

    <main class="content">
        <div class="topbar topbar-admin">
            <button class="sidebar-toggle">&#9776;</button>
            <div class="topbar-title">Application Management</div>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.username}</span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="admin-content">
            <section class="panel dashboard-intro">
                <h1>TA Application Management</h1>
                <p>Review, approve, or reject all TA application records.</p>
            </section>

            <section class="panel">
                <div class="panel-header">
                    <h2>Application List</h2>
                </div>

                <div class="filter-bar">
                    <form method="get" action="${pageContext.request.contextPath}/admin/applications" class="filter-form">
                        <select name="status" class="filter-select">
                            <option value="ALL">All Status</option>
                            <option value="PENDING" ${currentStatus == 'PENDING' ? 'selected' : ''}>Pending</option>
                            <option value="ACCEPTED" ${currentStatus == 'ACCEPTED' ? 'selected' : ''}>Accepted</option>
                            <option value="REJECTED" ${currentStatus == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                            <option value="INTERVIEW" ${currentStatus == 'INTERVIEW' ? 'selected' : ''}>Interview</option>
                        </select>

                        <select name="jobId" class="filter-select">
                            <option value="">All Jobs</option>
                            <c:forEach var="job" items="${jobs}">
                                <option value="${job.jobId}" ${currentJob == job.jobId ? 'selected' : ''}>${job.title} (${job.jobId})</option>
                            </c:forEach>
                        </select>

                        <input type="text" name="search" placeholder="Search application ID/username..." value="${currentSearch}" class="filter-input">

                        <button type="submit" class="btn btn-primary btn-small">Filter</button>
                        <a href="${pageContext.request.contextPath}/admin/applications" class="btn btn-secondary btn-small">Reset</a>
                    </form>
                </div>

                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                        <tr>
                            <th>Application ID</th>
                            <th>Applicant</th>
                            <th>Job</th>
                            <th>Status</th>
                            <th>Submitted At</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty applications}">
                                <tr>
                                    <td colspan="6" class="empty-state">
                                        <div class="empty-content">
                                            <span class="empty-icon">&#9734;</span>
                                            <p>No applications found</p>
                                            <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary btn-small">Browse Jobs</a>
                                        </div>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="a" items="${applications}">
                                    <tr data-application-id="${a.applicationId}">
                                        <td><span class="app-id">${a.applicationId}</span></td>
                                        <td>
                                            <div class="user-info">
                                                <span class="user-name">${applicantNames[a.userId]}</span>
                                                <span class="user-id">${a.userId}</span>
                                            </div>
                                        </td>
                                        <td><span class="module-code">${jobTitles[a.jobId]}</span></td>
                                        <td><span class="badge ${a.status}">${a.status == 'PENDING' ? 'Pending' : a.status == 'ACCEPTED' ? 'Accepted' : a.status == 'REJECTED' ? 'Rejected' : 'Interview'}</span></td>
                                        <td>${a.submittedAt}</td>
                                        <td>
                                            <div class="action-buttons">
                                                <c:if test="${a.status == 'PENDING'}">
                                                    <form action="${pageContext.request.contextPath}/admin/applications/approve" method="post" class="inline-form">
                                                        <input type="hidden" name="applicationId" value="${a.applicationId}">
                                                        <button type="submit" class="btn btn-action btn-approve" onclick="return confirmAction(this, 'Approve this application?')">
                                                            <span class="btn-icon-svg">&#10003;</span> Approve
                                                        </button>
                                                    </form>
                                                    <button type="button" class="btn btn-action btn-reject" onclick="showRejectModal('${a.applicationId}')">
                                                        <span class="btn-icon-svg">&#10007;</span> Reject
                                                    </button>
                                                </c:if>
                                                <c:if test="${a.status != 'PENDING'}">
                                                    <span class="action-completed">Processed</span>
                                                </c:if>
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

<div class="modal-overlay" id="rejectModal">
    <div class="modal">
        <div class="modal-header">
            <h3>Reject Application</h3>
            <button class="modal-close" onclick="closeRejectModal()">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/applications/reject" method="post" class="modal-form">
            <input type="hidden" name="applicationId" id="rejectApplicationId" value="">
            <div class="form-group">
                <label>Rejection Reason <span class="required">*</span></label>
                <textarea name="rejectReason" id="rejectReason" rows="4" required placeholder="Please enter the rejection reason..."></textarea>
            </div>
            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" onclick="closeRejectModal()">Cancel</button>
                <button type="submit" class="btn btn-danger">Confirm Rejection</button>
            </div>
        </form>
    </div>
</div>

<div class="modal-overlay" id="confirmModal">
    <div class="modal modal-small">
        <div class="modal-header">
            <h3>Confirm Action</h3>
            <button class="modal-close" onclick="closeConfirmModal()">&times;</button>
        </div>
        <div class="modal-body">
            <p id="confirmMessage">Are you sure you want to perform this action?</p>
        </div>
        <div class="modal-actions">
            <button type="button" class="btn btn-secondary" onclick="closeConfirmModal()">Cancel</button>
            <button type="button" class="btn btn-primary" id="confirmOkBtn">OK</button>
        </div>
    </div>
</div>

<div class="toast-container" id="toastContainer"></div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

<script>
function showRejectModal(applicationId) {
    document.getElementById('rejectApplicationId').value = applicationId;
    document.getElementById('rejectModal').classList.add('active');
}

function closeRejectModal() {
    document.getElementById('rejectModal').classList.remove('active');
    document.getElementById('rejectApplicationId').value = '';
    document.getElementById('rejectReason').value = '';
}

function confirmAction(btn, message) {
    return confirm(message);
}

function showToast(message, type) {
    var container = document.getElementById('toastContainer');
    var toast = document.createElement('div');
    toast.className = 'toast toast-' + (type || 'info');
    toast.innerHTML = '<span class="toast-icon">' + (type === 'success' ? '&#10003;' : type === 'error' ? '&#10007;' : '&#9432;') + '</span><span class="toast-message">' + message + '</span>';
    container.appendChild(toast);
    setTimeout(function() {
        toast.classList.add('show');
    }, 10);
    setTimeout(function() {
        toast.classList.remove('show');
        setTimeout(function() {
            container.removeChild(toast);
        }, 300);
    }, 3000);
}

document.addEventListener('DOMContentLoaded', function() {
    var flashSuccess = '${sessionScope.flashSuccess}';
    var flashError = '${sessionScope.flashError}';
    if (flashSuccess) {
        showToast(flashSuccess, 'success');
    }
    if (flashError) {
        showToast(flashError, 'error');
    }
});
</script>
