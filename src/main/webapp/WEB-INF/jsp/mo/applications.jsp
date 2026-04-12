<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Applications");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<div class="layout">
    <aside class="sidebar">
        <div class="sidebar-brand">
            <div class="brand-logo brand-mo">MO</div>
            <div>
                <h3>Module Organiser</h3>
                <p>Recruitment Suite</p>
            </div>
        </div>

        <nav class="sidebar-nav">
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/dashboard">
                <span class="nav-icon">&#9632;</span> Dashboard
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/jobs/new">
                <span class="nav-icon">&#9651;</span> Post Job
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/mo/applications">
                <span class="nav-icon">&#9733;</span> Applications
            </a>
        </nav>
    </aside>

    <main class="content">
        <div class="topbar topbar-mo">
            <button type="button" class="sidebar-toggle">&#9776;</button>
            <div class="topbar-title">Applications</div>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.name}</span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <section class="panel dashboard-intro">
            <h1>Applications</h1>
            <p>Review applications for the jobs you posted and update candidate decisions.</p>
        </section>

        <section class="panel">
            <div class="panel-header">
                <h2>Applicant List</h2>
                <c:if test="${not empty filterJobId}">
                    <a href="${pageContext.request.contextPath}/mo/applications" class="btn btn-secondary btn-small">
                        View All
                    </a>
                </c:if>
            </div>
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                    <tr>
                        <th>Application ID</th>
                        <th>Applicant</th>
                        <th>Job</th>
                        <th>Availability</th>
                        <th>CV</th>
                        <th>Status</th>
                        <th>Submitted At</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty applications}">
                            <tr>
                                <td colspan="8" class="empty-state">No applications found</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="a" items="${applications}">
                                <tr>
                                    <td><span class="app-id">${a.applicationId}</span></td>
                                    <td>
                                        <div class="user-info">
                                            <span class="user-name">${applicantNames[a.userId]}</span>
                                            <span class="user-id">${a.userId}</span>
                                        </div>
                                    </td>
                                    <td><span class="module-code">${jobTitles[a.jobId]}</span></td>
                                    <td><span class="availability">${a.availability}</span></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/files/cv/${a.userId}" class="btn btn-outline btn-small" target="_blank">
                                            View CV
                                        </a>
                                    </td>
                                    <td><span class="badge ${a.status}">${statusLabels[a.status]}</span></td>
                                    <td>${a.submittedAt}</td>
                                    <td>
                                        <div class="decision-buttons">
                                            <form action="${pageContext.request.contextPath}/mo/applications/update" method="post" class="decision-form">
                                                <input type="hidden" name="applicationId" value="${a.applicationId}">
                                                <input type="hidden" name="status" value="ACCEPTED">
                                                <input type="hidden" name="filterJobId" value="${filterJobId}">
                                                <button type="submit" class="btn btn-decision btn-accept" ${a.status == 'ACCEPTED' ? 'disabled' : ''}>Accept</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/mo/applications/update" method="post" class="decision-form">
                                                <input type="hidden" name="applicationId" value="${a.applicationId}">
                                                <input type="hidden" name="status" value="INTERVIEW">
                                                <input type="hidden" name="filterJobId" value="${filterJobId}">
                                                <button type="submit" class="btn btn-decision btn-interview" ${a.status == 'INTERVIEW' ? 'disabled' : ''}>Interview</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/mo/applications/update" method="post" class="decision-form">
                                                <input type="hidden" name="applicationId" value="${a.applicationId}">
                                                <input type="hidden" name="status" value="REJECTED">
                                                <input type="hidden" name="filterJobId" value="${filterJobId}">
                                                <button type="submit" class="btn btn-decision btn-reject" ${a.status == 'REJECTED' ? 'disabled' : ''}>Reject</button>
                                            </form>
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


