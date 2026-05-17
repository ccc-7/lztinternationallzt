package edu.bupt.ta.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Job model.
 * Tests constructor, getters, setters, and status management.
 */
class JobTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create job with default constructor")
        void shouldCreateJobWithDefaultConstructor() {
            Job job = new Job();
            assertNotNull(job);
        }

        @Test
        @DisplayName("should create job with parameterized constructor")
        void shouldCreateJobWithParameterizedConstructor() {
            Job job = new Job(
                "J001",
                "Software Engineering TA",
                "EBU6304",
                "Prof.Chen",
                2, 4, 20,
                JobStatus.OPEN,
                "Java|Teamwork",
                95,
                "2026-12-31",
                3
            );

            assertEquals("J001", job.getJobId());
            assertEquals("Software Engineering TA", job.getTitle());
            assertEquals("EBU6304", job.getModuleCode());
            assertEquals("Prof.Chen", job.getOrganiser());
            assertEquals(2, job.getMinYear());
            assertEquals(4, job.getMaxYear());
            assertEquals(20, job.getHours());
            assertEquals(JobStatus.OPEN, job.getStatus());
            assertEquals("Java|Teamwork", job.getRequiredSkills());
            assertEquals(95, job.getMatchScore());
            assertEquals("2026-12-31", job.getDeadline());
            assertEquals(3, job.getVacancies());
        }
    }

    @Nested
    @DisplayName("Field Access Tests")
    class FieldAccessTests {

        @Test
        @DisplayName("should set and get all fields")
        void shouldSetAndGetAllFields() {
            Job job = new Job();
            job.setJobId("J002");
            job.setTitle("Embedded Systems TA");
            job.setModuleCode("EBU6201");
            job.setOrganiser("Prof.Li");
            job.setMinYear(2);
            job.setMaxYear(4);
            job.setHours(18);
            job.setStatus(JobStatus.OPEN);
            job.setRequiredSkills("C|STM32");
            job.setMatchScore(89);
            job.setDeadline("2026-05-15");
            job.setVacancies(2);

            assertEquals("J002", job.getJobId());
            assertEquals("Embedded Systems TA", job.getTitle());
            assertEquals("EBU6201", job.getModuleCode());
            assertEquals("Prof.Li", job.getOrganiser());
            assertEquals(2, job.getMinYear());
            assertEquals(4, job.getMaxYear());
            assertEquals(18, job.getHours());
            assertEquals(JobStatus.OPEN, job.getStatus());
            assertEquals("C|STM32", job.getRequiredSkills());
            assertEquals(89, job.getMatchScore());
            assertEquals("2026-05-15", job.getDeadline());
            assertEquals(2, job.getVacancies());
        }
    }

    @Nested
    @DisplayName("Status Tests")
    class StatusTests {

        @Test
        @DisplayName("should handle OPEN status")
        void shouldHandleOpenStatus() {
            Job job = new Job();
            job.setStatus(JobStatus.OPEN);
            assertEquals(JobStatus.OPEN, job.getStatus());
        }

        @Test
        @DisplayName("should handle CLOSED status")
        void shouldHandleClosedStatus() {
            Job job = new Job();
            job.setStatus(JobStatus.CLOSED);
            assertEquals(JobStatus.CLOSED, job.getStatus());
        }

        @Test
        @DisplayName("should toggle between OPEN and CLOSED")
        void shouldToggleBetweenOpenAndClosed() {
            Job job = new Job();
            job.setStatus(JobStatus.OPEN);
            assertEquals(JobStatus.OPEN, job.getStatus());

            job.setStatus(JobStatus.CLOSED);
            assertEquals(JobStatus.CLOSED, job.getStatus());
        }
    }

    @Nested
    @DisplayName("Match Score Tests")
    class MatchScoreTests {

        @Test
        @DisplayName("should handle match score of 0")
        void shouldHandleMatchScoreOf0() {
            Job job = new Job();
            job.setMatchScore(0);
            assertEquals(0, job.getMatchScore());
        }

        @Test
        @DisplayName("should handle match score of 100")
        void shouldHandleMatchScoreOf100() {
            Job job = new Job();
            job.setMatchScore(100);
            assertEquals(100, job.getMatchScore());
        }

        @Test
        @DisplayName("should handle intermediate match scores")
        void shouldHandleIntermediateMatchScores() {
            Job job = new Job();
            job.setMatchScore(75);
            assertEquals(75, job.getMatchScore());
        }
    }

    @Nested
    @DisplayName("Vacancy Tests")
    class VacancyTests {

        @Test
        @DisplayName("should handle single vacancy")
        void shouldHandleSingleVacancy() {
            Job job = new Job();
            job.setVacancies(1);
            assertEquals(1, job.getVacancies());
        }

        @Test
        @DisplayName("should handle multiple vacancies")
        void shouldHandleMultipleVacancies() {
            Job job = new Job();
            job.setVacancies(10);
            assertEquals(10, job.getVacancies());
        }

        @Test
        @DisplayName("should handle zero vacancies (filled)")
        void shouldHandleZeroVacancies() {
            Job job = new Job();
            job.setVacancies(0);
            assertEquals(0, job.getVacancies());
        }
    }
}
