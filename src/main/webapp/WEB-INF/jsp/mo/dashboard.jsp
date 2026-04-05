<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "MO Dashboard");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<div class="layout">
    <aside class="sidebar">
        <div class="sidebar-brand">
            <div class="brand-logo">MO</div>
            <div>
                <h3>Module Organiser</h3>
                <p>Recruitment Suite</p>
            </div>
        </div>

        <nav class="sidebar-nav">
            <a class="nav-item active" href="${pageContext.request.contextPath}/mo/dashboard">岗位总览</a>
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/jobs/new">发布岗位</a>
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/applications">申请管理</a>
            <a class="nav-item" href="${pageContext.request.contextPath}/logout">退出登录</a>
        </nav>
    </aside>

    <main class="content">
        <div class="topbar">
            <button class="sidebar-toggle">☰</button>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.name}</span>
                <a href="${pageContext.request.contextPath}/logout">退出登录</a>
            </div>
        </div>

        <section class="panel dashboard-intro">
            <h1>MO 工作台</h1>
            <p>发布岗位，筛选申请人，快速完成本学期 TA 招募。</p>
        </section>

        <section class="stats-grid">
            <div class="stat-card">
                <h4>岗位总数</h4>
                <div class="stat-value">${totalJobs}</div>
                <p>系统中所有已发布的岗位数量</p>
            </div>
            <div class="stat-card stat-card-highlight">
                <h4>开放岗位</h4>
                <div class="stat-value">${activeJobs}</div>
                <p>当前正在接收申请的岗位</p>
            </div>
            <div class="stat-card">
                <h4>申请总数</h4>
                <div class="stat-value">${totalApplicants}</div>
                <p>所有收到的 TA 申请数量</p>
            </div>
            <div class="stat-card">
                <h4>待审申请</h4>
                <div class="stat-value">${pendingCount}</div>
                <p>等待你处理的申请数</p>
            </div>
        </section>

        <section class="panel">
            <div class="panel-header">
                <h2>岗位列表</h2>
                <a href="${pageContext.request.contextPath}/mo/jobs/new" class="btn btn-primary btn-small">
                    <span class="btn-icon">+</span> 发布新岗位
                </a>
            </div>
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                    <tr>
                        <th>岗位编号</th>
                        <th>岗位标题</th>
                        <th>课程代码</th>
                        <th>招聘人数</th>
                        <th>申请人数</th>
                        <th>截止日期</th>
                        <th>状态</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty jobs}">
                            <tr>
                                <td colspan="8" class="empty-state">暂无岗位数据</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="job" items="${jobs}">
                                <tr>
                                    <td><span class="job-id">${job.jobId}</span></td>
                                    <td>${job.title}</td>
                                    <td><span class="module-code">${job.moduleCode}</span></td>
                                    <td>${job.vacancies}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/mo/applications?jobId=${job.jobId}" class="applicant-count">
                                            ${applicationCounts[job.jobId]}
                                        </a>
                                    </td>
                                    <td>${job.deadline}</td>
                                    <td>
                                        <span class="badge ${job.status}">${job.status == 'OPEN' ? '开放' : '关闭'}</span>
                                    </td>
                                    <td>
                                        <div class="action-buttons">
                                            <a href="${pageContext.request.contextPath}/mo/applications?jobId=${job.jobId}" class="btn-icon-action" title="查看申请人">
                                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                                    <circle cx="9" cy="7" r="4"></circle>
                                                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                                                    <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                                                </svg>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/mo/jobs/edit/${job.jobId}" class="btn-icon-action" title="编辑岗位">
                                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                                </svg>
                                            </a>
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
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
