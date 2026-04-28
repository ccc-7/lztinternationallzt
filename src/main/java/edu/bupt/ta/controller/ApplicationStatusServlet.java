package edu.bupt.ta.controller;

import edu.bupt.ta.model.Application;
import edu.bupt.ta.model.ApplicationWithJob;
import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.ApplicationService;
import edu.bupt.ta.service.JobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/applications")
public class ApplicationStatusServlet extends HttpServlet {

    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.TA) {
            req.getSession().setAttribute("flashError", "please log in as a TA to view your applications.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        List<Application> applications = applicationService.getApplicationsByUserId(user.getUserId());
        List<ApplicationWithJob> applicationsWithJob = new ArrayList<>();

        for (Application app : applications) {
            var job = jobService.findById(app.getJobId());
            if (job != null) {
                applicationsWithJob.add(new ApplicationWithJob(
                        app,
                        job.getTitle(),
                        job.getModuleCode(),
                        job.getOrganiser()
                ));
            } else {
                applicationsWithJob.add(new ApplicationWithJob(
                        app,
                        "Unknown Job",
                        "-",
                        "-"
                ));
            }
        }

        req.setAttribute("applications", applicationsWithJob);
        req.getRequestDispatcher("/WEB-INF/jsp/ta/applications.jsp").forward(req, resp);
    }
}
