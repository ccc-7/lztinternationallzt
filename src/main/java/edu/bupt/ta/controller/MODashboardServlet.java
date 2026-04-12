package edu.bupt.ta.controller;

import edu.bupt.ta.model.ApplicationStatus;
import edu.bupt.ta.model.Job;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet("/mo/dashboard")
public class MODashboardServlet extends HttpServlet {

    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.MO) {
            req.getSession().setAttribute("flashError", "please log in as an MO to view the dashboard");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        List<Job> myJobs = jobService.getJobsByOrganiser(user.getDisplayName());
        Set<String> myJobIds = new HashSet<>();
        for (Job job : myJobs) {
            myJobIds.add(job.getJobId());
        }

        req.setAttribute("jobCount", myJobs.size());
        req.setAttribute("pendingCount", applicationService.countApplicationsByJobIdsAndStatus(myJobIds, ApplicationStatus.PENDING));
        req.setAttribute("acceptedCount", applicationService.countApplicationsByJobIdsAndStatus(myJobIds, ApplicationStatus.ACCEPTED));

        req.setAttribute("totalJobs", myJobs.size());
        req.setAttribute("activeJobs", jobService.countActiveJobsByOrganiser(user.getDisplayName()));
        req.setAttribute("totalApplicants", applicationService.countApplicationsByJobIds(myJobIds));
        req.setAttribute("jobs", myJobs);

        Map<String, Integer> applicationCounts = new HashMap<>();
        for (Job job : myJobs) {
            applicationCounts.put(job.getJobId(), applicationService.countApplicationsByJobId(job.getJobId()));
        }
        req.setAttribute("applicationCounts", applicationCounts);

        req.getRequestDispatcher("/WEB-INF/jsp/mo/dashboard.jsp").forward(req, resp);
    }
}
