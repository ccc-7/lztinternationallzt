<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
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
                <a href="${pageContext.request.contextPath}/ta/profile">${sessionScope.currentUser.username}</a>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <section class="panel">
            <div class="section-header">
                <div>
                    <h1>Job Board</h1>
                    <p class="section-subtitle">Browse open positions and view system-recommended matches.</p>
                </div>
                <div class="match-legend">
                    <span class="legend-item"><span class="legend-dot high"></span>High Match (60%+)</span>
                    <span class="legend-item"><span class="legend-dot medium"></span>Medium Match</span>
                </div>
            </div>
        </section>

        <section class="job-grid">
            <c:forEach var="job" items="${jobs}">
                <div class="job-card ${job.matchScore >= 60 ? 'high-match' : ''}">
                    <div class="job-top">
                        <div class="job-title-row">
                            <h3>${job.title}</h3>
                            <span class="badge">${job.status}</span>
                        </div>
                        <div class="job-meta">
                            <span class="meta-item">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                    <circle cx="12" cy="7" r="4"></circle>
                                </svg>
                                ${job.organiser}
                            </span>
                            <span class="meta-item">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                    <line x1="16" y1="2" x2="16" y2="6"></line>
                                    <line x1="8" y1="2" x2="8" y2="6"></line>
                                    <line x1="3" y1="10" x2="21" y2="10"></line>
                                </svg>
                                ${job.moduleCode}
                            </span>
                        </div>
                    </div>

                    <div class="job-stats">
                        <div class="stat">
                            <span class="stat-label">Hours</span>
                            <span class="stat-value">${job.hours}h</span>
                        </div>
                        <div class="stat">
                            <span class="stat-label">Years</span>
                            <span class="stat-value">${job.minYear}-${job.maxYear}</span>
                        </div>
                        <div class="stat">
                            <span class="stat-label">Slots</span>
                            <span class="stat-value">${job.vacancies}</span>
                        </div>
                    </div>

                    <div class="job-skills">
                        <span class="skills-label">Required Skills:</span>
                        <div class="skills-tags">
                            <c:forEach var="skill" items="${fn:split(job.requiredSkills, '|')}">
                                <span class="skill-tag">${skill}</span>
                            </c:forEach>
                        </div>
                    </div>

                    <c:if test="${not empty job.description}">
                        <div class="job-description">
                            <span class="description-label">Responsibilities:</span>
                            <p class="description-text">${job.description}</p>
                        </div>
                    </c:if>

                    <div class="job-footer">
                        <div class="match-score ${job.matchScore >= 60 ? 'high' : job.matchScore >= 40 ? 'medium' : 'low'}">
                            <span class="match-label">AI Match</span>
                            <span class="match-value">${job.matchScore}%</span>
                        </div>
                        <form action="${pageContext.request.contextPath}/apply" method="post" class="apply-form">
                            <input type="hidden" name="jobId" value="${job.jobId}">
                            <button type="submit" class="btn btn-primary">Apply</button>
                        </form>
                    </div>
                </div>
            </c:forEach>
        </section>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

