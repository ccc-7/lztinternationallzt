<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CV - ${cvUser.name} - TA Recruitment System</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f7fa;
            color: #333;
            line-height: 1.6;
            padding: 20px;
        }
        .cv-container {
            max-width: 800px;
            margin: 0 auto;
            background: #fff;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            border-radius: 8px;
            overflow: hidden;
        }
        .cv-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            text-align: center;
        }
        .cv-header h1 {
            font-size: 2.5em;
            margin-bottom: 10px;
        }
        .cv-header .user-id {
            font-size: 0.9em;
            opacity: 0.8;
        }
        .cv-header .email {
            margin-top: 15px;
            font-size: 1.1em;
        }
        .cv-body {
            padding: 40px;
        }
        .cv-section {
            margin-bottom: 30px;
        }
        .cv-section h2 {
            color: #667eea;
            font-size: 1.4em;
            border-bottom: 2px solid #667eea;
            padding-bottom: 10px;
            margin-bottom: 20px;
        }
        .info-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
        }
        .info-item {
            padding: 15px;
            background: #f8f9fa;
            border-radius: 6px;
        }
        .info-item label {
            display: block;
            font-weight: 600;
            color: #666;
            font-size: 0.9em;
            margin-bottom: 5px;
        }
        .info-item span {
            font-size: 1.1em;
            color: #333;
        }
        .skills-list {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
        }
        .skill-chip {
            background: #e7e9fc;
            color: #667eea;
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 0.95em;
            font-weight: 500;
        }
        .availability-box {
            background: #f0f9f0;
            border: 1px solid #c3e6cb;
            border-radius: 6px;
            padding: 15px 20px;
        }
        .availability-box label {
            display: block;
            font-weight: 600;
            color: #28a745;
            margin-bottom: 5px;
        }
        .no-availability {
            color: #999;
            font-style: italic;
        }
        .cv-footer {
            text-align: center;
            padding: 20px;
            background: #f8f9fa;
            color: #666;
            font-size: 0.9em;
        }
        .print-btn {
            display: block;
            margin: 20px auto;
            padding: 12px 30px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 1em;
            cursor: pointer;
            transition: background 0.3s;
        }
        .print-btn:hover {
            background: #5568d3;
        }
        @media print {
            body {
                background: white;
                padding: 0;
            }
            .cv-container {
                box-shadow: none;
            }
            .print-btn {
                display: none;
            }
        }
    </style>
</head>
<body>
    <button class="print-btn" onclick="window.print()">Print CV</button>
    
    <div class="cv-container">
        <div class="cv-header">
            <h1>${cvUser.name}</h1>
            <div class="user-id">TA ID: ${cvUser.userId}</div>
            <div class="email">${cvUser.email}</div>
        </div>

        <div class="cv-body">
            <div class="cv-section">
                <h2>Basic Information</h2>
                <div class="info-grid">
                    <div class="info-item">
                        <label>Username</label>
                        <span>${cvUser.username}</span>
                    </div>
                    <div class="info-item">
                        <label>Year</label>
                        <span>Year ${cvUser.year}</span>
                    </div>
                    <div class="info-item">
                        <label>Major</label>
                        <span>${cvUser.major}</span>
                    </div>
                    <div class="info-item">
                        <label>Status</label>
                        <span>${cvUser.status}</span>
                    </div>
                </div>
            </div>

            <div class="cv-section">
                <h2>Skills</h2>
                <div class="skills-list">
                    <c:choose>
                        <c:when test="${not empty cvUser.skills}">
                            <c:forTokens items="${cvUser.skills}" delims="|" var="skill">
                                <span class="skill-chip">${skill}</span>
                            </c:forTokens>
                        </c:when>
                        <c:otherwise>
                            <span class="no-availability">No skills added yet</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="cv-section">
                <h2>Availability</h2>
                <div class="availability-box">
                    <label>Available Time:</label>
                    <c:choose>
                        <c:when test="${not empty cvUser.availability}">
                            <span>${cvUser.availability}</span>
                        </c:when>
                        <c:otherwise>
                            <span class="no-availability">Not specified</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="cv-footer">
            Generated from TA Recruitment System | ${pageContext.request.contextPath}
        </div>
    </div>
</body>
</html>
