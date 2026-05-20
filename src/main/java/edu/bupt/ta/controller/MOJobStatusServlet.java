package edu.bupt.ta.controller;

import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.JobStatus;
import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.JobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Handles closing and opening (reopening) job postings.
 * Accessible only by Module Organisers (MO).
 */
@WebServlet(urlPatterns = {"/mo/jobs/close/*", "/mo/jobs/open/*"})
public class MOJobStatusServlet extends HttpServlet {

    private final JobService jobService = new JobService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.MO) {
            req.getSession().setAttribute("flashError", "Please log in as a MO to manage job status.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.length() <= 1) {
            req.getSession().setAttribute("flashError", "Invalid job ID");
            resp.sendRedirect(req.getContextPath() + "/mo/dashboard");
            return;
        }

        String jobId = pathInfo.substring(1);
        Job job = jobService.findById(jobId);

        if (job == null) {
            req.getSession().setAttribute("flashError", "Job not found");
            resp.sendRedirect(req.getContextPath() + "/mo/dashboard");
            return;
        }

        // Verify ownership - MO can only modify their own jobs
        if (!user.getDisplayName().equals(job.getOrganiser())) {
            req.getSession().setAttribute("flashError", "You can only modify your own jobs");
            resp.sendRedirect(req.getContextPath() + "/mo/dashboard");
            return;
        }

        try {
            JobStatus currentStatus = job.getStatus();
            JobStatus targetStatus = servletPath.equals("/mo/jobs/close") ? JobStatus.CLOSED : JobStatus.OPEN;

            // Only toggle if status needs to change
            if (currentStatus != targetStatus) {
                jobService.toggleJobStatus(jobId);
                String action = servletPath.equals("/mo/jobs/close") ? "closed" : "reopened";
                req.getSession().setAttribute("flashSuccess", "Job " + action + " successfully");
            }
            resp.sendRedirect(req.getContextPath() + "/mo/dashboard");
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Operation failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/mo/dashboard");
        }
    }
}
