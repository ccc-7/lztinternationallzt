<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "职位大厅");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<div class="layout layout-ta">
    <aside class="sidebar sidebar-ta" id="sidebar">
        <div class="sidebar-brand">
            <div class="brand-logo brand-ta">TA</div>
            <div>
                <h3>TA Portal</h3>
                <p>Recruitment System</p>
            </div>
        </div>

        <nav class="sidebar-nav">
            <a class="nav-item" href="${pageContext.request.contextPath}/ta/dashboard">
                <span class="nav-icon">&#9632;</span> 工作台
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/jobs">
                <span class="nav-icon">&#9651;</span> 职位大厅
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/applications">
                <span class="nav-icon">&#9733;</span> 申请状态
            </a>
        </nav>
    </aside>

    <main class="content content-ta">
        <div class="topbar topbar-ta">
            <button type="button" class="sidebar-toggle">&#9776;</button>
            <div class="topbar-title">Available Positions</div>
            <div class="topbar-right">
                <a href="${pageContext.request.contextPath}/ta/profile">${sessionScope.currentUser.name}</a>
                <a href="${pageContext.request.contextPath}/logout">退出登录</a>
            </div>
        </div>

        <section class="panel">
            <h1>职位大厅</h1>
            <p>浏览当前开放岗位，并查看系统推荐的匹配项。</p>
        </section>

        <section class="job-grid">
            <c:forEach var="job" items="${jobs}">
                <div class="job-card">
                    <div class="job-top">
                        <h3>${job.title}</h3>
                        <span class="badge">${job.status}</span>
                    </div>

                    <p><strong>课程代码：</strong>${job.moduleCode}</p>
                    <p><strong>发布教师：</strong>${job.organiser}</p>
                    <p><strong>时长：</strong>${job.hours} 小时</p>
                    <p><strong>年级要求：</strong>${job.minYear} - ${job.maxYear}</p>
                    <p><strong>技能要求：</strong>${job.requiredSkills}</p>
                    <p><strong>AI 匹配分：</strong>${job.matchScore}</p>

                    <form action="${pageContext.request.contextPath}/apply" method="post" class="apply-form">
                        <input type="hidden" name="jobId" value="${job.jobId}">
                        <button type="submit" class="btn btn-primary full-btn">申请该岗位</button>
                    </form>
                </div>
            </c:forEach>
        </section>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

