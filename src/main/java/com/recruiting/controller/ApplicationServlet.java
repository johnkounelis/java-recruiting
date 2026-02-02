package com.recruiting.controller;

import com.google.gson.Gson;
import com.recruiting.model.Application;
import com.recruiting.model.User;
import com.recruiting.service.ApplicationService;
import com.recruiting.util.InputSanitizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
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

@WebServlet("/api/applications/*")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class ApplicationServlet extends HttpServlet {
    private ApplicationService applicationService = new ApplicationService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
        String pathInfo = request.getPathInfo();

        // Search/filter parameters
        String search = request.getParameter("search");
        String status = request.getParameter("status");

        try {
            // Pagination parameters
            String pageStr = request.getParameter("page");
            int page = 1;
            if (pageStr != null) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (NumberFormatException e) {
                }
            }
            int limit = 10;
            int offset = (page - 1) * limit;

            if (pathInfo != null && pathInfo.equals("/all")) {
                // Admin: Get all applications with optional search/filter
                if (!user.getRole().equals("ADMIN")) {
                    result.put("success", false);
                    result.put("message", "Μη εξουσιοδοτημένος");
                    return;
                }
                List<Application> applications = applicationService.searchAllApplications(search, status, limit,
                        offset);
                int total = applicationService.countAllApplications(search, status);
                int totalPages = (int) Math.ceil((double) total / limit);

                result.put("success", true);
                result.put("applications", applications);
                result.put("currentPage", page);
                result.put("totalPages", totalPages);
                result.put("totalApplications", total);

            } else if (pathInfo != null && pathInfo.startsWith("/job/")) {
                // Get applications for a job (for recruiters) with optional search/filter
                if (!user.getRole().equals("RECRUITER") && !user.getRole().equals("ADMIN")) {
                    result.put("success", false);
                    result.put("message", "Μη εξουσιοδοτημένος");
                    out.print(gson.toJson(result));
                    return;
                }
                try {
                    int jobId = Integer.parseInt(pathInfo.substring(5));
                    
                    if ("RECRUITER".equals(user.getRole())) {
                        com.recruiting.service.JobService jobService = new com.recruiting.service.JobService();
                        java.util.Optional<com.recruiting.model.Job> jobOpt = jobService.getJobById(jobId);
                        if (!jobOpt.isPresent() || jobOpt.get().getPostedBy() != user.getId()) {
                            result.put("success", false);
                            result.put("message", "Μη εξουσιοδοτημένος. Καμία πρόσβαση σε αυτή τη θέση.");
                            return;
                        }
                    }

                    List<Application> applications = applicationService.searchApplicationsByJob(jobId, search, status,
                            limit, offset);
                    int total = applicationService.countApplicationsByJob(jobId, search, status);
                    int totalPages = (int) Math.ceil((double) total / limit);

                    result.put("success", true);
                    result.put("applications", applications);
                    result.put("currentPage", page);
                    result.put("totalPages", totalPages);
                    result.put("totalApplications", total);
                } catch (NumberFormatException e) {
                    result.put("success", false);
                    result.put("message", "Λάθος Job ID");
                }
            } else {
                // Get applications for current candidate with optional search/filter
                List<Application> applications = applicationService.searchApplicationsByCandidate(user.getId(), search,
                        status, limit, offset);
                int total = applicationService.countApplicationsByCandidate(user.getId(), search, status);
                int totalPages = (int) Math.ceil((double) total / limit);

                result.put("success", true);
                result.put("applications", applications);
                result.put("currentPage", page);
                result.put("totalPages", totalPages);
                result.put("totalApplications", total);
            }
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("Database")) {
                System.err.println("Database error in ApplicationServlet GET: " + errorMsg);
                result.put("success", false);
                result.put("message", "Σφάλμα σύνδεσης με τη βάση δεδομένων.");
            } else {
                System.err.println("Error in ApplicationServlet GET: " + errorMsg);
                result.put("success", false);
                result.put("message", "Σφάλμα κατά τη φόρτωση των αιτήσεων.");
            }
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in ApplicationServlet GET: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Απροσδόκητο σφάλμα.");
        } finally {
            out.print(gson.toJson(result));
        }
    }

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
        if (!user.getRole().equals("CANDIDATE")) {
            result.put("success", false);
            result.put("message", "Μόνο candidates μπορούν να υποβάλλουν αίτηση");
            out.print(gson.toJson(result));
            return;
        }

        String jobIdStr = request.getParameter("jobId");
        String notes = InputSanitizer.sanitizeNotes(request.getParameter("notes"));

        try {
            if (jobIdStr == null || jobIdStr.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Job ID είναι υποχρεωτικό");
                return;
            }

            // Handle resume upload if present
            String resumeFilename = null;
            try {
                javax.servlet.http.Part resumePart = request.getPart("resume");
                if (resumePart != null && resumePart.getSize() > 0) {
                    String origName = resumePart.getSubmittedFileName();
                    String ext = origName != null && origName.contains(".")
                            ? origName.substring(origName.lastIndexOf('.')).toLowerCase() : "";

                    if (".pdf".equals(ext) || ".doc".equals(ext) || ".docx".equals(ext)) {
                        resumeFilename = java.util.UUID.randomUUID().toString() + ext;
                        String uploadDir = getServletContext().getRealPath("")
                                + java.io.File.separator + "uploads" + java.io.File.separator + "resumes";
                        java.io.File dir = new java.io.File(uploadDir);
                        if (!dir.exists()) dir.mkdirs();
                        resumePart.write(uploadDir + java.io.File.separator + resumeFilename);
                    }
                }
            } catch (Exception e) {
                // Resume upload is optional, continue without it
                System.err.println("Resume upload skipped: " + e.getMessage());
            }

            int jobId = Integer.parseInt(jobIdStr);
            Application application = new Application();
            application.setJobId(jobId);
            application.setCandidateId(user.getId());
            application.setStatus("PENDING");
            application.setNotes(notes != null ? notes.trim() : null);
            application.setResumeFilename(resumeFilename);

            boolean applied = applicationService.applyForJob(application);
            if (applied) {
                result.put("success", true);
                result.put("message", "Αίτηση υποβλήθηκε επιτυχώς");
            } else {
                result.put("success", false);
                result.put("message", "Η αίτηση απέτυχε. Μπορεί να έχετε ήδη υποβάλλει αίτηση.");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Λάθος Job ID");
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("Database")) {
                System.err.println("Database error in ApplicationServlet: " + errorMsg);
                result.put("success", false);
                result.put("message", "Σφάλμα σύνδεσης με τη βάση δεδομένων.");
            } else {
                System.err.println("Error in ApplicationServlet: " + errorMsg);
                result.put("success", false);
                result.put("message", "Σφάλμα κατά την υποβολή της αίτησης.");
            }
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in ApplicationServlet: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Απροσδόκητο σφάλμα.");
        } finally {
            out.print(gson.toJson(result));
        }
    }
}
