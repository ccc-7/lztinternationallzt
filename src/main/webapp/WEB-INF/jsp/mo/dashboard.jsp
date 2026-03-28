<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "MO Dashboard");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<div class="layout">
    <aside class="sidebar">
        <div class="sidebar-brand">
            <div class="brand-logo">MO</div>
            <div>
                <h3>Module Organiser</h3>
                <p>Recruitment Suite</p>
            </div>
        </div>

        <nav class="sidebar-nav">
            <a class="nav-item active" href="${pageContext.request.contextPath}/mo/dashboard">岗位总览</a>
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/jobs/new">发布岗位</a>
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/applications">申请管理</a>
            <a class="nav-item" href="${pageContext.request.contextPath}/logout">退出登录</a>
        </nav>
    </aside>

    <main class="content">
        <div class="topbar">
            <button class="sidebar-toggle">☰</button>
            <div class="topbar-right">
                <span>${sessionScope.currentUser.name}</span>
                <a href="${pageContext.request.contextPath}/logout">退出登录</a>
            </div>
        </div>

        <section class="panel">
            <h1>MO 工作台</h1>
            <p>发布岗位，筛选申请人，快速完成本学期 TA 招募。</p>
        </section>

        <section class="stats-grid">
            <div class="stat-card">
                <h4>开放岗位</h4>
                <div class="stat-value">${jobCount}</div>
                <p>当前由你负责的岗位数量</p>
            </div>
            <div class="stat-card">
                <h4>待审申请</h4>
                <div class="stat-value">${pendingCount}</div>
                <p>等待你处理的申请数</p>
            </div>
            <div class="stat-card">
                <h4>已录用</h4>
                <div class="stat-value">${acceptedCount}</div>
                <p>已通过筛选的 TA 数量</p>
            </div>
            <div class="stat-card">
                <h4>下一步</h4>
                <div class="stat-value">Review</div>
                <p>建议尽快处理 pending 申请</p>
            </div>
        </section>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>