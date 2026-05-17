package edu.bupt.ta.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Application model.
 * Tests constructor, getters, setters, and status management.
 */
class ApplicationTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create application with default constructor")
        void shouldCreateApplicationWithDefaultConstructor() {
            Application app = new Application();
            assertNotNull(app);
        }

        @Test
        @DisplayName("should create application with parameterized constructor")
        void shouldCreateApplicationWithParameterizedConstructor() {
            Application app = new Application(
                "A001",
                "T001",
                "J001",
                ApplicationStatus.PENDING,
                "2026-01-15 10:00:00",
                "First application",
                "Mon/Wed afternoons"
            );

            assertEquals("A001", app.getApplicationId());
            assertEquals("T001", app.getUserId());
            assertEquals("J001", app.getJobId());
            assertEquals(ApplicationStatus.PENDING, app.getStatus());
            assertEquals("2026-01-15 10:00:00", app.getSubmittedAt());
            assertEquals("First application", app.getNotes());
            assertEquals("Mon/Wed afternoons", app.getAvailability());
        }
    }

    @Nested
    @DisplayName("Field Access Tests")
    class FieldAccessTests {

        @Test
        @DisplayName("should set and get all fields")
        void shouldSetAndGetAllFields() {
            Application app = new Application();
            app.setApplicationId("A002");
            app.setUserId("T002");
            app.setJobId("J002");
            app.setStatus(ApplicationStatus.INTERVIEW);
            app.setSubmittedAt("2026-01-16 11:00:00");
            app.setNotes("Second application");
            app.setAvailability("Tue mornings");

            assertEquals("A002", app.getApplicationId());
            assertEquals("T002", app.getUserId());
            assertEquals("J002", app.getJobId());
            assertEquals(ApplicationStatus.INTERVIEW, app.getStatus());
            assertEquals("2026-01-16 11:00:00", app.getSubmittedAt());
            assertEquals("Second application", app.getNotes());
            assertEquals("Tue mornings", app.getAvailability());
        }
    }

    @Nested
    @DisplayName("Status Tests")
    class StatusTests {

        @Test
        @DisplayName("should handle PENDING status")
        void shouldHandlePendingStatus() {
            Application app = new Application();
            app.setStatus(ApplicationStatus.PENDING);
            assertEquals(ApplicationStatus.PENDING, app.getStatus());
        }

        @Test
        @DisplayName("should handle INTERVIEW status")
        void shouldHandleInterviewStatus() {
            Application app = new Application();
            app.setStatus(ApplicationStatus.INTERVIEW);
            assertEquals(ApplicationStatus.INTERVIEW, app.getStatus());
        }

        @Test
        @DisplayName("should handle ACCEPTED status")
        void shouldHandleAcceptedStatus() {
            Application app = new Application();
            app.setStatus(ApplicationStatus.ACCEPTED);
            assertEquals(ApplicationStatus.ACCEPTED, app.getStatus());
        }

        @Test
        @DisplayName("should handle REJECTED status")
        void shouldHandleRejectedStatus() {
            Application app = new Application();
            app.setStatus(ApplicationStatus.REJECTED);
            assertEquals(ApplicationStatus.REJECTED, app.getStatus());
        }

        @Test
        @DisplayName("should transition through all statuses")
        void shouldTransitionThroughAllStatuses() {
            Application app = new Application();

            app.setStatus(ApplicationStatus.PENDING);
            assertEquals(ApplicationStatus.PENDING, app.getStatus());

            app.setStatus(ApplicationStatus.INTERVIEW);
            assertEquals(ApplicationStatus.INTERVIEW, app.getStatus());

            app.setStatus(ApplicationStatus.ACCEPTED);
            assertEquals(ApplicationStatus.ACCEPTED, app.getStatus());
        }
    }

    @Nested
    @DisplayName("Availability Tests")
    class AvailabilityTests {

        @Test
        @DisplayName("should handle empty availability")
        void shouldHandleEmptyAvailability() {
            Application app = new Application();
            app.setAvailability("");
            assertEquals("", app.getAvailability());
        }

        @Test
        @DisplayName("should handle multi-day availability")
        void shouldHandleMultiDayAvailability() {
            Application app = new Application();
            app.setAvailability("Mon/Wed/Fri afternoons");
            assertEquals("Mon/Wed/Fri afternoons", app.getAvailability());
        }
    }
}
