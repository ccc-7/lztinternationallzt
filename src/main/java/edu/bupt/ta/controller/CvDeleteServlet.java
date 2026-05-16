package edu.bupt.ta.controller;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.CvFileService;
import edu.bupt.ta.service.UserService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Handles deletion of the current TA's uploaded CV. Removes the file from disk
 * via {@link edu.bupt.ta.service.CvFileService#deleteCv} and clears the CV
 * metadata columns in the CSV.
 *
 * @see edu.bupt.ta.service.CvFileService
 */
@WebServlet(urlPatterns = {"/ta/profile/cv/delete"})
public class CvDeleteServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final CvFileService cvFileService = new CvFileService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() != UserRole.TA) {
            req.getSession().setAttribute("flashError", "please log in as a TA to manage your CV.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        try {
            cvFileService.deleteCv(currentUser.getCvStoredName());
            User updated = userService.clearCvMetadata(currentUser.getUserId());
            req.getSession().setAttribute("currentUser", updated);
            req.getSession().setAttribute("flashSuccess", "Uploaded CV removed successfully.");
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Failed to delete CV. Please try again.");
        }

        resp.sendRedirect(req.getContextPath() + "/ta/profile");
    }
}
