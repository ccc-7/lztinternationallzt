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
                <a href="${pageContext.request.contextPath}/ta/profile">${sessionScope.currentUser.username}</a>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <section class="panel">
            <div class="page-header">
                <h1>Job Board</h1>
                <p>Find your ideal teaching assistant position</p>
            </div>
        </section>

        <section class="job-grid-simple">
            <c:forEach var="job" items="${jobs}">
                <div class="job-card-simple ${job.matchScore >= 60 ? 'high-match' : ''}" onclick="showJobDetail('${job.jobId}')">
                    <div class="job-card-header">
                        <div class="job-info">
                            <h3>${job.title}</h3>
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
                        <span class="view-details">View Details →</span>
                    </div>
                </div>
            </c:forEach>
        </section>
    </main>
</div>

<!-- Job Detail Modal -->
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
            <form id="modalApplyForm" action="${pageContext.request.contextPath}/apply" method="post">
                <input type="hidden" name="jobId" id="modalJobId" value="">
                <button type="submit" class="btn btn-primary">Apply Now</button>
            </form>
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
