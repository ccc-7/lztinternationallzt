package edu.bupt.ta.controller;

import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.JobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/mo/jobs/new")
public class MOJobServlet extends HttpServlet {

    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.MO) {
            req.getSession().setAttribute("flashError", "please log in as a MO to create jobs.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/jsp/mo/new-job.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.MO) {
            req.getSession().setAttribute("flashError", "please log in as a MO to create jobs.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        try {
            String title = req.getParameter("title");
            String moduleCode = req.getParameter("moduleCode");
            String organiser = req.getParameter("organiser");
            String hoursStr = req.getParameter("hours");
            String minYearStr = req.getParameter("minYear");
            String maxYearStr = req.getParameter("maxYear");
            String requiredSkills = req.getParameter("requiredSkills");
            String deadline = req.getParameter("deadline");
            String vacanciesStr = req.getParameter("vacancies");

            int hours = Integer.parseInt(hoursStr);
            int minYear = Integer.parseInt(minYearStr);
            int maxYear = Integer.parseInt(maxYearStr);
            int vacancies = Integer.parseInt(vacanciesStr);

            if (organiser == null || organiser.isBlank()) {
                organiser = user.getDisplayName();
            }

            jobService.createJob(title, moduleCode, organiser, minYear, maxYear, hours, requiredSkills, deadline, vacancies);
            req.getSession().setAttribute("flashSuccess", "Job created successfully");
            resp.sendRedirect(req.getContextPath() + "/mo/dashboard");
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Failed to create job, please check your input");
            resp.sendRedirect(req.getContextPath() + "/mo/jobs/new");
        }
    }
}