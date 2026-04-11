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
            <button class="sidebar-toggle">☰</button>
            <div class="topbar-title">Post Job</div>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.name}</span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <section class="panel form-panel">
            <h1>Post New Job</h1>

            <form action="${pageContext.request.contextPath}/mo/jobs/new" method="post" class="grid-form">
                <div>
                    <label>Job Title</label>
                    <input type="text" name="title" required>
                </div>

                <div>
                    <label>Module Code</label>
                    <input type="text" name="moduleCode" required>
                </div>

                <div>
                    <label>Instructor Name</label>
                    <input type="text" name="organiser" required>
                </div>

                <div>
                    <label>Total Hours</label>
                    <input type="number" name="hours" min="1" required>
                </div>

                <div>
                    <label>Minimum Year</label>
                    <input type="number" name="minYear" min="1" max="8" required>
                </div>

                <div>
                    <label>Maximum Year</label>
                    <input type="number" name="maxYear" min="1" max="8" required>
                </div>

                <div>
                    <label>Vacancies</label>
                    <input type="number" name="vacancies" min="1" placeholder="e.g. 2" required>
                </div>

                <div>
                    <label>Application Deadline</label>
                    <input type="date" name="deadline" required>
                </div>

                <div class="full-width">
                    <label>Required Skills</label>
                    <input type="text" name="requiredSkills" placeholder="Java, Python, Communication">
                </div>

                <div class="full-width form-actions">
                    <button type="submit" class="btn btn-primary">Post Job</button>
                    <a href="${pageContext.request.contextPath}/mo/dashboard" class="btn btn-secondary">Back</a>
                </div>
            </form>
        </section>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
