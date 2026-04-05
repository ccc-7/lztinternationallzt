<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "用户管理");
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
                <span class="nav-icon">&#9632;</span> 工作台
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/applications">
                <span class="nav-icon">&#9733;</span> 应用管理
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/jobs">
                <span class="nav-icon">&#9651;</span> 职位管理
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/admin/users">
                <span class="nav-icon">&#9679;</span> 用户管理
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/logs">
                <span class="nav-icon">&#9633;</span> 系统日志
            </a>
        </nav>
    </aside>

    <main class="content">
        <div class="topbar topbar-admin">
            <button class="sidebar-toggle">&#9776;</button>
            <div class="topbar-title">用户管理</div>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.name}</span>
                <a href="${pageContext.request.contextPath}/logout">退出</a>
            </div>
        </div>

        <div class="admin-content">
            <section class="panel dashboard-intro">
                <div class="intro-header">
                    <div>
                        <h1>用户管理</h1>
                        <p>管理系统用户账号，支持 TA、MO、Admin 三种角色。</p>
                    </div>
                    <button class="btn btn-primary" onclick="showUserModal()">
                        <span class="btn-icon">+</span> 新增用户
                    </button>
                </div>
            </section>

            <section class="panel">
                <div class="tab-nav">
                    <a href="?role=TA" class="tab-item ${currentRole == 'TA' ? 'active' : ''}">
                        TA 用户 <span class="tab-count">${taCount}</span>
                    </a>
                    <a href="?role=MO" class="tab-item ${currentRole == 'MO' ? 'active' : ''}">
                        MO 用户 <span class="tab-count">${moCount}</span>
                    </a>
                    <a href="?role=ADMIN" class="tab-item ${currentRole == 'ADMIN' ? 'active' : ''}">
                        Admin <span class="tab-count">${adminCount}</span>
                    </a>
                </div>

                <div class="filter-bar">
                    <form method="get" action="${pageContext.request.contextPath}/admin/users" class="filter-form">
                        <input type="hidden" name="role" value="${currentRole}">
                        <select name="status" class="filter-select">
                            <option value="ALL">全部状态</option>
                            <option value="ACTIVE" ${currentStatus == 'ACTIVE' ? 'selected' : ''}>活跃</option>
                            <option value="INACTIVE" ${currentStatus == 'INACTIVE' ? 'selected' : ''}>禁用</option>
                        </select>

                        <input type="text" name="search" placeholder="搜索用户名/ID..." value="${currentSearch}" class="filter-input">

                        <button type="submit" class="btn btn-primary btn-small">筛选</button>
                        <a href="${pageContext.request.contextPath}/admin/users?role=${currentRole}" class="btn btn-secondary btn-small">重置</a>
                    </form>
                </div>

                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                        <tr>
                            <th>用户ID</th>
                            <th>用户名</th>
                            <th>姓名</th>
                            <th>邮箱</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty users}">
                                <tr>
                                    <td colspan="6" class="empty-state">
                                        <div class="empty-content">
                                            <span class="empty-icon">&#9679;</span>
                                            <p>暂无用户数据</p>
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
                                                ${user.status == 'ACTIVE' ? '活跃' : '禁用'}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="action-buttons">
                                                <form action="${pageContext.request.contextPath}/admin/users/toggle" method="post" class="inline-form">
                                                    <input type="hidden" name="action" value="toggle">
                                                    <input type="hidden" name="userId" value="${user.userId}">
                                                    <input type="hidden" name="role" value="${currentRole}">
                                                    <button type="submit" class="btn btn-action ${user.status == 'ACTIVE' ? 'btn-warning' : 'btn-success'}" title="${user.status == 'ACTIVE' ? '禁用' : '启用'}">
                                                        ${user.status == 'ACTIVE' ? '&#10007; 禁用' : '&#10003; 启用'}
                                                    </button>
                                                </form>
                                                <c:if test="${currentRole == 'ADMIN'}">
                                                    <button class="btn btn-action btn-edit" onclick="showPasswordModal('${user.userId}')" title="修改密码">
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
            <h3>新增用户</h3>
            <button class="modal-close" onclick="closeUserModal()">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/users/create" method="post" id="userForm" class="modal-form">
            <input type="hidden" name="action" value="create">
            <input type="hidden" name="role" value="${currentRole}">

            <div class="form-row">
                <div class="form-group">
                    <label>用户名 <span class="required">*</span></label>
                    <input type="text" name="username" required placeholder="唯一用户名">
                </div>
                <div class="form-group">
                    <label>密码 <span class="required">*</span></label>
                    <input type="password" name="password" required minlength="6" placeholder="至少6位">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>姓名</label>
                    <input type="text" name="name" placeholder="真实姓名">
                </div>
                <div class="form-group">
                    <label>邮箱</label>
                    <input type="email" name="email" placeholder="邮箱地址">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>年级</label>
                    <input type="number" name="year" min="1" max="8" placeholder="仅TA需要">
                </div>
                <div class="form-group">
                    <label>专业</label>
                    <input type="text" name="major" placeholder="专业方向">
                </div>
            </div>

            <div class="form-group">
                <label>技能（TA用户）</label>
                <input type="text" name="skills" placeholder="例如：Java, Python">
            </div>

            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" onclick="closeUserModal()">取消</button>
                <button type="submit" class="btn btn-primary">创建用户</button>
            </div>
        </form>
    </div>
</div>

<div class="modal-overlay" id="passwordModal">
    <div class="modal modal-small">
        <div class="modal-header">
            <h3>修改密码</h3>
            <button class="modal-close" onclick="closePasswordModal()">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/users/changePassword" method="post" class="modal-form">
            <input type="hidden" name="action" value="changePassword">
            <input type="hidden" name="userId" id="passwordUserId" value="">
            <div class="form-group">
                <label>原密码 <span class="required">*</span></label>
                <input type="password" name="oldPassword" required>
            </div>
            <div class="form-group">
                <label>新密码 <span class="required">*</span></label>
                <input type="password" name="newPassword" required minlength="6" placeholder="至少6位，需包含数字和字母">
            </div>
            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" onclick="closePasswordModal()">取消</button>
                <button type="submit" class="btn btn-primary">确认修改</button>
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
