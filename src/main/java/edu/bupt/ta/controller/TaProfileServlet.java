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

@WebServlet(urlPatterns = {"/ta/profile"})
public class TaProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.TA) {
            req.getSession().setAttribute("flashError", "请先以 TA 身份登录");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.setAttribute("profileUser", userService.findById(user.getUserId()));
        req.getRequestDispatcher("/WEB-INF/jsp/ta/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.TA) {
            req.getSession().setAttribute("flashError", "请先以 TA 身份登录");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String yearStr = req.getParameter("year");
        String major = req.getParameter("major");
        String skills = req.getParameter("skills");
        String availability = req.getParameter("availability");

        int year = 0;
        if (yearStr != null && !yearStr.isBlank()) {
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                req.getSession().setAttribute("flashError", "Year 格式不正确");
                resp.sendRedirect(req.getContextPath() + "/ta/profile");
                return;
            }
        }

        try {
            User updated = userService.updateProfile(user.getUserId(), name, email, year, major, skills, availability);
            req.getSession().setAttribute("currentUser", updated);
            req.getSession().setAttribute("flashSuccess", "资料已保存（已写入 CSV）");
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "保存失败，请稍后重试");
        }

        resp.sendRedirect(req.getContextPath() + "/ta/profile");
    }
}

