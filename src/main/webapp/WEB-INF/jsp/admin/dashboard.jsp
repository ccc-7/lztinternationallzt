<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Admin Dashboard");
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
            <a class="nav-item active" href="${pageContext.request.contextPath}/admin/dashboard">
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
            <div class="topbar-title">Admin Dashboard</div>
            <div class="topbar-right">
                <div class="user-menu">
                    <span class="user-name">${sessionScope.currentUser.username}</span>
                    <div class="user-dropdown">
                        <a href="#" class="dropdown-item" onclick="showPasswordModal()">Change Password</a>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="admin-content">
            <section class="panel dashboard-intro">
                <h1>Data Statistics Overview</h1>
                <p>View the overall system status and statistics.</p>
            </section>

            <section class="stats-grid stats-admin">
                <div class="stat-card stat-card-primary">
                    <div class="stat-icon">TA</div>
                    <div class="stat-content">
                        <h4>Total TA Accounts</h4>
                        <div class="stat-value">${stats.totalTA}</div>
                        <p class="stat-sub">Active: ${stats.activeTA}</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">MO</div>
                    <div class="stat-content">
                        <h4>Total MO Accounts</h4>
                        <div class="stat-value">${stats.totalMO}</div>
                    </div>
                </div>
                <div class="stat-card stat-card-success">
                    <div class="stat-icon">&#10003;</div>
                    <div class="stat-content">
                        <h4>Total Applications</h4>
                        <div class="stat-value">${stats.totalApplications}</div>
                        <p class="stat-sub">Pending: ${stats.pendingApplications}</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon"><svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg></div>
                    <div class="stat-content">
                        <h4>Total Jobs</h4>
                        <div class="stat-value">${stats.totalJobs}</div>
                        <p class="stat-sub">Open: ${stats.openJobs}</p>
                    </div>
                </div>
            </section>

            <div class="dashboard-grid">
                <section class="panel">
                    <div class="panel-header">
                        <h2>Application Statistics</h2>
                    </div>
                    <div class="application-stats">
                        <div class="app-stat-item">
                            <span class="app-stat-badge pending">Pending</span>
                            <span class="app-stat-count">${stats.pendingApplications}</span>
                        </div>
                        <div class="app-stat-item">
                            <span class="app-stat-badge accepted">Accepted</span>
                            <span class="app-stat-count">${stats.acceptedApplications}</span>
                        </div>
                        <div class="app-stat-item">
                            <span class="app-stat-badge rejected">Rejected</span>
                            <span class="app-stat-count">${stats.rejectedApplications}</span>
                        </div>
                    </div>
                    <div class="stat-bar">
                        <div class="stat-bar-fill accepted" style="width: ${stats.totalApplications > 0 ? (stats.acceptedApplications * 100 / stats.totalApplications) : 0}%"></div>
                        <div class="stat-bar-fill pending" style="width: ${stats.totalApplications > 0 ? (stats.pendingApplications * 100 / stats.totalApplications) : 0}%"></div>
                        <div class="stat-bar-fill rejected" style="width: ${stats.totalApplications > 0 ? (stats.rejectedApplications * 100 / stats.totalApplications) : 0}%"></div>
                    </div>
                </section>

                <section class="panel">
                    <div class="panel-header">
                        <h2>TOP 5 TAs by Applications</h2>
                    </div>
                    <div class="ranking-list">
                        <c:forEach var="entry" items="${stats.topTAs}" varStatus="status">
                            <div class="ranking-item">
                                <span class="ranking-position">${status.index + 1}</span>
                                <span class="ranking-label">${entry.key}</span>
                                <span class="ranking-value">${entry.value} applications</span>
                            </div>
                        </c:forEach>
                        <c:if test="${empty stats.topTAs}">
                            <div class="empty-state-small">No data available</div>
                        </c:if>
                    </div>
                </section>

                <section class="panel">
                    <div class="panel-header">
                        <h2>TOP 3 Popular Jobs</h2>
                    </div>
                    <div class="ranking-list">
                        <c:forEach var="entry" items="${stats.topJobs}" varStatus="status">
                            <div class="ranking-item">
                                <span class="ranking-position">${status.index + 1}</span>
                                <span class="ranking-label">${entry.key}</span>
                                <span class="ranking-value">${entry.value} applicants</span>
                            </div>
                        </c:forEach>
                        <c:if test="${empty stats.topJobs}">
                            <div class="empty-state-small">No data available</div>
                        </c:if>
                    </div>
                </section>
            </div>

            <section class="panel">
                <div class="panel-header">
                    <h2>Quick Actions</h2>
                </div>
                <div class="quick-actions">
                    <a href="${pageContext.request.contextPath}/admin/applications" class="quick-action-btn">
                        <span class="qa-icon"><svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg></span>
                        <span>Review Applications</span>
                        <c:if test="${stats.pendingApplications > 0}">
                            <span class="qa-badge">${stats.pendingApplications}</span>
                        </c:if>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/jobs" class="quick-action-btn">
                        <span class="qa-icon"><svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg></span>
                        <span>Manage Jobs</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/users" class="quick-action-btn">
                        <span class="qa-icon"><svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg></span>
                        <span>Manage Users</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/logs" class="quick-action-btn">
                        <span class="qa-icon"><svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg></span>
                        <span>View Logs</span>
                    </a>
                </div>
            </section>
        </div>
    </main>
</div>

<div class="modal-overlay" id="passwordModal">
    <div class="modal">
        <div class="modal-header">
            <h3>Change Password</h3>
            <button class="modal-close" onclick="closePasswordModal()">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/users/changePassword" method="post" class="modal-form">
            <input type="hidden" name="action" value="changePassword">
            <input type="hidden" name="userId" value="${sessionScope.currentUser.userId}">
            <div class="form-group">
                <label>Current Password</label>
                <input type="password" name="oldPassword" required>
            </div>
            <div class="form-group">
                <label>New Password</label>
                <input type="password" name="newPassword" required minlength="6">
                <small>At least 6 characters, must contain numbers and letters</small>
            </div>
            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" onclick="closePasswordModal()">Cancel</button>
                <button type="submit" class="btn btn-primary">Confirm</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

<script>
function showPasswordModal() {
    document.getElementById('passwordModal').classList.add('active');
}
function closePasswordModal() {
    document.getElementById('passwordModal').classList.remove('active');
}
document.addEventListener('DOMContentLoaded', function() {
    var dropdown = document.querySelector('.user-menu');
    if (dropdown) {
        dropdown.addEventListener('click', function(e) {
            if (e.target === dropdown || dropdown.contains(e.target)) {
                dropdown.classList.toggle('active');
            }
        });
        document.addEventListener('click', function(e) {
            if (!dropdown.contains(e.target)) {
                dropdown.classList.remove('active');
            }
        });
    }
});
</script>
