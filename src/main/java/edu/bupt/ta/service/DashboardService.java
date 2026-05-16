package edu.bupt.ta.service;

import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.User;

import java.util.Comparator;
import java.util.List;

/**
 * Provides data for the TA dashboard: pending application counts, matched job counts,
 * and personalised best-match messages. Used by TaDashboardServlet.
 */
public class DashboardService {

    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();

    /**
     * Returns the number of active (PENDING or INTERVIEW) applications for the user.
     *
     * @param user the TA user (may be null)
     * @return the count, or 0 if user is null
     */
    public int getPendingCount(User user) {
        if (user == null) {
            return 0;
        }
        return applicationService.countUserPendingAndInterview(user.getUserId());
    }

    /**
     * Counts how many open jobs have a matchScore of 60 or higher for the given user.
     * A score of 60 or above is treated as a "matched" job on the dashboard.
     *
     * @param user the TA user (may be null)
     * @return the count of matched jobs, or 0 if user is null
     */
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

    /**
     * @param user the TA user (may be null)
     * @return the number of active applications (alias of {@link #getPendingCount})
     */
    public int getTodoCount(User user) {
        return getPendingCount(user);
    }

    /**
     * Returns a personalised message describing the highest-scoring job for the user.
     * If no jobs are available, returns a "check back later" message.
     *
     * @param user the TA user (may be null)
     * @return a display message string
     */
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