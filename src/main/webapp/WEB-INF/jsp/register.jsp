<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	request.setAttribute("pageTitle", "TA Register");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<div class="page-center">
	<section class="form-panel">
		<div class="brand-inline">
			<div class="brand-logo">TA</div>
			<div>
				<h2>TA Registration</h2>
				<p>Create your account to apply for positions</p>
			</div>
		</div>

		<form class="grid-form" action="${pageContext.request.contextPath}/register" method="post">
			<div>
				<label for="username">Username</label>
				<input id="username" name="username" type="text" required placeholder="Choose a username">
			</div>

			<div>
				<label for="password">Password</label>
				<input id="password" name="password" type="password" required placeholder="Set a password">
			</div>

			<div>
				<label for="name">Name</label>
				<input id="name" name="name" type="text" required placeholder="Your full name">
			</div>

			<div>
				<label for="email">Email</label>
				<input id="email" name="email" type="email" required placeholder="you@example.com">
			</div>

			<div>
				<label for="year">Year</label>
				<input id="year" name="year" type="number" min="1" max="8" required placeholder="e.g. 2">
			</div>

			<div>
				<label for="major">Major</label>
				<input id="major" name="major" type="text" required placeholder="e.g. Computer Science">
			</div>

			<div class="full-width">
				<label for="skills">Skills</label>
				<textarea id="skills" name="skills" rows="4" placeholder="e.g. Java, Data Structures, SQL"></textarea>
			</div>

			<div class="full-width form-actions">
				<button class="btn btn-primary" type="submit">Create Account</button>
				<a class="btn btn-secondary" href="${pageContext.request.contextPath}/home">Back to Login</a>
			</div>
		</form>
	</section>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
