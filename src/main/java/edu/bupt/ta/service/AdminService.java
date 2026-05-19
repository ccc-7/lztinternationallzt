package edu.bupt.ta.service;

import edu.bupt.ta.model.Application;
import edu.bupt.ta.model.ApplicationStatus;
import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.JobStatus;
import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;

import java.util.*;

/**
 * Provides administrative operations: dashboard statistics, user workload analysis,
 * and bulk user management. Delegates to UserService, ApplicationService, and JobService.
 */
public class AdminService {

    private UserService userService;
    private ApplicationService applicationService;
    private JobService jobService;

    /**
     * Default constructor. Uses default service instances.
     */
    public AdminService() {
        this.userService = new UserService();
        this.applicationService = new ApplicationService();
        this.jobService = new JobService();
    }

    /**
     * Constructor with injected FileStorageUtil path. Used by tests.
     *
     * @param dataDir the data directory for CSV files
     * @param mirrorDir the mirror directory (may be null)
     */
    public AdminService(java.nio.file.Path dataDir, java.nio.file.Path mirrorDir) {
        this.userService = new UserService(dataDir, mirrorDir);
        this.applicationService = new ApplicationService(dataDir, mirrorDir);
        this.jobService = new JobService(dataDir, mirrorDir);
    }

    /**
     * Constructor with injected FileStorageUtil. Used by tests to share storage.
     *
     * @param storage the FileStorageUtil instance to use
     */
    public AdminService(edu.bupt.ta.storage.FileStorageUtil storage) {
        this.userService = new UserService(storage);
        this.applicationService = new ApplicationService(storage);
        this.jobService = new JobService(storage);
    }

    /**
     * Returns a map of TA user IDs to total accepted workload hours.
     * Only {@link ApplicationStatus#ACCEPTED} applications contribute; each accepted
     * application adds the linked job's weekly hours.
     *
     * @return a map from userId to total accepted hours
     */
    public Map<String, Integer> calculateUserWorkloads() {
        List<User> users = userService.getAllUsers();
        List<Application> applications = applicationService.getAllApplications();

        Map<String, Integer> workloads = new LinkedHashMap<>();
        for (User user : users) {
            if (user.getRole() == UserRole.TA) {
                workloads.put(user.getUserId(), 0);
            }
        }

        for (Application app : applications) {
            if (app.getStatus() != ApplicationStatus.ACCEPTED) {
                continue;
            }
            if (!workloads.containsKey(app.getUserId())) {
                continue;
            }
            Job job = jobService.findById(app.getJobId());
            if (job != null) {
                workloads.merge(app.getUserId(), job.getHours(), Integer::sum);
            }
        }

        return workloads;
    }

    /**
     * Classifies total accepted workload hours into a display level.
     *
     * @param totalHours total accepted hours for a TA
     * @return {@code Normal} (0–20), {@code Warning} (21–40), or {@code Overloaded} (&gt;40)
     */
    public static String getWorkloadLevel(int totalHours) {
        if (totalHours > 40) {
            return "Overloaded";
        }
        if (totalHours > 20) {
            return "Warning";
        }
        return "Normal";
    }

    /**
     * Returns a comprehensive set of dashboard statistics:
     * TA/MO counts, application counts by status, job counts, and the top 5 TAs
     * and top 3 jobs ranked by application count.
     *
     * @return a map of statistic name to value
     */
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
        int interviewApps = 0;
        int acceptedApps = 0;
        int rejectedApps = 0;
        for (Application a : allApps) {
            switch (a.getStatus()) {
                case PENDING: pendingApps++; break;
                case INTERVIEW: interviewApps++; break;
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
                .limit(5)
                .forEachOrdered(e -> topJobs.put(e.getKey(), e.getValue()));

        Map<String, String> topJobTitles = new LinkedHashMap<>();
        for (String jobId : topJobs.keySet()) {
            Job job = jobService.findById(jobId);
            topJobTitles.put(jobId, job != null ? job.getTitle() : jobId);
        }

        stats.put("totalTA", totalTA);
        stats.put("activeTA", activeTA);
        stats.put("totalMO", totalMO);
        stats.put("totalApplications", totalApps);
        stats.put("pendingApplications", pendingApps);
        stats.put("interviewApplications", interviewApps);
        stats.put("acceptedApplications", acceptedApps);
        stats.put("rejectedApplications", rejectedApps);
        stats.put("totalJobs", totalJobs);
        stats.put("openJobs", openJobs);
        stats.put("topTAs", topTAs);
        stats.put("topJobs", topJobs);
        stats.put("topJobTitles", topJobTitles);

        return stats;
    }

    /**
     * Toggles a user's status between ACTIVE and INACTIVE.
     *
     * @param userId the user ID to toggle
     */
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

    /**
     * Directly persists a user list to CSV, bypassing individual field updates.
     * Used by the Admin user management page.
     *
     * @param users the complete user list to save
     */
    public void saveUsersDirect(List<User> users) {
        userService.saveUsersDirect(users);
    }
}