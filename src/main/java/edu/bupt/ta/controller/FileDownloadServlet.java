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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Streams a TA's uploaded CV PDF to the browser. Access is restricted:
 * TA users can only download their own CV; MO and ADMIN can download any TA's CV.
 * Content-Type is set from the stored metadata with PDF as a fallback.
 *
 * @see edu.bupt.ta.service.CvFileService#resolveExistingCvPath
 */
@WebServlet(urlPatterns = {"/files/cv/*"})
public class FileDownloadServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final CvFileService cvFileService = new CvFileService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User ID is required");
            return;
        }

        String userId = pathInfo.substring(1);
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            req.getSession().setAttribute("flashError", "Please login first.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        User targetUser = userService.findById(userId);
        if (targetUser == null || targetUser.getRole() != UserRole.TA) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "TA not found");
            return;
        }

        if (!canViewCv(currentUser, targetUser)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to view this CV");
            return;
        }

        Path cvPath = cvFileService.resolveExistingCvPath(resolveStoredName(targetUser));
        if (cvPath == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No uploaded CV file is available for this TA");
            return;
        }

        String fileName = targetUser.getCvOriginalName();
        if (fileName == null || fileName.isBlank()) {
            fileName = cvPath.getFileName().toString();
        }
        String contentType = targetUser.getCvContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = getServletContext().getMimeType(fileName);
        }
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/pdf";
        }

        resp.setContentType(contentType);
        resp.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
        resp.setContentLengthLong(Files.size(cvPath));

        try (InputStream inputStream = Files.newInputStream(cvPath);
             OutputStream outputStream = resp.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private boolean canViewCv(User currentUser, User targetUser) {
        if (currentUser == null || targetUser == null) {
            return false;
        }
        if (currentUser.getRole() == UserRole.TA) {
            return currentUser.getUserId().equals(targetUser.getUserId());
        }
        return currentUser.getRole() == UserRole.MO || currentUser.getRole() == UserRole.ADMIN;
    }

    private String resolveStoredName(User targetUser) {
        if (targetUser.getCvStoredName() != null && !targetUser.getCvStoredName().isBlank()) {
            return targetUser.getCvStoredName();
        }
        return targetUser.getUserId() + ".pdf";
    }
}
