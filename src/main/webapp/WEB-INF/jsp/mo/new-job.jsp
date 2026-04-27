<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    request.setAttribute("pageTitle", "Post Job");
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
            <a class="nav-item active" href="${pageContext.request.contextPath}/mo/jobs/new">
                <span class="nav-icon">&#9651;</span> Post Job
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/applications">
                <span class="nav-icon">&#9733;</span> Applications
            </a>
        </nav>
    </aside>

    <main class="content">
        <div class="topbar topbar-mo">
            <button type="button" class="sidebar-toggle">&#9776;</button>
            <div class="topbar-title">Post Job</div>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.username}</span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <section class="panel form-panel">
            <div class="form-header">
                <div class="form-icon">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                        <line x1="16" y1="2" x2="16" y2="6"></line>
                        <line x1="8" y1="2" x2="8" y2="6"></line>
                        <line x1="3" y1="10" x2="21" y2="10"></line>
                        <line x1="12" y1="14" x2="12" y2="18"></line>
                        <line x1="10" y1="16" x2="14" y2="16"></line>
                    </svg>
                </div>
                <div class="form-title-area">
                    <h1>Post New Job</h1>
                    <p class="form-subtitle">Create a new TA position listing for students</p>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/mo/jobs/new" method="post" class="job-form">
                <div class="form-section">
                    <h3 class="section-title">
                        <span class="section-icon">&#128203;</span>
                        Basic Information
                    </h3>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="title">Job Title <span class="required">*</span></label>
                            <input type="text" id="title" name="title" required placeholder="e.g. Software Engineering TA">
                        </div>
                        <div class="form-group">
                            <label for="moduleCode">Module Code <span class="required">*</span></label>
                            <input type="text" id="moduleCode" name="moduleCode" required placeholder="e.g. EBU6304">
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="organiser">Instructor Name</label>
                            <input type="text" id="organiser" name="organiser" placeholder="Leave blank to use your name">
                        </div>
                        <div class="form-group">
                            <label for="vacancies">Vacancies <span class="required">*</span></label>
                            <input type="number" id="vacancies" name="vacancies" min="1" required placeholder="e.g. 2">
                        </div>
                    </div>
                </div>

                <div class="form-section">
                    <h3 class="section-title">
                        <span class="section-icon">&#9200;</span>
                        Time & Workload
                    </h3>
                    <div class="form-row three-col">
                        <div class="form-group">
                            <label for="hours">Total Hours <span class="required">*</span></label>
                            <input type="number" id="hours" name="hours" min="1" required placeholder="e.g. 20">
                        </div>
                        <div class="form-group">
                            <label for="minYear">Min Year <span class="required">*</span></label>
                            <select id="minYear" name="minYear" required>
                                <option value="">Select</option>
                                <option value="1">Year 1</option>
                                <option value="2">Year 2</option>
                                <option value="3">Year 3</option>
                                <option value="4">Year 4</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="maxYear">Max Year <span class="required">*</span></label>
                            <select id="maxYear" name="maxYear" required>
                                <option value="">Select</option>
                                <option value="1">Year 1</option>
                                <option value="2">Year 2</option>
                                <option value="3">Year 3</option>
                                <option value="4">Year 4</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="deadline">Application Deadline <span class="required">*</span></label>
                            <input type="date" id="deadline" name="deadline" required>
                        </div>
                    </div>
                </div>

                <div class="form-section">
                    <h3 class="section-title">
                        <span class="section-icon">&#9872;</span>
                        Requirements
                    </h3>
                    <div class="form-group">
                        <label for="requiredSkills">Required Skills</label>
                        <input type="text" id="requiredSkills" name="requiredSkills" placeholder="Java, Python, Teamwork (separate with comma)">
                        <span class="hint">Separate skills with comma or vertical bar</span>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary btn-large">
                        <span class="btn-icon">&#10003;</span>
                        Post Job
                    </button>
                    <a href="${pageContext.request.contextPath}/mo/dashboard" class="btn btn-secondary btn-large">
                        Cancel
                    </a>
                </div>
            </form>
        </section>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
