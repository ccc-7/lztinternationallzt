<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="edu.bupt.ta.model.User" %>
<%
    request.setAttribute("pageTitle", "TA Dashboard");
    User currentUser = (User) session.getAttribute("currentUser");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<div class="layout">
    <aside class="sidebar">
        <div class="sidebar-brand">
            <div class="brand-logo">TA</div>
            <div>
                <h3>TA Portal</h3>
                <p>Recruitment System</p>
            </div>
        </div>

        <nav class="sidebar-nav">
            <a class="nav-item active" href="${pageContext.request.contextPath}/ta/dashboard">Dashboard</a>
            <a class="nav-item" href="${pageContext.request.contextPath}/jobs">Available Positions</a>
            <a class="nav-item" href="${pageContext.request.contextPath}/applications">My Applications</a>
            <a class="nav-item" href="${pageContext.request.contextPath}/logout">Log out</a>
        </nav>
    </aside>

    <main class="content">
        <div class="topbar">
            <button class="sidebar-toggle">☰</button>
            <div class="topbar-right">
                <span><%= currentUser != null ? currentUser.getDisplayName() : "" %></span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="portal-shell">
            <div class="portal-main">
                <section class="panel portal-header-panel">
                    <h1>Dashboard</h1>
                    <p class="portal-greeting">Hi, ${sessionScope.currentUser.name}!</p>
                </section>

                <section class="panel portal-timeline-panel">
                    <div class="portal-section-title">Application timeline</div>

                    <div class="portal-toolbar">
                        <select>
                            <option>This semester</option>
                            <option>This month</option>
                            <option>All</option>
                        </select>

                        <select>
                            <option>Sort by latest</option>
                            <option>Sort by status</option>
                        </select>

                        <input type="text" placeholder="Search by position title or module code">
                    </div>

                    <div class="timeline-list">
                        <div class="timeline-item">
                            <div class="timeline-time">Now</div>
                            <div class="timeline-content">
                                <h4>Application summary updated</h4>
                                <p>You currently have <strong>${pendingCount}</strong> active application(s) under review or interview.</p>
                            </div>
                            <a class="btn btn-secondary btn-small" href="${pageContext.request.contextPath}/applications">View details</a>
                        </div>

                        <div class="timeline-item">
                            <div class="timeline-time">Match</div>
                            <div class="timeline-content">
                                <h4>Recommended positions</h4>
                                <p>${bestMatchMessage}</p>
                            </div>
                            <a class="btn btn-secondary btn-small" href="${pageContext.request.contextPath}/jobs">Open jobs</a>
                        </div>

                        <div class="timeline-item">
                            <div class="timeline-time">Profile</div>
                            <div class="timeline-content">
                                <h4>Profile readiness</h4>
                                <p>Keep your major, skills and year information updated to improve role matching.</p>
                            </div>
                            <a class="btn btn-secondary btn-small" href="${pageContext.request.contextPath}/ta/dashboard">Review profile</a>
                        </div>
                    </div>
                </section>

                <section class="stats-grid">
                    <div class="stat-card">
                        <h4>Active applications</h4>
                        <div class="stat-value">${pendingCount}</div>
                        <p>Pending or interview-stage applications.</p>
                    </div>

                    <div class="stat-card">
                        <h4>Matched positions</h4>
                        <div class="stat-value">${matchedJobs}</div>
                        <p>Open positions matching your profile.</p>
                    </div>

                    <div class="stat-card">
                        <h4>Next actions</h4>
                        <div class="stat-value">${todoCount}</div>
                        <p>Items that may require your attention.</p>
                    </div>

                    <div class="stat-card">
                        <h4>Profile overview</h4>
                        <div class="ring-score">TA</div>
                        <p>Your profile is visible to module organisers.</p>
                    </div>
                </section>

                <section class="panel">
                    <div class="portal-section-title">My profile</div>
                    <div class="profile-grid">
                        <div><strong>Username:</strong> ${sessionScope.currentUser.username}</div>
                        <div><strong>Name:</strong> ${sessionScope.currentUser.name}</div>
                        <div><strong>Email:</strong> ${sessionScope.currentUser.email}</div>
                        <div><strong>Year:</strong> ${sessionScope.currentUser.year}</div>
                        <div><strong>Major:</strong> ${sessionScope.currentUser.major}</div>
                        <div><strong>Skills:</strong> ${sessionScope.currentUser.skills}</div>
                    </div>
                </section>
            </div>

            <aside class="portal-side">
                <section class="panel portal-side-box">
                    <div class="portal-side-title">Position search</div>
                    <input class="portal-side-input" type="text" placeholder="Search positions">
                    <div class="portal-side-actions">
                        <a class="btn btn-secondary btn-small" href="${pageContext.request.contextPath}/jobs">Search my matches</a>
                        <a class="btn btn-secondary btn-small" href="${pageContext.request.contextPath}/jobs">Search all positions</a>
                    </div>
                </section>

                <section class="panel portal-side-box">
                    <div class="portal-side-title">Recently used</div>
                    <div class="portal-link-list">
                        <a href="${pageContext.request.contextPath}/applications">Application Status</a>
                        <a href="${pageContext.request.contextPath}/jobs">Available Positions</a>
                        <a href="${pageContext.request.contextPath}/ta/dashboard">My Profile</a>
                    </div>
                </section>

                <section class="panel portal-side-box">
                    <div class="portal-side-title">Quick access</div>
                    <div class="portal-link-list">
                        <a href="${pageContext.request.contextPath}/jobs">Browse open positions</a>
                        <a href="${pageContext.request.contextPath}/applications">Check current progress</a>
                        <a href="${pageContext.request.contextPath}/logout">Secure sign out</a>
                    </div>
                </section>
            </aside>
        </div>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>