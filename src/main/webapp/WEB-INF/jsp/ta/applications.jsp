<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%
    request.setAttribute("pageTitle", "Applications");
%>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<%@ include file="/WEB-INF/jsp/common/flash.jspf" %>

<style>
.feedback-btn {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    background: none;
    border: 1px solid;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 0.75rem;
    cursor: pointer;
    transition: all 0.2s;
}
.feedback-btn svg {
    flex-shrink: 0;
}
.feedback-btn-rejected {
    border-color: #dc3545;
    color: #dc3545;
}
.feedback-btn-rejected:hover {
    background: #dc3545;
    color: #fff;
}
.feedback-btn-rejected:hover svg {
    color: #fff;
}
.feedback-btn-interview {
    border-color: #007bff;
    color: #007bff;
}
.feedback-btn-interview:hover {
    background: #007bff;
    color: #fff;
}
.feedback-btn-interview:hover svg {
    color: #fff;
}
.feedback-btn-accepted {
    border-color: #28a745;
    color: #28a745;
}
.feedback-btn-accepted:hover {
    background: #28a745;
    color: #fff;
}
.feedback-btn-accepted:hover svg {
    color: #fff;
}
.feedback-none {
    color: #999;
    font-size: 0.8125rem;
}
.modal-overlay {
    position: fixed;
    top: 0; left: 0;
    width: 100%; height: 100%;
    background: rgba(0,0,0,0.5);
    display: none;
    z-index: 1000;
    justify-content: center;
    align-items: center;
}
.modal-overlay.active {
    display: flex;
}
.modal {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    max-width: 500px;
    width: 90%;
    box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}
.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
}
.modal-header h3 {
    margin: 0;
    font-size: 1.125rem;
}
.modal-close {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: #666;
}
.modal-body {
    margin-bottom: 16px;
}
.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
}
.modal-form .form-group {
    margin-bottom: 16px;
}
.modal-form label {
    display: block;
    margin-bottom: 6px;
    font-weight: 500;
}
.modal-form textarea {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 0.875rem;
    resize: vertical;
}
.btn-danger {
    background: #dc3545;
    color: #fff;
    border: none;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
}

/* Right-click context menu */
.context-menu {
    position: fixed;
    background: #fff;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0,0,0,0.15);
    min-width: 160px;
    z-index: 1001;
    display: none;
    overflow: hidden;
}
.context-menu.active {
    display: block;
}
.context-menu-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 16px;
    cursor: pointer;
    transition: background 0.15s;
    font-size: 0.875rem;
    color: #333;
    text-decoration: none;
}
.context-menu-item:hover {
    background: #f5f5f5;
}
.context-menu-item.danger {
    color: #dc3545;
}
.context-menu-item.danger:hover {
    background: #fff1f1;
}
.context-menu-item svg {
    flex-shrink: 0;
    width: 16px;
    height: 16px;
}
.context-menu-divider {
    height: 1px;
    background: #eee;
    margin: 4px 0;
}
.context-menu-item.disabled {
    color: #aaa;
    cursor: not-allowed;
    pointer-events: none;
}
</style>

