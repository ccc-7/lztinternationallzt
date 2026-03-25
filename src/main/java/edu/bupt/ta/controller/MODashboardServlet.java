package edu.bupt.ta.controller;

import edu.bupt.ta.model.ApplicationStatus;
import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.ApplicationService;
import edu.bupt.ta.service.JobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/mo/dashboard")
public class MODashboardServlet extends HttpServlet {

    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.MO) {
            req.getSession().setAttribute("flashError", "请先以 MO 身份登录");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.setAttribute("jobCount", jobService.countJobsByOrganiser(user.getDisplayName()));
        req.setAttribute("pendingCount", applicationService.countAllByStatus(ApplicationStatus.PENDING));
        req.setAttribute("acceptedCount", applicationService.countAllByStatus(ApplicationStatus.ACCEPTED));

        req.getRequestDispatcher("/WEB-INF/jsp/mo/dashboard.jsp").forward(req, resp);
    }
}