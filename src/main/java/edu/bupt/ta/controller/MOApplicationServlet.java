package edu.bupt.ta.controller;

import edu.bupt.ta.model.ApplicationStatus;
import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.ApplicationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/mo/applications", "/mo/applications/update"})
public class MOApplicationServlet extends HttpServlet {

    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.MO) {
            req.getSession().setAttribute("flashError", "请先以 MO 身份登录");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.setAttribute("applications", applicationService.getAllApplications());
        req.getRequestDispatcher("/WEB-INF/jsp/mo/applications.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.MO) {
            req.getSession().setAttribute("flashError", "请先以 MO 身份登录");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String applicationId = req.getParameter("applicationId");
        String status = req.getParameter("status");

        try {
            applicationService.updateStatus(applicationId, ApplicationStatus.fromString(status));
            req.getSession().setAttribute("flashSuccess", "申请状态更新成功");
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "更新失败：" + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/mo/applications");
    }
}