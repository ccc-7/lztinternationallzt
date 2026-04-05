package edu.bupt.ta.controller;

import edu.bupt.ta.model.*;
import edu.bupt.ta.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {
    "/admin/applications",
    "/admin/applications/approve",
    "/admin/applications/reject",
    "/admin/jobs/*",
    "/admin/users/*",
    "/admin/logs",
    "/admin/stats"
})
public class AdminServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();
    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();
    private final UserService userService = new UserService();
    private final LogService logService = new LogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = checkAdminAccess(req, resp);
        if (user == null) {
            return;
        }

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/applications")) {
            showApplications(req, resp);
        } else if (pathInfo.equals("/jobs") || pathInfo.startsWith("/jobs/")) {
            showJobs(req, resp, pathInfo);
        } else if (pathInfo.equals("/users") || pathInfo.startsWith("/users/")) {
            showUsers(req, resp, pathInfo);
        } else if (pathInfo.equals("/logs")) {
            showLogs(req, resp);
        } else if (pathInfo.equals("/stats")) {
            showStats(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = checkAdminAccess(req, resp);
        if (user == null) {
            return;
        }

        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        String action = req.getParameter("action");

        if (pathInfo != null && pathInfo.equals("/applications/approve") || "approve".equals(action)) {
            handleApprove(req, resp, user);
        } else if (pathInfo != null && pathInfo.equals("/applications/reject") || "reject".equals(action)) {
            handleReject(req, resp, user);
        } else if (pathInfo != null && pathInfo.startsWith("/jobs/")) {
            handleJobAction(req, resp, user, pathInfo);
        } else if (pathInfo != null && pathInfo.startsWith("/users/")) {
            handleUserAction(req, resp, user, pathInfo);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private User checkAdminAccess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            req.getSession().setAttribute("flashError", "请先以 Admin 身份登录");
            resp.sendRedirect(req.getContextPath() + "/home");
            return null;
        }
        return user;
    }

    private void showApplications(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String statusFilter = req.getParameter("status");
        String searchKeyword = req.getParameter("search");
        String jobFilter = req.getParameter("jobId");

        List<Application> applications = applicationService.getAllApplications();

        if (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equals("ALL")) {
            ApplicationStatus status = ApplicationStatus.fromString(statusFilter);
            applications = applications.stream()
                    .filter(a -> a.getStatus() == status)
                    .collect(Collectors.toList());
        }

        if (jobFilter != null && !jobFilter.isBlank()) {
            applications = applications.stream()
                    .filter(a -> a.getJobId().equals(jobFilter))
                    .collect(Collectors.toList());
        }

        if (searchKeyword != null && !searchKeyword.isBlank()) {
            String lower = searchKeyword.toLowerCase();
            List<User> users = userService.getAllUsers();
            Map<String, String> userMap = new HashMap<>();
            for (User u : users) {
                userMap.put(u.getUserId(), u.getDisplayName());
            }
            applications = applications.stream()
                    .filter(a -> a.getApplicationId().toLowerCase().contains(lower)
                            || userMap.getOrDefault(a.getUserId(), "").toLowerCase().contains(lower)
                            || a.getJobId().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        applications.sort((a, b) -> b.getSubmittedAt().compareTo(a.getSubmittedAt()));

        Map<String, String> applicantNames = new HashMap<>();
        Map<String, String> jobTitles = new HashMap<>();
        List<Job> jobs = jobService.getAllJobs();
        for (User u : userService.getAllUsers()) {
            applicantNames.put(u.getUserId(), u.getDisplayName());
        }
        for (Job j : jobs) {
            jobTitles.put(j.getJobId(), j.getTitle());
        }

        req.setAttribute("applications", applications);
        req.setAttribute("jobs", jobs);
        req.setAttribute("applicantNames", applicantNames);
        req.setAttribute("jobTitles", jobTitles);
        req.setAttribute("currentStatus", statusFilter);
        req.setAttribute("currentSearch", searchKeyword);
        req.setAttribute("currentJob", jobFilter);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/applications-manage.jsp").forward(req, resp);
    }

    private void showJobs(HttpServletRequest req, HttpServletResponse resp, String pathInfo) throws ServletException, IOException {
        String statusFilter = req.getParameter("status");
        String searchKeyword = req.getParameter("search");

        List<Job> jobs = jobService.getAllJobs();

        if (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equals("ALL")) {
            JobStatus status = JobStatus.fromString(statusFilter);
            jobs = jobs.stream()
                    .filter(j -> j.getStatus() == status)
                    .collect(Collectors.toList());
        }

        if (searchKeyword != null && !searchKeyword.isBlank()) {
            String lower = searchKeyword.toLowerCase();
            jobs = jobs.stream()
                    .filter(j -> j.getTitle().toLowerCase().contains(lower)
                            || j.getJobId().toLowerCase().contains(lower)
                            || j.getModuleCode().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        jobs.sort((a, b) -> b.getJobId().compareTo(a.getJobId()));

        Map<String, Integer> applicantCounts = new HashMap<>();
        for (Job j : jobs) {
            applicantCounts.put(j.getJobId(), applicationService.countApplicationsByJobId(j.getJobId()));
        }

        req.setAttribute("jobs", jobs);
        req.setAttribute("applicantCounts", applicantCounts);
        req.setAttribute("currentStatus", statusFilter);
        req.setAttribute("currentSearch", searchKeyword);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/jobs-manage.jsp").forward(req, resp);
    }

    private void showUsers(HttpServletRequest req, HttpServletResponse resp, String pathInfo) throws ServletException, IOException {
        String roleFilter = req.getParameter("role");
        String statusFilter = req.getParameter("status");
        String searchKeyword = req.getParameter("search");

        if (roleFilter == null || roleFilter.isBlank()) {
            roleFilter = "TA";
        }

        List<User> allUsers = userService.getAllUsers();

        int taCount = 0, moCount = 0, adminCount = 0;
        for (User u : allUsers) {
            switch (u.getRole()) {
                case TA: taCount++; break;
                case MO: moCount++; break;
                case ADMIN: adminCount++; break;
            }
        }
        req.setAttribute("taCount", taCount);
        req.setAttribute("moCount", moCount);
        req.setAttribute("adminCount", adminCount);

        List<User> users = userService.getAllUsers();

        UserRole targetRole = UserRole.valueOf(roleFilter);
        users = users.stream()
                .filter(u -> u.getRole() == targetRole)
                .collect(Collectors.toList());

        if (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equals("ALL")) {
            users = users.stream()
                    .filter(u -> u.getStatus() != null && u.getStatus().equalsIgnoreCase(statusFilter))
                    .collect(Collectors.toList());
        }

        if (searchKeyword != null && !searchKeyword.isBlank()) {
            String lower = searchKeyword.toLowerCase();
            users = users.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(lower)
                            || u.getDisplayName().toLowerCase().contains(lower)
                            || u.getUserId().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        users.sort(Comparator.comparing(User::getUserId).reversed());

        req.setAttribute("users", users);
        req.setAttribute("currentRole", roleFilter);
        req.setAttribute("currentStatus", statusFilter);
        req.setAttribute("currentSearch", searchKeyword);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/users-manage.jsp").forward(req, resp);
    }

    private void showLogs(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String operatorFilter = req.getParameter("operator");
        String typeFilter = req.getParameter("type");
        String searchKeyword = req.getParameter("search");
        String pageStr = req.getParameter("page");
        int page = 1;
        if (pageStr != null && !pageStr.isBlank()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) page = 1;
            } catch (NumberFormatException ignored) {}
        }
        int pageSize = 20;

        List<SystemLog> logs = logService.getAllLogs();

        if (operatorFilter != null && !operatorFilter.isBlank()) {
            logs = logs.stream()
                    .filter(l -> l.getOperatorId().equals(operatorFilter))
                    .collect(Collectors.toList());
        }

        if (typeFilter != null && !typeFilter.isBlank() && !typeFilter.equals("ALL")) {
            logs = logs.stream()
                    .filter(l -> l.getOperationType().equals(typeFilter))
                    .collect(Collectors.toList());
        }

        if (searchKeyword != null && !searchKeyword.isBlank()) {
            String lower = searchKeyword.toLowerCase();
            logs = logs.stream()
                    .filter(l -> (l.getOperatorName() != null && l.getOperatorName().toLowerCase().contains(lower))
                            || (l.getDetails() != null && l.getDetails().toLowerCase().contains(lower))
                            || (l.getTargetId() != null && l.getTargetId().toLowerCase().contains(lower)))
                    .collect(Collectors.toList());
        }

        int totalPages = (int) Math.ceil((double) logs.size() / pageSize);
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, logs.size());
        List<SystemLog> paginatedLogs = start < logs.size() ? logs.subList(start, end) : new ArrayList<>();

        List<User> admins = userService.getAllUsers().stream()
                .filter(u -> u.getRole() == UserRole.ADMIN || u.getRole() == UserRole.MO)
                .collect(Collectors.toList());

        req.setAttribute("logs", paginatedLogs);
        req.setAttribute("admins", admins);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("currentOperator", operatorFilter);
        req.setAttribute("currentType", typeFilter);
        req.setAttribute("currentSearch", searchKeyword);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/logs.jsp").forward(req, resp);
    }

    private void showStats(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> stats = adminService.getDashboardStats();
        req.setAttribute("stats", stats);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/stats.jsp").forward(req, resp);
    }

    private void handleApprove(HttpServletRequest req, HttpServletResponse resp, User admin) throws IOException {
        String applicationId = req.getParameter("applicationId");
        if (applicationId == null || applicationId.isBlank()) {
            req.getSession().setAttribute("flashError", "申请ID不能为空");
            resp.sendRedirect(req.getContextPath() + "/admin/applications");
            return;
        }

        try {
            applicationService.updateStatus(applicationId, ApplicationStatus.ACCEPTED);
            Application app = applicationService.getAllApplications().stream()
                    .filter(a -> a.getApplicationId().equals(applicationId))
                    .findFirst().orElse(null);

            logService.log(admin.getUserId(), admin.getDisplayName(), "APPROVE", "Application",
                    applicationId, "批准申请 " + applicationId, getClientIP(req));

            req.getSession().setAttribute("flashSuccess", "申请已批准");
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "操作失败：" + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/applications");
    }

    private void handleReject(HttpServletRequest req, HttpServletResponse resp, User admin) throws IOException {
        String applicationId = req.getParameter("applicationId");
        String reason = req.getParameter("rejectReason");

        if (applicationId == null || applicationId.isBlank()) {
            req.getSession().setAttribute("flashError", "申请ID不能为空");
            resp.sendRedirect(req.getContextPath() + "/admin/applications");
            return;
        }

        if (reason == null || reason.isBlank()) {
            req.getSession().setAttribute("flashError", "拒绝理由不能为空");
            resp.sendRedirect(req.getContextPath() + "/admin/applications");
            return;
        }

        try {
            applicationService.updateStatus(applicationId, ApplicationStatus.REJECTED);

            logService.log(admin.getUserId(), admin.getDisplayName(), "REJECT", "Application",
                    applicationId, "拒绝申请 " + applicationId + "，原因：" + reason, getClientIP(req));

            req.getSession().setAttribute("flashSuccess", "申请已拒绝");
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "操作失败：" + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/applications");
    }

    private void handleJobAction(HttpServletRequest req, HttpServletResponse resp, User admin, String pathInfo) throws IOException {
        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            String jobId = req.getParameter("jobId");
            if (jobId != null && !jobId.isBlank()) {
                jobService.deleteJob(jobId);
                logService.log(admin.getUserId(), admin.getDisplayName(), "DELETE", "Job",
                        jobId, "删除职位 " + jobId, getClientIP(req));
                req.getSession().setAttribute("flashSuccess", "职位已删除");
            }
        } else if ("toggle".equals(action)) {
            String jobId = req.getParameter("jobId");
            if (jobId != null && !jobId.isBlank()) {
                jobService.toggleJobStatus(jobId);
                String newStatus = jobService.findById(jobId).getStatus().name();
                logService.log(admin.getUserId(), admin.getDisplayName(), newStatus.equals("OPEN") ? "ENABLE" : "DISABLE",
                        "Job", jobId, (newStatus.equals("OPEN") ? "启用" : "禁用") + "职位 " + jobId, getClientIP(req));
                req.getSession().setAttribute("flashSuccess", "职位状态已更新");
            }
        } else if ("create".equals(action) || "update".equals(action)) {
            handleJobSave(req, resp, admin, action);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/jobs");
    }

    private void handleJobSave(HttpServletRequest req, HttpServletResponse resp, User admin, String action) throws IOException {
        try {
            String jobId = req.getParameter("jobId");
            String title = req.getParameter("title");
            String moduleCode = req.getParameter("moduleCode");
            String organiser = req.getParameter("organiser");
            String hoursStr = req.getParameter("hours");
            String minYearStr = req.getParameter("minYear");
            String maxYearStr = req.getParameter("maxYear");
            String requiredSkills = req.getParameter("requiredSkills");
            String deadline = req.getParameter("deadline");
            String vacanciesStr = req.getParameter("vacancies");

            int hours = Integer.parseInt(hoursStr);
            int minYear = Integer.parseInt(minYearStr);
            int maxYear = Integer.parseInt(maxYearStr);
            int vacancies = Integer.parseInt(vacanciesStr);

            if ("create".equals(action)) {
                jobService.createJob(title, moduleCode, organiser, minYear, maxYear, hours, requiredSkills, deadline, vacancies);
                logService.log(admin.getUserId(), admin.getDisplayName(), "CREATE", "Job",
                        title, "创建职位：" + title, getClientIP(req));
                req.getSession().setAttribute("flashSuccess", "职位创建成功");
            } else {
                jobService.updateJob(jobId, title, moduleCode, organiser, minYear, maxYear, hours, requiredSkills, deadline, vacancies);
                logService.log(admin.getUserId(), admin.getDisplayName(), "UPDATE", "Job",
                        jobId, "更新职位：" + title, getClientIP(req));
                req.getSession().setAttribute("flashSuccess", "职位更新成功");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "操作失败：" + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/jobs");
    }

    private void handleUserAction(HttpServletRequest req, HttpServletResponse resp, User admin, String pathInfo) throws IOException {
        String action = req.getParameter("action");

        if ("toggle".equals(action)) {
            String userId = req.getParameter("userId");
            if (userId != null && !userId.isBlank()) {
                userService.toggleUserStatus(userId);
                User target = userService.findById(userId);
                String newStatus = target != null ? target.getStatus() : "UNKNOWN";
                logService.log(admin.getUserId(), admin.getDisplayName(), newStatus.equals("ACTIVE") ? "ENABLE" : "DISABLE",
                        "User", userId, (newStatus.equals("ACTIVE") ? "启用" : "禁用") + "用户 " + userId, getClientIP(req));
                req.getSession().setAttribute("flashSuccess", "用户状态已更新");
            }
        } else if ("create".equals(action)) {
            try {
                String username = req.getParameter("username");
                String password = req.getParameter("password");
                String name = req.getParameter("name");
                String email = req.getParameter("email");
                String role = req.getParameter("role");
                String yearStr = req.getParameter("year");
                String major = req.getParameter("major");
                String skills = req.getParameter("skills");

                int year = 0;
                if (yearStr != null && !yearStr.isBlank()) {
                    year = Integer.parseInt(yearStr);
                }

                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setName(name);
                newUser.setEmail(email);
                newUser.setRole(UserRole.valueOf(role));
                newUser.setYear(year);
                newUser.setMajor(major);
                newUser.setSkills(skills);
                newUser.setStatus("ACTIVE");

                userService.registerUser(newUser);

                logService.log(admin.getUserId(), admin.getDisplayName(), "CREATE", "User",
                        username, "创建用户：" + username + " (角色:" + role + ")", getClientIP(req));
                req.getSession().setAttribute("flashSuccess", "用户创建成功");
            } catch (Exception e) {
                req.getSession().setAttribute("flashError", "创建失败：" + e.getMessage());
            }
        } else if ("changePassword".equals(action)) {
            try {
                String userId = req.getParameter("userId");
                String oldPassword = req.getParameter("oldPassword");
                String newPassword = req.getParameter("newPassword");

                if (!oldPassword.equals(admin.getPassword())) {
                    req.getSession().setAttribute("flashError", "原密码错误");
                    resp.sendRedirect(req.getContextPath() + "/admin/users?role=ADMIN");
                    return;
                }

                if (newPassword == null || newPassword.length() < 6) {
                    req.getSession().setAttribute("flashError", "新密码至少需要6位");
                    resp.sendRedirect(req.getContextPath() + "/admin/users?role=ADMIN");
                    return;
                }

                userService.updatePassword(userId, newPassword);

                logService.log(admin.getUserId(), admin.getDisplayName(), "UPDATE", "User",
                        userId, "修改密码", getClientIP(req));
                req.getSession().setAttribute("flashSuccess", "密码修改成功");
                req.getSession().setAttribute("currentUser", userService.findById(userId));
            } catch (Exception e) {
                req.getSession().setAttribute("flashError", "操作失败：" + e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/users?role=ADMIN");
            return;
        }

        String role = req.getParameter("role");
        if (role == null || role.isBlank()) role = "TA";
        resp.sendRedirect(req.getContextPath() + "/admin/users?role=" + role);
    }

    private String getClientIP(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        return ip;
    }
}
