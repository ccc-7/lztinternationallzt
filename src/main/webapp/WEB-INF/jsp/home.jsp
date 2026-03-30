<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	request.setAttribute("pageTitle", "TA Recruitment Login");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<div class="hero-bg">
	<div class="glass-card login-shell">
		<div class="login-top">
			<div class="brand">
				<div class="brand-logo">TA</div>
				<div>
					<h1>BUPT Recruitment</h1>
					<p>Teaching Assistant Portal</p>
				</div>
			</div>
			<div class="theme-pill">Session Login</div>
		</div>

		<div class="role-tabs">
			<button type="button" class="role-tab active" data-tab="TA">TA</button>
			<button type="button" class="role-tab" data-tab="MO">MO</button>
			<button type="button" class="role-tab" data-tab="ADMIN">Admin</button>
		</div>

		<form class="login-form" action="${pageContext.request.contextPath}/login" method="post">
			<input type="hidden" id="roleInput" name="role" value="TA">

			<label for="username">Username</label>
			<input id="username" name="username" type="text" required placeholder="Enter username">

			<label for="password">Password</label>
			<input id="password" name="password" type="password" required placeholder="Enter password">

			<div class="login-actions">
				<button class="btn btn-primary" type="submit">Sign In</button>
				<a class="btn btn-secondary" href="${pageContext.request.contextPath}/register">TA Register</a>
			</div>
		</form>

		<p class="hint-text">Test accounts: TA seele/123456, MO mo1/123456, Admin admin/123456.</p>
	</div>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
