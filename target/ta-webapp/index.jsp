<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>TA Recruitment System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: sans-serif; background: #f5f5f5; padding: 2rem; }
        .container { max-width: 800px; margin: 0 auto; text-align: center; }
        h1 { color: #333; margin-bottom: 0.5rem; }
        .subtitle { color: #666; margin-bottom: 2rem; font-size: 0.95rem; }
        .cards { display: flex; gap: 1.5rem; justify-content: center; flex-wrap: wrap; }
        .card { background: #fff; padding: 1.5rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); width: 220px; text-align: left; }
        .card h2 { color: #1976d2; font-size: 1rem; margin-bottom: 0.5rem; }
        .card p { color: #666; font-size: 0.85rem; line-height: 1.4; margin-bottom: 0.75rem; }
        .card a { color: #1976d2; text-decoration: none; font-size: 0.9rem; }
        .card a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="container">
        <h1>BUPT International School</h1>
        <p class="subtitle">TA Recruitment System</p>
        <div class="cards">
            <div class="card">
                <h2>For TA Applicants</h2>
                <p>Register, login, browse jobs, apply, and check application status.</p>
                <a href="#">Login / Register</a>
            </div>
            <div class="card">
                <h2>For Module Organisers</h2>
                <p>Post jobs, view applicants, and manage application status.</p>
                <a href="#">Login</a>
            </div>
            <div class="card">
                <h2>For Admin</h2>
                <p>View workload overview and manage system data.</p>
                <a href="#">Login</a>
            </div>
        </div>
    </div>
</body>
</html>
