<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Applications");
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
            <a class="nav-item" href="${pageContext.request.contextPath}/jobs">
                <span class="nav-icon">&#9651;</span> Job Board
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/applications">
                <span class="nav-icon">&#9733;</span> Applications
            </a>
        </nav>
    </aside>

    <main class="content content-ta">
        <div class="topbar topbar-ta">
            <button type="button" class="sidebar-toggle">&#9776;</button>
            <div class="topbar-title">My Applications</div>
            <div class="topbar-right">
                <a href="${pageContext.request.contextPath}/ta/profile">${sessionScope.currentUser.name}</a>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <section class="panel">
            <h1>My Applications</h1>
            <p>Track the status of all your applications here.</p>
        </section>

        <section class="panel">
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                    <tr>
                        <th>Application ID</th>
                        <th>Job ID</th>
                        <th>Status</th>
                        <th>Submitted At</th>
                        <th>Notes</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty applications}">
                            <tr>
                                <td colspan="5" class="empty-state">
                                    <div class="empty-content">
                                        <span class="empty-icon">&#128203;</span>
                                        <p>No applications yet</p>
                                        <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary btn-small">Browse Positions</a>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="a" items="${applications}">
                                <tr>
                                    <td><span class="app-id">${a.applicationId}</span></td>
                                    <td><span class="job-id">${a.jobId}</span></td>
                                    <td><span class="badge ${a.status}">${a.status}</span></td>
                                    <td>${a.submittedAt}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${empty a.notes}">
                                                <span class="empty-state-small">None</span>
                                            </c:when>
                                            <c:otherwise>
                                                ${a.notes}
                                            </c:otherwise>
                                        </c:choose>
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