<div class="layout layout-ta">
    <aside class="sidebar sidebar-ta" id="sidebar">
        <div class="sidebar-brand">
            <div class="brand-logo brand-ta">TA</div>
            <div>
                <h3>TA Portal</h3>
                <p>Recruitment System</p>
            </div>
        </div>

        <nav class="sidebar-nav">
            <a class="nav-item" href="${pageContext.request.contextPath}/ta/dashboard">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
                </span> Dashboard
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/jobs">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 7V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v2"/><line x1="12" y1="12" x2="12" y2="12"/></svg>
                </span> Job Board
            </a>
            <a class="nav-item active" href="${pageContext.request.contextPath}/applications">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                </span> Applications
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/ta/profile">
                <span class="nav-icon">
                    <svg viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                </span> My Profile
            </a>
        </nav>
    </aside>

    <main class="content content-ta">
        <div class="topbar topbar-ta">
            <button type="button" class="sidebar-toggle">
                <svg viewBox="0 0 24 24"><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
            </button>
            <div class="topbar-title">My Applications</div>
            <div class="topbar-right">
                <a class="user-name" href="${pageContext.request.contextPath}/ta/profile">${sessionScope.currentUser.username}</a>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </div>
        </div>

        <div class="ta-content">
            <section class="panel dashboard-intro">
                <h1>My Applications</h1>
                <p>Track the status of all your applications here.</p>
            </section>

            <section class="panel">
            <div class="table-responsive">
                <table class="custom-table">
                    <thead>
                    <tr>
                        <th>Application ID</th>
                        <th>Job Title</th>
                        <th>Module</th>
                        <th>Organiser</th>
                        <th>Status</th>
                        <th>Submitted At</th>
                        <th>Feedback</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty applications}">
                            <tr>
                                <td colspan="7" class="empty-state">
                                    <div class="empty-content">
                                        <span class="empty-icon">&#128203;</span>
                                        <p>No applications yet</p>
                                        <a href="${pageContext.request.contextPath}/jobs" class="btn btn-primary btn-small">Browse Positions</a>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="a" items="${applications}">
                                <tr oncontextmenu="showContextMenu(event, '${a.applicationId}', '${a.status}')" data-app-id="${a.applicationId}" data-status="${a.status}">
                                    <td><span class="app-id">${a.applicationId}</span></td>
                                    <td><strong>${a.jobTitle}</strong></td>
                                    <td><span class="module-code">${a.moduleCode}</span></td>
                                    <td>${a.organiser}</td>
                                    <td><span class="badge ${a.status}">${a.status}</span></td>
                                    <td>${a.submittedAt}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${a.status == 'REJECTED' && not empty a.notes}">
                                                <button type="button" class="feedback-btn feedback-btn-rejected btn-view-feedback" data-notes="${fn:escapeXml(a.notes)}" data-title="Rejection Reason">
                                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                                                    View Reason
                                                </button>
                                            </c:when>
                                            <c:when test="${a.status == 'INTERVIEW' && not empty a.notes}">
                                                <button type="button" class="feedback-btn feedback-btn-interview btn-view-feedback" data-notes="${fn:escapeXml(a.notes)}" data-title="Interview Details">
                                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                                                    View Details
                                                </button>
                                            </c:when>
                                            <c:when test="${a.status == 'ACCEPTED' && not empty a.notes}">
                                                <button type="button" class="feedback-btn feedback-btn-accepted btn-view-feedback" data-notes="${fn:escapeXml(a.notes)}" data-title="Acceptance Notice">
                                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>
                                                    View Notice
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="feedback-none">-</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </section>
        </div>
    </main>
</div>

<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>

<div class="modal-overlay" id="reasonModal">
    <div class="modal">
        <div class="modal-header">
            <h3 id="reasonModalTitle">Details</h3>
            <button class="modal-close" onclick="closeReasonModal()">&times;</button>
        </div>
        <div class="modal-body">
            <p id="reasonContent" style="margin: 0; color: #333; line-height: 1.6;"></p>
        </div>
        <div class="modal-actions">
            <button class="btn btn-secondary" onclick="closeReasonModal()">Close</button>
        </div>
    </div>
</div>

<!-- Right-click context menu -->
<div class="context-menu" id="contextMenu">
    <div class="context-menu-item" id="menuWithdraw" onclick="withdrawApplication()">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
        </svg>
        Withdraw
    </div>
    <div class="context-menu-divider"></div>
    <div class="context-menu-item danger" id="menuDelete" onclick="deleteApplication()">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="3 6 5 6 21 6"/>
            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
        </svg>
        Delete Record
    </div>
</div>

<!-- Confirmation modal for delete -->
<div class="modal-overlay" id="confirmModal">
    <div class="modal">
        <div class="modal-header">
            <h3>Confirm Delete</h3>
            <button class="modal-close" onclick="closeConfirmModal()">&times;</button>
        </div>
        <div class="modal-body">
            <p>Are you sure you want to delete this application record? This action cannot be undone.</p>
        </div>
        <div class="modal-actions">
            <button class="btn btn-secondary" onclick="closeConfirmModal()">Cancel</button>
            <form id="deleteForm" method="post" style="display: inline;">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="applicationId" id="deleteAppId">
                <button type="submit" class="btn btn-danger">Delete</button>
            </form>
        </div>
    </div>
