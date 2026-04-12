package edu.bupt.ta.controller;

import edu.bupt.ta.model.Application;
import edu.bupt.ta.model.ApplicationStatus;
import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.ApplicationService;
import edu.bupt.ta.service.JobService;
import edu.bupt.ta.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet(urlPatterns = {"/mo/applications", "/mo/applications/update"})
public class MOApplicationServlet extends HttpServlet {

    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.MO) {
            req.getSession().setAttribute("flashError", "please log in as an MO to view applications.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        List<Job> jobs = jobService.getJobsByOrganiser(user.getDisplayName());
        Set<String> myJobIds = new HashSet<>();
        for (Job job : jobs) {
            myJobIds.add(job.getJobId());
        }

        String filterJobId = req.getParameter("jobId");
        List<Application> applications;
        if (filterJobId != null && !filterJobId.isBlank()) {
            if (!myJobIds.contains(filterJobId)) {
                req.getSession().setAttribute("flashError", "you can only view applications for your own jobs.");
                resp.sendRedirect(req.getContextPath() + "/mo/applications");
                return;
            }
            applications = applicationService.getApplicationsByJobId(filterJobId);
            req.setAttribute("filterJobId", filterJobId);
        } else {
            applications = applicationService.getApplicationsByJobIds(myJobIds);
        }

        Map<String, String> jobTitles = new HashMap<>();
        for (Job job : jobs) {
            jobTitles.put(job.getJobId(), job.getTitle());
        }

        Map<String, String> applicantNames = new HashMap<>();
        for (Application app : applications) {
            User applicant = userService.findById(app.getUserId());
            if (applicant != null) {
                applicantNames.put(app.getUserId(), applicant.getDisplayName());
            }
        }

        Map<String, String> statusLabels = new HashMap<>();
        statusLabels.put("PENDING", "Pending");
        statusLabels.put("ACCEPTED", "Accepted");
        statusLabels.put("REJECTED", "Rejected");
        statusLabels.put("INTERVIEW", "Interview");

        req.setAttribute("applications", applications);
        req.setAttribute("jobs", jobs);
        req.setAttribute("jobTitles", jobTitles);
        req.setAttribute("applicantNames", applicantNames);
        req.setAttribute("statusLabels", statusLabels);

        req.getRequestDispatcher("/WEB-INF/jsp/mo/applications.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.MO) {
            req.getSession().setAttribute("flashError", "please log in as an MO to update application status");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String applicationId = req.getParameter("applicationId");
        String status = req.getParameter("status");
        String filterJobId = req.getParameter("filterJobId");

        try {
            Application application = applicationService.findById(applicationId);
            if (application == null) {
                throw new IllegalArgumentException("application not found");
            }

            Job job = jobService.findById(application.getJobId());
            if (job == null || job.getOrganiser() == null || !job.getOrganiser().equalsIgnoreCase(user.getDisplayName())) {
                throw new IllegalArgumentException("you can only update applications for your own jobs");
            }

            applicationService.updateStatus(applicationId, ApplicationStatus.fromString(status));
            req.getSession().setAttribute("flashSuccess", "Application status updated successfully");
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Failed to update status: " + e.getMessage());
        }

        if (filterJobId != null && !filterJobId.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/mo/applications?jobId=" + filterJobId);
        } else {
            resp.sendRedirect(req.getContextPath() + "/mo/applications");
        }
    }
}
