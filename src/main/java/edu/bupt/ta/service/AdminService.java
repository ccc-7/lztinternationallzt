package edu.bupt.ta.service;

import edu.bupt.ta.model.Application;
import edu.bupt.ta.model.ApplicationStatus;
import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.JobStatus;
import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;

import java.util.*;

public class AdminService {

    private final UserService userService = new UserService();
    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();

    public Map<String, Integer> calculateUserWorkloads() {
        List<User> users = userService.getAllUsers();
        List<Application> applications = applicationService.getAllApplications();

        Map<String, Integer> workloads = new LinkedHashMap<>();

        for (User user : users) {
            if (user.getRole() == UserRole.TA) {
                int count = 0;
                for (Application app : applications) {
                    if (app.getUserId().equals(user.getUserId())) {
                        count++;
                    }
                }
                workloads.put(user.getUserId(), count);
            }
        }

        return workloads;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        List<User> allUsers = userService.getAllUsers();
        int totalTA = 0;
        int totalMO = 0;
        int activeTA = 0;
        for (User u : allUsers) {
            if (u.getRole() == UserRole.TA) {
                totalTA++;
                if ("ACTIVE".equals(u.getStatus())) {
                    activeTA++;
                }
            } else if (u.getRole() == UserRole.MO) {
                totalMO++;
            }
        }

        List<Application> allApps = applicationService.getAllApplications();
        int totalApps = allApps.size();
        int pendingApps = 0;
        int acceptedApps = 0;
        int rejectedApps = 0;
        for (Application a : allApps) {
            switch (a.getStatus()) {
                case PENDING: pendingApps++; break;
                case ACCEPTED: acceptedApps++; break;
                case REJECTED: rejectedApps++; break;
            }
        }

        List<Job> allJobs = jobService.getAllJobs();
        int totalJobs = allJobs.size();
        int openJobs = 0;
        for (Job j : allJobs) {
            if (j.getStatus() == JobStatus.OPEN) {
                openJobs++;
            }
        }

        Map<String, Integer> topTAs = new LinkedHashMap<>();
        Map<String, Integer> appCounts = new HashMap<>();
        for (Application a : allApps) {
            appCounts.merge(a.getUserId(), 1, Integer::sum);
        }
        appCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEachOrdered(e -> topTAs.put(e.getKey(), e.getValue()));

        Map<String, Integer> topJobs = new LinkedHashMap<>();
        Map<String, Integer> jobAppCounts = new HashMap<>();
        for (Application a : allApps) {
            jobAppCounts.merge(a.getJobId(), 1, Integer::sum);
        }
        jobAppCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .forEachOrdered(e -> topJobs.put(e.getKey(), e.getValue()));

        stats.put("totalTA", totalTA);
        stats.put("activeTA", activeTA);
        stats.put("totalMO", totalMO);
        stats.put("totalApplications", totalApps);
        stats.put("pendingApplications", pendingApps);
        stats.put("acceptedApplications", acceptedApps);
        stats.put("rejectedApplications", rejectedApps);
        stats.put("totalJobs", totalJobs);
        stats.put("openJobs", openJobs);
        stats.put("topTAs", topTAs);
        stats.put("topJobs", topJobs);

        return stats;
    }

    public void toggleUserStatus(String userId) {
        User user = userService.findById(userId);
        if (user != null) {
            user.setStatus("ACTIVE".equals(user.getStatus()) ? "INACTIVE" : "ACTIVE");
            List<User> users = userService.getAllUsers();
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUserId().equals(userId)) {
                    users.set(i, user);
                    break;
                }
            }
            userService.saveUsersDirect(users);
        }
    }

    public void saveUsersDirect(List<User> users) {
        userService.saveUsersDirect(users);
    }
}