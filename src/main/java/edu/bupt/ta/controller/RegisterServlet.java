package edu.bupt.ta.controller;

import edu.bupt.ta.model.User;
import edu.bupt.ta.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String yearStr = req.getParameter("year");
        String major = req.getParameter("major");
        String skills = req.getParameter("skills");

        try {
            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                req.getSession().setAttribute("flashError", "please fill in both username and password");
                resp.sendRedirect(req.getContextPath() + "/register");
                return;
            }

            int year = 0;
            if (yearStr != null && !yearStr.isBlank()) {
                year = Integer.parseInt(yearStr);
            }

            User user = userService.registerTa(
                    username,
                    password,
                    name == null ? "" : name,
                    email == null ? "" : email,
                    year,
                    major == null ? "" : major,
                    skills == null ? "" : skills
            );
            req.getSession().setAttribute("currentUser", user);
            req.getSession().setAttribute("flashSuccess", "Registration successful, you are now logged in");
            resp.sendRedirect(req.getContextPath() + "/ta/dashboard");
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("flashError", "Invalid year format");
            resp.sendRedirect(req.getContextPath() + "/register");
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/register");
        }
    }
}