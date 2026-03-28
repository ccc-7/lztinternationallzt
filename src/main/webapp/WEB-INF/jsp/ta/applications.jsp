<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setAttribute("pageTitle", "申请状态");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<div class="layout">
    <aside class="sidebar">
        <div class="sidebar-brand">
            <div class="brand-logo">TA</div>
            <div>
                <h3>Teaching Assistant</h3>
                <p>Recruitment Suite</p>
            </div>
        </div>

        <nav class="sidebar-nav">
            <a class="nav-item" href="${pageContext.request.contextPath}/ta/dashboard">个人档案</a>
            <a class="nav-item" href="${pageContext.request.contextPath}/jobs">职位大厅</a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/applications">申请状态</a>
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
            <h1>申请状态</h1>
            <p>查看你所有申请的处理进度。</p>
        </section>

        <section class="panel">
            <table class="custom-table">
                <thead>
                <tr>
                    <th>申请编号</th>
                    <th>岗位编号</th>
                    <th>状态</th>
                    <th>提交时间</th>
                    <th>备注</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="a" items="${applications}">
                    <tr>
                        <td>${a.applicationId}</td>
                        <td>${a.jobId}</td>
                        <td><span class="badge">${a.status}</span></td>
                        <td>${a.submittedAt}</td>
                        <td>${a.notes}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </section>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>