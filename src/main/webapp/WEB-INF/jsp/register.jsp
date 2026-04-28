<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	request.setAttribute("pageTitle", "Create Account");
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
        * {
            box-sizing: border-box;
        }

        .reg-page {
            min-height: 100vh;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
            position: relative;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px 20px;
            overflow-x: hidden;
        }

        .reg-page::before {
            content: "";
            position: absolute;
            inset: 0;
            background: 
                radial-gradient(ellipse at 0% 0%, rgba(255, 255, 255, 0.15) 0%, transparent 50%),
                radial-gradient(ellipse at 100% 100%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
            animation: gradientMove 15s ease infinite alternate;
        }

        @keyframes gradientMove {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }

        .particles {
            position: absolute;
            inset: 0;
            overflow: hidden;
            pointer-events: none;
        }

        .particle {
            position: absolute;
            width: 10px;
            height: 10px;
            background: rgba(255, 255, 255, 0.3);
            border-radius: 50%;
            animation: float-particle 15s ease-in-out infinite;
        }

        @keyframes float-particle {
            0%, 100% { transform: translateY(0) translateX(0); opacity: 0; }
            10% { opacity: 1; }
            90% { opacity: 1; }
            100% { transform: translateY(-100vh) translateX(50px); opacity: 0; }
        }

        .reg-container {
            position: relative;
            z-index: 1;
            width: 100%;
            max-width: 520px;
        }

        .reg-header {
            text-align: center;
            margin-bottom: 30px;
            color: #fff;
        }

        .reg-header h1 {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 8px;
            text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
        }

        .header-decoration {
            display: flex;
            justify-content: center;
            gap: 16px;
            margin-bottom: 16px;
        }

        .header-decoration .deco-icon {
            font-size: 2rem;
            animation: bounce 2s ease-in-out infinite;
        }

        .header-decoration .deco-icon:nth-child(1) { animation-delay: 0s; }
        .header-decoration .deco-icon:nth-child(2) { animation-delay: 0.3s; }
        .header-decoration .deco-icon:nth-child(3) { animation-delay: 0.6s; }

        @keyframes bounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-10px); }
        }

        .reg-header * {
            position: relative;
            z-index: 1;
        }

        .reg-header p {
            font-size: 1rem;
            opacity: 0.9;
        }

        .reg-card {
            background: #fff;
            border-radius: 24px;
            box-shadow: 
                0 25px 50px -12px rgba(0, 0, 0, 0.25),
                0 0 0 1px rgba(255, 255, 255, 0.1);
            overflow: hidden;
            border: 3px solid rgba(255, 255, 255, 0.3);
        }

        .progress-container {
            padding: 24px 32px 0;
        }

        .progress-bar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: relative;
        }

        .progress-bar::before {
            content: "";
            position: absolute;
            top: 50%;
            left: 24px;
            right: 24px;
            height: 3px;
            background: #e5e5ea;
            transform: translateY(-50%);
            border-radius: 2px;
        }

        .progress-line {
            position: absolute;
            top: 50%;
            left: 24px;
            height: 3px;
            background: linear-gradient(90deg, #667eea, #764ba2);
            transform: translateY(-50%);
            border-radius: 2px;
            transition: width 0.5s ease;
            width: 0%;
        }

        .progress-step {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: #fff;
            border: 3px solid #e5e5ea;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            font-size: 0.9rem;
            color: #86868b;
            transition: all 0.4s ease;
            position: relative;
            z-index: 1;
            cursor: pointer;
        }

        .progress-step.active {
            border-color: #667eea;
            color: #667eea;
            transform: scale(1.1);
            box-shadow: 0 0 0 6px rgba(102, 126, 234, 0.2);
        }

        .progress-step.completed {
            background: linear-gradient(135deg, #667eea, #764ba2);
            border-color: #667eea;
            color: #fff;
        }

        .progress-step.completed::after {
            content: "✓";
            font-size: 1rem;
        }

        .progress-step.completed span {
            display: none;
        }

        .form-container {
            padding: 32px;
        }

        .step-section {
            display: none;
            animation: slideIn 0.4s ease;
        }

        .step-section.active {
            display: block;
        }

        @keyframes slideIn {
            from { opacity: 0; transform: translateX(20px); }
            to { opacity: 1; transform: translateX(0); }
        }

        .step-section.exiting {
            animation: slideOut 0.3s ease forwards;
        }

        @keyframes slideOut {
            to { opacity: 0; transform: translateX(-20px); }
        }

        .step-title {
            font-size: 1.25rem;
            font-weight: 700;
            color: #1d1d1f;
            margin-bottom: 6px;
        }

        .step-subtitle {
            color: #86868b;
            font-size: 0.9rem;
            margin-bottom: 0;
        }

        .step-header {
            display: flex;
            align-items: center;
            gap: 16px;
            margin-bottom: 24px;
            padding-bottom: 16px;
            border-bottom: 2px dashed #e5e5ea;
        }

        .step-icon {
            width: 56px;
            height: 56px;
            border-radius: 16px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
            flex-shrink: 0;
        }

        .field-group {
            position: relative;
            margin-bottom: 20px;
        }

        .field-group label {
            position: absolute;
            left: 16px;
            top: 50%;
            transform: translateY(-50%);
            color: #86868b;
            font-size: 0.95rem;
            pointer-events: none;
            transition: all 0.3s ease;
            background: #fff;
            padding: 0 4px;
        }

        .field-group input,
        .field-group textarea {
            width: 100%;
            padding: 16px;
            border: 2px solid #e5e5ea;
            border-radius: 12px;
            font-size: 1rem;
            color: #1d1d1f;
            transition: all 0.3s ease;
            background: #fff;
        }

        .field-group textarea {
            padding-top: 28px;
            min-height: 100px;
            resize: vertical;
        }

        .field-group input:focus,
        .field-group textarea:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.15);
            outline: none;
        }

        .field-group input:focus + label,
        .field-group input:not(:placeholder-shown) + label,
        .field-group textarea:focus + label,
        .field-group textarea:not(:placeholder-shown) + label {
            top: 0;
            transform: translateY(-50%);
            font-size: 0.75rem;
            color: #667eea;
            font-weight: 600;
        }

        .password-wrapper {
            position: relative;
        }

        .password-toggle {
            position: absolute;
            right: 16px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            cursor: pointer;
            color: #86868b;
            padding: 4px;
            transition: color 0.3s ease;
        }

        .password-toggle:hover {
            color: #1d1d1f;
        }

        .password-wrapper input {
            padding-right: 48px;
        }

        /* Requirements Box */
        .requirements-box {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border: 2px dashed #dee2e6;
            border-radius: 12px;
            padding: 16px;
            margin-bottom: 20px;
        }

        .requirements-box-title {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 0.85rem;
            font-weight: 600;
            color: #495057;
            margin-bottom: 12px;
        }

        .requirements-box-title svg {
            color: #667eea;
        }

        .requirement-list {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .requirement-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 0.8rem;
            color: #6c757d;
            transition: all 0.3s ease;
        }

        .requirement-item .req-icon {
            font-size: 1rem;
            width: 24px;
            text-align: center;
        }

        .requirement-item.valid {
            color: #34c759;
        }

        .requirement-item.valid::before {
            content: "✓";
            color: #34c759;
            font-weight: 600;
        }

        .requirement-item:not(.valid)::before {
            content: "○";
            color: #c7c7cc;
        }

        /* Password Strength */
        .password-strength {
            margin-top: 16px;
            padding: 12px 16px;
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-radius: 10px;
            border: 1px solid #dee2e6;
        }

        .strength-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
        }

        .strength-label {
            font-size: 0.75rem;
            color: #86868b;
        }

        .strength-text {
            font-size: 0.75rem;
            font-weight: 600;
        }

        .strength-text.weak { color: #ff3b30; }
        .strength-text.medium { color: #ff9500; }
        .strength-text.strong { color: #34c759; }

        .strength-bars {
            display: flex;
            gap: 4px;
        }

        .strength-bar {
            flex: 1;
            height: 4px;
            background: #e5e5ea;
            border-radius: 2px;
            transition: all 0.3s ease;
        }

        .strength-bar.active.weak { background: #ff3b30; }
        .strength-bar.active.medium { background: #ff9500; }
        .strength-bar.active.strong { background: #34c759; }

        /* Match Status */
        .match-status {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-top: 8px;
            font-size: 0.8rem;
            opacity: 0;
            transform: translateY(-5px);
            transition: all 0.3s ease;
        }

        .match-status.show {
            opacity: 1;
            transform: translateY(0);
        }

        .match-status.match { color: #34c759; }
        .match-status.mismatch { color: #ff3b30; }

        /* Validation States */
        .field-group.valid input,
        .field-group.valid textarea {
            border-color: #34c759;
        }

        .field-group.has-prefix-icon .input-prefix-icon {
            position: absolute;
            left: 16px;
            top: 50%;
            transform: translateY(-50%);
            color: #86868b;
            z-index: 0;
            transition: color 0.3s ease;
            pointer-events: none;
        }

        .field-group.has-prefix-icon input {
            padding-left: 48px;
            z-index: 1;
            background: transparent;
        }

        .field-group.has-prefix-icon label {
            left: 48px;
            z-index: 2;
        }

        .field-group.has-prefix-icon input:focus ~ .input-prefix-icon,
        .field-group.has-prefix-icon input:not(:placeholder-shown) ~ .input-prefix-icon {
            color: #667eea;
        }

        .password-wrapper {
            position: relative;
        }

        .password-wrapper input {
            padding-left: 48px;
        }

        .field-group.error input,
        .field-group.error textarea {
            border-color: #ff3b30;
        }

        /* Form Row */
        .form-row {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 16px;
        }

        @media (max-width: 500px) {
            .form-row { grid-template-columns: 1fr; }
        }

        /* Skills Tags */
        .skills-section {
            margin-top: 16px;
        }

        .skills-label {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 0.85rem;
            font-weight: 600;
            color: #495057;
            margin-bottom: 12px;
        }

        .skills-label svg {
            color: #667eea;
        }

        .skills-tags {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            margin-bottom: 16px;
        }

        .skill-tag {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 8px 14px;
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border: 2px solid #dee2e6;
            border-radius: 20px;
            font-size: 0.8rem;
            color: #495057;
            cursor: pointer;
            transition: all 0.3s ease;
            user-select: none;
        }

        .skill-tag:hover {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-color: #667eea;
            color: #fff;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
        }

        .skill-tag.selected {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-color: #667eea;
            color: #fff;
        }

        .skill-tag .tag-icon {
            font-size: 1rem;
        }

        /* Floating decorations */
        .floating-deco {
            position: absolute;
            font-size: 1.5rem;
            opacity: 0.2;
            animation: float 6s ease-in-out infinite;
            pointer-events: none;
            z-index: 0;
            filter: blur(1px);
        }

        .floating-deco:nth-child(1) { top: 10%; left: 5%; animation-delay: 0s; }
        .floating-deco:nth-child(2) { top: 20%; right: 8%; animation-delay: 1s; }
        .floating-deco:nth-child(3) { bottom: 30%; left: 10%; animation-delay: 2s; }
        .floating-deco:nth-child(4) { bottom: 15%; right: 5%; animation-delay: 3s; }

        @keyframes float {
            0%, 100% { transform: translateY(0) rotate(0deg); }
            50% { transform: translateY(-20px) rotate(10deg); }
        }

        /* Form Actions */
        .form-actions {
            display: flex;
            gap: 12px;
            margin-top: 24px;
        }

        .btn {
            flex: 1;
            padding: 16px 24px;
            border-radius: 12px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            border: none;
        }

        .btn-prev {
            background: #f5f5f7;
            color: #424245;
        }

        .btn-prev:hover {
            background: #e5e5ea;
        }

        .btn-next {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: #fff;
            box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
        }

        .btn-next:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 32px rgba(102, 126, 234, 0.5);
        }

        .btn-submit {
            background: linear-gradient(135deg, #34c759, #2cb945);
            color: #fff;
            box-shadow: 0 8px 24px rgba(52, 199, 89, 0.4);
        }

        .btn-submit:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 32px rgba(52, 199, 89, 0.5);
        }

        .btn svg {
            transition: transform 0.3s ease;
        }

        .btn:hover svg {
            transform: translateX(3px);
        }

        .btn-prev:hover svg {
            transform: translateX(-3px);
        }

        /* Terms */
        .terms-checkbox {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            margin-top: 20px;
            padding: 16px;
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            border-radius: 12px;
            border: 2px dashed #dee2e6;
        }

        .terms-checkbox input[type="checkbox"] {
            width: 20px;
            height: 20px;
            accent-color: #667eea;
            cursor: pointer;
            flex-shrink: 0;
            margin-top: 2px;
        }

        .terms-checkbox label {
            font-size: 0.85rem;
            color: #424245;
            cursor: pointer;
            line-height: 1.5;
        }

        .terms-checkbox a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
        }

        .terms-checkbox a:hover {
            text-decoration: underline;
        }

        /* Footer */
        .reg-footer {
            text-align: center;
            padding: 20px 32px 24px;
            border-top: 1px solid #f0f0f0;
        }

        .reg-footer a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
            font-size: 0.9rem;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .reg-footer a:hover {
            text-decoration: underline;
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            10%, 30%, 50%, 70%, 90% { transform: translateX(-4px); }
            20%, 40%, 60%, 80% { transform: translateX(4px); }
        }

        .shake {
            animation: shake 0.5s ease;
        }

        @media (max-width: 768px) {
            .reg-container { max-width: 100%; }
            .form-container { padding: 24px 20px; }
            .progress-container { padding: 20px 20px 0; }
            .reg-header h1 { font-size: 1.75rem; }
        }
    </style>
</head>
<body>

<div class="reg-page">
    <div class="particles" id="particles"></div>
    <div class="floating-deco">📖</div>
    <div class="floating-deco">✏️</div>
    <div class="floating-deco">🎯</div>
    <div class="floating-deco">💡</div>

    <div class="reg-container">
        <div class="reg-header">
            <div class="header-decoration">
                <span class="deco-icon">🎓</span>
                <span class="deco-icon">📚</span>
                <span class="deco-icon">💻</span>
            </div>
            <h1>Join TA Portal</h1>
            <p>Create your account to get started</p>
        </div>

        <div class="reg-card">
            <div class="progress-container">
                <div class="progress-bar">
                    <div class="progress-line" id="progressLine"></div>
                    <div class="progress-step active" data-step="1" onclick="jumpToStep(1)"><span>1</span></div>
                    <div class="progress-step" data-step="2" onclick="jumpToStep(2)"><span>2</span></div>
                    <div class="progress-step" data-step="3" onclick="jumpToStep(3)"><span>3</span></div>
                </div>
            </div>

            <form id="regForm" action="${pageContext.request.contextPath}/register" method="post" autocomplete="off">
                <div class="form-container">
                    
                    <!-- Step 1: Account -->
                    <div class="step-section active" data-section="1">
                        <div class="step-header">
                            <div class="step-icon">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                    <circle cx="12" cy="7" r="4"></circle>
                                </svg>
                            </div>
			<div>
                                <h2 class="step-title">Account Details</h2>
                                <p class="step-subtitle">Choose your login credentials</p>
                            </div>
                        </div>

                        <!-- Username -->
                        <div class="field-group has-prefix-icon">
                            <span class="input-prefix-icon">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                    <circle cx="12" cy="7" r="4"></circle>
                                </svg>
                            </span>
                            <input type="text" id="username" name="username" placeholder=" " required autocomplete="username">
                            <label for="username">Username *</label>
                        </div>
                        <div class="requirements-box">
                            <div class="requirements-box-title">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <circle cx="12" cy="12" r="10"></circle>
                                    <line x1="12" y1="16" x2="12" y2="12"></line>
                                    <line x1="12" y1="8" x2="12.01" y2="8"></line>
                                </svg>
                                <span>Username Requirements</span>
                            </div>
                            <div class="requirement-list">
                                <div class="requirement-item" id="req-username-length">
                                    <span class="req-icon">🎯</span> At least 3 characters
                                </div>
                                <div class="requirement-item" id="req-username-chars">
                                    <span class="req-icon">✨</span> Letters, numbers, underscore only
                                </div>
                                <div class="requirement-item" id="req-username-start">
                                    <span class="req-icon">🔤</span> Must start with a letter
                                </div>
                            </div>
                        </div>

                        <!-- Password -->
                        <div class="field-group has-prefix-icon">
                            <span class="input-prefix-icon">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                                    <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                                </svg>
                            </span>
                            <div class="password-wrapper">
                                <input type="password" id="password" name="password" placeholder=" " required autocomplete="new-password">
                                <label for="password">Password *</label>
                                <button type="button" class="password-toggle" onclick="togglePassword('password', this)">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                        <circle cx="12" cy="12" r="3"></circle>
                                    </svg>
                                </button>
                            </div>
                        </div>
                        <div class="password-strength" id="strengthMeter">
                            <div class="strength-header">
                                <span class="strength-label">Password Strength:</span>
                                <span class="strength-text" id="strengthText">-</span>
                            </div>
                            <div class="strength-bars">
                                <div class="strength-bar" id="bar1"></div>
                                <div class="strength-bar" id="bar2"></div>
                                <div class="strength-bar" id="bar3"></div>
			</div>
		</div>

                        <!-- Confirm Password -->
                        <div class="field-group has-prefix-icon" style="margin-top: 24px;">
                            <span class="input-prefix-icon">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                                    <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                                </svg>
                            </span>
                            <div class="password-wrapper">
                                <input type="password" id="confirmPassword" name="confirmPassword" placeholder=" " required autocomplete="new-password">
                                <label for="confirmPassword">Confirm Password *</label>
                                <button type="button" class="password-toggle" onclick="togglePassword('confirmPassword', this)">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                        <circle cx="12" cy="12" r="3"></circle>
                                    </svg>
                                </button>
                            </div>
                        </div>
                        <div class="match-status" id="matchStatus">
                            <span class="match-icon" id="matchIcon"></span>
                            <span id="matchText"></span>
			</div>

                        <div class="form-actions">
                            <button type="button" class="btn btn-prev" style="visibility: hidden;"></button>
                            <button type="button" class="btn btn-next" onclick="nextStep(1)">
                                Continue
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M5 12h14M12 5l7 7-7 7"/>
                                </svg>
                            </button>
                        </div>
			</div>

                    <!-- Step 2: Profile -->
                    <div class="step-section" data-section="2">
                        <div class="step-header">
                            <div class="step-icon">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                                    <polyline points="14 2 14 8 20 8"></polyline>
                                    <line x1="16" y1="13" x2="8" y2="13"></line>
                                    <line x1="16" y1="17" x2="8" y2="17"></line>
                                    <polyline points="10 9 9 9 8 9"></polyline>
                                </svg>
                            </div>
			<div>
                                <h2 class="step-title">Personal Info</h2>
                                <p class="step-subtitle">Tell us about yourself</p>
                            </div>
			</div>

                        <div class="form-row">
                            <div class="field-group">
                                <input type="text" id="name" name="name" placeholder=" " autocomplete="name">
                                <label for="name">Full Name</label>
                            </div>
                            <div class="field-group">
                                <input type="email" id="email" name="email" placeholder=" " autocomplete="email">
				<label for="email">Email</label>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="field-group">
                                <input type="number" id="year" name="year" placeholder=" " min="1" max="8" autocomplete="off">
                                <label for="year">Year of Study</label>
                            </div>
                            <div class="field-group">
                                <input type="text" id="major" name="major" placeholder=" " autocomplete="off">
                                <label for="major">Major</label>
                            </div>
                        </div>

                        <div class="form-actions">
                            <button type="button" class="btn btn-prev" onclick="prevStep(2)">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M19 12H5M12 19l-7-7 7-7"/>
                                </svg>
                                Back
                            </button>
                            <button type="button" class="btn btn-next" onclick="nextStep(2)">
                                Continue
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M5 12h14M12 5l7 7-7 7"/>
                                </svg>
                            </button>
                        </div>
			</div>

                    <!-- Step 3: Skills -->
                    <div class="step-section" data-section="3">
                        <div class="step-header">
                            <div class="step-icon">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                                </svg>
                            </div>
			<div>
                                <h2 class="step-title">Your Skills</h2>
                                <p class="step-subtitle">Show us what you can do</p>
                            </div>
                        </div>

                        <!-- Skill Tags -->
                        <div class="skills-section">
                            <div class="skills-label">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                                </svg>
                                <span>Quick Select Skills</span>
                            </div>
                            <div class="skills-tags" id="skillsTags">
                                <div class="skill-tag" data-skill="Java"><span class="tag-icon">☕</span> Java</div>
                                <div class="skill-tag" data-skill="Python"><span class="tag-icon">🐍</span> Python</div>
                                <div class="skill-tag" data-skill="JavaScript"><span class="tag-icon">⚡</span> JavaScript</div>
                                <div class="skill-tag" data-skill="C/C++"><span class="tag-icon">⚙️</span> C/C++</div>
                                <div class="skill-tag" data-skill="Machine Learning"><span class="tag-icon">🤖</span> ML</div>
                                <div class="skill-tag" data-skill="Data Structures"><span class="tag-icon">📊</span> Data Structures</div>
                                <div class="skill-tag" data-skill="Algorithms"><span class="tag-icon">🧩</span> Algorithms</div>
                                <div class="skill-tag" data-skill="Database"><span class="tag-icon">🗄️</span> Database</div>
                                <div class="skill-tag" data-skill="Web Development"><span class="tag-icon">🌐</span> Web Dev</div>
                                <div class="skill-tag" data-skill="UI/UX Design"><span class="tag-icon">🎨</span> UI/UX</div>
                                <div class="skill-tag" data-skill="React"><span class="tag-icon">⚛️</span> React</div>
                                <div class="skill-tag" data-skill="Node.js"><span class="tag-icon">🟢</span> Node.js</div>
                            </div>
			</div>

                        <div class="field-group textarea">
                            <textarea id="skills" name="skills" placeholder=" " rows="4"></textarea>
                            <label for="skills">Skills & Expertise</label>
			</div>

                        <div class="terms-checkbox">
                            <input type="checkbox" id="terms" name="terms" required>
                            <label for="terms">I agree to the <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a></label>
			</div>

                        <div class="form-actions">
                            <button type="button" class="btn btn-prev" onclick="prevStep(3)">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M19 12H5M12 19l-7-7 7-7"/>
                                </svg>
                                Back
                            </button>
                            <button type="submit" class="btn btn-submit" id="submitBtn">
                                Create Account
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M20 6L9 17l-5-5"/>
                                </svg>
                            </button>
                        </div>
                    </div>
			</div>
		</form>

            <div class="reg-footer">
                <a href="${pageContext.request.contextPath}/home">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M19 12H5M12 19l-7-7 7-7"/>
                    </svg>
                    Already have an account? Sign in
                </a>
            </div>
        </div>
    </div>
</div>

<script>
var currentStep = 1;
var totalSteps = 3;

// Create particles
(function() {
    var container = document.getElementById('particles');
    for (var i = 0; i < 20; i++) {
        var particle = document.createElement('div');
        particle.className = 'particle';
        particle.style.left = Math.random() * 100 + '%';
        particle.style.top = Math.random() * 100 + '%';
        particle.style.animationDelay = Math.random() * 15 + 's';
        particle.style.animationDuration = (15 + Math.random() * 10) + 's';
        container.appendChild(particle);
    }
})();

// Toggle password visibility
function togglePassword(inputId, btn) {
    var input = document.getElementById(inputId);
    if (input.type === 'password') {
        input.type = 'text';
        btn.innerHTML = '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>';
    } else {
        input.type = 'password';
        btn.innerHTML = '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>';
    }
}

// Username validation
function validateUsername(username) {
    var results = {
        length: username.length >= 3,
        chars: /^[a-zA-Z0-9_]+$/.test(username),
        start: /^[a-zA-Z]/.test(username)
    };

    document.getElementById('req-username-length').classList.toggle('valid', results.length);
    document.getElementById('req-username-chars').classList.toggle('valid', results.chars);
    document.getElementById('req-username-start').classList.toggle('valid', results.start);

    return results.length && results.chars && results.start;
}

// Password strength display
function updatePasswordStrength(password) {
    var bars = [
        document.getElementById('bar1'),
        document.getElementById('bar2'),
        document.getElementById('bar3')
    ];
    var text = document.getElementById('strengthText');

    var strength = 0;
    if (password.length >= 4) strength = 1;
    if (password.length >= 8) strength = 2;
    if (password.length >= 12) strength = 3;

    if (password.length === 0) {
        text.textContent = '-';
        text.className = 'strength-text';
    } else if (strength === 1) {
        text.textContent = 'Weak';
        text.className = 'strength-text weak';
    } else if (strength === 2) {
        text.textContent = 'Fair';
        text.className = 'strength-text medium';
    } else {
        text.textContent = 'Strong';
        text.className = 'strength-text strong';
    }

    bars.forEach(function(bar, index) {
        bar.classList.remove('active', 'weak', 'medium', 'strong');
        if (index < strength) {
            bar.classList.add('active');
            if (strength === 1) bar.classList.add('weak');
            else if (strength === 2) bar.classList.add('medium');
            else bar.classList.add('strong');
        }
    });
}

// Check password match
function checkPasswordMatch() {
    var password = document.getElementById('password').value;
    var confirm = document.getElementById('confirmPassword').value;
    var status = document.getElementById('matchStatus');
    var icon = document.getElementById('matchIcon');
    var text = document.getElementById('matchText');
    var field = document.getElementById('confirmPassword').parentNode.parentNode;

    if (confirm.length === 0) {
        status.classList.remove('show', 'match', 'mismatch');
        field.classList.remove('valid', 'error');
        return false;
    }

    status.classList.add('show');

    if (password === confirm) {
        status.classList.remove('mismatch');
        status.classList.add('match');
        icon.textContent = '✓';
        text.textContent = 'Passwords match';
        field.classList.remove('error');
        field.classList.add('valid');
        return true;
    } else {
        status.classList.remove('match');
        status.classList.add('mismatch');
        icon.textContent = '✕';
        text.textContent = 'Passwords do not match';
        field.classList.remove('valid');
        field.classList.add('error');
        return false;
    }
}

// Event listeners
document.getElementById('username').addEventListener('input', function() {
    validateUsername(this.value);
});

document.getElementById('password').addEventListener('input', function() {
    updatePasswordStrength(this.value);
    if (document.getElementById('confirmPassword').value.length > 0) {
        checkPasswordMatch();
    }
});

document.getElementById('confirmPassword').addEventListener('input', checkPasswordMatch);

// Skills tags functionality
var selectedSkills = [];

document.querySelectorAll('.skill-tag').forEach(function(tag) {
    tag.addEventListener('click', function() {
        var skill = this.getAttribute('data-skill');
        var textarea = document.getElementById('skills');
        
        this.classList.toggle('selected');
        
        if (this.classList.contains('selected')) {
            if (!selectedSkills.includes(skill)) {
                selectedSkills.push(skill);
            }
        } else {
            selectedSkills = selectedSkills.filter(function(s) { return s !== skill; });
        }
        
        textarea.value = selectedSkills.join(', ');
    });
});

// Update progress
function updateProgress() {
    var steps = document.querySelectorAll('.progress-step');
    var line = document.getElementById('progressLine');

    steps.forEach(function(step, index) {
        var stepNum = index + 1;
        step.classList.remove('active', 'completed');

        if (stepNum < currentStep) {
            step.classList.add('completed');
        } else if (stepNum === currentStep) {
            step.classList.add('active');
        }
    });

    var progress = ((currentStep - 1) / (totalSteps - 1)) * 100;
    line.style.width = progress + '%';
}

// Validate step
function validateStep(step) {
    var section = document.querySelector('.step-section[data-section="' + step + '"]');
    var isValid = true;

    if (step === 1) {
        var username = document.getElementById('username');
        var password = document.getElementById('password');
        var confirm = document.getElementById('confirmPassword');

        if (!validateUsername(username.value.trim())) {
            username.parentNode.classList.add('error');
            isValid = false;
        } else {
            username.parentNode.classList.remove('error');
            username.parentNode.classList.add('valid');
        }

        if (!password.value) {
            password.parentNode.parentNode.classList.add('error');
            isValid = false;
        } else {
            password.parentNode.parentNode.classList.remove('error');
            password.parentNode.parentNode.classList.add('valid');
        }

        if (!checkPasswordMatch()) {
            isValid = false;
        }
    }

    if (!isValid) {
        section.classList.add('shake');
        setTimeout(function() { section.classList.remove('shake'); }, 500);
    }

    return isValid;
}

// Navigation
function nextStep(from) {
    if (!validateStep(from)) return;
    
    var currentSection = document.querySelector('.step-section[data-section="' + from + '"]');
    currentSection.classList.add('exiting');
    
    setTimeout(function() {
        currentSection.classList.remove('exiting', 'active');
        currentStep = from + 1;
        document.querySelector('.step-section[data-section="' + currentStep + '"]').classList.add('active');
        updateProgress();
        document.querySelector('.reg-card').scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 300);
}

function prevStep(from) {
    var currentSection = document.querySelector('.step-section[data-section="' + from + '"]');
    currentSection.classList.add('exiting');
    
    setTimeout(function() {
        currentSection.classList.remove('exiting', 'active');
        currentStep = from - 1;
        document.querySelector('.step-section[data-section="' + currentStep + '"]').classList.add('active');
        updateProgress();
        document.querySelector('.reg-card').scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 300);
}

function jumpToStep(step) {
    if (step > currentStep) {
        if (!validateStep(currentStep)) return;
    }
    
    var currentSection = document.querySelector('.step-section.active');
    currentSection.classList.remove('active');
    
    currentStep = step;
    document.querySelector('.step-section[data-section="' + step + '"]').classList.add('active');
    updateProgress();
}

// Clear errors on input
document.querySelectorAll('input, textarea').forEach(function(input) {
    input.addEventListener('input', function() {
        this.parentNode.classList.remove('error');
    });
});

// Form submission
document.getElementById('regForm').addEventListener('submit', function(e) {
    if (!validateStep(1)) {
        jumpToStep(1);
        e.preventDefault();
        return;
    }

    var terms = document.getElementById('terms');
    if (!terms.checked) {
        alert('Please agree to the Terms of Service');
        e.preventDefault();
        return;
    }

    var btn = document.getElementById('submitBtn');
    btn.innerHTML = '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10" stroke-dasharray="40" stroke-dashoffset="10" style="animation: spin 1s linear infinite;"/></svg> Creating...';
    btn.disabled = true;

    var style = document.createElement('style');
    style.textContent = '@keyframes spin { 100% { transform: rotate(360deg); } }';
    document.head.appendChild(style);
});

// Initialize
updateProgress();
</script>

</body>
</html>