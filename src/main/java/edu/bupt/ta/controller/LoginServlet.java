package edu.bupt.ta.controller;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.LogService;
import edu.bupt.ta.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final LogService logService = new LogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/home");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String roleParam = req.getParameter("role");

        User user = userService.authenticate(username, password);
        if (user == null) {
            setFlash(req, "flashError", "username or password is incorrect");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        UserRole expectedRole = UserRole.fromString(roleParam);
        if (user.getRole() != expectedRole) {
            setFlash(req, "flashError", "login identity does not match the account role");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            setFlash(req, "flashError", "The account has been disabled. Please contact the administrator.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        HttpSession session = req.getSession();
        session.setAttribute("currentUser", user);

        logService.log(user.getUserId(), user.getDisplayName(), "LOGIN", "User",
                user.getUserId(), "User login", getClientIP(req));

        setFlash(req, "flashSuccess", "Login successful, welcome back " + user.getDisplayName());

        switch (user.getRole()) {
            case TA -> resp.sendRedirect(req.getContextPath() + "/ta/dashboard");
            case MO -> resp.sendRedirect(req.getContextPath() + "/mo/dashboard");
            case ADMIN -> resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        }
    }

    private void setFlash(HttpServletRequest req, String key, String value) {
        req.getSession().setAttribute(key, value);
    }

    private String getClientIP(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }
        return ip;
    }
}