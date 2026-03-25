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

        <section class="panel">
            <h1>申请管理</h1>
            <p>浏览所有投递记录，并对申请进行录用或拒绝。</p>
        </section>

        <section class="panel">
            <table class="custom-table">
                <thead>
                <tr>
                    <th>申请编号</th>
                    <th>用户编号</th>
                    <th>岗位编号</th>
                    <th>状态</th>
                    <th>提交时间</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="a" items="${applications}">
                    <tr>
                        <td>${a.applicationId}</td>
                        <td>${a.userId}</td>
                        <td>${a.jobId}</td>
                        <td><span class="badge">${a.status}</span></td>
                        <td>${a.submittedAt}</td>
                        <td>
                            <div class="table-actions">
                                <form action="${pageContext.request.contextPath}/mo/applications/update" method="post">
                                    <input type="hidden" name="applicationId" value="${a.applicationId}">
                                    <input type="hidden" name="status" value="ACCEPTED">
                                    <button type="submit" class="btn btn-small btn-primary">录用</button>
                                </form>

                                <form action="${pageContext.request.contextPath}/mo/applications/update" method="post">
                                    <input type="hidden" name="applicationId" value="${a.applicationId}">
                                    <input type="hidden" name="status" value="REJECTED">
                                    <button type="submit" class="btn btn-small btn-danger">拒绝</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </section>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>