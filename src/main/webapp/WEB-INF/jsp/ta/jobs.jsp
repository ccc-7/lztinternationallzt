<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Job Board");
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
                <span class="nav-icon">&#9632;</span> Dashboard
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/jobs">
                <span class="nav-icon">&#9651;</span> Job Board
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/applications">
                <span class="nav-icon">&#9733;</span> Applications
            </a>
        </nav>
    </aside>

    <main class="content content-ta">
        <div class="topbar topbar-ta">
            <button type="button" class="sidebar-toggle">&#9776;</button>
            <div class="topbar-title">Available Positions</div>
            <div class="topbar-right">
                <a href="${pageContext.request.contextPath}/ta/profile">${sessionScope.currentUser.name}</a>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <section class="panel">
            <h1>Job Board</h1>
            <p>Browse open positions and view system-recommended matches.</p>
        </section>

        <section class="job-grid">
            <c:forEach var="job" items="${jobs}">
                <div class="job-card">
                    <div class="job-top">
                        <h3>${job.title}</h3>
                        <span class="badge">${job.status}</span>
                    </div>

                    <p><strong>Module Code:</strong> ${job.moduleCode}</p>
                    <p><strong>Organizer:</strong> ${job.organiser}</p>
                    <p><strong>Hours:</strong> ${job.hours} hours</p>
                    <p><strong>Year Requirement:</strong> ${job.minYear} - ${job.maxYear}</p>
                    <p><strong>Required Skills:</strong> ${job.requiredSkills}</p>
                    <p><strong>AI Match Score:</strong> ${job.matchScore}</p>

                    <form action="${pageContext.request.contextPath}/apply" method="post" class="apply-form">
                        <input type="hidden" name="jobId" value="${job.jobId}">
                        <button type="submit" class="btn btn-primary full-btn">Apply for This Position</button>
                    </form>
                </div>
            </c:forEach>
        </section>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

