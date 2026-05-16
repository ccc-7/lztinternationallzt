package edu.bupt.ta.controller;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Displays a structured candidate summary view for a TA's profile.
 * TA users can only view their own summary; MO and ADMIN users can view any TA's summary.
 * The summary is rendered via the shared {@code cv-view.jsp} template.
 *
 * @see edu.bupt.ta.model.ApplicationWithJob
 */
@WebServlet(urlPatterns = {"/files/cv-summary/*"})
public class CandidateSummaryServlet extends HttpServlet {

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
            req.getSession().setAttribute("flashError", "Please login first.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        if (currentUser.getRole() == UserRole.TA && !currentUser.getUserId().equals(userId)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You can only view your own candidate summary");
            return;
        }

        User target = userService.findById(userId);
        if (target == null || target.getRole() != UserRole.TA) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "TA not found");
            return;
        }

        req.setAttribute("cvUser", target);
        req.setAttribute("summaryViewMode", Boolean.TRUE);
        req.setAttribute("cvFileAvailable", userService.hasUploadedCv(target));
        req.setAttribute("cvDownloadHref", req.getContextPath() + "/files/cv/" + target.getUserId());
        req.setAttribute("summaryNotice", "Generated from structured profile information. Original PDF CV is now managed separately from this summary.");
        req.getRequestDispatcher("/WEB-INF/jsp/common/cv-view.jsp").forward(req, resp);
    }
}
