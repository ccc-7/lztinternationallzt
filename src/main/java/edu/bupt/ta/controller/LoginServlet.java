package edu.bupt.ta.controller;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
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
            setFlash(req, "flashError", "用户名或密码错误");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        UserRole expectedRole = UserRole.fromString(roleParam);
        if (user.getRole() != expectedRole) {
            setFlash(req, "flashError", "登录身份与账号角色不匹配");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        HttpSession session = req.getSession();
        session.setAttribute("currentUser", user);
        setFlash(req, "flashSuccess", "登录成功，欢迎回来 " + user.getDisplayName());

        switch (user.getRole()) {
            case TA -> resp.sendRedirect(req.getContextPath() + "/ta/dashboard");
            case MO -> resp.sendRedirect(req.getContextPath() + "/mo/dashboard");
            case ADMIN -> resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        }
    }

    private void setFlash(HttpServletRequest req, String key, String value) {
        req.getSession().setAttribute(key, value);
    }
}