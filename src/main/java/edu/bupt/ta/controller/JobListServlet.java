package edu.bupt.ta.controller;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.JobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/jobs")
public class JobListServlet extends HttpServlet {

    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.TA) {
            req.getSession().setAttribute("flashError", "please log in as a TA to view available jobs.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.setAttribute("jobs", jobService.getOpenJobsForUser(user));
        req.getRequestDispatcher("/WEB-INF/jsp/ta/jobs.jsp").forward(req, resp);
    }
}