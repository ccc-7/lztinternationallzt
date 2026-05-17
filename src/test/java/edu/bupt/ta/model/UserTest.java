package edu.bupt.ta.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User model.
 * Tests constructor, getters, setters, and utility methods.
 */
class UserTest {

    @Nested
    @DisplayName("Constructor and Field Access")
    class ConstructorTests {

        @Test
        @DisplayName("should create user with default constructor")
        void shouldCreateUserWithDefaultConstructor() {
            User user = new User();
            assertNotNull(user);
        }

        @Test
        @DisplayName("should set and get all fields")
        void shouldSetAndGetAllFields() {
            User user = new User();
            user.setUserId("U001");
            user.setUsername("testuser");
            user.setPassword("password");
            user.setName("Test User");
            user.setEmail("test@example.com");
            user.setRole(UserRole.TA);
            user.setYear(2);
            user.setMajor("Computer Science");
            user.setSkills("Java|Python");
            user.setStatus("ACTIVE");
            user.setAvailability("Mon afternoons");
            user.setPersonalStatement("Test statement");
            user.setRelevantCourses("EBU6304");
            user.setProjectExperience("Test project");
            user.setPreferredRole("Lab Support");
            user.setSummaryStatus("SUMMARY_COMPLETE");
            user.setCvStoredName("U001.pdf");
            user.setCvOriginalName("resume.pdf");
            user.setCvContentType("application/pdf");
            user.setCvUploadedAt("2026-01-01 10:00:00");
            user.setCvStatus("UPLOADED");

            assertEquals("U001", user.getUserId());
            assertEquals("testuser", user.getUsername());
            assertEquals("password", user.getPassword());
            assertEquals("Test User", user.getName());
            assertEquals("test@example.com", user.getEmail());
            assertEquals(UserRole.TA, user.getRole());
            assertEquals(2, user.getYear());
            assertEquals("Computer Science", user.getMajor());
            assertEquals("Java|Python", user.getSkills());
            assertEquals("ACTIVE", user.getStatus());
            assertEquals("Mon afternoons", user.getAvailability());
            assertEquals("Test statement", user.getPersonalStatement());
            assertEquals("EBU6304", user.getRelevantCourses());
            assertEquals("Test project", user.getProjectExperience());
            assertEquals("Lab Support", user.getPreferredRole());
            assertEquals("SUMMARY_COMPLETE", user.getSummaryStatus());
            assertEquals("U001.pdf", user.getCvStoredName());
            assertEquals("resume.pdf", user.getCvOriginalName());
            assertEquals("application/pdf", user.getCvContentType());
            assertEquals("2026-01-01 10:00:00", user.getCvUploadedAt());
            assertEquals("UPLOADED", user.getCvStatus());
        }
    }

    @Nested
    @DisplayName("Role Assignment")
    class RoleTests {

        @Test
        @DisplayName("should assign TA role")
        void shouldAssignTaRole() {
            User user = new User();
            user.setRole(UserRole.TA);
            assertEquals(UserRole.TA, user.getRole());
        }

        @Test
        @DisplayName("should assign MO role")
        void shouldAssignMoRole() {
            User user = new User();
            user.setRole(UserRole.MO);
            assertEquals(UserRole.MO, user.getRole());
        }

        @Test
        @DisplayName("should assign ADMIN role")
        void shouldAssignAdminRole() {
            User user = new User();
            user.setRole(UserRole.ADMIN);
            assertEquals(UserRole.ADMIN, user.getRole());
        }
    }

    @Nested
    @DisplayName("Year Field")
    class YearTests {

        @Test
        @DisplayName("should handle year 0 for faculty members")
        void shouldHandleYear0ForFaculty() {
            User user = new User();
            user.setYear(0);
            assertEquals(0, user.getYear());
        }

        @Test
        @DisplayName("should handle various academic years")
        void shouldHandleVariousAcademicYears() {
            User user = new User();
            user.setYear(1);
            assertEquals(1, user.getYear());
            user.setYear(4);
            assertEquals(4, user.getYear());
        }
    }

    @Nested
    @DisplayName("CV Related Fields")
    class CvFieldTests {

        @Test
        @DisplayName("should handle MISSING CV status")
        void shouldHandleMissingCvStatus() {
            User user = new User();
            user.setCvStatus("MISSING");
            assertEquals("MISSING", user.getCvStatus());
            assertNull(user.getCvStoredName());
        }

        @Test
        @DisplayName("should handle UPLOADED CV status")
        void shouldHandleUploadedCvStatus() {
            User user = new User();
            user.setCvStatus("UPLOADED");
            user.setCvStoredName("U001.pdf");
            user.setCvOriginalName("my_resume.pdf");
            assertEquals("UPLOADED", user.getCvStatus());
            assertEquals("U001.pdf", user.getCvStoredName());
        }
    }
}
