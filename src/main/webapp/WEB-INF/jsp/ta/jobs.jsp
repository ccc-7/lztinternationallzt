<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "职位大厅");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<div class="layout">
    <aside class="sidebar">
        <div class="sidebar-brand">
            <div class="brand-logo">TA</div>
            <div>
                <h3>Teaching Assistant</h3>
                <p>Recruitment Suite</p>
            </div>
        </div>

        <nav class="sidebar-nav">
            <a class="nav-item" href="${pageContext.request.contextPath}/ta/dashboard">个人档案</a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/jobs">职位大厅</a>
            <a class="nav-item" href="${pageContext.request.contextPath}/applications">申请状态</a>
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