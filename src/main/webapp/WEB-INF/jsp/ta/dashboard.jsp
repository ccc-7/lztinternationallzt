<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="edu.bupt.ta.model.User" %>
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
                <span class="nav-icon">&#9632;</span> 工作台
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/jobs">
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
            <div class="topbar-title">TA Dashboard</div>
            <div class="topbar-right">
                <div class="user-menu">
                    <a class="user-name" href="${pageContext.request.contextPath}/ta/profile"><%= currentUser != null ? currentUser.getDisplayName() : "" %></a>
                </div>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="ta-dashboard">
            <div class="portal-shell">
                <div class="portal-main">
                    <section class="panel portal-header-panel">
                        <h1>Dashboard</h1>
                        <p class="portal-greeting">Hi, ${sessionScope.currentUser.name}!</p>
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
                            <div class="stat-icon">&#9651;</div>
                            <div class="stat-content">
                                <h4>Total Open</h4>
                                <div class="stat-value">${totalOpenPositions}</div>
                                <p>Available positions</p>
                            </div>
                        </div>
                    </section>

                    <section class="panel portal-timeline-panel">
                        <div class="portal-section-title">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <polyline points="12 6 12 12 16 14"></polyline>
                            </svg>
                            Application Timeline
                        </div>

                        <c:choose>
                            <c:when test="${empty myApplications}">
                                <div class="empty-state-panel">
                                    <span class="empty-icon-large">&#128203;</span>
                                    <p>No applications yet</p>
                                    <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary btn-small">Browse Positions</a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="timeline-list">
                                    <c:forEach var="app" items="${myApplications}">
                                        <div class="timeline-item">
                                            <div class="timeline-time">${app.submittedAt}</div>
                                            <div class="timeline-content">
                                                <h4>Application: ${jobTitles[app.jobId]}</h4>
                                                <p>Status: <span class="badge ${app.status}">${app.status == 'PENDING' ? 'Pending' : app.status == 'ACCEPTED' ? 'Accepted' : app.status == 'REJECTED' ? 'Rejected' : 'Interview'}</span></p>
                                            </div>
                                            <a class="btn btn-secondary btn-small" href="${pageContext.request.contextPath}/applications">Details</a>
                                        </div>
                                    </c:forEach>
                                </div>
                                <div class="timeline-footer">
                                    <a href="${pageContext.request.contextPath}/applications" class="link-more">View all applications &rarr;</a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <section class="panel">
                        <div class="portal-section-title">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                            </svg>
                            Recommended For You
                        </div>
                        <p class="best-match-msg">${bestMatchMessage}</p>
                        <div class="match-actions">
                            <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary btn-small">Browse All Matches</a>
                        </div>
                    </section>

                </div>

                <aside class="portal-side">
                    <section class="panel portal-side-box">
                        <div class="portal-side-title">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"></polygon>
                            </svg>
                            Quick Actions
                        </div>
                        <div class="quick-action-grid">
                            <a href="${pageContext.request.contextPath}/jobs" class="quick-action-card">
                                <span class="qa-icon">&#128269;</span>
                                <span class="qa-text">Apply for Position</span>
                            </a>
                            <a href="${pageContext.request.contextPath}/applications" class="quick-action-card">
                                <span class="qa-icon">&#128203;</span>
                                <span class="qa-text">Check Applications</span>
                            </a>
                            <a href="${pageContext.request.contextPath}/jobs" class="quick-action-card">
                                <span class="qa-icon">&#128640;</span>
                                <span class="qa-text">View Matches</span>
                            </a>
                        </div>
                    </section>

                    <section class="panel portal-side-box">
                        <div class="portal-side-title">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M12 8v4l3 3"></path>
                                <circle cx="12" cy="12" r="10"></circle>
                            </svg>
                            Recently Used
                        </div>
                        <div class="portal-link-list" data-recent-container></div>
                    </section>

                    <section class="panel portal-side-box">
                        <div class="portal-side-title">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <line x1="18" y1="20" x2="18" y2="10"></line>
                                <line x1="12" y1="20" x2="12" y2="4"></line>
                                <line x1="6" y1="20" x2="6" y2="14"></line>
                            </svg>
                            Hot Positions
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
                                <div class="empty-state-small">No data available</div>
                            </c:if>
                        </div>
                    </section>

                    <section class="panel portal-side-box">
                        <div class="portal-side-title">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="12" y1="16" x2="12" y2="12"></line>
                                <line x1="12" y1="8" x2="12.01" y2="8"></line>
                            </svg>
                            Profile Completeness
                        </div>
                        <div class="completeness-bar">
                            <div class="completeness-fill" style="width: ${currentUser.major != null && currentUser.skills != null && currentUser.year > 0 ? 80 : 50}%"></div>
                        </div>
                        <div class="completeness-text">
                            <c:choose>
                                <c:when test="${currentUser.major != null && currentUser.skills != null && currentUser.year > 0}">
                                    Your profile is nearly complete! Add more skills for better matches.
                                </c:when>
                                <c:otherwise>
                                    Complete your profile (major, year, skills) to improve position matching.
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </section>
                </aside>
            </div>
        </div>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

