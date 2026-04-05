package edu.bupt.ta.controller;

import edu.bupt.ta.model.Application;
import edu.bupt.ta.model.ApplicationStatus;
import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;
import edu.bupt.ta.service.ApplicationService;
import edu.bupt.ta.service.JobService;
import edu.bupt.ta.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/mo/applications", "/mo/applications/update"})
public class MOApplicationServlet extends HttpServlet {

    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null || user.getRole() != UserRole.MO) {
            req.getSession().setAttribute("flashError", "请先以 MO 身份登录");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        List<Application> applications;
        if (req.getParameter("jobId") != null && !req.getParameter("jobId").isBlank()) {
            applications = applicationService.getApplicationsByJobId(req.getParameter("jobId"));
            req.setAttribute("filterJobId", req.getParameter("jobId"));
        } else {
            applications = applicationService.getAllApplications();
        }

        List<Job> jobs = jobService.getAllJobs();
        Map<String, String> jobTitles = new HashMap<>();
        for (Job job : jobs) {
            jobTitles.put(job.getJobId(), job.getTitle());
        }

        Map<String, String> applicantNames = new HashMap<>();
        Map<String, Integer> applicantCounts = new HashMap<>();
        for (Application app : applications) {
            User applicant = userService.findById(app.getUserId());
            if (applicant != null) {
                applicantNames.put(app.getUserId(), applicant.getDisplayName());
            }
        }

        Map<String, String> statusLabels = new HashMap<>();
        statusLabels.put("PENDING", "待审核");
        statusLabels.put("ACCEPTED", "已录用");
        statusLabels.put("REJECTED", "已拒绝");
        statusLabels.put("INTERVIEW", "面试");

        req.setAttribute("applications", applications);
        req.setAttribute("jobs", jobs);
        req.setAttribute("jobTitles", jobTitles);
        req.setAttribute("applicantNames", applicantNames);
        req.setAttribute("statusLabels", statusLabels);

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
        String filterJobId = req.getParameter("filterJobId");

        try {
            applicationService.updateStatus(applicationId, ApplicationStatus.fromString(status));
            req.getSession().setAttribute("flashSuccess", "申请状态更新成功");
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "更新失败：" + e.getMessage());
        }

        if (filterJobId != null && !filterJobId.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/mo/applications?jobId=" + filterJobId);
        } else {
            resp.sendRedirect(req.getContextPath() + "/mo/applications");
        }
    }
}