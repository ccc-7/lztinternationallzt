<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "System Logs");
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
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/jobs">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><line x1="12" y1="12" x2="12" y2="12"/></svg>
                </span> Jobs
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/users">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                </span> Users
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/admin/logs">
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
            <div class="topbar-title">System Logs</div>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.username}</span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="admin-content">
            <section class="panel dashboard-intro">
                <div class="intro-header">
                    <div>
                        <h1>System Logs</h1>
                        <p>View all system operation logs. Critical operation logs are permanently retained and cannot be bulk deleted.</p>
                    </div>
                    <button class="btn btn-secondary" onclick="exportLogs()">
                        <span class="btn-icon">&#8595;</span> Export Excel
                    </button>
                </div>
            </section>

            <section class="panel">
                <div class="filter-bar">
                    <form method="get" action="${pageContext.request.contextPath}/admin/logs" class="filter-form">
                        <select name="operator" class="filter-select">
                            <option value="">All Operators</option>
                            <c:forEach var="admin" items="${admins}">
                                <option value="${admin.userId}" ${currentOperator == admin.userId ? 'selected' : ''}>${admin.displayName} (${admin.userId})</option>
                            </c:forEach>
                        </select>

                        <select name="type" class="filter-select">
                            <option value="ALL">All Types</option>
                            <option value="LOGIN" ${currentType == 'LOGIN' ? 'selected' : ''}>Login</option>
                            <option value="LOGOUT" ${currentType == 'LOGOUT' ? 'selected' : ''}>Logout</option>
                            <option value="CREATE" ${currentType == 'CREATE' ? 'selected' : ''}>Create</option>
                            <option value="UPDATE" ${currentType == 'UPDATE' ? 'selected' : ''}>Update</option>
                            <option value="DELETE" ${currentType == 'DELETE' ? 'selected' : ''}>Delete</option>
                            <option value="APPROVE" ${currentType == 'APPROVE' ? 'selected' : ''}>Approve</option>
                            <option value="REJECT" ${currentType == 'REJECT' ? 'selected' : ''}>Reject</option>
                            <option value="ENABLE" ${currentType == 'ENABLE' ? 'selected' : ''}>Enable</option>
                            <option value="DISABLE" ${currentType == 'DISABLE' ? 'selected' : ''}>Disable</option>
                            <option value="EXPORT" ${currentType == 'EXPORT' ? 'selected' : ''}>Export</option>
                        </select>

                        <input type="text" name="search" placeholder="Search log content..." value="${currentSearch}" class="filter-input">

                        <button type="submit" class="btn btn-primary btn-small">Filter</button>
                        <a href="${pageContext.request.contextPath}/admin/logs" class="btn btn-secondary btn-small">Reset</a>
                    </form>
                </div>

                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                        <tr>
                            <th>Log ID</th>
                            <th>Time</th>
                            <th>Operator</th>
                            <th>Type</th>
                            <th>Target Type</th>
                            <th>Target ID</th>
                            <th>Details</th>
                            <th>IP Address</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty logs}">
                                <tr>
                                    <td colspan="8" class="empty-state">
                                        <div class="empty-content">
                                            <span class="empty-icon"><svg viewBox="0 0 24 24" width="48" height="48" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg></span>
                                            <p>No log records found</p>
                                        </div>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="log" items="${logs}">
                                    <tr>
                                        <td><span class="log-id">${log.logId}</span></td>
                                        <td>${log.createdAtText}</td>
                                        <td>
                                            <div class="user-info">
                                                <span class="user-name">${log.operatorName}</span>
                                                <span class="user-id">${log.operatorId}</span>
                                            </div>
                                        </td>
                                        <td><span class="badge ${log.operationType}">${log.operationTypeLabel}</span></td>
                                        <td>${log.targetType}</td>
                                        <td><code>${log.targetId}</code></td>
                                        <td class="log-details">${log.details}</td>
                                        <td><code class="ip-address">${log.ipAddress}</code></td>
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
                            <a href="?page=${currentPage - 1}&operator=${currentOperator}&type=${currentType}&search=${currentSearch}" class="page-link">&laquo; Previous</a>
                        </c:if>

                        <span class="page-info">Page ${currentPage} / ${totalPages}</span>

                        <c:if test="${currentPage < totalPages}">
                            <a href="?page=${currentPage + 1}&operator=${currentOperator}&type=${currentType}&search=${currentSearch}" class="page-link">Next &raquo;</a>
                        </c:if>
                    </div>
                </c:if>
            </section>
        </div>
    </main>
</div>

<div class="toast-container" id="toastContainer"></div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

<script>
function exportLogs() {
    window.location.href = '${pageContext.request.contextPath}/admin/logs/export';
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
