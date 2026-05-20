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
            <div class="brand-logo brand-mo">MO</div>
            <div>
                <h3>Module Organiser</h3>
                <p>Recruitment Suite</p>
            </div>
        </div>

        <nav class="sidebar-nav">
            <a class="nav-item active" href="${pageContext.request.contextPath}/mo/dashboard">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
                </span> Dashboard
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/jobs/new">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
                </span> Post Job
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/applications">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                </span> Applications
            </a>
        </nav>
    </aside>

    <main class="content">
        <div class="topbar topbar-mo">
            <button type="button" class="sidebar-toggle">
                <svg viewBox="0 0 24 24"><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
            </button>
            <div class="topbar-title">MO Dashboard</div>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.username}</span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="mo-content">
            <section class="panel dashboard-intro">
                <h1>MO Dashboard</h1>
                <p>Post jobs, review applicants, and complete your TA recruitment for this semester.</p>
            </section>

            <section class="stats-grid stats-mo">
            <div class="stat-card">
                <div class="stat-icon">&#128196;</div>
                <div class="stat-content">
                    <h4>My Jobs</h4>
                    <div class="stat-value">${totalJobs}</div>
                    <p>Jobs posted by you</p>
                </div>
            </div>
            <div class="stat-card stat-card-highlight">
                <div class="stat-icon">&#10004;</div>
                <div class="stat-content">
                    <h4>My Open Jobs</h4>
                    <div class="stat-value">${activeJobs}</div>
                    <p>Your jobs currently accepting applications</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon"><svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg></div>
                <div class="stat-content">
                    <h4>Applications Received</h4>
                    <div class="stat-value">${totalApplicants}</div>
                    <p>Applications for your posted jobs</p>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">&#9203;</div>
                <div class="stat-content">
                    <h4>Pending Review</h4>
                    <div class="stat-value">${pendingCount}</div>
                    <p>Pending applications for your jobs</p>
                </div>
            </div>
        </section>

        <section class="panel">
            <div class="panel-header">
                <h2>Job List</h2>
                <a href="${pageContext.request.contextPath}/mo/jobs/new" class="btn btn-primary btn-small">
                    <span class="btn-icon">+</span> Post New Job
                </a>
            </div>
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                    <tr>
                        <th>Job ID</th>
                        <th>Job Title</th>
                        <th>Module Code</th>
                        <th>Vacancies</th>
                        <th>Applicants</th>
                        <th>Deadline</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty jobs}">
                            <tr>
                                <td colspan="8" class="empty-state">
                                    <div class="empty-state-illustration">
                                        <div class="empty-illustration-icon">&#128196;</div>
                                        <p class="empty-illustration-title">No jobs posted yet</p>
                                        <p class="empty-illustration-desc">Start by posting a new job position for TA recruitment</p>
                                        <a href="${pageContext.request.contextPath}/mo/jobs/new" class="btn btn-primary btn-small">Post First Job</a>
                                    </div>
                                </td>
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
                                        <span class="badge ${job.status}">${job.status == 'OPEN' ? 'Open' : 'Closed'}</span>
                                    </td>
                                    <td>
                                        <div class="action-buttons">
                                            <a href="${pageContext.request.contextPath}/mo/applications?jobId=${job.jobId}" class="btn-icon-action" title="View Applicants">
                                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                                    <circle cx="9" cy="7" r="4"></circle>
                                                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                                                    <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                                                </svg>
                                            </a>
                                            <c:choose>
                                                <c:when test="${job.status == 'OPEN'}">
                                                    <form method="post" action="${pageContext.request.contextPath}/mo/jobs/close/${job.jobId}" style="display:inline;">
                                                        <button type="submit" class="btn-icon-action btn-close" title="Close Job">
                                                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                                <circle cx="12" cy="12" r="10"></circle>
                                                                <line x1="15" y1="9" x2="9" y2="15"></line>
                                                                <line x1="9" y1="9" x2="15" y2="15"></line>
                                                            </svg>
                                                        </button>
                                                    </form>
                                                </c:when>
                                                <c:otherwise>
                                                    <form method="post" action="${pageContext.request.contextPath}/mo/jobs/open/${job.jobId}" style="display:inline;">
                                                        <button type="submit" class="btn-icon-action btn-open" title="Reopen Job">
                                                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                                <circle cx="12" cy="12" r="10"></circle>
                                                                <line x1="12" y1="8" x2="12" y2="16"></line>
                                                                <line x1="8" y1="12" x2="16" y2="12"></line>
                                                            </svg>
                                                        </button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                            <a href="${pageContext.request.contextPath}/mo/jobs/edit/${job.jobId}" class="btn-icon-action" title="Edit Job">
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
        </div>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>


