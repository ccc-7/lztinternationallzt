<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "My Profile");
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
                <span class="nav-icon">&#9632;</span> 工作台
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/jobs">
                <span class="nav-icon">&#9651;</span> 职位大厅
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/applications">
                <span class="nav-icon">&#9733;</span> 申请状态
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/ta/profile">
                <span class="nav-icon">&#9679;</span> My Profile
            </a>
        </nav>
    </aside>

    <main class="content content-ta">
        <div class="topbar topbar-ta">
            <button type="button" class="sidebar-toggle">&#9776;</button>
            <div class="topbar-title">My Profile</div>
            <div class="topbar-right">
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="admin-content">
            <section class="panel dashboard-intro">
                <h1>My Profile</h1>
                <p>完善个人信息以获得更准确的岗位匹配，并将信息写入 CSV 存档。</p>
            </section>

            <section class="panel">
                <form action="${pageContext.request.contextPath}/ta/profile" method="post" class="grid-form">
                    <div>
                        <label>Username</label>
                        <input type="text" value="${profileUser.username}" disabled>
                    </div>

                    <div>
                        <label>Name</label>
                        <input type="text" name="name" value="${profileUser.name}" placeholder="Your full name">
                    </div>

                    <div>
                        <label>Email</label>
                        <input type="email" name="email" value="${profileUser.email}" placeholder="you@example.com">
                    </div>

                    <div>
                        <label>Year</label>
                        <input type="number" name="year" min="1" max="8" value="${profileUser.year}" placeholder="e.g. 2">
                    </div>

                    <div>
                        <label>Major</label>
                        <input type="text" name="major" value="${profileUser.major}" placeholder="e.g. Computer Science">
                    </div>

                    <div>
                        <label>Availability</label>
                        <input type="text" name="availability" value="${profileUser.availability}" placeholder="e.g. Mon/Wed afternoons">
                    </div>

                    <div class="full-width">
                        <label>Skills</label>
                        <textarea name="skills" rows="4" placeholder="e.g. Java, SQL, Data Structures">${profileUser.skills}</textarea>
                        <p class="hint-text" style="text-align:left; margin-top: 8px;">Skills 会自动规范化保存到 CSV（用 “|” 分隔）。</p>
                    </div>

                    <div class="full-width form-actions">
                        <button type="submit" class="btn btn-primary">Save</button>
                        <a href="${pageContext.request.contextPath}/ta/dashboard" class="btn btn-secondary">Back</a>
                    </div>
                </form>
            </section>
        </div>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

