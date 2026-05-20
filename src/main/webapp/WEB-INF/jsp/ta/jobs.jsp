<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "Job Board");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<style>
html {
    overflow-y: scroll !important;
}
.job-filter-bar input {
    height: 38px;
    box-sizing: border-box;
    padding: 0 10px;
    vertical-align: middle;
}
.job-filter-bar .btn {
    height: 38px;
    vertical-align: middle;
}

.job-results-panel {
    margin-top: 20px;
}

.job-results-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    flex-wrap: wrap;
}

.job-results-scroll {
    max-height: calc(100vh - 300px);
    overflow-y: auto;
    padding-right: 8px;
}

.job-results-scroll::-webkit-scrollbar {
    width: 10px;
}

.job-results-scroll::-webkit-scrollbar-thumb {
    background: #d3dceb;
    border-radius: 999px;
}

.job-results-meta {
    font-size: 0.875rem;
    color: var(--text-tertiary);
}

.job-empty-state {
    padding: 36px 20px;
    border: 1px dashed var(--border-color);
    border-radius: var(--radius-lg);
    background: #fafcff;
    text-align: center;
    color: var(--text-secondary);
}

.closed-notice {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 10px 16px;
    background: #fef2f2;
    color: #dc2626;
    border-radius: var(--radius);
    font-weight: 500;
    font-size: 0.875rem;
}
</style>

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
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
                </span> Dashboard
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/jobs">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><line x1="12" y1="12" x2="12" y2="12"/></svg>
                </span> Job Board
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/applications">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                </span> Applications
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/ta/profile">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                </span> My Profile
            </a>
        </nav>
    </aside>

    <main class="content content-ta">
        <div class="topbar topbar-ta">
            <button type="button" class="sidebar-toggle">
                <svg viewBox="0 0 24 24"><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
            </button>
            <div class="topbar-title">Available Positions</div>
            <div class="topbar-right">
                <a class="user-name" href="${pageContext.request.contextPath}/ta/profile">${sessionScope.currentUser.username}</a>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="ta-content">
            <section class="panel">
                <div class="page-header">
                    <h1>Job Board</h1>
                    <p>Find your ideal teaching assistant position</p>
                </div>

                <form class="job-filter-bar" action="${pageContext.request.contextPath}/jobs" method="get">
                    <input type="text" name="search" value="${search}" placeholder="Search keyword">
                    <input type="text" name="moduleCode" value="${moduleCode}" placeholder="Module Code">
                    <input type="number" name="minMatchScore" min="0" max="100" value="${minMatchScore}" placeholder="Min Match Score">
                    <button type="submit" class="btn btn-primary">Apply Filters</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/jobs">Clear</a>
                </form>
            </section>

            <section class="panel job-results-panel">
                <div class="job-results-header">
                    <h2>Available Jobs</h2>
                    <span class="job-results-meta">${jobsCount} position(s) shown</span>
                </div>
                <div class="job-results-scroll">
                    <c:choose>
                        <c:when test="${empty jobs}">
                            <div class="job-empty-state">
                                No jobs match the current filters. Try clearing filters or lowering the match score threshold.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <section class="job-grid-simple">
                            <c:forEach var="job" items="${jobs}">
                                <div class="job-card-simple ${job.matchScore >= 60 ? 'high-match' : ''}"
                                     role="button"
                                     tabindex="0"
                                     aria-labelledby="job-card-title-${job.jobId}"
                                     onclick="showJobDetail('${job.jobId}')"
                                     onkeydown="if(event.key==='Enter'||event.key===' '){event.preventDefault();showJobDetail('${job.jobId}');}">
                                    <div class="job-card-header">
                                        <div class="job-info">
                                            <h3 id="job-card-title-${job.jobId}">${job.title}</h3>
                                            <span class="module-code">${job.moduleCode}</span>
                                        </div>
                                        <span class="badge badge-${job.status == 'OPEN' ? 'green' : 'gray'}">${job.status}</span>
                                    </div>
                                    <div class="job-card-body">
                                        <span class="organiser">${job.organiser}</span>
                                    </div>
                                    <div class="job-card-footer">
                                        <div class="match-indicator match-${job.matchScore >= 60 ? 'high' : job.matchScore >= 40 ? 'medium' : 'low'}">
                                            <span class="match-number">${job.matchScore}</span>
                                            <span class="match-text">match</span>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                            </section>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </div>
    </main>
</div>

