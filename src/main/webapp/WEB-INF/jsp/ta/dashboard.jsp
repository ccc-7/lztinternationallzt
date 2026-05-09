<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="edu.bupt.ta.model.User" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%
    request.setAttribute("pageTitle", "TA Dashboard");
    User currentUser = (User) session.getAttribute("currentUser");
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
            <a class="nav-item active" href="${pageContext.request.contextPath}/ta/dashboard">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
                </span> Dashboard
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/jobs">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><line x1="12" y1="12" x2="12" y2="12"/></svg>
                </span> Job Board
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/applications">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                </span> Applications
            </a>
        </nav>
    </aside>

    <main class="content content-ta">
        <div class="topbar topbar-ta">
            <button type="button" class="sidebar-toggle">
                <svg viewBox="0 0 24 24"><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
            </button>
            <div class="topbar-title">TA Dashboard</div>
            <div class="topbar-right">
                <div class="user-menu">
                    <a class="user-name" href="${pageContext.request.contextPath}/ta/profile"><%= currentUser != null ? currentUser.getDisplayName() : "" %></a>
                </div>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="ta-content">
            <section class="panel dashboard-intro">
                <div class="intro-header">
                    <div>
                        <h1>Welcome, ${sessionScope.currentUser.username}!</h1>
                        <p>Track your applications and discover new opportunities.</p>
                    </div>
                </div>
            </section>

            <section class="stats-grid stats-ta">
                <div class="stat-card">
                    <div class="stat-icon">&#128203;</div>
                    <div class="stat-content">
                        <h4>Active Applications</h4>
                        <div class="stat-value">${pendingCount}</div>
                        <p>Pending or interview stage</p>
                    </div>
                </div>
                <div class="stat-card stat-highlight">
                    <div class="stat-icon">&#10004;</div>
                    <div class="stat-content">
                        <h4>Accepted</h4>
                        <div class="stat-value">${acceptedApplications}</div>
                        <p>Applications approved</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">&#128640;</div>
                    <div class="stat-content">
                        <h4>Matched Positions</h4>
                        <div class="stat-value">${matchCount}</div>
                        <p>Open positions for you</p>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon"><svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/></svg></div>
                    <div class="stat-content">
                        <h4>Total Open</h4>
                        <div class="stat-value">${totalOpenPositions}</div>
                        <p>Available positions</p>
                    </div>
                </div>
            </section>

            <div class="dashboard-grid">
                <section class="panel">
                    <div class="panel-header">
                        <h2>Application Timeline</h2>
                        <a href="${pageContext.request.contextPath}/applications" class="btn btn-outline btn-small">View All</a>
                    </div>

                    <c:choose>
                        <c:when test="${empty myApplications}">
                            <div class="empty-state-illustration">
                                <div class="empty-illustration-icon">&#128203;</div>
                                <p class="empty-illustration-title">No applications yet</p>
                                <p class="empty-illustration-desc">Start applying for positions to see your application history here</p>
                                <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary btn-small">Browse Positions</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="timeline-list">
                                <c:forEach var="app" items="${myApplications}">
                                    <div class="timeline-item">
                                        <div class="timeline-time">${app.submittedAt}</div>
                                        <div class="timeline-content">
                                            <h4>${jobTitles[app.jobId]}</h4>
                                            <p><span class="badge ${app.status}">${app.status == 'PENDING' ? 'Pending' : app.status == 'ACCEPTED' ? 'Accepted' : app.status == 'REJECTED' ? 'Rejected' : 'Interview'}</span></p>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </section>

                <section class="panel">
                    <div class="panel-header">
                        <h2>Quick Actions</h2>
                    </div>
                    <div class="quick-actions-grid">
                        <a href="${pageContext.request.contextPath}/jobs" class="quick-action-btn">
                            <span class="qa-icon">&#128269;</span>
                            <span>Browse Positions</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/applications" class="quick-action-btn">
                            <span class="qa-icon">&#128203;</span>
                            <span>My Applications</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/ta/profile" class="quick-action-btn">
                            <span class="qa-icon">&#9998;</span>
                            <span>Edit Profile</span>
                        </a>
                        <a href="${pageContext.request.contextPath}/logout" class="quick-action-btn">
                            <span class="qa-icon">&#128682;</span>
                            <span>Log Out</span>
                        </a>
                    </div>
                </section>

                <section class="panel">
                    <div class="panel-header">
                        <h2>Hot Positions</h2>
                    </div>
                    <div class="hot-jobs-list">
                        <c:forEach var="entry" items="${topJobs}" varStatus="status">
                            <div class="hot-job-item">
                                <span class="hot-job-rank">${status.index + 1}</span>
                                <div class="hot-job-info">
                                    <span class="hot-job-title">${jobTitles[entry.key]}</span>
                                    <span class="hot-job-count">${entry.value} applicants</span>
                                </div>
                            </div>
                        </c:forEach>
                        <c:if test="${empty topJobs}">
                            <div class="empty-state-small">No positions available</div>
                        </c:if>
                    </div>
                </section>

                <section class="panel profile-progress-panel">
                    <div class="panel-header">
                        <h2>Profile Progress</h2>
                        <a href="${pageContext.request.contextPath}/ta/profile" class="btn btn-outline btn-small">Edit Profile</a>
                    </div>
                    <div class="profile-progress-content">
                        <div class="profile-progress-main">
                            <div class="completeness-display">
                                <div class="completeness-circle">
                                    <svg viewBox="0 0 36 36" class="circular-chart">
                                        <path class="circle-bg" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"/>
                                        <path class="circle" stroke-dasharray="${currentUser.major != null && currentUser.skills != null && currentUser.year > 0 ? 85 : 40}, 100" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"/>
                                        <text x="18" y="20.35" class="percentage">${currentUser.major != null && currentUser.skills != null && currentUser.year > 0 ? 85 : 40}%</text>
                                    </svg>
                                </div>
                                <div class="completeness-info">
                                    <h4>Profile Completeness</h4>
                                    <p>
                                        <c:choose>
                                            <c:when test="${currentUser.major != null && currentUser.skills != null && currentUser.year > 0}">
                                                Your profile is well completed! Consider adding more skills for better matches.
                                            </c:when>
                                            <c:otherwise>
                                                Complete your profile (major, year, skills) to improve position matching.
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="profile-progress-skills">
                            <h4>My Skills</h4>
                            <div class="skills-showcase">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.currentUser.skills}">
                                        <c:forEach var="skill" items="${fn:split(sessionScope.currentUser.skills, '|')}">
                                            <span class="skill-tag">${skill}</span>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="empty-state-small">No skills added yet</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </section>
            </div>

            <section class="panel">
                <div class="panel-header">
                    <h2>Recommended For You</h2>
                </div>
                <p class="best-match-msg">${bestMatchMessage}</p>
                <div class="match-actions">
                    <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary">Browse All Positions</a>
                </div>
            </section>
        </div>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

<svg width="0" height="0" style="position: absolute;">
    <defs>
        <linearGradient id="gradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" style="stop-color:#5856d6"/>
            <stop offset="100%" style="stop-color:#af52de"/>
        </linearGradient>
    </defs>
</svg>

