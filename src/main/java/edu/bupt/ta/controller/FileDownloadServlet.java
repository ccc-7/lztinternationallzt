package edu.bupt.ta.controller;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.UserService;
import edu.bupt.ta.storage.FileStorageUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet(urlPatterns = {"/files/cv/*"})
public class FileDownloadServlet extends HttpServlet {

    private static final String CV_DIR = "cvs";
    private final UserService userService = new UserService();

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
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login first");
            return;
        }

        if (currentUser.getRole() != UserRole.MO && currentUser.getRole() != UserRole.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to view CVs");
            return;
        }

        FileStorageUtil storage = FileStorageUtil.getInstance();
        File cvDir = new File(storage.getBaseDir().toFile(), CV_DIR);

        if (!cvDir.exists()) {
            cvDir.mkdirs();
        }

        File cvFile = new File(cvDir, userId + ".pdf");

        if (!cvFile.exists()) {
            File[] files = cvDir.listFiles((dir, name) -> name.startsWith(userId + "_"));
            if (files != null && files.length > 0) {
                cvFile = files[0];
            }
        }

        if (cvFile.exists() && cvFile.isFile()) {
            String fileName = cvFile.getName();
            String contentType = getServletContext().getMimeType(fileName);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            resp.setContentType(contentType);
            resp.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
            resp.setContentLength((int) cvFile.length());

            try (FileInputStream fis = new FileInputStream(cvFile);
                 OutputStream os = resp.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } else {
            User taUser = userService.findById(userId);
            if (taUser == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "TA not found");
                return;
            }

            req.setAttribute("cvUser", taUser);
            req.getRequestDispatcher("/WEB-INF/jsp/common/cv-view.jsp").forward(req, resp);
        }
    }
}
