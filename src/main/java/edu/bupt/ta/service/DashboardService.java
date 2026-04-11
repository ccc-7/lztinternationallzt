package edu.bupt.ta.service;

import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.User;

import java.util.Comparator;
import java.util.List;

public class DashboardService {

    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();

    public int getPendingCount(User user) {
        if (user == null) {
            return 0;
        }
        return applicationService.countUserPendingAndInterview(user.getUserId());
    }

    public int getMatchedJobs(User user) {
        if (user == null) {
            return 0;
        }
        int count = 0;
        List<Job> jobs = jobService.getOpenJobsForUser(user);
        for (Job job : jobs) {
            if (job.getMatchScore() >= 60) {
                count++;
            }
        }
        return count;
    }

    public int getTodoCount(User user) {
        return getPendingCount(user);
    }

    public String getBestMatchMessage(User user) {
        if (user == null) {
            return "please login to view recommended jobs.";
        }

        List<Job> jobs = jobService.getOpenJobsForUser(user);
        Job best = jobs.stream()
                .max(Comparator.comparingInt(Job::getMatchScore))
                .orElse(null);

        if (best == null) {
            return "no matching jobs found, please check back later.";
        }

        return "Just posted a 【" + best.getTitle() + "】 vacancy. The system analyzed your historical experience and skills, "
                + "and found that you match this position " + best.getMatchScore() + "% "
                + "in terms of " + displaySkills(best.getRequiredSkills()) + ".";
    }

    private String displaySkills(String skills) {
        if (skills == null) {
            return "";
        }
        return skills.replace("|", "、");
    }
}