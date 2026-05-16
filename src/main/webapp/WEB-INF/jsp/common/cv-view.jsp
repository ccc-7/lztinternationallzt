<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Candidate Summary - ${cvUser.displayName}</title>
    <style>
        :root {
            color-scheme: light;
            --summary-blue: #1d5fe9;
            --summary-blue-soft: #eaf2ff;
            --summary-purple: #5f4acb;
            --summary-bg: #eef4fb;
            --summary-text: #1f2a37;
            --summary-muted: #6b7280;
            --summary-border: #d9e3f0;
            --summary-card: #ffffff;
            --summary-success: #e9f8ef;
            --summary-success-text: #137a3a;
            --summary-warn: #fff7e8;
            --summary-warn-text: #a45a00;
            --summary-danger: #fff0f0;
            --summary-danger-text: #b42318;
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(180deg, #f7fbff 0%, var(--summary-bg) 100%);
            color: var(--summary-text);
            padding: 32px 18px 48px;
        }

        .summary-page {
            max-width: 1080px;
            margin: 0 auto;
        }

        .summary-toolbar {
            display: flex;
            justify-content: space-between;
            gap: 12px;
            align-items: center;
            margin-bottom: 18px;
            flex-wrap: wrap;
        }

        .summary-toolbar-note {
            color: var(--summary-muted);
            font-size: 0.95rem;
        }

        .summary-toolbar-actions {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .summary-btn {
            border: none;
            border-radius: 999px;
            padding: 11px 18px;
            font-size: 0.92rem;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            transition: transform 0.18s ease, box-shadow 0.18s ease;
        }

        .summary-btn:hover {
            transform: translateY(-1px);
            box-shadow: 0 10px 24px rgba(29, 95, 233, 0.14);
        }

        .summary-btn-primary {
            background: var(--summary-blue);
            color: #fff;
        }

        .summary-btn-secondary {
            background: #fff;
            color: var(--summary-blue);
            border: 1px solid var(--summary-border);
        }

        .summary-shell {
            background: rgba(255, 255, 255, 0.74);
            border: 1px solid rgba(217, 227, 240, 0.9);
            border-radius: 28px;
            overflow: hidden;
            backdrop-filter: blur(10px);
            box-shadow: 0 22px 60px rgba(65, 94, 148, 0.12);
        }

        .summary-hero {
            background: linear-gradient(135deg, var(--summary-blue) 0%, var(--summary-purple) 100%);
            color: #fff;
            padding: 42px;
            display: grid;
            grid-template-columns: 1.5fr 1fr;
            gap: 28px;
            align-items: end;
        }

        .summary-eyebrow {
            letter-spacing: 0.08em;
            text-transform: uppercase;
            font-size: 0.8rem;
            opacity: 0.84;
            margin-bottom: 12px;
        }

        .summary-hero h1 {
            margin: 0 0 10px;
            font-size: 2.7rem;
            line-height: 1.05;
        }

        .summary-hero p {
            margin: 0;
            color: rgba(255, 255, 255, 0.84);
            max-width: 540px;
            line-height: 1.65;
        }

        .summary-meta-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 12px;
        }

        .summary-meta-card {
            background: rgba(255, 255, 255, 0.13);
            border: 1px solid rgba(255, 255, 255, 0.18);
            border-radius: 18px;
            padding: 16px 18px;
        }

        .summary-meta-label {
            display: block;
            font-size: 0.76rem;
            text-transform: uppercase;
            letter-spacing: 0.08em;
            opacity: 0.76;
            margin-bottom: 8px;
        }

        .summary-meta-value {
            font-size: 1.02rem;
            font-weight: 600;
        }

        .summary-status-badge {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 7px 14px;
            border-radius: 999px;
            font-size: 0.85rem;
            font-weight: 700;
        }

        .summary-status-badge.INCOMPLETE {
            background: rgba(255, 206, 167, 0.2);
            color: #fff6eb;
            border: 1px solid rgba(255, 230, 204, 0.26);
        }

        .summary-status-badge.BASIC_COMPLETE {
            background: rgba(255, 243, 205, 0.22);
            color: #fff7db;
            border: 1px solid rgba(255, 248, 225, 0.24);
        }

        .summary-status-badge.SUMMARY_COMPLETE {
            background: rgba(219, 252, 230, 0.18);
            color: #f4fff7;
            border: 1px solid rgba(231, 255, 239, 0.24);
        }

        .summary-body {
            padding: 32px;
        }

        .summary-notice {
            margin-bottom: 24px;
            border-radius: 18px;
            padding: 16px 18px;
            border: 1px solid #cfe0ff;
            background: #eef5ff;
            color: #244a90;
            line-height: 1.6;
        }

        .summary-grid {
            display: grid;
            grid-template-columns: 1.1fr 0.9fr;
            gap: 24px;
        }

        .summary-column {
            display: flex;
            flex-direction: column;
            gap: 24px;
        }

        .summary-section {
            background: var(--summary-card);
            border: 1px solid var(--summary-border);
            border-radius: 22px;
            padding: 24px;
        }

        .summary-section h2 {
            margin: 0 0 16px;
            font-size: 1.2rem;
            color: #20304d;
        }

        .summary-section p {
            margin: 0;
            line-height: 1.72;
            color: var(--summary-text);
        }

        .summary-info-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 14px;
        }

        .summary-info-card {
            background: #f8fbff;
            border: 1px solid #dde8f5;
            border-radius: 18px;
            padding: 16px 18px;
        }

        .summary-info-card strong {
            display: block;
            margin-bottom: 8px;
            color: #51627d;
            font-size: 0.86rem;
        }

        .summary-chip-list {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
        }

        .summary-chip {
            display: inline-flex;
            align-items: center;
            padding: 9px 14px;
            border-radius: 999px;
            background: var(--summary-blue-soft);
            color: var(--summary-blue);
            font-weight: 600;
            font-size: 0.9rem;
        }

        .summary-list {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        .summary-list-item {
            padding: 14px 16px;
            background: #fafcff;
            border: 1px solid #dde8f5;
            border-radius: 16px;
            line-height: 1.6;
        }

        .summary-placeholder {
            border-radius: 18px;
            border: 1px dashed #bfd0ea;
            background: #fbfdff;
            padding: 18px;
            color: var(--summary-muted);
            line-height: 1.7;
        }

        .summary-phase-box {
            border-radius: 18px;
            border: 1px solid #dde8f5;
            background: linear-gradient(180deg, #fcfdff 0%, #f6faff 100%);
            padding: 18px;
        }

        .summary-phase-box strong {
            display: block;
            margin-bottom: 10px;
            color: #20304d;
        }

        .summary-phase-box .phase-state {
            display: inline-flex;
            padding: 7px 12px;
            border-radius: 999px;
            background: var(--summary-warn);
            color: var(--summary-warn-text);
            font-weight: 700;
            margin-bottom: 10px;
        }

        .summary-section-subtitle {
            margin: -6px 0 16px;
            color: var(--summary-muted);
            line-height: 1.55;
            font-size: 0.95rem;
        }

        .summary-availability {
            padding: 16px 18px;
            border-radius: 18px;
            background: var(--summary-success);
            color: var(--summary-success-text);
            border: 1px solid #bfe6cd;
        }

        .summary-availability strong {
            display: block;
            margin-bottom: 8px;
        }

        @media print {
            body { background: #fff; padding: 0; }
            .summary-toolbar { display: none; }
            .summary-shell { box-shadow: none; border: none; }
        }

        @media (max-width: 900px) {
            .summary-hero,
            .summary-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 640px) {
            .summary-hero,
            .summary-body {
                padding: 22px;
            }

            .summary-info-grid,
            .summary-meta-grid {
                grid-template-columns: 1fr;
            }

            .summary-hero h1 {
                font-size: 2rem;
            }
        }
    </style>
</head>
<body>
<div class="summary-page">
    <div class="summary-toolbar">
        <div class="summary-toolbar-note">Candidate Summary view for TA/MO/Admin review</div>
        <div class="summary-toolbar-actions">
            <c:if test="${cvFileAvailable}">
                <a href="${cvDownloadHref}" target="_blank" class="summary-btn summary-btn-secondary">Open Original PDF</a>
            </c:if>
            <button type="button" class="summary-btn summary-btn-secondary" onclick="window.history.length > 1 ? window.history.back() : window.close()">Back</button>
            <button type="button" class="summary-btn summary-btn-primary" onclick="window.print()">Print Summary</button>
        </div>
    </div>

    <div class="summary-shell">
        <header class="summary-hero">
            <div>
                <div class="summary-eyebrow">Generated Candidate Summary</div>
                <h1>${cvUser.displayName}</h1>
                <p>
                    Structured candidate information prepared from the TA profile and summary builder fields.
                    This page is intended for recruiter review and does not represent an uploaded original CV file.
                </p>
            </div>
            <div class="summary-meta-grid">
                <div class="summary-meta-card">
                    <span class="summary-meta-label">TA ID</span>
                    <span class="summary-meta-value">${cvUser.userId}</span>
                </div>
                <div class="summary-meta-card">
                    <span class="summary-meta-label">Summary Status</span>
                    <span class="summary-status-badge ${cvUser.summaryStatus}">
                        <c:choose>
                            <c:when test="${cvUser.summaryStatus == 'SUMMARY_COMPLETE'}">Summary Complete</c:when>
                            <c:when test="${cvUser.summaryStatus == 'BASIC_COMPLETE'}">Basic Complete</c:when>
                            <c:otherwise>Incomplete</c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <div class="summary-meta-card">
                    <span class="summary-meta-label">Email</span>
                    <span class="summary-meta-value">${empty cvUser.email ? 'Not provided' : cvUser.email}</span>
                </div>
                <div class="summary-meta-card">
                    <span class="summary-meta-label">Preferred Role</span>
                    <span class="summary-meta-value">${empty cvUser.preferredRole ? 'Not specified' : cvUser.preferredRole}</span>
                </div>
            </div>
        </header>

        <main class="summary-body">
            <c:if test="${not empty summaryNotice}">
                <div class="summary-notice">${summaryNotice}</div>
            </c:if>

            <div class="summary-grid">
                <div class="summary-column">
                    <section class="summary-section">
                        <h2>Basic Information</h2>
                        <div class="summary-info-grid">
                            <div class="summary-info-card">
                                <strong>Username</strong>
                                <span>${cvUser.username}</span>
                            </div>
                            <div class="summary-info-card">
                                <strong>Year of Study</strong>
                                <span><c:choose><c:when test="${cvUser.year > 0}">Year ${cvUser.year}</c:when><c:otherwise>Not specified</c:otherwise></c:choose></span>
                            </div>
                            <div class="summary-info-card">
                                <strong>Major</strong>
                                <span>${empty cvUser.major ? 'Not specified' : cvUser.major}</span>
                            </div>
                            <div class="summary-info-card">
                                <strong>Account Status</strong>
                                <span>${empty cvUser.status ? 'Unknown' : cvUser.status}</span>
                            </div>
                        </div>
                    </section>

                    <section class="summary-section">
                        <h2>Skills</h2>
                        <p class="summary-section-subtitle">These tags are used for matching and recruiter review.</p>
                        <c:choose>
                            <c:when test="${not empty cvUser.skills}">
                                <div class="summary-chip-list">
                                    <c:forTokens items="${cvUser.skills}" delims="|" var="skill">
                                        <span class="summary-chip">${skill}</span>
                                    </c:forTokens>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="summary-placeholder">No skills have been added yet.</div>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <section class="summary-section">
                        <h2>Personal Statement</h2>
                        <c:choose>
                            <c:when test="${not empty cvUser.personalStatement}">
                                <p>${cvUser.personalStatement}</p>
                            </c:when>
                            <c:otherwise>
                                <div class="summary-placeholder">No personal statement has been provided yet.</div>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <section class="summary-section">
                        <h2>Project / Teaching Experience</h2>
                        <c:choose>
                            <c:when test="${not empty cvUser.projectExperience}">
                                <div class="summary-list">
                                    <c:forTokens items="${cvUser.projectExperience}" delims="|" var="item">
                                        <div class="summary-list-item">${item}</div>
                                    </c:forTokens>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="summary-placeholder">No project or teaching experience has been added yet.</div>
                            </c:otherwise>
                        </c:choose>
                    </section>
                </div>

                <div class="summary-column">
                    <section class="summary-section">
                        <h2>Availability</h2>
                        <c:choose>
                            <c:when test="${not empty cvUser.availability}">
                                <div class="summary-availability">
                                    <strong>Available Time</strong>
                                    <span>${cvUser.availability}</span>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="summary-placeholder">Availability has not been provided yet.</div>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <section class="summary-section">
                        <h2>Relevant Courses</h2>
                        <c:choose>
                            <c:when test="${not empty cvUser.relevantCourses}">
                                <div class="summary-list">
                                    <c:forTokens items="${cvUser.relevantCourses}" delims="|" var="course">
                                        <div class="summary-list-item">${course}</div>
                                    </c:forTokens>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="summary-placeholder">Relevant courses have not been listed yet.</div>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <section class="summary-section">
                        <h2>Preferred Role</h2>
                        <c:choose>
                            <c:when test="${not empty cvUser.preferredRole}">
                                <div class="summary-chip-list">
                                    <c:forTokens items="${cvUser.preferredRole}" delims="|" var="role">
                                        <span class="summary-chip">${role}</span>
                                    </c:forTokens>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="summary-placeholder">No preferred role has been selected yet.</div>
                            </c:otherwise>
                        </c:choose>
                    </section>

                    <section class="summary-section">
                        <h2>Original CV File</h2>
                        <c:choose>
                            <c:when test="${cvFileAvailable}">
                                <div class="summary-phase-box">
                                    <span class="phase-state" style="background:#e9f8ef;color:#137a3a;">Uploaded PDF ready</span>
                                    <strong>${empty cvUser.cvOriginalName ? 'Original CV available' : cvUser.cvOriginalName}</strong>
                                    <p>
                                        This candidate has uploaded a separate PDF CV.
                                        Use the “Open Original PDF” action to review the original file alongside this summary.
                                    </p>
                                    <p style="margin-top:10px;color:#51627d;">
                                        Uploaded at: ${empty cvUser.cvUploadedAt ? 'Unknown' : cvUser.cvUploadedAt}
                                    </p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="summary-phase-box">
                                    <span class="phase-state">PDF missing</span>
                                    <strong>Only structured Candidate Summary is available</strong>
                                    <p>
                                        No original PDF CV has been uploaded yet.
                                        Reviewers can still use this structured summary for first-pass screening.
                                    </p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </section>
                </div>
            </div>
        </main>
    </div>
</div>
</body>
</html>
