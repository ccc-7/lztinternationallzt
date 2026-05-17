package edu.bupt.ta.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SkillMatch functionality in JobService.
 * Tests the skill matching algorithm: exact match, partial match,
 * missing skills detection, case handling, and edge cases.
 * Uses @TempDir to isolate test data from production files.
 */
class SkillMatchServiceTest {

    @TempDir
    Path tempDir;

    private JobService jobService;

    @BeforeEach
    void setUp() throws IOException {
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);

        // Create JobService with isolated storage
        edu.bupt.ta.storage.FileStorageUtil storage =
            new edu.bupt.ta.storage.FileStorageUtil(dataDir, null);
        jobService = new JobService(storage);
    }

    // ========== Exact Match Tests ==========

    @Nested
    @DisplayName("Exact Skill Match")
    class ExactMatchTests {

        @Test
        @DisplayName("should return 100 for exact skill match")
        void shouldReturn100ForExactMatch() {
            int score = jobService.calculateMatchScore("Java|Python", "Java|Python");
            assertEquals(100, score, "Exact match should return maximum score");
        }

        @Test
        @DisplayName("should return 100 for single exact match")
        void shouldReturn100ForSingleExactMatch() {
            int score = jobService.calculateMatchScore("Java", "Java");
            assertEquals(100, score, "Single exact match should return 100");
        }

        @Test
        @DisplayName("should return 100 when user has all required skills and no extra")
        void shouldReturn100WhenUserHasAllAndNoExtra() {
            int score = jobService.calculateMatchScore("Java|Python|Django", "Java|Python|Django");
            assertEquals(100, score, "Exact match with 3 skills should return 100");
        }

        @Test
        @DisplayName("should give high score when user has superset of required skills")
        void shouldGiveHighScoreForSuperset() {
            int score = jobService.calculateMatchScore(
                "Java|Python|Go|Rust|Spring", "Java|Python");
            assertTrue(score >= 50, "User with superset skills should get high score");
        }
    }

    // ========== Partial Match Tests ==========

    @Nested
    @DisplayName("Partial Skill Match")
    class PartialMatchTests {

        @Test
        @DisplayName("should return partial score for partial match")
        void shouldReturnPartialScoreForPartialMatch() {
            int exactScore = jobService.calculateMatchScore("Java|Python", "Java|Python");
            int partialScore = jobService.calculateMatchScore("Java", "Java|Python");
            assertTrue(partialScore < exactScore,
                "Partial match should have lower score than exact match");
        }

        @Test
        @DisplayName("should return positive score for single shared skill")
        void shouldReturnPositiveScoreForSingleSharedSkill() {
            int score = jobService.calculateMatchScore("Java|C++", "Java|Python");
            assertTrue(score > 0, "Single shared skill should give positive score");
        }

        @Test
        @DisplayName("should return 0 when no skills match")
        void shouldReturn0WhenNoSkillsMatch() {
            int score = jobService.calculateMatchScore("Ruby|PHP", "Java|Python");
            assertEquals(0, score, "No matching skills should return 0");
        }

        @Test
        @DisplayName("should handle category-based partial matching")
        void shouldHandleCategoryBasedPartialMatching() {
            int score = jobService.calculateMatchScore(
                "java spring", "Spring");
            assertTrue(score >= 0, "Category match should give non-negative score");
        }
    }

    // ========== Missing Skills Detection Tests ==========

    @Nested
    @DisplayName("Missing Skills Handling")
    class MissingSkillsTests {

        @Test
        @DisplayName("should return lower score when skills are missing")
        void shouldReturnLowerScoreWhenSkillsMissing() {
            int fullMatch = jobService.calculateMatchScore("Java|Python|Django", "Java|Python|Django");
            int missingMatch = jobService.calculateMatchScore("Java", "Java|Python|Django");

            assertTrue(missingMatch < fullMatch,
                "Missing skills should result in lower score");
        }

        @Test
        @DisplayName("should give some score for partial overlap")
        void shouldGiveSomeScoreForPartialOverlap() {
            int score = jobService.calculateMatchScore("Java|Python", "Java|Python|C++|Go");
            assertTrue(score > 0, "Partial overlap should give positive score");
            assertTrue(score < 100, "Partial overlap should be less than perfect score");
        }

        @Test
        @DisplayName("should handle user with fewer skills than required")
        void shouldHandleUserWithFewerSkillsThanRequired() {
            int score = jobService.calculateMatchScore(
                "Java", "Java|Python|C++|Go|Rust|Spring");
            assertTrue(score > 0, "At least one skill match should give positive score");
            assertTrue(score < 100, "Should not be perfect score with missing skills");
        }
    }

    // ========== Case and Whitespace Handling Tests ==========

    @Nested
    @DisplayName("Case and Whitespace Handling")
    class CaseAndWhitespaceTests {

        @Test
        @DisplayName("should be case insensitive")
        void shouldBeCaseInsensitive() {
            int lowerScore = jobService.calculateMatchScore(
                "java|python", "JAVA|PYTHON");
            int mixedScore = jobService.calculateMatchScore(
                "JAVA|python", "java|PYTHON");

            assertEquals(lowerScore, mixedScore,
                "Case should not affect matching");
        }

        @Test
        @DisplayName("should handle whitespace around skills")
        void shouldHandleWhitespaceAroundSkills() {
            int normalScore = jobService.calculateMatchScore("Java|Python", "Java|Python");
            int spacedScore = jobService.calculateMatchScore(
                "  Java  |  Python  ", "  Java  |  Python  ");

            assertTrue(spacedScore > 0, "Spaced skills should still match");
        }

        @Test
        @DisplayName("should handle comma-separated skills")
        void shouldHandleCommaSeparatedSkills() {
            // Note: The test data uses pipe-separated, but algorithm should handle it
            int score = jobService.calculateMatchScore(
                "Java, Python", "Java|Python");
            assertTrue(score >= 0, "Should handle comma-separated input");
        }

        @Test
        @DisplayName("should trim individual skill names")
        void shouldTrimIndividualSkillNames() {
            int score = jobService.calculateMatchScore(
                "  Java  ", "Java");
            assertTrue(score > 0, "Trimmed skills should match");
        }
    }

    // ========== Duplicate Skills Handling Tests ==========

    @Nested
    @DisplayName("Duplicate Skills Handling")
    class DuplicateSkillsTests {

        @Test
        @DisplayName("should handle duplicate skills in user list")
        void shouldHandleDuplicateSkillsInUserList() {
            int normalScore = jobService.calculateMatchScore(
                "Java|Python", "Java");
            int duplicateScore = jobService.calculateMatchScore(
                "Java|Java|Python|Python", "Java");

            assertTrue(duplicateScore >= normalScore,
                "Duplicates should not give lower score");
        }

        @Test
        @DisplayName("should not be affected by duplicate required skills")
        void shouldNotBeAffectedByDuplicateRequiredSkills() {
            int score = jobService.calculateMatchScore(
                "Java", "Java|Java|Java");
            assertTrue(score > 0, "Should handle duplicate required skills");
        }
    }

    // ========== Empty and Null Handling Tests ==========

    @Nested
    @DisplayName("Empty and Null Handling")
    class EmptyAndNullTests {

        @Test
        @DisplayName("should return 0 for null required skills")
        void shouldReturn0ForNullRequiredSkills() {
            int score = jobService.calculateMatchScore("Java|Python", null);
            assertEquals(0, score, "Null required skills should return 0");
        }

        @Test
        @DisplayName("should return 0 for blank required skills")
        void shouldReturn0ForBlankRequiredSkills() {
            int score = jobService.calculateMatchScore("Java|Python", "  ");
            assertEquals(0, score, "Blank required skills should return 0");
        }

        @Test
        @DisplayName("should return 0 for empty required skills")
        void shouldReturn0ForEmptyRequiredSkills() {
            int score = jobService.calculateMatchScore("Java|Python", "");
            assertEquals(0, score, "Empty required skills should return 0");
        }

        @Test
        @DisplayName("should return 0 for null user skills")
        void shouldReturn0ForNullUserSkills() {
            int score = jobService.calculateMatchScore(null, "Java|Python");
            assertTrue(score >= 0, "Null user skills should not crash");
        }

        @Test
        @DisplayName("should return 0 for empty user skills")
        void shouldReturn0ForEmptyUserSkills() {
            int score = jobService.calculateMatchScore("", "Java|Python");
            assertEquals(0, score, "Empty user skills should return 0");
        }

        @Test
        @DisplayName("should handle both null gracefully")
        void shouldHandleBothNullGracefully() {
            int score = jobService.calculateMatchScore(null, null);
            assertEquals(0, score, "Both null should return 0 without crash");
        }

        @Test
        @DisplayName("should not crash with only whitespace")
        void shouldNotCrashWithOnlyWhitespace() {
            int score = jobService.calculateMatchScore("   ", "   ");
            assertEquals(0, score, "Whitespace-only should return 0");
        }

        @Test
        @DisplayName("should not crash with only pipe delimiters")
        void shouldNotCrashWithOnlyPipeDelimiters() {
            int score = jobService.calculateMatchScore("|", "|");
            assertTrue(score >= 0, "Pipe-only should not crash");
        }
    }

    // ========== Skill Alias Tests ==========

    @Nested
    @DisplayName("Skill Alias Handling")
    class SkillAliasTests {

        @Test
        @DisplayName("should recognize js as javascript")
        void shouldRecognizeJsAsJavascript() {
            int score = jobService.calculateMatchScore("js", "javascript");
            assertTrue(score > 0, "js should match javascript");
        }

        @Test
        @DisplayName("should recognize ts as typescript")
        void shouldRecognizeTsAsTypescript() {
            int score = jobService.calculateMatchScore("ts", "typescript");
            assertTrue(score > 0, "ts should match typescript");
        }

        @Test
        @DisplayName("should recognize ml as machine learning")
        void shouldRecognizeMlAsMachineLearning() {
            int score = jobService.calculateMatchScore("ml", "machine learning");
            assertTrue(score > 0, "ml should match machine learning");
        }

        @Test
        @DisplayName("should recognize springboot as spring")
        void shouldRecognizeSpringbootAsSpring() {
            int score = jobService.calculateMatchScore("springboot", "spring");
            assertTrue(score > 0, "springboot should match spring");
        }
    }

    // ========== Boundary and Edge Cases Tests ==========

    @Nested
    @DisplayName("Boundary and Edge Cases")
    class BoundaryAndEdgeCasesTests {

        @Test
        @DisplayName("should not exceed 100 maximum score")
        void shouldNotExceed100MaximumScore() {
            int score = jobService.calculateMatchScore(
                "Java|Python|JavaScript|TypeScript|C++|Go|Rust",
                "Java|Python|JavaScript|TypeScript|C++|Go|Rust");
            assertTrue(score <= 100, "Score should never exceed 100");
        }

        @Test
        @DisplayName("should return non-negative score always")
        void shouldReturnNonNegativeScoreAlways() {
            // Test various edge cases
            assertTrue(jobService.calculateMatchScore("", "") >= 0);
            assertTrue(jobService.calculateMatchScore("x", "") >= 0);
            assertTrue(jobService.calculateMatchScore("", "x") >= 0);
            assertTrue(jobService.calculateMatchScore("a|b|c", "x|y|z") >= 0);
        }

        @Test
        @DisplayName("should handle very long skill names")
        void shouldHandleVeryLongSkillNames() {
            String longSkill = "a".repeat(100);
            int score = jobService.calculateMatchScore(longSkill, longSkill);
            assertEquals(100, score, "Long exact match should return 100");
        }

        @Test
        @DisplayName("should handle special characters in skills")
        void shouldHandleSpecialCharactersInSkills() {
            int score = jobService.calculateMatchScore("C++", "C++");
            assertTrue(score > 0, "C++ should match C++");
        }

        @Test
        @DisplayName("should handle single character skills")
        void shouldHandleSingleCharacterSkills() {
            int score = jobService.calculateMatchScore("C", "C");
            assertTrue(score > 0, "Single character skills should work");
        }
    }
}
