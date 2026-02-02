package com.recruiting.controller;

import com.google.gson.Gson;
import com.recruiting.model.Application;
import com.recruiting.model.User;
import com.recruiting.service.ApplicationService;
import com.recruiting.service.JobService;
import com.recruiting.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/statistics")
public class StatisticsServlet extends HttpServlet {
    private UserService userService = new UserService();
    private JobService jobService = new JobService();
    private ApplicationService applicationService = new ApplicationService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                result.put("success", false);
                result.put("message", "Πρέπει να είστε συνδεδεμένος");
                out.print(gson.toJson(result));
                return;
            }

            User user = (User) session.getAttribute("user");
            Map<String, Object> stats = new HashMap<>();

            if (user.getRole().equals("ADMIN")) {
                // Admin statistics
                stats.put("totalUsers", userService.getAllUsers().size());
                stats.put("totalJobs", jobService.getAllActiveJobs().size());
                stats.put("totalApplications", applicationService.getAllApplications().size());
            } else if (user.getRole().equals("RECRUITER")) {
                // Recruiter statistics
                List<com.recruiting.model.Job> myJobs = jobService.getJobsByRecruiter(user.getId());
                stats.put("myJobs", myJobs.size());
                int myTotalApps = 0;
                for (com.recruiting.model.Job job : myJobs) {
                    myTotalApps += applicationService.getApplicationsByJob(job.getId()).size();
                }
                stats.put("totalApplications", myTotalApps);
            } else if (user.getRole().equals("CANDIDATE")) {
                // Candidate statistics
                stats.put("myApplications", applicationService.getApplicationsByCandidate(user.getId()).size());
                stats.put("activeJobs", jobService.getAllActiveJobs().size());

                // Count by status
                List<Application> applications = applicationService.getApplicationsByCandidate(user.getId());
                long pending = 0;
                long reviewed = 0;
                long accepted = 0;
                long rejected = 0;
                for (Application app : applications) {
                    String status = app.getStatus();
                    if ("PENDING".equals(status))
                        pending++;
                    else if ("REVIEWED".equals(status))
                        reviewed++;
                    else if ("ACCEPTED".equals(status))
                        accepted++;
                    else if ("REJECTED".equals(status))
                        rejected++;
                }

                stats.put("pendingApplications", pending);
                stats.put("reviewedApplications", reviewed);
                stats.put("acceptedApplications", accepted);
                stats.put("rejectedApplications", rejected);
            }

            result.put("success", true);
            result.put("statistics", stats);
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("Database")) {
                System.err.println("Database error in StatisticsServlet: " + errorMsg);
                result.put("success", false);
                result.put("message", "Σφάλμα σύνδεσης με τη βάση δεδομένων.");
            } else {
                System.err.println("Error in StatisticsServlet: " + errorMsg);
                result.put("success", false);
                result.put("message", "Σφάλμα κατά τη φόρτωση των στατιστικών.");
            }
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in StatisticsServlet: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Απροσδόκητο σφάλμα.");
        } finally {
            out.print(gson.toJson(result));
        }
    }
}
