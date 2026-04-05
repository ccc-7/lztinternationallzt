<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	request.setAttribute("pageTitle", "Sign In");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} — TA Recruitment</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        body { display: flex; align-items: center; justify-content: center; min-height: 100vh; }
    </style>
</head>
<body>

<div class="hero-bg">
    <div class="glass-card">
        <div class="login-top">
            <div class="brand">
                <div class="brand-logo" style="background: linear-gradient(135deg, #5856d6, #af52de);">TA</div>
                <div>
                    <h1>BUPT Recruitment</h1>
                    <p>Teaching Assistant Portal</p>
                </div>
            </div>
        </div>

        <div class="role-tabs">
            <button type="button" class="role-tab active" data-tab="TA">TA</button>
            <button type="button" class="role-tab" data-tab="MO">MO</button>
            <button type="button" class="role-tab" data-tab="ADMIN">Admin</button>
        </div>

        <form class="login-form" action="${pageContext.request.contextPath}/login" method="post">
            <input type="hidden" id="roleInput" name="role" value="TA">

            <label for="username">Username</label>
            <input id="username" name="username" type="text" required placeholder="Enter your username" autocomplete="username">

            <label for="password">Password</label>
            <input id="password" name="password" type="password" required placeholder="Enter your password" autocomplete="current-password">

            <div class="login-actions">
                <button class="btn btn-primary full-btn" type="submit">Sign In</button>
            </div>
        </form>

        <div style="margin-top: 16px;">
            <a href="${pageContext.request.contextPath}/register" class="btn btn-secondary full-btn" style="display: flex; justify-content: center;">
                Create TA Account
            </a>
        </div>

        <p class="hint-text">Demo accounts — TA: seele / 123456 · MO: mo1 / 123456 · Admin: admin / 123456</p>
    </div>
</div>

<script>
document.querySelectorAll('.role-tab').forEach(function(tab) {
    tab.addEventListener('click', function() {
        document.querySelectorAll('.role-tab').forEach(function(t) { t.classList.remove('active'); });
        tab.classList.add('active');
        document.getElementById('roleInput').value = tab.dataset.tab;
    });
});
</script>

</body>
</html>
