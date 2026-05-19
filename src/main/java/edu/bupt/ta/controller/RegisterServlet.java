package edu.bupt.ta.controller;

import edu.bupt.ta.model.User;
import edu.bupt.ta.service.JobService;
import edu.bupt.ta.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Handles TA registration. GET shows the registration form; POST creates a new TA account.
 * On success the user is automatically logged in and redirected to the TA dashboard.
 *
 * @see edu.bupt.ta.service.UserService#registerTa
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("username".equals(req.getParameter("check"))) {
            handleUsernameAvailability(req, resp);
            return;
        }
        req.setAttribute("preferredRoleOptions", jobService.getPreferredRoleOptions());
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String yearStr = req.getParameter("year");
        String major = req.getParameter("major");
        String skills = req.getParameter("skills");
        String availability = req.getParameter("availability");
        String personalStatement = req.getParameter("personalStatement");
        String relevantCourses = req.getParameter("relevantCourses");
        String projectExperience = req.getParameter("projectExperience");
        String preferredRole = mergePreferredRoles(req.getParameterValues("preferredRoleSelection"),
                req.getParameter("preferredRole"));

        try {
            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                req.getSession().setAttribute("flashError", "please fill in both username and password");
                resp.sendRedirect(req.getContextPath() + "/register");
                return;
            }

            int year = 0;
            if (yearStr != null && !yearStr.isBlank()) {
                year = Integer.parseInt(yearStr);
            }

            User user = userService.registerTa(
                    username,
                    password,
                    name == null ? "" : name,
                    email == null ? "" : email,
                    year,
                    major == null ? "" : major,
                    skills == null ? "" : skills,
                    availability == null ? "" : availability,
                    personalStatement == null ? "" : personalStatement,
                    relevantCourses == null ? "" : relevantCourses,
                    projectExperience == null ? "" : projectExperience,
                    preferredRole == null ? "" : preferredRole
            );
            req.getSession().setAttribute("currentUser", user);
            req.getSession().setAttribute("flashSuccess", "Registration successful, you are now logged in");
            resp.sendRedirect(req.getContextPath() + "/ta/dashboard");
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("flashError", "Invalid year format");
            resp.sendRedirect(req.getContextPath() + "/register");
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/register");
        }
    }

    private void handleUsernameAvailability(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String username = req.getParameter("username");
        boolean formatValid = username != null
                && username.matches("[A-Za-z][A-Za-z0-9_]{2,}");
        boolean exists = formatValid && userService.usernameExists(username);
        boolean available = formatValid && !exists;

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        String message;
        if (username == null || username.isBlank()) {
            message = "Username is required.";
        } else if (!formatValid) {
            message = "Use at least 3 characters, start with a letter, and only use letters, numbers, or underscore.";
        } else if (exists) {
            message = "This username already exists. Please choose another one.";
        } else {
            message = "Username is available.";
        }
        resp.getWriter().write("{\"available\":" + available + ",\"message\":\"" + escapeJson(message) + "\"}");
    }

    private String mergePreferredRoles(String[] selections, String fallback) {
        if (selections != null && selections.length > 0) {
            return String.join("|", selections);
        }
        return fallback == null ? "" : fallback;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
