<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "申请管理");
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
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/dashboard">岗位总览</a>
            <a class="nav-item" href="${pageContext.request.contextPath}/mo/jobs/new">发布岗位</a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/mo/applications">申请管理</a>
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

        <section class="panel dashboard-intro">
            <h1>申请管理</h1>
            <p>浏览所有投递记录，并对申请进行录用或拒绝。</p>
        </section>

        <section class="panel">
            <div class="panel-header">
                <h2>申请人列表</h2>
                <c:if test="${not empty filterJobId}">
                    <a href="${pageContext.request.contextPath}/mo/applications" class="btn btn-secondary btn-small">
                        查看全部
                    </a>
                </c:if>
            </div>
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                    <tr>
                        <th>申请编号</th>
                        <th>申请人</th>
                        <th>岗位</th>
                        <th>可工作时间</th>
                        <th>CV</th>
                        <th>状态</th>
                        <th>提交时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty applications}">
                            <tr>
                                <td colspan="8" class="empty-state">暂无申请记录</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="a" items="${applications}">
                                <tr>
                                    <td><span class="app-id">${a.applicationId}</span></td>
                                    <td>
                                        <div class="user-info">
                                            <span class="user-name">${applicantNames[a.userId]}</span>
                                            <span class="user-id">${a.userId}</span>
                                        </div>
                                    </td>
                                    <td><span class="module-code">${jobTitles[a.jobId]}</span></td>
                                    <td><span class="availability">${a.availability}</span></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/files/cv/${a.userId}" class="btn btn-outline btn-small" target="_blank">
                                            查看简历
                                        </a>
                                    </td>
                                    <td><span class="badge ${a.status}">${statusLabels[a.status]}</span></td>
                                    <td>${a.submittedAt}</td>
                                    <td>
                                        <div class="decision-buttons">
                                            <form action="${pageContext.request.contextPath}/mo/applications/update" method="post" class="decision-form">
                                                <input type="hidden" name="applicationId" value="${a.applicationId}">
                                                <input type="hidden" name="status" value="ACCEPTED">
                                                <input type="hidden" name="filterJobId" value="${filterJobId}">
                                                <button type="submit" class="btn btn-decision btn-accept" ${a.status == 'ACCEPTED' ? 'disabled' : ''}>录用</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/mo/applications/update" method="post" class="decision-form">
                                                <input type="hidden" name="applicationId" value="${a.applicationId}">
                                                <input type="hidden" name="status" value="INTERVIEW">
                                                <input type="hidden" name="filterJobId" value="${filterJobId}">
                                                <button type="submit" class="btn btn-decision btn-interview" ${a.status == 'INTERVIEW' ? 'disabled' : ''}>面试</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/mo/applications/update" method="post" class="decision-form">
                                                <input type="hidden" name="applicationId" value="${a.applicationId}">
                                                <input type="hidden" name="status" value="REJECTED">
                                                <input type="hidden" name="filterJobId" value="${filterJobId}">
                                                <button type="submit" class="btn btn-decision btn-reject" ${a.status == 'REJECTED' ? 'disabled' : ''}>拒绝</button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
