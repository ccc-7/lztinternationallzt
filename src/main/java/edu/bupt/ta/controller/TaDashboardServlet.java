package edu.bupt.ta.controller;

import edu.bupt.ta.model.Application;
import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.ApplicationService;
import edu.bupt.ta.service.DashboardService;
import edu.bupt.ta.service.JobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/ta/dashboard")
public class TaDashboardServlet extends HttpServlet {

    private final DashboardService dashboardService = new DashboardService();
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = getCurrentUser(req);
        if (user == null || user.getRole() != UserRole.TA) {
            req.getSession().setAttribute("flashError", "please log in as a TA to access the dashboard.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.setAttribute("pendingCount", dashboardService.getPendingCount(user));
        req.setAttribute("matchedJobs", dashboardService.getMatchedJobs(user));
        req.setAttribute("todoCount", dashboardService.getTodoCount(user));
        req.setAttribute("bestMatchMessage", dashboardService.getBestMatchMessage(user));

        List<Application> myApps = applicationService.getApplicationsByUserId(user.getUserId());
        int totalApplications = myApps.size();
        int acceptedApplications = 0;
        int rejectedApplications = 0;
        for (Application app : myApps) {
            switch (app.getStatus()) {
                case ACCEPTED: acceptedApplications++; break;
                case REJECTED: rejectedApplications++; break;
            }
        }

        List<Job> allOpenJobs = jobService.getOpenJobs();
        int totalOpenPositions = allOpenJobs.size();

        List<Job> myMatchedJobs = jobService.getOpenJobsForUser(user);
        int matchCount = (int) myMatchedJobs.stream().filter(j -> j.getMatchScore() >= 60).count();

        Map<String, Integer> jobApplicationCounts = new HashMap<>();
        for (Job j : allOpenJobs) {
            jobApplicationCounts.put(j.getJobId(), applicationService.countApplicationsByJobId(j.getJobId()));
        }

        List<Map.Entry<String, Integer>> topJobs = jobApplicationCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        Map<String, String> jobTitles = new HashMap<>();
        for (Job j : allOpenJobs) {
            jobTitles.put(j.getJobId(), j.getTitle());
        }

        req.setAttribute("totalApplications", totalApplications);
        req.setAttribute("acceptedApplications", acceptedApplications);
        req.setAttribute("rejectedApplications", rejectedApplications);
        req.setAttribute("totalOpenPositions", totalOpenPositions);
        req.setAttribute("matchCount", matchCount);
        req.setAttribute("topJobs", topJobs);
        req.setAttribute("jobTitles", jobTitles);
        req.setAttribute("myApplications", myApps.stream().limit(5).collect(Collectors.toList()));

        req.getRequestDispatcher("/WEB-INF/jsp/ta/dashboard.jsp").forward(req, resp);
    }

    private User getCurrentUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("currentUser");
    }
}