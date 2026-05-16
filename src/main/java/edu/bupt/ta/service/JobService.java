package edu.bupt.ta.service;

import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.JobStatus;
import edu.bupt.ta.model.User;
import edu.bupt.ta.storage.FileStorageUtil;

import java.util.*;

/**
 * Provides business-logic operations for job postings.
 * Handles job CRUD, filtering by organiser, and the skill-match scoring algorithm
 * used to rank jobs for TA users.
 */
public class JobService {

    private final FileStorageUtil storage = new FileStorageUtil();

    // Skill alias mappings (alternative names -> canonical names)
    private static final Map<String, String> SKILL_ALIASES = new HashMap<>();
    // Skill categories for weighted matching
    private static final Map<String, Set<String>> SKILL_CATEGORIES = new HashMap<>();

    static {
        // Programming languages
        Set<String> languages = new HashSet<>(Arrays.asList(
            "java", "python", "javascript", "js", "typescript", "ts", "c++", "c/c++", "cpp",
            "c", "csharp", "c#", "go", "golang", "rust", "ruby", "php", "swift", "kotlin",
            "scala", "r", "matlab", "lua", "perl", "haskell", "elixir", "clojure"
        ));
        SKILL_CATEGORIES.put("language", languages);

        // Frameworks
        Set<String> frameworks = new HashSet<>(Arrays.asList(
            "react", "vue", "angular", "node.js", "nodejs", "express", "django", "flask",
            "spring", "springboot", "hibernate", "flutter", "react native", "next.js",
            "nuxt", "svelte", "bootstrap", "tailwind", "material ui"
        ));
        SKILL_CATEGORIES.put("framework", frameworks);

        // ML/AI
        Set<String> ml = new HashSet<>(Arrays.asList(
            "machine learning", "ml", "deep learning", "dl", "neural network", "tensorflow",
            "pytorch", "keras", "scikit-learn", "sklearn", "nlp", "computer vision",
            "opencv", "pandas", "numpy", "scipy", "data science", "ai", "artificial intelligence"
        ));
        SKILL_CATEGORIES.put("ml", ml);

        // Databases
        Set<String> databases = new HashSet<>(Arrays.asList(
            "mysql", "postgresql", "mongodb", "redis", "elasticsearch", "sql", "nosql",
            "sqlite", "oracle", "sql server", "dynamodb", "cassandra", "neo4j"
        ));
        SKILL_CATEGORIES.put("database", databases);

        // Tools & DevOps
        Set<String> tools = new HashSet<>(Arrays.asList(
            "git", "github", "docker", "kubernetes", "aws", "azure", "gcp", "jenkins",
            "ci/cd", "linux", "unix", "bash", "shell", "maven", "gradle", "npm", "yarn"
        ));
        SKILL_CATEGORIES.put("tools", tools);

        // Core concepts (higher weight)
        Set<String> concepts = new HashSet<>(Arrays.asList(
            "data structures", "algorithms", "oop", "design patterns", "concurrency",
            "distributed systems", "microservices", "api", "rest", "graphql", "security",
            "testing", "tdd", "agile", "scrum"
        ));
        SKILL_CATEGORIES.put("concept", concepts);

        // Build aliases mappings
        SKILL_ALIASES.put("js", "javascript");
        SKILL_ALIASES.put("ts", "typescript");
        SKILL_ALIASES.put("c/c++", "c++");
        SKILL_ALIASES.put("nodejs", "node.js");
        SKILL_ALIASES.put("springboot", "spring");
        SKILL_ALIASES.put("ml", "machine learning");
        SKILL_ALIASES.put("dl", "deep learning");
        SKILL_ALIASES.put("sklearn", "scikit-learn");
    }

    /**
     * Returns all jobs from the CSV, regardless of status.
     *
     * @return a list of all jobs
     */
    public List<Job> getAllJobs() {
        return storage.loadJobs();
    }

    /**
     * Returns all jobs whose status is OPEN.
     *
     * @return a list of open jobs
     */
    public List<Job> getOpenJobs() {
        List<Job> result = new ArrayList<>();
        for (Job job : storage.loadJobs()) {
            if (job.getStatus() == JobStatus.OPEN) {
                result.add(job);
            }
        }
        return result;
    }

    /**
     * Returns all jobs whose organiser matches the given name (case-insensitive).
     *
     * @param organiser the MO's display name
     * @return a list of jobs created by that organiser
     */
    public List<Job> getJobsByOrganiser(String organiser) {
        List<Job> result = new ArrayList<>();
        if (organiser == null || organiser.isBlank()) {
            return result;
        }

        for (Job job : storage.loadJobs()) {
            if (job.getOrganiser() != null && job.getOrganiser().equalsIgnoreCase(organiser)) {
                result.add(job);
            }
        }
        return result;
    }

