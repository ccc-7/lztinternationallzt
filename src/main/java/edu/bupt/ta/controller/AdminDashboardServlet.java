package edu.bupt.ta.controller;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.AdminService;
import edu.bupt.ta.service.LogService;
import edu.bupt.ta.service.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Displays the Admin dashboard: workload statistics and summary metrics.
 *
 * @see edu.bupt.ta.service.AdminService#getDashboardStats
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final AdminService adminService = new AdminService();
    private final UserService userService = new UserService();
    private final LogService logService = new LogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.ADMIN) {
            req.getSession().setAttribute("flashError", "Please log in as an administrator to access the dashboard.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        Map<String, Integer> workloads = adminService.calculateUserWorkloads();
        Map<String, Object> stats = adminService.getDashboardStats();

        int maxWorkloadHours = workloads.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
        if (maxWorkloadHours < 1) {
            maxWorkloadHours = 1;
        }

        @SuppressWarnings("unchecked")
        Map<String, Integer> topJobs = (Map<String, Integer>) stats.get("topJobs");
        int maxJobApplicants = 0;
        if (topJobs != null && !topJobs.isEmpty()) {
            maxJobApplicants = topJobs.values().stream()
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);
        }
        if (maxJobApplicants < 1) {
            maxJobApplicants = 1;
        }

        List<User> taUsers = userService.getAllUsers().stream()
                .filter(u -> u.getRole() == UserRole.TA)
                .sorted(Comparator.comparingInt(
                        (User u) -> workloads.getOrDefault(u.getUserId(), 0)).reversed())
                .collect(Collectors.toList());

        req.setAttribute("workloads", workloads);
        req.setAttribute("stats", stats);
        req.setAttribute("taUsers", taUsers);
        req.setAttribute("maxWorkloadHours", maxWorkloadHours);
        req.setAttribute("maxJobApplicants", maxJobApplicants);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp").forward(req, resp);
    }
}