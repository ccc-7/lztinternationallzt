<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "User Management");
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
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/applications">
                <span class="nav-icon">&#9733;</span> Applications
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/jobs">
                <span class="nav-icon">&#9651;</span> Jobs
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/admin/users">
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
            <div class="topbar-title">User Management</div>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.name}</span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="admin-content">
            <section class="panel dashboard-intro">
                <div class="intro-header">
                    <div>
                        <h1>User Management</h1>
                        <p>Manage system user accounts. Supports TA, MO, and Admin roles.</p>
                    </div>
                    <button class="btn btn-primary" onclick="showUserModal()">
                        <span class="btn-icon">+</span> Add User
                    </button>
                </div>
            </section>

            <section class="panel">
                <div class="tab-nav">
                    <a href="?role=TA" class="tab-item ${currentRole == 'TA' ? 'active' : ''}">
                        TA Users <span class="tab-count">${taCount}</span>
                    </a>
                    <a href="?role=MO" class="tab-item ${currentRole == 'MO' ? 'active' : ''}">
                        MO Users <span class="tab-count">${moCount}</span>
                    </a>
                    <a href="?role=ADMIN" class="tab-item ${currentRole == 'ADMIN' ? 'active' : ''}">
                        Admin <span class="tab-count">${adminCount}</span>
                    </a>
                </div>

                <div class="filter-bar">
                    <form method="get" action="${pageContext.request.contextPath}/admin/users" class="filter-form">
                        <input type="hidden" name="role" value="${currentRole}">
                        <select name="status" class="filter-select">
                            <option value="ALL">All Status</option>
                            <option value="ACTIVE" ${currentStatus == 'ACTIVE' ? 'selected' : ''}>Active</option>
                            <option value="INACTIVE" ${currentStatus == 'INACTIVE' ? 'selected' : ''}>Disabled</option>
                        </select>

                        <input type="text" name="search" placeholder="Search username/ID..." value="${currentSearch}" class="filter-input">

                        <button type="submit" class="btn btn-primary btn-small">Filter</button>
                        <a href="${pageContext.request.contextPath}/admin/users?role=${currentRole}" class="btn btn-secondary btn-small">Reset</a>
                    </form>
                </div>

                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                        <tr>
                            <th>User ID</th>
                            <th>Username</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty users}">
                                <tr>
                                    <td colspan="6" class="empty-state">
                                        <div class="empty-content">
                                            <span class="empty-icon">&#9679;</span>
                                            <p>No user data available</p>
                                        </div>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="user" items="${users}">
                                    <tr data-user-id="${user.userId}">
                                        <td><span class="user-id-badge">${user.userId}</span></td>
                                        <td><strong>${user.username}</strong></td>
                                        <td>${user.displayName}</td>
                                        <td>${user.email}</td>
                                        <td>
                                            <span class="badge ${user.status == 'ACTIVE' ? 'ACCEPTED' : 'REJECTED'}">
                                                ${user.status == 'ACTIVE' ? 'Active' : 'Disabled'}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="action-buttons">
                                                <form action="${pageContext.request.contextPath}/admin/users/toggle" method="post" class="inline-form">
                                                    <input type="hidden" name="action" value="toggle">
                                                    <input type="hidden" name="userId" value="${user.userId}">
                                                    <input type="hidden" name="role" value="${currentRole}">
                                                    <button type="submit" class="btn btn-action ${user.status == 'ACTIVE' ? 'btn-warning' : 'btn-success'}" title="${user.status == 'ACTIVE' ? 'Disable' : 'Enable'}">
                                                        ${user.status == 'ACTIVE' ? '&#10007; Disable' : '&#10003; Enable'}
                                                    </button>
                                                </form>
                                                <c:if test="${currentRole == 'ADMIN'}">
                                                    <button class="btn btn-action btn-edit" onclick="showPasswordModal('${user.userId}')" title="Change Password">
                                                        <span class="btn-icon-svg">&#128273;</span>
                                                    </button>
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

<div class="modal-overlay" id="userModal">
    <div class="modal">
        <div class="modal-header">
            <h3>Add User</h3>
            <button class="modal-close" onclick="closeUserModal()">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/users/create" method="post" id="userForm" class="modal-form">
            <input type="hidden" name="action" value="create">
            <input type="hidden" name="role" value="${currentRole}">

            <div class="form-row">
                <div class="form-group">
                    <label>Username <span class="required">*</span></label>
                    <input type="text" name="username" required placeholder="Unique username">
                </div>
                <div class="form-group">
                    <label>Password <span class="required">*</span></label>
                    <input type="password" name="password" required minlength="6" placeholder="At least 6 characters">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Name</label>
                    <input type="text" name="name" placeholder="Full name">
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" placeholder="Email address">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Year</label>
                    <input type="number" name="year" min="1" max="8" placeholder="Required for TA only">
                </div>
                <div class="form-group">
                    <label>Major</label>
                    <input type="text" name="major" placeholder="Major field">
                </div>
            </div>

            <div class="form-group">
                <label>Skills (TA Users)</label>
                <input type="text" name="skills" placeholder="e.g. Java, Python">
            </div>

            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" onclick="closeUserModal()">Cancel</button>
                <button type="submit" class="btn btn-primary">Create User</button>
            </div>
        </form>
    </div>
</div>

<div class="modal-overlay" id="passwordModal">
    <div class="modal modal-small">
        <div class="modal-header">
            <h3>Change Password</h3>
            <button class="modal-close" onclick="closePasswordModal()">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/users/changePassword" method="post" class="modal-form">
            <input type="hidden" name="action" value="changePassword">
            <input type="hidden" name="userId" id="passwordUserId" value="">
            <div class="form-group">
                <label>Current Password <span class="required">*</span></label>
                <input type="password" name="oldPassword" required>
            </div>
            <div class="form-group">
                <label>New Password <span class="required">*</span></label>
                <input type="password" name="newPassword" required minlength="6" placeholder="At least 6 characters with numbers and letters">
            </div>
            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" onclick="closePasswordModal()">Cancel</button>
                <button type="submit" class="btn btn-primary">Confirm</button>
            </div>
        </form>
    </div>
</div>

<div class="toast-container" id="toastContainer"></div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

<script>
function showUserModal() {
    document.getElementById('userModal').classList.add('active');
}

function closeUserModal() {
    document.getElementById('userModal').classList.remove('active');
    document.getElementById('userForm').reset();
}

function showPasswordModal(userId) {
    document.getElementById('passwordUserId').value = userId;
    document.getElementById('passwordModal').classList.add('active');
}

function closePasswordModal() {
    document.getElementById('passwordModal').classList.remove('active');
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
