<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "职位管理");
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
            <a class="nav-item active" href="${pageContext.request.contextPath}/admin/jobs">
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
            <div class="topbar-title">职位管理</div>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.name}</span>
                <a href="${pageContext.request.contextPath}/logout">退出</a>
            </div>
        </div>

        <div class="admin-content">
            <section class="panel dashboard-intro">
                <div class="intro-header">
                    <div>
                        <h1>职位管理</h1>
                        <p>管理系统中的所有 TA 职位，添加、编辑、启用/禁用职位。</p>
                    </div>
                    <button class="btn btn-primary" onclick="showJobModal()">
                        <span class="btn-icon">+</span> 新增职位
                    </button>
                </div>
            </section>

            <section class="panel">
                <div class="filter-bar">
                    <form method="get" action="${pageContext.request.contextPath}/admin/jobs" class="filter-form">
                        <select name="status" class="filter-select">
                            <option value="ALL">全部状态</option>
                            <option value="OPEN" ${currentStatus == 'OPEN' ? 'selected' : ''}>开放</option>
                            <option value="CLOSED" ${currentStatus == 'CLOSED' ? 'selected' : ''}>关闭</option>
                        </select>

                        <input type="text" name="search" placeholder="搜索职位名称/代码..." value="${currentSearch}" class="filter-input">

                        <button type="submit" class="btn btn-primary btn-small">筛选</button>
                        <a href="${pageContext.request.contextPath}/admin/jobs" class="btn btn-secondary btn-small">重置</a>
                    </form>
                </div>

                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                        <tr>
                            <th>职位ID</th>
                            <th>职位名称</th>
                            <th>课程代码</th>
                            <th>配额</th>
                            <th>申请数</th>
                            <th>截止日期</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty jobs}">
                                <tr>
                                    <td colspan="8" class="empty-state">
                                        <div class="empty-content">
                                            <span class="empty-icon">&#9651;</span>
                                            <p>暂无职位数据</p>
                                            <button class="btn btn-primary btn-small" onclick="showJobModal()">创建第一个职位</button>
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
                                        <td><span class="applicant-count">${applicationCounts[job.jobId]}</span></td>
                                        <td>${job.deadline}</td>
                                        <td>
                                            <span class="badge ${job.status}">${job.status == 'OPEN' ? '开放' : '关闭'}</span>
                                        </td>
                                        <td>
                                            <div class="action-buttons">
                                                <button class="btn btn-action btn-edit" onclick="editJob('${job.jobId}')" title="编辑">
                                                    <span class="btn-icon-svg">&#9998;</span>
                                                </button>
                                                <form action="${pageContext.request.contextPath}/admin/jobs/toggle" method="post" class="inline-form">
                                                    <input type="hidden" name="action" value="toggle">
                                                    <input type="hidden" name="jobId" value="${job.jobId}">
                                                    <button type="submit" class="btn btn-action ${job.status == 'OPEN' ? 'btn-warning' : 'btn-success'}" title="${job.status == 'OPEN' ? '禁用' : '启用'}">
                                                        ${job.status == 'OPEN' ? '&#10007;' : '&#10003;'}
                                                    </button>
                                                </form>
                                                <button class="btn btn-action btn-danger" onclick="confirmDeleteJob('${job.jobId}')" title="删除">
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
            <h3 id="jobModalTitle">新增职位</h3>
            <button class="modal-close" onclick="closeJobModal()">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/jobs/save" method="post" id="jobForm" class="modal-form">
            <input type="hidden" name="action" id="jobFormAction" value="create">
            <input type="hidden" name="jobId" id="jobId" value="">

            <div class="form-row">
                <div class="form-group">
                    <label>职位名称 <span class="required">*</span></label>
                    <input type="text" name="title" id="jobTitle" required placeholder="例如：Software Engineering TA">
                </div>
                <div class="form-group">
                    <label>课程代码 <span class="required">*</span></label>
                    <input type="text" name="moduleCode" id="jobModuleCode" required placeholder="例如：EBU6304">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>组织者/教师</label>
                    <input type="text" name="organiser" id="jobOrganiser" placeholder="例如：Dr.Wang">
                </div>
                <div class="form-group">
                    <label>总工时</label>
                    <input type="number" name="hours" id="jobHours" min="1" value="20">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>最低年级</label>
                    <input type="number" name="minYear" id="jobMinYear" min="1" max="8" value="1">
                </div>
                <div class="form-group">
                    <label>最高年级</label>
                    <input type="number" name="maxYear" id="jobMaxYear" min="1" max="8" value="4">
                </div>
                <div class="form-group">
                    <label>招聘人数 <span class="required">*</span></label>
                    <input type="number" name="vacancies" id="jobVacancies" min="1" required value="1">
                </div>
                <div class="form-group">
                    <label>截止日期</label>
                    <input type="date" name="deadline" id="jobDeadline">
                </div>
            </div>

            <div class="form-group">
                <label>技能要求</label>
                <input type="text" name="requiredSkills" id="jobSkills" placeholder="例如：Java, Python, Teamwork">
            </div>

            <div class="modal-actions">
                <button type="button" class="btn btn-secondary" onclick="closeJobModal()">取消</button>
                <button type="submit" class="btn btn-primary" id="jobSubmitBtn">创建职位</button>
            </div>
        </form>
    </div>
</div>

<div class="modal-overlay" id="confirmModal">
    <div class="modal modal-small">
        <div class="modal-header">
            <h3>确认删除</h3>
            <button class="modal-close" onclick="closeConfirmModal()">&times;</button>
        </div>
        <div class="modal-body">
            <p>确定要删除此职位吗？此操作将同时清理关联的申请记录，且无法恢复。</p>
        </div>
        <div class="modal-actions">
            <button type="button" class="btn btn-secondary" onclick="closeConfirmModal()">取消</button>
            <form action="${pageContext.request.contextPath}/admin/jobs/delete" method="post" id="deleteForm" class="inline-form">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="jobId" id="deleteJobId" value="">
                <button type="submit" class="btn btn-danger">确认删除</button>
            </form>
        </div>
    </div>
</div>

<div class="toast-container" id="toastContainer"></div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

<script>
var jobsData = {
<c:forEach var="job" items="${jobs}" varStatus="status">
    '${job.jobId}': { title: '${job.title}', moduleCode: '${job.moduleCode}', organiser: '${job.organiser}',
        hours: ${job.hours}, minYear: ${job.minYear}, maxYear: ${job.maxYear}, vacancies: ${job.vacancies},
        deadline: '${job.deadline}', requiredSkills: '${job.requiredSkills}'}${status.last ? '' : ','}
</c:forEach>
};

function showJobModal() {
    document.getElementById('jobModalTitle').textContent = '新增职位';
    document.getElementById('jobFormAction').value = 'create';
    document.getElementById('jobId').value = '';
    document.getElementById('jobForm').reset();
    document.getElementById('jobSubmitBtn').textContent = '创建职位';
    document.getElementById('jobModal').classList.add('active');
}

function editJob(jobId) {
    var job = jobsData[jobId];
    if (!job) return;

    document.getElementById('jobModalTitle').textContent = '编辑职位';
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
    document.getElementById('jobSubmitBtn').textContent = '保存更改';
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
