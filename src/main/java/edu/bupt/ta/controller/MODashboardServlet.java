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
import java.util.List;
import java.util.Map;

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

        req.setAttribute("totalJobs", jobService.countTotalJobs());
        req.setAttribute("activeJobs", jobService.countActiveJobs());
        req.setAttribute("totalApplicants", applicationService.countTotalApplications());
        req.setAttribute("jobs", jobService.getAllJobs());

        List<Job> jobs = jobService.getAllJobs();
        Map<String, Integer> applicationCounts = new HashMap<>();
        for (Job job : jobs) {
            applicationCounts.put(job.getJobId(), applicationService.countApplicationsByJobId(job.getJobId()));
        }
        req.setAttribute("applicationCounts", applicationCounts);

        req.getRequestDispatcher("/WEB-INF/jsp/mo/dashboard.jsp").forward(req, resp);
    }
}