    /**
     * @param organiser the organiser name to filter by
     * @return all OPEN jobs whose organiser matches the given name
     */
    public List<Job> getOpenJobsByOrganiser(String organiser) {
        List<Job> result = new ArrayList<>();
        for (Job job : getJobsByOrganiser(organiser)) {
            if (job.getStatus() == JobStatus.OPEN) {
                result.add(job);
            }
        }
        return result;
    }

    /**
     * Returns all OPEN jobs with a matchScore for the given TA user, sorted by score
     * descending. The matchScore is calculated by {@link #calculateMatchScore} and set
     * directly on each Job object before sorting.
     *
     * @param user the TA user (may be null; all jobs will have score 0 in that case)
     * @return open jobs sorted by match score descending
     */
    public List<Job> getOpenJobsForUser(User user) {
        List<Job> jobs = getOpenJobs();
        for (Job job : jobs) {
            int score = calculateMatchScore(user == null ? "" : user.getSkills(), job.getRequiredSkills());
            job.setMatchScore(score);
        }
        // Sort by match score descending
        jobs.sort((a, b) -> b.getMatchScore() - a.getMatchScore());
        return jobs;
    }

    /**
     * Looks up a job by its ID.
     *
     * @param jobId the job ID (e.g. "J001")
     * @return the Job, or null if not found
     */
    public Job findById(String jobId) {
        return storage.loadJobs().stream()
                .filter(j -> j.getJobId().equals(jobId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Creates and persists a new job posting. Assigns the next sequential job ID,
     * sets status to OPEN, and normalises the requiredSkills field.
     *
     * @param title          job title
     * @param moduleCode     course module code
     * @param organiser      the MO's display name (must match their user record)
     * @param minYear        minimum academic year required (1-based)
     * @param maxYear        maximum academic year allowed (1-based)
     * @param hours          expected weekly working hours
     * @param requiredSkills pipe-separated skill list
     * @param deadline       application deadline in "yyyy-MM-dd" format
     * @param vacancies      number of open positions
     * @return the newly created Job
     */
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

    /**
     * @return the total number of jobs in the CSV
     */
    public int countTotalJobs() {
        return storage.loadJobs().size();
    }

    /**
     * @return the number of OPEN jobs
     */
    public int countActiveJobs() {
        int count = 0;
        for (Job job : storage.loadJobs()) {
            if (job.getStatus() == JobStatus.OPEN) {
                count++;
            }
        }
        return count;
    }

    /**
     * Updates the editable fields of an existing job. The jobId, organiser, and status
     * fields are not changed by this method.
     *
     * @param jobId          the ID of the job to update
     * @param title          new title
     * @param moduleCode     new module code
     * @param organiser      new organiser name
     * @param minYear        new minimum year
     * @param maxYear        new maximum year
     * @param hours          new weekly hours
     * @param requiredSkills new skill requirements
     * @param deadline       new deadline
     * @param vacancies      new vacancy count
     */
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

    /**
     * Permanently removes a job from the CSV by ID.
     *
     * @param jobId the ID of the job to delete
     */
    public void deleteJob(String jobId) {
        List<Job> jobs = storage.loadJobs();
        jobs.removeIf(job -> job.getJobId().equals(jobId));
        storage.saveJobs(jobs);
    }

    /**
     * Toggles a job's status between OPEN and CLOSED.
     *
     * @param jobId the ID of the job to toggle
     */
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

    /** @return the total number of jobs */
    public int countAllJobs() {
        return storage.loadJobs().size();
    }

    /** @param organiser the organiser name to filter by @return how many jobs belong to this organiser */
    public int countJobsByOrganiser(String organiser) {
        int count = 0;
        for (Job job : storage.loadJobs()) {
            if (job.getOrganiser() != null && job.getOrganiser().equalsIgnoreCase(organiser)) {
                count++;
            }
        }
        return count;
    }

    /** @param organiser the organiser name to filter by @return how many OPEN jobs belong to this organiser */
    public int countActiveJobsByOrganiser(String organiser) {
        int count = 0;
        for (Job job : getJobsByOrganiser(organiser)) {
            if (job.getStatus() == JobStatus.OPEN) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates a match score (0-100) between a TA's skills and a job's required skills.
     * The score is computed as a weighted category match plus a Jaccard similarity bonus.
     *
     * <p>Supported match types (in order of priority):
     * <ol>
     *   <li>Direct match: user skill exactly equals required skill</li>
     *   <li>Alias match: user skill matches a canonical alias of the required skill</li>
     *   <li>Partial match: user and required skills are in the same category and share a substring (70% weight)</li>
     *   <li>Reverse alias match: required skill matches a canonical alias of a user skill (80% weight)</li>
     * </ol>
     *
     * <p>Category weights: concept (1.5) &gt; language (1.3) &gt; ml (1.2) &gt; database (1.1) &gt; framework (1.0) &gt; tools (0.9).
     * Jaccard bonus: up to 15 points for overlap between user and required skill sets.
     *
     * @param userSkills     pipe-separated TA skill list
     * @param requiredSkills pipe-separated job skill requirements
     * @return an integer score from 0 to 100, or 0 if requiredSkills is blank
     */
    public int calculateMatchScore(String userSkills, String requiredSkills) {
        if (requiredSkills == null || requiredSkills.isBlank()) {
            return 0;
        }

        Set<String> userNormalized = normalizeSkillSet(tokenize(userSkills));
        Set<String> jobNormalized = normalizeSkillSet(tokenize(requiredSkills));

        if (jobNormalized.isEmpty()) {
            return 0;
        }

        double weightedScore = 0;
        double totalWeight = 0;

        for (String required : jobNormalized) {
            String category = getSkillCategory(required);
            double weight = getCategoryWeight(category);
            totalWeight += weight;

            // Check direct match
            if (userNormalized.contains(required)) {
                weightedScore += weight;
                continue;
            }

            // Check alias match
            String canonical = SKILL_ALIASES.get(required);
            if (canonical != null && userNormalized.contains(canonical)) {
                weightedScore += weight;
                continue;
            }

            // Check partial match (for longer skill names)
            if (required.length() >= 4) {
                boolean partialMatch = false;
                for (String userSkill : userNormalized) {
                    if (userSkill.length() >= 4 &&
                        (required.contains(userSkill) || userSkill.contains(required))) {
                        // Check if they're in the same category
                        String userCategory = getSkillCategory(userSkill);
                        if (category.equals(userCategory) || "concept".equals(category)) {
                            weightedScore += weight * 0.7; // Partial match gets 70%
                            partialMatch = true;
                            break;
                        }
                    }
                }
                // Check reverse alias
                if (!partialMatch) {
                    for (Map.Entry<String, String> alias : SKILL_ALIASES.entrySet()) {
                        if (alias.getValue().equals(required) && userNormalized.contains(alias.getKey())) {
                            weightedScore += weight * 0.8;
                            partialMatch = true;
                            break;
                        }
                    }
                }
            }
        }

        // Jaccard similarity bonus (if user has extra relevant skills)
        Set<String> intersection = new HashSet<>(userNormalized);
        intersection.retainAll(jobNormalized);

        Set<String> union = new HashSet<>(userNormalized);
        union.addAll(jobNormalized);

        double jaccard = union.isEmpty() ? 0 : (double) intersection.size() / union.size();
        // Combine weighted match with Jaccard for bonus
        double baseScore = totalWeight > 0 ? (weightedScore / totalWeight) * 100 : 0;
        double jaccardBonus = jaccard * 15; // Up to 15% bonus for having relevant skills

        int finalScore = (int) Math.round(Math.min(100, baseScore + jaccardBonus));
        return finalScore;
    }

    /**
     * Converts a skill string to a normalised lowercase set, and also adds the
     * canonical alias target for each skill if applicable.
     */
    private Set<String> normalizeSkillSet(Set<String> skills) {
        Set<String> normalized = new HashSet<>();
        for (String skill : skills) {
            normalized.add(skill.toLowerCase().trim());
            // Also add alias targets
            String canonical = SKILL_ALIASES.get(skill.toLowerCase().trim());
            if (canonical != null) {
                normalized.add(canonical);
            }
        }
        return normalized;
    }

    /**
     * Looks up the category of a skill by checking which pre-defined skill set contains it.
     * Returns "other" if the skill does not belong to any known category.
     */
    private String getSkillCategory(String skill) {
        String lower = skill.toLowerCase();
        for (Map.Entry<String, Set<String>> entry : SKILL_CATEGORIES.entrySet()) {
            if (entry.getValue().contains(lower)) {
                return entry.getKey();
            }
        }
        return "other";
    }

    /** Returns the multiplier weight for a skill category. */
    private double getCategoryWeight(String category) {
        switch (category) {
            case "concept": return 1.5;  // Core concepts most important
            case "language": return 1.3; // Languages important
            case "ml": return 1.2;       // ML skills valuable
            case "database": return 1.1;  // Database skills
            case "framework": return 1.0; // Frameworks
            case "tools": return 0.9;     // Tools less critical
            default: return 1.0;
        }
    }

    /**
     * Splits a pipe/comma-separated skill string into a deduplicated lowercase set.
     */
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

    /** Normalises a skill list by replacing commas with pipes and trimming. */
    private String normalizeSkills(String skills) {
        if (skills == null || skills.isBlank()) {
            return "";
        }
        return skills.replace(",", "|").trim();
    }

    /** Generates the next sequential job ID (e.g. "J005"). */
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
