package com.recruiting.controller;

import com.google.gson.Gson;
import com.recruiting.model.User;
import com.recruiting.service.ApplicationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/applications/update")
public class ApplicationUpdateServlet extends HttpServlet {
    private ApplicationService applicationService = new ApplicationService();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        Map<String, Object> result = new HashMap<>();

        if (session == null || session.getAttribute("user") == null) {
            result.put("success", false);
            result.put("message", "Πρέπει να είστε συνδεδεμένος");
            out.print(gson.toJson(result));
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!user.getRole().equals("RECRUITER") && !user.getRole().equals("ADMIN")) {
            result.put("success", false);
            result.put("message", "Μη εξουσιοδοτημένος");
            out.print(gson.toJson(result));
            return;
        }

        String idStr = request.getParameter("id");
        String status = request.getParameter("status");
        String notes = request.getParameter("notes");

        try {
            if (idStr == null || idStr.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "ID είναι υποχρεωτικό");
                return;
            }

            int id = Integer.parseInt(idStr);
            if (status == null || status.isEmpty()) {
                result.put("success", false);
                result.put("message", "Status είναι υποχρεωτικό");
                return;
            }

            if ("RECRUITER".equals(user.getRole())) {
                java.util.Optional<com.recruiting.model.Application> appOpt = applicationService.getApplicationById(id);
                if (appOpt.isPresent()) {
                    com.recruiting.service.JobService jobService = new com.recruiting.service.JobService();
                    java.util.Optional<com.recruiting.model.Job> jobOpt = jobService.getJobById(appOpt.get().getJobId());
                    if (!jobOpt.isPresent() || jobOpt.get().getPostedBy() != user.getId()) {
                        result.put("success", false);
                        result.put("message", "Μη εξουσιοδοτημένη ενέργεια.");
                        return;
                    }
                } else {
                    result.put("success", false);
                    result.put("message", "Η αίτηση δεν βρέθηκε.");
                    return;
                }
            }

            // Validate status
            if (!status.equals("PENDING") && !status.equals("REVIEWED") && 
                !status.equals("ACCEPTED") && !status.equals("REJECTED")) {
                result.put("success", false);
                result.put("message", "Μη έγκυρο status");
                return;
            }

            boolean updated = applicationService.updateApplicationStatus(id, status, notes != null ? notes.trim() : "");
            if (updated) {
                result.put("success", true);
                result.put("message", "Η κατάσταση ενημερώθηκε επιτυχώς");
            } else {
                result.put("success", false);
                result.put("message", "Η ενημέρωση απέτυχε. Η αίτηση μπορεί να μην υπάρχει.");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Λάθος ID");
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("Database")) {
                System.err.println("Database error in ApplicationUpdateServlet: " + errorMsg);
                result.put("success", false);
                result.put("message", "Σφάλμα σύνδεσης με τη βάση δεδομένων.");
            } else {
                System.err.println("Error in ApplicationUpdateServlet: " + errorMsg);
                result.put("success", false);
                result.put("message", "Σφάλμα κατά την ενημέρωση της κατάστασης.");
            }
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in ApplicationUpdateServlet: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Απροσδόκητο σφάλμα.");
        } finally {
            out.print(gson.toJson(result));
        }
    }
}
