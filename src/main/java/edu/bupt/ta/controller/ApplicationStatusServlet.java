package edu.bupt.ta.controller;

import edu.bupt.ta.model.Application;
import edu.bupt.ta.model.ApplicationWithJob;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Displays the current TA's submitted applications, enriched with job details
 * (title, module code, organiser) via {@link edu.bupt.ta.model.ApplicationWithJob}.
 * Supports POST requests for withdrawing or deleting own applications.
 *
 * @see edu.bupt.ta.service.ApplicationService#getApplicationsByUserId
 */
@WebServlet("/applications")
public class ApplicationStatusServlet extends HttpServlet {

    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.TA) {
            req.getSession().setAttribute("flashError", "please log in as a TA to view your applications.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        List<Application> applications = applicationService.getApplicationsByUserId(user.getUserId());
        List<ApplicationWithJob> applicationsWithJob = new ArrayList<>();

        for (Application app : applications) {
            var job = jobService.findById(app.getJobId());
            if (job != null) {
                applicationsWithJob.add(new ApplicationWithJob(
                        app,
                        job.getTitle(),
                        job.getModuleCode(),
                        job.getOrganiser()
                ));
            } else {
                applicationsWithJob.add(new ApplicationWithJob(
                        app,
                        "Unknown Job",
                        "-",
                        "-"
                ));
            }
        }

        req.setAttribute("applications", applicationsWithJob);
        req.getRequestDispatcher("/WEB-INF/jsp/ta/applications.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.TA) {
            req.getSession().setAttribute("flashError", "please log in as a TA to perform this action.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String action = req.getParameter("action");
        String applicationId = req.getParameter("applicationId");

        if (applicationId == null || applicationId.isBlank()) {
            req.getSession().setAttribute("flashError", "Invalid application ID.");
            resp.sendRedirect(req.getContextPath() + "/applications");
            return;
        }

        try {
            if ("withdraw".equals(action)) {
                applicationService.withdrawApplication(applicationId, user.getUserId());
                req.getSession().setAttribute("flashSuccess", "Application withdrawn successfully.");
            } else if ("delete".equals(action)) {
                applicationService.deleteOwnApplication(applicationId, user.getUserId());
                req.getSession().setAttribute("flashSuccess", "Application deleted successfully.");
            } else {
                req.getSession().setAttribute("flashError", "Unknown action.");
            }
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/applications");
    }
}
