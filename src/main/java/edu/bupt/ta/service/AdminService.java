package edu.bupt.ta.service;

import edu.bupt.ta.model.Application;
import edu.bupt.ta.model.User;
import edu.bupt.ta.model.UserRole;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminService {

    private final UserService userService = new UserService();
    private final ApplicationService applicationService = new ApplicationService();

    public Map<String, Integer> calculateUserWorkloads() {
        List<User> users = userService.getAllUsers();
        List<Application> applications = applicationService.getAllApplications();

        Map<String, Integer> workloads = new LinkedHashMap<>();

        for (User user : users) {
            if (user.getRole() == UserRole.TA) {
                int count = 0;
                for (Application app : applications) {
                    if (app.getUserId().equals(user.getUserId())) {
                        count++;
                    }
                }
                workloads.put(user.getUserId(), count);
            }
        }

        return workloads;
    }
}