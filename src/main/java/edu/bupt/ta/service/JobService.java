package edu.bupt.ta.service;

import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.JobStatus;
import edu.bupt.ta.model.User;
import edu.bupt.ta.storage.FileStorageUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JobService {

    private final FileStorageUtil storage = new FileStorageUtil();

    public List<Job> getAllJobs() {
        return storage.loadJobs();
    }

    public List<Job> getOpenJobs() {
        List<Job> result = new ArrayList<>();
        for (Job job : storage.loadJobs()) {
            if (job.getStatus() == JobStatus.OPEN) {
                result.add(job);
            }
        }
        return result;
    }

    public List<Job> getOpenJobsForUser(User user) {
        List<Job> jobs = getOpenJobs();
        for (Job job : jobs) {
            int score = calculateMatchScore(user == null ? "" : user.getSkills(), job.getRequiredSkills());
            if (score > 0) {
                job.setMatchScore(score);
            }
        }
        return jobs;
    }

    public Job findById(String jobId) {
        return storage.loadJobs().stream()
                .filter(j -> j.getJobId().equals(jobId))
                .findFirst()
                .orElse(null);
    }

    public Job createJob(String title, String moduleCode, String organiser, int minYear,
                         int maxYear, int hours, String requiredSkills, String deadline, int vacancies) {
        List<Job> jobs = storage.loadJobs();

        Job job = new Job();
        job.setJobId(nextJobId(jobs));
        job.setTitle(title);
        job.setModuleCode(moduleCode);
        job.setOrganiser(organiser);
        job.setMinYear(minYear);
        job.setMaxYear(maxYear);
        job.setHours(hours);
        job.setStatus(JobStatus.OPEN);
        job.setRequiredSkills(normalizeSkills(requiredSkills));
        job.setMatchScore(0);
        job.setDeadline(deadline);
        job.setVacancies(vacancies);

        jobs.add(job);
        storage.saveJobs(jobs);
        return job;
    }

    public int countTotalJobs() {
        return storage.loadJobs().size();
    }

    public int countActiveJobs() {
        int count = 0;
        for (Job job : storage.loadJobs()) {
            if (job.getStatus() == JobStatus.OPEN) {
                count++;
            }
        }
        return count;
    }

    public void updateJob(String jobId, String title, String moduleCode, String organiser,
                         int minYear, int maxYear, int hours, String requiredSkills,
                         String deadline, int vacancies) {
        List<Job> jobs = storage.loadJobs();
        for (Job job : jobs) {
            if (job.getJobId().equals(jobId)) {
                job.setTitle(title);
                job.setModuleCode(moduleCode);
                job.setOrganiser(organiser);
                job.setMinYear(minYear);
                job.setMaxYear(maxYear);
                job.setHours(hours);
                job.setRequiredSkills(normalizeSkills(requiredSkills));
                job.setDeadline(deadline);
                job.setVacancies(vacancies);
                break;
            }
        }
        storage.saveJobs(jobs);
    }

    public void deleteJob(String jobId) {
        List<Job> jobs = storage.loadJobs();
        jobs.removeIf(job -> job.getJobId().equals(jobId));
        storage.saveJobs(jobs);
    }

    public void toggleJobStatus(String jobId) {
        List<Job> jobs = storage.loadJobs();
        for (Job job : jobs) {
            if (job.getJobId().equals(jobId)) {
                job.setStatus(job.getStatus() == JobStatus.OPEN ? JobStatus.CLOSED : JobStatus.OPEN);
                break;
            }
        }
        storage.saveJobs(jobs);
    }

    public int countAllJobs() {
        return storage.loadJobs().size();
    }

    public int countJobsByOrganiser(String organiser) {
        int count = 0;
        for (Job job : storage.loadJobs()) {
            if (job.getOrganiser() != null && job.getOrganiser().equalsIgnoreCase(organiser)) {
                count++;
            }
        }
        return count;
    }

    public int calculateMatchScore(String userSkills, String requiredSkills) {
        Set<String> userSet = tokenize(userSkills);
        Set<String> jobSet = tokenize(requiredSkills);

        if (jobSet.isEmpty()) {
            return 0;
        }

        int hit = 0;
        for (String skill : jobSet) {
            if (userSet.contains(skill)) {
                hit++;
            }
        }

        return (int) Math.round((hit * 100.0) / jobSet.size());
    }

    private Set<String> tokenize(String skills) {
        Set<String> result = new HashSet<>();
        if (skills == null || skills.isBlank()) {
            return result;
        }
        String[] arr = skills.replace(",", "|").split("\\|");
        for (String s : arr) {
            if (!s.isBlank()) {
                result.add(s.trim().toLowerCase());
            }
        }
        return result;
    }

    private String normalizeSkills(String skills) {
        if (skills == null || skills.isBlank()) {
            return "";
        }
        return skills.replace("，", ",").replace(",", "|").trim();
    }

    private String nextJobId(List<Job> jobs) {
        int max = jobs.stream()
                .map(Job::getJobId)
                .filter(id -> id != null && id.startsWith("J"))
                .map(id -> {
                    try {
                        return Integer.parseInt(id.substring(1));
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .max(Comparator.naturalOrder())
                .orElse(0);
        return String.format("J%03d", max + 1);
    }
}