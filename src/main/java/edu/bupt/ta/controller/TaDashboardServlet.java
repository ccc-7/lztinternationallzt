package edu.bupt.ta.controller;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.DashboardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/ta/dashboard")
public class TaDashboardServlet extends HttpServlet {

    private final DashboardService dashboardService = new DashboardService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = getCurrentUser(req);
        if (user == null || user.getRole() != UserRole.TA) {
            req.getSession().setAttribute("flashError", "请先以 TA 身份登录");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.setAttribute("pendingCount", dashboardService.getPendingCount(user));
        req.setAttribute("matchedJobs", dashboardService.getMatchedJobs(user));
        req.setAttribute("todoCount", dashboardService.getTodoCount(user));
        req.setAttribute("bestMatchMessage", dashboardService.getBestMatchMessage(user));

        req.getRequestDispatcher("/WEB-INF/jsp/ta/dashboard.jsp").forward(req, resp);
    }

    private User getCurrentUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("currentUser");
    }
}