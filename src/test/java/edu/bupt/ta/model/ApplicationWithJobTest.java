package edu.bupt.ta.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ApplicationWithJob model.
 * Tests the denormalized view object combining Application and job detail strings.
 */
class ApplicationWithJobTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create ApplicationWithJob with constructor")
        void shouldCreateApplicationWithJobWithConstructor() {
            Application app = new Application(
                "A001", "T001", "J001",
                ApplicationStatus.PENDING,
                "2026-01-15 10:00:00",
                "First application",
                "Mon afternoons"
            );

            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app,
                "Software Engineering TA",
                "EBU6304",
                "Prof.Chen"
            );

            assertNotNull(appWithJob);
            assertEquals("A001", appWithJob.getApplicationId());
            assertEquals("J001", appWithJob.getJobId());
            assertEquals("Software Engineering TA", appWithJob.getJobTitle());
            assertEquals("EBU6304", appWithJob.getModuleCode());
            assertEquals("Prof.Chen", appWithJob.getOrganiser());
        }

        @Test
        @DisplayName("should expose underlying application")
        void shouldExposeUnderlyingApplication() {
            Application app = new Application(
                "A002", "T002", "J002",
                ApplicationStatus.INTERVIEW,
                "2026-01-16 11:00:00",
                "Second application",
                "Tue mornings"
            );

            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app, "Embedded Systems TA", "EBU6201", "Prof.Li"
            );

            assertSame(app, appWithJob.getApplication());
        }
    }

    @Nested
    @DisplayName("Application Field Access Tests")
    class ApplicationFieldTests {

        @Test
        @DisplayName("should expose application ID through view object")
        void shouldExposeApplicationIdThroughViewObject() {
            Application app = new Application(
                "A003", "T003", "J003",
                ApplicationStatus.ACCEPTED,
                "2026-01-17 12:00:00",
                "Third application",
                "Wed afternoons"
            );

            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app, "Data Structures TA", "EBU6102", "Prof.Wang"
            );

            assertEquals("A003", appWithJob.getApplicationId());
        }

        @Test
        @DisplayName("should expose job ID through view object")
        void shouldExposeJobIdThroughViewObject() {
            Application app = new Application(
                "A004", "T004", "J004",
                ApplicationStatus.REJECTED,
                "2026-01-18 13:00:00",
                "Fourth application",
                "Thu mornings"
            );

            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app, "Python TA", "EBU6103", "Prof.Zhang"
            );

            assertEquals("J004", appWithJob.getJobId());
        }

        @Test
        @DisplayName("should expose status as string through view object")
        void shouldExposeStatusAsStringThroughViewObject() {
            Application app = new Application(
                "A005", "T005", "J005",
                ApplicationStatus.PENDING,
                "2026-01-19 14:00:00",
                "Fifth application",
                "Fri afternoons"
            );

            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app, "ML TA", "EBU6401", "Prof.Wang"
            );

            assertEquals("PENDING", appWithJob.getStatus());
        }

        @Test
        @DisplayName("should expose submittedAt through view object")
        void shouldExposeSubmittedAtThroughViewObject() {
            Application app = new Application(
                "A006", "T006", "J006",
                ApplicationStatus.INTERVIEW,
                "2026-01-20 15:00:00",
                "Sixth application",
                "Sat mornings"
            );

            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app, "Algorithm TA", "EBU6104", "Prof.Liu"
            );

            assertEquals("2026-01-20 15:00:00", appWithJob.getSubmittedAt());
        }

        @Test
        @DisplayName("should expose notes through view object")
        void shouldExposeNotesThroughViewObject() {
            Application app = new Application(
                "A007", "T007", "J007",
                ApplicationStatus.ACCEPTED,
                "2026-01-21 16:00:00",
                "Custom note here",
                "Sun afternoons"
            );

            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app, "Web Dev TA", "EBU6105", "Prof.Chen"
            );

            assertEquals("Custom note here", appWithJob.getNotes());
        }
    }

    @Nested
    @DisplayName("Job Field Access Tests")
    class JobFieldTests {

        @Test
        @DisplayName("should expose job title through view object")
        void shouldExposeJobTitleThroughViewObject() {
            Application app = new Application(
                "A008", "T008", "J008",
                ApplicationStatus.PENDING,
                "2026-01-22 17:00:00",
                "Application",
                "Mon mornings"
            );

            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app, "Software Engineering TA", "EBU6304", "Prof.Chen"
            );

            assertEquals("Software Engineering TA", appWithJob.getJobTitle());
        }

        @Test
        @DisplayName("should expose module code through view object")
        void shouldExposeModuleCodeThroughViewObject() {
            Application app = new Application(
                "A009", "T009", "J009",
                ApplicationStatus.PENDING,
                "2026-01-23 18:00:00",
                "Application",
                "Tue afternoons"
            );

            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app, "Embedded TA", "EBU6201", "Prof.Li"
            );

            assertEquals("EBU6201", appWithJob.getModuleCode());
        }

        @Test
        @DisplayName("should expose organiser through view object")
        void shouldExposeOrganiserThroughViewObject() {
            Application app = new Application(
                "A010", "T010", "J010",
                ApplicationStatus.PENDING,
                "2026-01-24 19:00:00",
                "Application",
                "Wed mornings"
            );

            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app, "Data Science TA", "EBU6402", "Prof.Wang"
            );

            assertEquals("Prof.Wang", appWithJob.getOrganiser());
        }
    }

    @Nested
    @DisplayName("Status Conversion Tests")
    class StatusConversionTests {

        @Test
        @DisplayName("should return PENDING as string")
        void shouldReturnPendingAsString() {
            Application app = new Application(
                "A011", "T011", "J011",
                ApplicationStatus.PENDING,
                "2026-01-25 10:00:00",
                "App",
                "Mon"
            );
            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app, "TA", "EBU0001", "Prof"
            );
            assertEquals("PENDING", appWithJob.getStatus());
        }

        @Test
        @DisplayName("should return INTERVIEW as string")
        void shouldReturnInterviewAsString() {
            Application app = new Application(
                "A012", "T012", "J012",
                ApplicationStatus.INTERVIEW,
                "2026-01-26 10:00:00",
                "App",
                "Tue"
            );
            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app, "TA", "EBU0002", "Prof"
            );
            assertEquals("INTERVIEW", appWithJob.getStatus());
        }

        @Test
        @DisplayName("should return ACCEPTED as string")
        void shouldReturnAcceptedAsString() {
            Application app = new Application(
                "A013", "T013", "J013",
                ApplicationStatus.ACCEPTED,
                "2026-01-27 10:00:00",
                "App",
                "Wed"
            );
            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app, "TA", "EBU0003", "Prof"
            );
            assertEquals("ACCEPTED", appWithJob.getStatus());
        }

        @Test
        @DisplayName("should return REJECTED as string")
        void shouldReturnRejectedAsString() {
            Application app = new Application(
                "A014", "T014", "J014",
                ApplicationStatus.REJECTED,
                "2026-01-28 10:00:00",
                "App",
                "Thu"
            );
            ApplicationWithJob appWithJob = new ApplicationWithJob(
                app, "TA", "EBU0004", "Prof"
            );
            assertEquals("REJECTED", appWithJob.getStatus());
        }
    }
}