<div id="jobDetailModal" class="job-modal-overlay" onclick="closeModal(event)">
    <div class="job-detail-modal" onclick="event.stopPropagation()">
        <button class="job-modal-close-btn" onclick="closeModal()">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
        </button>
        
        <div class="job-detail-content">
            <div class="job-detail-header">
                <div class="job-detail-title-row">
                    <h2 id="modalJobTitle">Job Title</h2>
                    <span id="modalJobStatus" class="badge badge-green">OPEN</span>
                </div>
                <p id="modalModuleCode" class="job-detail-module">Module Code</p>
            </div>

            <div class="job-detail-info-grid">
                <div class="job-info-item">
                    <span class="job-info-label">Instructor</span>
                    <span id="modalOrganiser" class="job-info-value">Organiser</span>
                </div>
                <div class="job-info-item">
                    <span class="job-info-label">Workload</span>
                    <span id="modalHours" class="job-info-value">Hours</span>
                </div>
                <div class="job-info-item">
                    <span class="job-info-label">Year Range</span>
                    <span id="modalYearReq" class="job-info-value">Year Range</span>
                </div>
                <div class="job-info-item">
                    <span class="job-info-label">Vacancies</span>
                    <span id="modalVacancies" class="job-info-value">Vacancies</span>
                </div>
                <div class="job-info-item deadline">
                    <span class="job-info-label">Deadline</span>
                    <span id="modalDeadline" class="job-info-value">Deadline</span>
                </div>
            </div>

            <div class="job-detail-section">
                <h4>Required Skills</h4>
                <div id="modalSkills" class="job-skills-list"></div>
            </div>

            <div class="job-detail-match">
                <div class="job-match-circle match-high">
                    <span id="modalMatchScore" class="job-match-big">85%</span>
                    <span class="job-match-text">Your Match</span>
                </div>
            </div>
        </div>

        <div class="job-detail-actions">
            <button class="btn btn-secondary" onclick="closeModal()">Close</button>
            <form id="modalApplyForm" action="${pageContext.request.contextPath}/apply" method="post" style="display:none;">
                <input type="hidden" name="jobId" id="modalJobId" value="">
                <button type="submit" class="btn btn-primary">Apply Now</button>
            </form>
            <div id="modalClosedNotice" class="closed-notice" style="display:none;">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <line x1="15" y1="9" x2="9" y2="15"></line>
                    <line x1="9" y1="9" x2="15" y2="15"></line>
                </svg>
                Applications Closed
            </div>
        </div>
    </div>
</div>

<script>
function jsStr(s) {
    if (s == null) return '';
    return String(s)
        .replace(/\\/g, '\\\\')
        .replace(/\n/g, '\\n')
        .replace(/'/g, "\\'");
}

const jobData = {
<c:forEach var="job" items="${jobs}" varStatus="status">
    '${job.jobId}': {
        title: jsStr('${job.title}'),
        status: '${job.status}',
        moduleCode: jsStr('${job.moduleCode}'),
        organiser: jsStr('${job.organiser}'),
        hours: '${job.hours}',
        deadline: '${job.deadline}',
        minYear: '${job.minYear}',
        maxYear: '${job.maxYear}',
        vacancies: '${job.vacancies}',
        skills: jsStr('${job.requiredSkills}'),
        matchScore: '${job.matchScore}'
    }<c:if test="${!status.last}">,</c:if>
</c:forEach>
};

function showJobDetail(jobId) {
    const job = jobData[jobId];
    if (!job) return;

    document.getElementById('modalJobTitle').textContent = job.title;
    document.getElementById('modalJobStatus').textContent = job.status;
    document.getElementById('modalJobStatus').className = 'badge badge-' + (job.status === 'OPEN' ? 'green' : 'gray');
    document.getElementById('modalModuleCode').textContent = job.moduleCode;
    document.getElementById('modalOrganiser').textContent = job.organiser;
    document.getElementById('modalHours').textContent = job.hours + ' hours';
    document.getElementById('modalDeadline').textContent = job.deadline;
    document.getElementById('modalYearReq').textContent = 'Year ' + job.minYear + ' - ' + job.maxYear;
    document.getElementById('modalVacancies').textContent = job.vacancies;
    document.getElementById('modalJobId').value = jobId;
    document.getElementById('modalMatchScore').textContent = job.matchScore + '%';

    // Show/hide apply button based on job status
    const applyForm = document.getElementById('modalApplyForm');
    const closedNotice = document.getElementById('modalClosedNotice');
    if (job.status === 'OPEN') {
        applyForm.style.display = 'inline-block';
        closedNotice.style.display = 'none';
    } else {
        applyForm.style.display = 'none';
        closedNotice.style.display = 'flex';
    }

    const skillsContainer = document.getElementById('modalSkills');
    const skills = job.skills.split('|').filter(s => s.trim());
    skillsContainer.innerHTML = skills.map(skill =>
        '<span class="job-skill-chip">' + skill.trim() + '</span>'
    ).join('');

    const matchScore = parseInt(job.matchScore);
    const matchCircle = document.querySelector('.job-match-circle');
    matchCircle.className = 'job-match-circle match-' + (matchScore >= 60 ? 'high' : 'medium');

    document.getElementById('jobDetailModal').classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeModal(event) {
    if (event && event.target !== event.currentTarget) return;
    document.getElementById('jobDetailModal').classList.remove('active');
    document.body.style.overflow = '';
}

document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') closeModal();
});
</script>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
