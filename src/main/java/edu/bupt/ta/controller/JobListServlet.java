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
import java.util.List;

/**
 * Lists all open jobs for the current TA, sorted by match score descending.
 *
 * @see edu.bupt.ta.service.JobService#getOpenJobsForUser
 */
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

        String search = req.getParameter("search");
        String moduleCode = req.getParameter("moduleCode");
        Integer minMatchScore = parseMinMatchScore(req.getParameter("minMatchScore"));
        List<edu.bupt.ta.model.Job> jobs = jobService.getOpenJobsForUser(user, search, moduleCode, minMatchScore);

        req.setAttribute("jobs", jobs);
        req.setAttribute("jobsCount", jobs.size());
        req.setAttribute("search", search == null ? "" : search);
        req.setAttribute("moduleCode", moduleCode == null ? "" : moduleCode);
        req.setAttribute("minMatchScore", minMatchScore == null ? "" : minMatchScore);
        req.getRequestDispatcher("/WEB-INF/jsp/ta/jobs.jsp").forward(req, resp);
    }

    private Integer parseMinMatchScore(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
