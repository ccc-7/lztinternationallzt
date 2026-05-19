<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Applications");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<style>
.document-action-group {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: wrap;
}

.document-action-group .btn {
    min-width: 112px;
    padding: 7px 12px;
}

.document-pill-muted {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 112px;
    padding: 7px 12px;
    border-radius: var(--radius-md);
    border: 1px solid #d5dde8;
    background: #f7f9fc;
    color: var(--text-tertiary);
    font-size: 0.8125rem;
    font-weight: 600;
    line-height: 1;
    cursor: pointer;
}
</style>

<div class="layout">
    <aside class="sidebar">
        <div class="sidebar-brand">
            <div class="brand-logo brand-mo">MO</div>
            <div>
                <h3>Module Organiser</h3>
                <p>Recruitment Suite</p>
            </div>
        </div>

        <nav class="sidebar-nav">
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/dashboard">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
                </span> Dashboard
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/jobs/new">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
                </span> Post Job
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/mo/applications">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                </span> Applications
            </a>
        </nav>
    </aside>

    <main class="content">
        <div class="topbar topbar-mo">
            <button type="button" class="sidebar-toggle">
                <svg viewBox="0 0 24 24"><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
            </button>
            <div class="topbar-title">Applications</div>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.username}</span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="mo-content">
            <section class="panel dashboard-intro">
                <h1>Applications</h1>
                <p>Review applications for the jobs you posted and update candidate decisions.</p>
            </section>

            <section class="panel">
            <div class="panel-header">
                <h2>Applicant List</h2>
                <c:if test="${not empty filterJobId}">
                    <a href="${pageContext.request.contextPath}/mo/applications" class="btn btn-secondary btn-small">
                        View All
                    </a>
                </c:if>
            </div>
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                    <tr>
                        <th>Application ID</th>
                        <th>Applicant</th>
                        <th>Job</th>
                        <th>Availability</th>
                        <th>Documents</th>
                        <th>Status</th>
                        <th>Submitted At</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty applications}">
                            <tr>
                                <td colspan="8" class="empty-state">No applications found</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="a" items="${applications}">
                                <tr>
                                    <td><span class="app-id">${a.applicationId}</span></td>
                                    <td>
                                        <div class="user-info">
                                            <span class="user-name">${applicantNames[a.userId]}</span>
                                            <span class="user-id">${a.userId}</span>
                                        </div>
                                    </td>
                                    <td><span class="module-code">${jobTitles[a.jobId]}</span></td>
                                    <td><span class="availability">${a.availability}</span></td>
                                    <td>
                                        <div class="document-action-group">
                                            <a href="${pageContext.request.contextPath}/files/cv-summary/${a.userId}" class="btn btn-outline btn-small" target="_blank">
                                                View Profile
                                            </a>
                                            <c:choose>
                                                <c:when test="${applicantHasCv[a.userId]}">
                                                    <a href="${pageContext.request.contextPath}/files/cv/${a.userId}" class="btn btn-outline btn-small" target="_blank">
                                                        View CV
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="button" class="document-pill-muted" onclick="showNoCvNotice('${applicantNames[a.userId]}')">View CV</button>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                    <td><span class="badge ${a.status}">${statusLabels[a.status]}</span></td>
                                    <td>${a.submittedAt}</td>
                                    <td>
                                        <div class="decision-buttons">
                                            <form action="${pageContext.request.contextPath}/mo/applications/update" method="post" class="decision-form">
                                                <input type="hidden" name="applicationId" value="${a.applicationId}">
                                                <input type="hidden" name="status" value="ACCEPTED">
                                                <input type="hidden" name="filterJobId" value="${filterJobId}">
                                                <button type="submit" class="btn btn-decision btn-accept" ${a.status == 'ACCEPTED' ? 'disabled' : ''}>Accept</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/mo/applications/update" method="post" class="decision-form">
                                                <input type="hidden" name="applicationId" value="${a.applicationId}">
                                                <input type="hidden" name="status" value="INTERVIEW">
                                                <input type="hidden" name="filterJobId" value="${filterJobId}">
                                                <button type="submit" class="btn btn-decision btn-interview" ${a.status == 'INTERVIEW' ? 'disabled' : ''}>Interview</button>
                                            </form>
                                            <button type="button" class="btn btn-decision btn-reject" onclick="showRejectModal('${a.applicationId}')" ${a.status == 'REJECTED' ? 'disabled' : ''}>Reject</button>
                                        </div>
                                    </td>
                                    </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>

            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="?page=${currentPage - 1}<c:if test='${not empty filterJobId}'>&jobId=${filterJobId}</c:if>" class="page-link">Previous</a>
                    </c:if>

                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <a href="?page=${i}<c:if test='${not empty filterJobId}'>&jobId=${filterJobId}</c:if>"
                           class="page-link ${i == currentPage ? 'active' : ''}">${i}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <a href="?page=${currentPage + 1}<c:if test='${not empty filterJobId}'>&jobId=${filterJobId}</c:if>" class="page-link">Next</a>
                    </c:if>
                </div>
            </c:if>
        </section>
        </div>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

<div class="modal-overlay" id="rejectModal">
    <div class="modal">
        <div class="modal-header">
            <h3>Reject Application</h3>
            <button class="modal-close" onclick="closeRejectModal()">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/mo/applications/update" method="post" class="modal-form">
            <input type="hidden" name="applicationId" id="rejectApplicationId" value="">
            <input type="hidden" name="status" value="REJECTED">
            <input type="hidden" name="filterJobId" value="${filterJobId}">
            <div class="form-group">
                <label>Rejection Reason <span class="required">*</span></label>
                <textarea name="rejectReason" id="rejectReason" rows="4" required placeholder="Please enter the reason for rejection..."></textarea>
            </div>
            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" onclick="closeRejectModal()">Cancel</button>
                <button type="submit" class="btn btn-danger">Confirm Rejection</button>
            </div>
        </form>
    </div>
</div>

<script>
function showNoCvNotice(displayName) {
    alert((displayName || 'This user') + ' has not uploaded a CV yet.');
}

function showRejectModal(applicationId) {
    document.getElementById('rejectApplicationId').value = applicationId;
    document.getElementById('rejectModal').classList.add('active');
}

function closeRejectModal() {
    document.getElementById('rejectModal').classList.remove('active');
    document.getElementById('rejectApplicationId').value = '';
    document.getElementById('rejectReason').value = '';
}

// Close modal on overlay click
document.getElementById('rejectModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeRejectModal();
    }
});
</script>

