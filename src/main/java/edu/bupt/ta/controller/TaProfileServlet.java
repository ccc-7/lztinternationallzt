package edu.bupt.ta.controller;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.CvFileService;
import edu.bupt.ta.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Manages the TA profile page. GET displays the profile form populated with the
 * current user's data; POST saves profile field updates via
 * {@link edu.bupt.ta.service.UserService#updateProfile}.
 * Also handles the {@code taProfileFieldRings} request attribute to activate
 * CSS field-rings styling on the profile form (see {@code style.css}).
 */
@WebServlet(urlPatterns = {"/ta/profile"})
public class TaProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final CvFileService cvFileService = new CvFileService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.TA) {
            req.getSession().setAttribute("flashError", "please log in as a TA to view your profile.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        User profileUser = userService.findById(user.getUserId());
        req.setAttribute("profileUser", profileUser);
        req.setAttribute("profileHasCv", userService.hasUploadedCv(profileUser));
        req.setAttribute("cvMaxSizeMb", cvFileService.getMaxCvSizeBytes() / (1024 * 1024));
        req.setAttribute("taProfileFieldRings", Boolean.TRUE);
        req.getRequestDispatcher("/WEB-INF/jsp/ta/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.TA) {
            req.getSession().setAttribute("flashError", "please log in as a TA to view your profile.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String yearStr = req.getParameter("year");
        String major = req.getParameter("major");
        String skills = req.getParameter("skills");
        String availability = req.getParameter("availability");
        String personalStatement = req.getParameter("personalStatement");
        String relevantCourses = req.getParameter("relevantCourses");
        String projectExperience = req.getParameter("projectExperience");
        String preferredRole = req.getParameter("preferredRole");

        int year = 0;
        if (yearStr != null && !yearStr.isBlank()) {
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                req.getSession().setAttribute("flashError", "Invalid year format");
                resp.sendRedirect(req.getContextPath() + "/ta/profile");
                return;
            }
        }

        try {
            User updated = userService.updateProfile(
                    user.getUserId(), name, email, year, major, skills, availability,
                    personalStatement, relevantCourses, projectExperience, preferredRole);
            req.getSession().setAttribute("currentUser", updated);
            req.getSession().setAttribute("flashSuccess", "Profile information saved (written to CSV)");
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Failed to save profile information, please try again later");
        }

        resp.sendRedirect(req.getContextPath() + "/ta/profile");
    }
}

