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
                <span class="nav-icon">&#9632;</span> 工作台
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/applications">
                <span class="nav-icon">&#9733;</span> 应用管理
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/jobs">
                <span class="nav-icon">&#9651;</span> 职位管理
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/users">
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
            <div class="topbar-title">Admin 工作台</div>
            <div class="topbar-right">
                <div class="user-menu">
                    <span class="user-name">${sessionScope.currentUser.name}</span>
                    <div class="user-dropdown">
                        <a href="#" class="dropdown-item" onclick="showPasswordModal()">修改密码</a>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/logout">退出登录</a>
            </div>
        </div>

        <div class="admin-dashboard">
            <section class="panel dashboard-intro">
                <h1>数据统计概览</h1>
                <p>查看系统整体运行状态和数据统计。</p>
            </section>

            <section class="stats-grid stats-admin">
                <div class="stat-card stat-card-primary">
                    <div class="stat-icon">TA</div>
                    <div class="stat-content">
                        <h4>TA 账号总数</h4>
                        <div class="stat-value">${stats.totalTA}</div>
                        <p class="stat-sub">活跃: ${stats.activeTA}</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">MO</div>
                    <div class="stat-content">
                        <h4>MO 账号总数</h4>
                        <div class="stat-value">${stats.totalMO}</div>
                    </div>
                </div>
                <div class="stat-card stat-card-success">
                    <div class="stat-icon">&#10003;</div>
                    <div class="stat-content">
                        <h4>申请总数</h4>
                        <div class="stat-value">${stats.totalApplications}</div>
                        <p class="stat-sub">待审: ${stats.pendingApplications}</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">&#9651;</div>
                    <div class="stat-content">
                        <h4>职位总数</h4>
                        <div class="stat-value">${stats.totalJobs}</div>
                        <p class="stat-sub">开放: ${stats.openJobs}</p>
                    </div>
                </div>
            </section>

            <div class="dashboard-grid">
                <section class="panel">
                    <div class="panel-header">
                        <h2>申请统计</h2>
                    </div>
                    <div class="application-stats">
                        <div class="app-stat-item">
                            <span class="app-stat-badge pending">待审核</span>
                            <span class="app-stat-count">${stats.pendingApplications}</span>
                        </div>
                        <div class="app-stat-item">
                            <span class="app-stat-badge accepted">已录用</span>
                            <span class="app-stat-count">${stats.acceptedApplications}</span>
                        </div>
                        <div class="app-stat-item">
                            <span class="app-stat-badge rejected">已拒绝</span>
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
                        <h2>申请次数 TOP 5 TA</h2>
                    </div>
                    <div class="ranking-list">
                        <c:forEach var="entry" items="${stats.topTAs}" varStatus="status">
                            <div class="ranking-item">
                                <span class="ranking-position">${status.index + 1}</span>
                                <span class="ranking-label">${entry.key}</span>
                                <span class="ranking-value">${entry.value} 次申请</span>
                            </div>
                        </c:forEach>
                        <c:if test="${empty stats.topTAs}">
                            <div class="empty-state-small">暂无数据</div>
                        </c:if>
                    </div>
                </section>

                <section class="panel">
                    <div class="panel-header">
                        <h2>热门职位 TOP 3</h2>
                    </div>
                    <div class="ranking-list">
                        <c:forEach var="entry" items="${stats.topJobs}" varStatus="status">
                            <div class="ranking-item">
                                <span class="ranking-position">${status.index + 1}</span>
                                <span class="ranking-label">${entry.key}</span>
                                <span class="ranking-value">${entry.value} 人申请</span>
                            </div>
                        </c:forEach>
                        <c:if test="${empty stats.topJobs}">
                            <div class="empty-state-small">暂无数据</div>
                        </c:if>
                    </div>
                </section>
            </div>

            <section class="panel">
                <div class="panel-header">
                    <h2>快捷操作</h2>
                </div>
                <div class="quick-actions">
                    <a href="${pageContext.request.contextPath}/admin/applications" class="quick-action-btn">
                        <span class="qa-icon">&#9733;</span>
                        <span>审核申请</span>
                        <span class="qa-badge">${stats.pendingApplications}</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/jobs" class="quick-action-btn">
                        <span class="qa-icon">&#9651;</span>
                        <span>职位管理</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/users" class="quick-action-btn">
                        <span class="qa-icon">&#9679;</span>
                        <span>用户管理</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/logs" class="quick-action-btn">
                        <span class="qa-icon">&#9633;</span>
                        <span>查看日志</span>
                    </a>
                </div>
            </section>
        </div>
    </main>
</div>

<div class="modal-overlay" id="passwordModal">
    <div class="modal">
        <div class="modal-header">
            <h3>修改密码</h3>
            <button class="modal-close" onclick="closePasswordModal()">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/users/changePassword" method="post" class="modal-form">
            <input type="hidden" name="action" value="changePassword">
            <input type="hidden" name="userId" value="${sessionScope.currentUser.userId}">
            <div class="form-group">
                <label>原密码</label>
                <input type="password" name="oldPassword" required>
            </div>
            <div class="form-group">
                <label>新密码</label>
                <input type="password" name="newPassword" required minlength="6">
                <small>至少6位，需包含数字和字母</small>
            </div>
            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" onclick="closePasswordModal()">取消</button>
                <button type="submit" class="btn btn-primary">确认修改</button>
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
