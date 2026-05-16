package edu.bupt.ta.controller;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.CvFileService;
import edu.bupt.ta.service.UserService;
import edu.bupt.ta.storage.FileStorageUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;

/**
 * Handles PDF CV file upload for the current TA. Validates the file (PDF only,
 * max 5 MB) via {@link edu.bupt.ta.service.CvFileService#savePdf}, then persists
 * the file to disk and updates the user's CV metadata in the CSV.
 *
 * @see edu.bupt.ta.service.CvFileService
 */
@WebServlet(urlPatterns = {"/ta/profile/cv/upload"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5L * 1024 * 1024,
        maxRequestSize = 6L * 1024 * 1024
)
public class CvUploadServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final CvFileService cvFileService = new CvFileService();
    private final FileStorageUtil storage = FileStorageUtil.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() != UserRole.TA) {
            req.getSession().setAttribute("flashError", "please log in as a TA to manage your CV.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        Part filePart = req.getPart("cvFile");
        String oldStoredName = currentUser.getCvStoredName();
        CvFileService.SavedCvFile savedCvFile = null;

        try {
            savedCvFile = cvFileService.savePdf(currentUser.getUserId(), filePart);
            User updated = userService.updateCvMetadata(
                    currentUser.getUserId(),
                    savedCvFile.getStoredName(),
                    savedCvFile.getOriginalName(),
                    savedCvFile.getContentType(),
                    storage.nowText()
            );
            req.getSession().setAttribute("currentUser", updated);
            req.getSession().setAttribute("flashSuccess", "CV uploaded successfully. Recruiters can now view your original PDF.");
        } catch (IllegalArgumentException e) {
            if (savedCvFile != null && (oldStoredName == null || !oldStoredName.equals(savedCvFile.getStoredName()))) {
                cvFileService.deleteCv(savedCvFile.getStoredName());
            }
            req.getSession().setAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            if (savedCvFile != null && (oldStoredName == null || !oldStoredName.equals(savedCvFile.getStoredName()))) {
                cvFileService.deleteCv(savedCvFile.getStoredName());
            }
            req.getSession().setAttribute("flashError", "Failed to upload CV. Please try again.");
        }

        resp.sendRedirect(req.getContextPath() + "/ta/profile");
    }
}
