package edu.bupt.ta.controller;

import edu.bupt.ta.model.Job;
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
 * Handles MO job creation and editing. GET shows the form; POST creates or updates a job via
 * {@link edu.bupt.ta.service.JobService}.
 *
 * @see edu.bupt.ta.service.JobService#createJob
 * @see edu.bupt.ta.service.JobService#updateJob
 */
@WebServlet(urlPatterns = {
    "/mo/jobs/new",
    "/mo/jobs/edit/*"
})
public class MOJobServlet extends HttpServlet {

    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.MO) {
            req.getSession().setAttribute("flashError", "please log in as a MO to access job management.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();

        // Edit mode: /mo/jobs/edit/{jobId}
        if (servletPath.equals("/mo/jobs/edit") && pathInfo != null && pathInfo.length() > 1) {
            String jobId = pathInfo.substring(1);
            Job job = jobService.findById(jobId);
            if (job == null) {
                req.getSession().setAttribute("flashError", "Job not found");
                resp.sendRedirect(req.getContextPath() + "/mo/dashboard");
                return;
            }
            req.setAttribute("job", job);
            req.setAttribute("isEdit", true);
        } else {
            req.setAttribute("isEdit", false);
        }

        req.getRequestDispatcher("/WEB-INF/jsp/mo/new-job.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.MO) {
            req.getSession().setAttribute("flashError", "please log in as a MO to manage jobs.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();
        boolean isEdit = servletPath.equals("/mo/jobs/edit") && pathInfo != null && pathInfo.length() > 1;

        try {
            String jobId = isEdit ? pathInfo.substring(1) : null;
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

            if (organiser == null || organiser.isBlank()) {
                organiser = user.getDisplayName();
            }

            if (isEdit) {
                jobService.updateJob(jobId, title, moduleCode, organiser, minYear, maxYear, hours, requiredSkills, deadline, vacancies);
                req.getSession().setAttribute("flashSuccess", "Job updated successfully");
            } else {
                jobService.createJob(title, moduleCode, organiser, minYear, maxYear, hours, requiredSkills, deadline, vacancies);
                req.getSession().setAttribute("flashSuccess", "Job created successfully");
            }
            resp.sendRedirect(req.getContextPath() + "/mo/dashboard");
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Operation failed: " + e.getMessage());
            if (isEdit) {
                resp.sendRedirect(req.getContextPath() + "/mo/jobs/edit" + pathInfo);
            } else {
                resp.sendRedirect(req.getContextPath() + "/mo/jobs/new");
            }
        }
    }
}