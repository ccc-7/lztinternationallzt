<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "系统日志");
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
            <a class="nav-item" href="${pageContext.request.contextPath}/admin/users">
                <span class="nav-icon">&#9679;</span> 用户管理
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/admin/logs">
                <span class="nav-icon">&#9633;</span> 系统日志
            </a>
        </nav>
    </aside>

    <main class="content">
        <div class="topbar topbar-admin">
            <button class="sidebar-toggle">&#9776;</button>
            <div class="topbar-title">系统日志</div>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.name}</span>
                <a href="${pageContext.request.contextPath}/logout">退出登录</a>
            </div>
        </div>

        <div class="admin-content">
            <section class="panel dashboard-intro">
                <div class="intro-header">
                    <div>
                        <h1>系统日志</h1>
                        <p>查看所有系统操作记录，关键操作日志永久保留，不可批量删除。</p>
                    </div>
                    <button class="btn btn-secondary" onclick="exportLogs()">
                        <span class="btn-icon">&#8595;</span> 导出 Excel
                    </button>
                </div>
            </section>

            <section class="panel">
                <div class="filter-bar">
                    <form method="get" action="${pageContext.request.contextPath}/admin/logs" class="filter-form">
                        <select name="operator" class="filter-select">
                            <option value="">全部操作员</option>
                            <c:forEach var="admin" items="${admins}">
                                <option value="${admin.userId}" ${currentOperator == admin.userId ? 'selected' : ''}>${admin.displayName} (${admin.userId})</option>
                            </c:forEach>
                        </select>

                        <select name="type" class="filter-select">
                            <option value="ALL">全部类型</option>
                            <option value="LOGIN" ${currentType == 'LOGIN' ? 'selected' : ''}>登录</option>
                            <option value="LOGOUT" ${currentType == 'LOGOUT' ? 'selected' : ''}>退出</option>
                            <option value="CREATE" ${currentType == 'CREATE' ? 'selected' : ''}>创建</option>
                            <option value="UPDATE" ${currentType == 'UPDATE' ? 'selected' : ''}>更新</option>
                            <option value="DELETE" ${currentType == 'DELETE' ? 'selected' : ''}>删除</option>
                            <option value="APPROVE" ${currentType == 'APPROVE' ? 'selected' : ''}>批准</option>
                            <option value="REJECT" ${currentType == 'REJECT' ? 'selected' : ''}>拒绝</option>
                            <option value="ENABLE" ${currentType == 'ENABLE' ? 'selected' : ''}>启用</option>
                            <option value="DISABLE" ${currentType == 'DISABLE' ? 'selected' : ''}>禁用</option>
                            <option value="EXPORT" ${currentType == 'EXPORT' ? 'selected' : ''}>导出</option>
                        </select>

                        <input type="text" name="search" placeholder="搜索日志内容..." value="${currentSearch}" class="filter-input">

                        <button type="submit" class="btn btn-primary btn-small">筛选</button>
                        <a href="${pageContext.request.contextPath}/admin/logs" class="btn btn-secondary btn-small">重置</a>
                    </form>
                </div>

                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                        <tr>
                            <th>日志ID</th>
                            <th>操作时间</th>
                            <th>操作员</th>
                            <th>操作类型</th>
                            <th>目标类型</th>
                            <th>目标ID</th>
                            <th>详情</th>
                            <th>IP地址</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty logs}">
                                <tr>
                                    <td colspan="8" class="empty-state">
                                        <div class="empty-content">
                                            <span class="empty-icon">&#9633;</span>
                                            <p>暂无日志记录</p>
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
                            <a href="?page=${currentPage - 1}&operator=${currentOperator}&type=${currentType}&search=${currentSearch}" class="page-link">&laquo; 上一页</a>
                        </c:if>

                        <span class="page-info">第 ${currentPage} / ${totalPages} 页</span>

                        <c:if test="${currentPage < totalPages}">
                            <a href="?page=${currentPage + 1}&operator=${currentOperator}&type=${currentType}&search=${currentSearch}" class="page-link">下一页 &raquo;</a>
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
