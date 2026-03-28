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
            return "请先登录后查看推荐岗位。";
        }

        List<Job> jobs = jobService.getOpenJobsForUser(user);
        Job best = jobs.stream()
                .max(Comparator.comparingInt(Job::getMatchScore))
                .orElse(null);

        if (best == null) {
            return "当前暂无可推荐岗位，请稍后再查看。";
        }

        return "刚刚发布了一个【" + best.getTitle() + "】空缺。系统分析了你的历史经历与技能，"
                + "发现你在 " + displaySkills(best.getRequiredSkills()) + " 方面与该岗位 "
                + best.getMatchScore() + "% 匹配。";
    }

    private String displaySkills(String skills) {
        if (skills == null) {
            return "";
        }
        return skills.replace("|", "、");
    }
}