</div>

<script>
var currentAppId = null;
var currentAppStatus = null;

function showContextMenu(event, appId, status) {
    event.preventDefault();
    currentAppId = appId;
    currentAppStatus = status;

    var menu = document.getElementById('contextMenu');
    var withdrawItem = document.getElementById('menuWithdraw');
    var deleteItem = document.getElementById('menuDelete');

    // Show/hide options based on application status
    // Only PENDING applications can be withdrawn
    // Delete is available for all statuses (PENDING, WITHDRAWN, ACCEPTED, REJECTED)
    if (status === 'PENDING') {
        withdrawItem.style.display = 'flex';
        deleteItem.style.display = 'flex';
    } else {
        // For WITHDRAWN, ACCEPTED, REJECTED - only show delete
        withdrawItem.style.display = 'none';
        deleteItem.style.display = 'flex';
    }

    // Position the menu
    var x = event.clientX;
    var y = event.clientY;
    var menuWidth = 160;
    var menuHeight = 90;

    // Adjust if menu would go off screen
    if (x + menuWidth > window.innerWidth) {
        x = window.innerWidth - menuWidth - 10;
    }
    if (y + menuHeight > window.innerHeight) {
        y = window.innerHeight - menuHeight - 10;
    }

    menu.style.left = x + 'px';
    menu.style.top = y + 'px';
    menu.classList.add('active');
}

function hideContextMenu() {
    document.getElementById('contextMenu').classList.remove('active');
}

function withdrawApplication() {
    if (!currentAppId) return;

    var form = document.createElement('form');
    form.method = 'post';
    form.action = '${pageContext.request.contextPath}/applications';

    var actionInput = document.createElement('input');
    actionInput.type = 'hidden';
    actionInput.name = 'action';
    actionInput.value = 'withdraw';
    form.appendChild(actionInput);

    var appIdInput = document.createElement('input');
    appIdInput.type = 'hidden';
    appIdInput.name = 'applicationId';
    appIdInput.value = currentAppId;
    form.appendChild(appIdInput);

    document.body.appendChild(form);
    form.submit();
}

function deleteApplication() {
    if (!currentAppId) return;
    document.getElementById('deleteAppId').value = currentAppId;
    document.getElementById('confirmModal').classList.add('active');
}

function closeConfirmModal() {
    document.getElementById('confirmModal').classList.remove('active');
}

function showReasonModal(content, title) {
    var modalTitle = title || 'Details';
    var displayContent = content || 'No details provided.';
    // Remove prefix if present
    if (displayContent.indexOf('Rejected: ') === 0) {
        displayContent = displayContent.substring(10);
    } else if (displayContent.indexOf('Interview: ') === 0) {
        displayContent = displayContent.substring(11);
    } else if (displayContent.indexOf('Accepted: ') === 0) {
        displayContent = displayContent.substring(10);
    }
    document.getElementById('reasonModalTitle').textContent = modalTitle;
    document.getElementById('reasonContent').textContent = displayContent;
    document.getElementById('reasonModal').classList.add('active');
}

function closeReasonModal() {
    document.getElementById('reasonModal').classList.remove('active');
}

// Event delegation for feedback buttons using data attributes
document.addEventListener('click', function(e) {
    var btn = e.target.closest('.btn-view-feedback');
    if (btn) {
        var notes = btn.getAttribute('data-notes');
        var title = btn.getAttribute('data-title');
        showReasonModal(notes, title);
    }
});

// Close context menu on click outside
document.addEventListener('click', function(e) {
    var menu = document.getElementById('contextMenu');
    if (!menu.contains(e.target)) {
        hideContextMenu();
    }
});

// Close modals on overlay click
document.getElementById('reasonModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeReasonModal();
    }
});

document.getElementById('confirmModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeConfirmModal();
    }
});
</script>

