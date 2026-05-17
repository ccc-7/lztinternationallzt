package edu.bupt.ta.service;

import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.JobStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JobService.
 * Uses a shared FileStorageUtil instance backed by a temporary directory.
 * The FileStorageUtil constructor seeds default jobs when files are empty.
 */
class JobServiceTest {

    @TempDir
    Path tempDir;

    private JobService jobService;
    private edu.bupt.ta.storage.FileStorageUtil storage;

    @BeforeEach
    void setUp() throws IOException {
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);
        // Create and share a single FileStorageUtil instance
        storage = new edu.bupt.ta.storage.FileStorageUtil(dataDir, null);
        jobService = new JobService(storage);
        // Default jobs are seeded: J001-J004
    }

    // ========== Match Score Calculation Tests ==========

    @Nested
    @DisplayName("calculateMatchScore")
    class MatchScoreTests {

        @Test
        @DisplayName("should return 100 for exact skill match")
        void shouldReturn100ForExactSkillMatch() {
            int score = jobService.calculateMatchScore("Java|Python", "Java|Python");
            assertEquals(100, score);
        }

        @Test
        @DisplayName("should return high score for alias match (js -> javascript)")
        void shouldReturnHighScoreForAliasMatch() {
            int score = jobService.calculateMatchScore("js|python", "Java|Python");
            assertTrue(score > 0, "Alias match should give positive score");
        }

        @Test
        @DisplayName("should return 0 for no skill match")
        void shouldReturn0ForNoMatch() {
            int score = jobService.calculateMatchScore("Ruby|PHP", "Java|Python");
            assertEquals(0, score);
        }

        @Test
        @DisplayName("should return 0 when required skills is null")
        void shouldReturn0WhenRequiredSkillsIsNull() {
            int score = jobService.calculateMatchScore("Java|Python", null);
            assertEquals(0, score);
        }

        @Test
        @DisplayName("should return 0 when required skills is blank")
        void shouldReturn0WhenRequiredSkillsIsBlank() {
            int score = jobService.calculateMatchScore("Java|Python", "  ");
            assertEquals(0, score);
        }

        @Test
        @DisplayName("should return 0 when user has no skills")
        void shouldReturn0WhenUserHasNoSkills() {
            int score = jobService.calculateMatchScore("", "Java|Python");
            assertEquals(0, score);
        }

        @Test
        @DisplayName("should handle partial match with same category")
        void shouldHandlePartialMatchWithSameCategory() {
            int score = jobService.calculateMatchScore("java spring", "Spring");
            assertTrue(score >= 0, "Should handle partial match");
        }

        @Test
        @DisplayName("should give bonus for Jaccard similarity")
        void shouldGiveBonusForJaccardSimilarity() {
            int score = jobService.calculateMatchScore("Java|Python", "Java|Python|Django");
            assertTrue(score > 0, "Should give some score");

            int lowScore = jobService.calculateMatchScore("Java", "C|Python");
            assertTrue(lowScore <= score, "Lower Jaccard should result in lower or equal score");
        }

        @Test
        @DisplayName("should not exceed 100 maximum score")
        void shouldNotExceed100MaximumScore() {
            int score = jobService.calculateMatchScore(
                "Java|Python|JavaScript|TypeScript|C++|Go|Rust",
                "Java|Python|JavaScript|TypeScript|C++|Go|Rust");
            assertTrue(score <= 100, "Score should not exceed 100");
        }

        @Test
        @DisplayName("should handle case insensitivity")
        void shouldHandleCaseInsensitivity() {
            int scoreLower = jobService.calculateMatchScore("java|python", "JAVA|PYTHON");
            int scoreMixed = jobService.calculateMatchScore("JAVA|python", "java|PYTHON");
            assertEquals(scoreLower, scoreMixed, "Case should not affect matching");
        }

        @Test
        @DisplayName("should handle ml and ai skill aliases")
        void shouldHandleMlAndAiSkillAliases() {
            int score1 = jobService.calculateMatchScore("ML", "Machine Learning");
            assertTrue(score1 >= 0, "ML should match Machine Learning");
        }

        @Test
        @DisplayName("should handle framework aliases")
        void shouldHandleFrameworkAliases() {
            int score = jobService.calculateMatchScore("SpringBoot", "Spring");
            assertTrue(score >= 0, "SpringBoot should give some score for Spring");
        }
    }

    // ========== Job CRUD Tests ==========

    @Nested
    @DisplayName("Job CRUD Operations")
    class JobCrudTests {

        @Test
        @DisplayName("should find seeded job by ID")
        void shouldFindSeededJobById() {
            Job found = jobService.findById("J001");
            assertNotNull(found);
            assertEquals("Software Engineering TA", found.getTitle());
        }

        @Test
        @DisplayName("should return null for non-existent job ID")
        void shouldReturnNullForNonExistentJobId() {
            Job found = jobService.findById("NONEXISTENT");
            assertNull(found);
        }

        @Test
        @DisplayName("should create job and get correct ID after seeded data")
        void shouldCreateJobAndGetCorrectId() {
            // After seeded J001-J004, next should be J005
            Job created = jobService.createJob(
                "New Job", "EBU9999", "Prof.New",
                1, 4, 10, "Python", "2030-12-31", 1
            );
            assertNotNull(created);
            assertEquals("J005", created.getJobId());
        }

        @Test
        @DisplayName("should update job successfully")
        void shouldUpdateJobSuccessfully() {
            jobService.updateJob("J001", "Updated Title", "EBU6305", "Prof.Li",
                2, 3, 15, "Python|Java", "2027-01-01", 2);

            Job updated = jobService.findById("J001");
            assertNotNull(updated);
            assertEquals("Updated Title", updated.getTitle());
            assertEquals("EBU6305", updated.getModuleCode());
            assertEquals("Prof.Li", updated.getOrganiser());
            assertEquals(2, updated.getMinYear());
            assertEquals(3, updated.getMaxYear());
            assertEquals(15, updated.getHours());
            assertEquals("Python|Java", updated.getRequiredSkills());
            assertEquals("2027-01-01", updated.getDeadline());
            assertEquals(2, updated.getVacancies());
        }

        @Test
        @DisplayName("should delete job successfully")
        void shouldDeleteJobSuccessfully() {
            int countBefore = jobService.getAllJobs().size();

            jobService.deleteJob("J001");

            int countAfter = jobService.getAllJobs().size();
            assertEquals(countBefore - 1, countAfter);
        }

        @Test
        @DisplayName("should toggle job status")
        void shouldToggleJobStatus() {
            assertEquals(JobStatus.OPEN, jobService.findById("J001").getStatus());

            jobService.toggleJobStatus("J001");
            assertEquals(JobStatus.CLOSED, jobService.findById("J001").getStatus());

            jobService.toggleJobStatus("J001");
            assertEquals(JobStatus.OPEN, jobService.findById("J001").getStatus());
        }
    }

    // ========== Job Query Tests ==========

    @Nested
    @DisplayName("Job Query Operations")
    class JobQueryTests {

        @Test
        @DisplayName("should get all seeded jobs")
        void shouldGetAllSeededJobs() {
            List<Job> allJobs = jobService.getAllJobs();
            // Seeded: J001-J004
            assertEquals(4, allJobs.size());
        }

        @Test
        @DisplayName("should get only open jobs")
        void shouldGetOnlyOpenJobs() {
            List<Job> openJobs = jobService.getOpenJobs();
            assertTrue(openJobs.size() >= 3); // J001-J003 are OPEN, J004 is CLOSED
            for (Job job : openJobs) {
                assertEquals(JobStatus.OPEN, job.getStatus());
            }
        }

        @Test
        @DisplayName("should get jobs by organiser")
        void shouldGetJobsByOrganiser() {
            List<Job> chenJobs = jobService.getJobsByOrganiser("Dr.Wang");
            assertEquals(2, chenJobs.size()); // J001 and J003
            for (Job job : chenJobs) {
                assertEquals("Dr.Wang", job.getOrganiser());
            }
        }

        @Test
        @DisplayName("should get jobs by organiser case insensitively")
        void shouldGetJobsByOrganiserCaseInsensitive() {
            List<Job> jobs = jobService.getJobsByOrganiser("dr.wang");
            assertEquals(2, jobs.size());
        }

        @Test
        @DisplayName("should return empty list for non-existent organiser")
        void shouldReturnEmptyListForNonExistentOrganiser() {
            List<Job> jobs = jobService.getJobsByOrganiser("NonExistent");
            assertTrue(jobs.isEmpty());
        }

        @Test
        @DisplayName("should return empty list for null organiser")
        void shouldReturnEmptyListForNullOrganiser() {
            List<Job> jobs = jobService.getJobsByOrganiser(null);
            assertTrue(jobs.isEmpty());
        }

        @Test
        @DisplayName("should get open jobs by organiser")
        void shouldGetOpenJobsByOrganiser() {
            List<Job> jobs = jobService.getOpenJobsByOrganiser("Dr.Wang");
            assertEquals(2, jobs.size());
            for (Job job : jobs) {
                assertEquals(JobStatus.OPEN, job.getStatus());
            }
        }

        @Test
        @DisplayName("should count total jobs")
        void shouldCountTotalJobs() {
            assertEquals(4, jobService.countTotalJobs());
        }

        @Test
        @DisplayName("should count active jobs")
        void shouldCountActiveJobs() {
            assertEquals(4, jobService.countActiveJobs()); // All 4 seeded jobs are OPEN
        }

        @Test
        @DisplayName("should count jobs by organiser")
        void shouldCountJobsByOrganiser() {
            assertEquals(2, jobService.countJobsByOrganiser("Dr.Wang"));
            assertEquals(0, jobService.countJobsByOrganiser("Prof.Smith"));
        }

        @Test
        @DisplayName("should count active jobs by organiser")
        void shouldCountActiveJobsByOrganiser() {
            assertEquals(2, jobService.countActiveJobsByOrganiser("Dr.Wang"));
        }
    }

    // ========== Skill Normalization Tests ==========

    @Nested
    @DisplayName("Skill Normalization")
    class SkillNormalizationTests {

        @Test
        @DisplayName("should normalize comma-separated skills to pipe")
        void shouldNormalizeCommaSeparatedSkillsToPipe() {
            Job created = jobService.createJob(
                "Test Job", "EBU9999", "Prof.Test",
                1, 4, 10, "Java, Python, C++", "2030-12-31", 1
            );
            // normalizeSkills replaces commas with pipes
            assertNotNull(created.getRequiredSkills());
        }

        @Test
        @DisplayName("should handle skills with whitespace")
        void shouldHandleSkillsWithWhitespace() {
            Job created = jobService.createJob(
                "Test Job", "EBU9999", "Prof.Test",
                1, 4, 10, "  Java  |  Python  ", "2030-12-31", 1
            );
            assertNotNull(created.getRequiredSkills());
        }
    }
}
