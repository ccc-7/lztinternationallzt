<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    request.setAttribute("pageTitle", "发布岗位");
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
                <span class="nav-icon">&#9632;</span> 工作台
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/mo/jobs/new">
                <span class="nav-icon">&#9651;</span> 发布岗位
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/applications">
                <span class="nav-icon">&#9733;</span> 申请管理
            </a>
        </nav>
    </aside>

    <main class="content">
        <div class="topbar topbar-mo">
            <button class="sidebar-toggle">☰</button>
            <div class="topbar-title">发布岗位</div>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.name}</span>
                <a href="${pageContext.request.contextPath}/logout">退出登录</a>
            </div>
        </div>

        <section class="panel form-panel">
            <h1>发布新岗位</h1>

            <form action="${pageContext.request.contextPath}/mo/jobs/new" method="post" class="grid-form">
                <div>
                    <label>岗位标题</label>
                    <input type="text" name="title" required>
                </div>

                <div>
                    <label>课程代码</label>
                    <input type="text" name="moduleCode" required>
                </div>

                <div>
                    <label>教师姓名</label>
                    <input type="text" name="organiser" required>
                </div>

                <div>
                    <label>总工时</label>
                    <input type="number" name="hours" min="1" required>
                </div>

                <div>
                    <label>最低年级</label>
                    <input type="number" name="minYear" min="1" max="8" required>
                </div>

                <div>
                    <label>最高年级</label>
                    <input type="number" name="maxYear" min="1" max="8" required>
                </div>

                <div>
                    <label>招聘人数</label>
                    <input type="number" name="vacancies" min="1" placeholder="例如：2" required>
                </div>

                <div>
                    <label>申请截止日期</label>
                    <input type="date" name="deadline" required>
                </div>

                <div class="full-width">
                    <label>技能要求</label>
                    <input type="text" name="requiredSkills" placeholder="Java, Python, Communication">
                </div>

                <div class="full-width form-actions">
                    <button type="submit" class="btn btn-primary">发布岗位</button>
                    <a href="${pageContext.request.contextPath}/mo/dashboard" class="btn btn-secondary">返回</a>
                </div>
            </form>
        </section>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>