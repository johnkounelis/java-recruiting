package com.recruiting.controller;

import com.google.gson.Gson;
import com.recruiting.model.Job;
import com.recruiting.model.User;
import com.recruiting.service.JobService;
import com.recruiting.util.InputSanitizer;

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
import java.util.Optional;

@WebServlet("/api/jobs/*")
public class JobServlet extends HttpServlet {
    private JobService jobService = new JobService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        try {
            String pathInfo = request.getPathInfo();
            String search = request.getParameter("search");

            if (pathInfo == null || pathInfo.equals("/")) {
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

                List<Job> jobs;
                int totalJobs = 0;

                if (search != null && !search.trim().isEmpty()) {
                    jobs = jobService.searchJobs(search.trim(), limit, offset);
                    totalJobs = jobService.countSearchJobs(search.trim());
                } else {
                    jobs = jobService.getAllActiveJobs(limit, offset);
                    totalJobs = jobService.countAllActiveJobs();
                }

                int totalPages = (int) Math.ceil((double) totalJobs / limit);

                result.put("success", true);
                result.put("jobs", jobs);
                result.put("currentPage", page);
                result.put("totalPages", totalPages);
                result.put("totalJobs", totalJobs);

                if (search != null && !search.trim().isEmpty()) {
                    result.put("searchTerm", search.trim());
                }
            } else {
                try {
                    int jobId = Integer.parseInt(pathInfo.substring(1));
                    Optional<Job> jobOpt = jobService.getJobById(jobId);
                    if (jobOpt.isPresent()) {
                        result.put("success", true);
                        result.put("job", jobOpt.get());
                    } else {
                        result.put("success", false);
                        result.put("message", "Job δεν βρέθηκε");
                    }
                } catch (NumberFormatException e) {
                    result.put("success", false);
                    result.put("message", "Λάθος Job ID");
                }
            }
        } catch (RuntimeException e) {
            handleError(result, e, "JobServlet GET");
        } catch (Exception e) {
            handleUnexpectedError(result, e, "JobServlet GET");
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

        if (!isAuthenticated(session, result, out))
            return;

        User user = (User) session.getAttribute("user");
        if (!user.getRole().equals("RECRUITER") && !user.getRole().equals("ADMIN")) {
            result.put("success", false);
            result.put("message", "Μόνο recruiters μπορούν να δημιουργήσουν jobs");
            out.print(gson.toJson(result));
            return;
        }

        try {
            String title = InputSanitizer.sanitizeTitle(request.getParameter("title"));
            String description = InputSanitizer.sanitizeDescription(request.getParameter("description"));
            String company = InputSanitizer.sanitizeCompany(request.getParameter("company"));
            String location = InputSanitizer.sanitizeLocation(request.getParameter("location"));
            String category = InputSanitizer.sanitizeGeneric(request.getParameter("category"));

            if (title == null || title.trim().isEmpty() || title.trim().length() < 3) {
                result.put("success", false);
                result.put("message", "Ο τίτλος πρέπει να έχει τουλάχιστον 3 χαρακτήρες");
                return;
            }
            if (description == null || description.trim().isEmpty() || description.trim().length() < 10) {
                result.put("success", false);
                result.put("message", "Η περιγραφή πρέπει να έχει τουλάχιστον 10 χαρακτήρες");
                return;
            }
            if (company == null || company.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Το όνομα εταιρείας είναι υποχρεωτικό");
                return;
            }
            if (location == null || location.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Η τοποθεσία είναι υποχρεωτική");
                return;
            }

            Job job = new Job();
            job.setTitle(title.trim());
            job.setDescription(description.trim());
            job.setCompany(company.trim());
            job.setLocation(location.trim());
            job.setCategory(category != null && !category.trim().isEmpty() ? category.trim() : "OTHER");
            job.setPostedBy(user.getId());
            job.setStatus("ACTIVE");

            boolean created = jobService.createJob(job);
            if (created) {
                result.put("success", true);
                result.put("message", "Job δημιουργήθηκε επιτυχώς");
            } else {
                result.put("success", false);
                result.put("message", "Η δημιουργία job απέτυχε");
            }
        } catch (RuntimeException e) {
            handleError(result, e, "JobServlet POST");
        } catch (Exception e) {
            handleUnexpectedError(result, e, "JobServlet POST");
        } finally {
            out.print(gson.toJson(result));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        Map<String, Object> result = new HashMap<>();

        if (!isAuthenticated(session, result, out))
            return;

        User user = (User) session.getAttribute("user");
        if (!user.getRole().equals("RECRUITER") && !user.getRole().equals("ADMIN")) {
            result.put("success", false);
            result.put("message", "Μη εξουσιοδοτημένος");
            out.print(gson.toJson(result));
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            result.put("success", false);
            result.put("message", "Job ID είναι υποχρεωτικό");
            out.print(gson.toJson(result));
            return;
        }

        try {
            int jobId = Integer.parseInt(pathInfo.substring(1));

            String title = InputSanitizer.sanitizeTitle(request.getParameter("title"));
            String description = InputSanitizer.sanitizeDescription(request.getParameter("description"));
            String company = InputSanitizer.sanitizeCompany(request.getParameter("company"));
            String location = InputSanitizer.sanitizeLocation(request.getParameter("location"));
            String category = InputSanitizer.sanitizeGeneric(request.getParameter("category"));
            String status = InputSanitizer.sanitizeGeneric(request.getParameter("status"));

            // If only status is provided, this is a close/reopen operation
            if (status != null && !status.trim().isEmpty() &&
                    (title == null || title.trim().isEmpty())) {
                if (status.equals("CLOSED")) {
                    boolean closed = jobService.closeJob(jobId);
                    if (closed) {
                        result.put("success", true);
                        result.put("message", "Η θέση έκλεισε επιτυχώς");
                    } else {
                        result.put("success", false);
                        result.put("message", "Η ενέργεια απέτυχε");
                    }
                } else {
                    result.put("success", false);
                    result.put("message", "Μη έγκυρη κατάσταση");
                }
            } else {
                // Full update
                if (title == null || title.trim().length() < 3) {
                    result.put("success", false);
                    result.put("message", "Ο τίτλος πρέπει να έχει τουλάχιστον 3 χαρακτήρες");
                    return;
                }
                if (description == null || description.trim().length() < 10) {
                    result.put("success", false);
                    result.put("message", "Η περιγραφή πρέπει να έχει τουλάχιστον 10 χαρακτήρες");
                    return;
                }

                Job job = new Job();
                job.setId(jobId);
                job.setTitle(title.trim());
                job.setDescription(description.trim());
                job.setCompany(company != null ? company.trim() : "");
                job.setLocation(location != null ? location.trim() : "");
                job.setCategory(category != null && !category.trim().isEmpty() ? category.trim() : "OTHER");
                job.setStatus(status != null ? status.trim() : "ACTIVE");

                boolean updated = jobService.updateJob(job, user.getId());
                if (updated) {
                    result.put("success", true);
                    result.put("message", "Η θέση ενημερώθηκε επιτυχώς");
                } else {
                    result.put("success", false);
                    result.put("message", "Η ενημέρωση απέτυχε. Η θέση μπορεί να μην υπάρχει ή να μην ανήκει σε εσάς.");
                }
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Λάθος Job ID");
        } catch (RuntimeException e) {
            handleError(result, e, "JobServlet PUT");
        } catch (Exception e) {
            handleUnexpectedError(result, e, "JobServlet PUT");
        } finally {
            out.print(gson.toJson(result));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        Map<String, Object> result = new HashMap<>();

        if (!isAuthenticated(session, result, out))
            return;

        User user = (User) session.getAttribute("user");
        if (!user.getRole().equals("RECRUITER") && !user.getRole().equals("ADMIN")) {
            result.put("success", false);
            result.put("message", "Μη εξουσιοδοτημένος");
            out.print(gson.toJson(result));
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            result.put("success", false);
            result.put("message", "Job ID είναι υποχρεωτικό");
            out.print(gson.toJson(result));
            return;
        }

        try {
            int jobId = Integer.parseInt(pathInfo.substring(1));
            
            if ("RECRUITER".equals(user.getRole())) {
                Optional<Job> jobOpt = jobService.getJobById(jobId);
                if (!jobOpt.isPresent() || jobOpt.get().getPostedBy() != user.getId()) {
                    result.put("success", false);
                    result.put("message", "Μη εξουσιοδοτημένος. Μπορείτε να διαγράψετε μόνο δικές σας θέσεις.");
                    return;
                }
            }

            boolean deleted = jobService.deleteJob(jobId);
            if (deleted) {
                result.put("success", true);
                result.put("message", "Η θέση διαγράφηκε επιτυχώς");
            } else {
                result.put("success", false);
                result.put("message", "Η διαγραφή απέτυχε. Η θέση μπορεί να μην υπάρχει.");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Λάθος Job ID");
        } catch (RuntimeException e) {
            handleError(result, e, "JobServlet DELETE");
        } catch (Exception e) {
            handleUnexpectedError(result, e, "JobServlet DELETE");
        } finally {
            out.print(gson.toJson(result));
        }
    }

    private boolean isAuthenticated(HttpSession session, Map<String, Object> result, PrintWriter out) {
        if (session == null || session.getAttribute("user") == null) {
            result.put("success", false);
            result.put("message", "Πρέπει να είστε συνδεδεμένος");
            out.print(gson.toJson(result));
            return false;
        }
        return true;
    }

    private void handleError(Map<String, Object> result, RuntimeException e, String location) {
        String errorMsg = e.getMessage();
        if (errorMsg != null && errorMsg.contains("Database")) {
            System.err.println("Database error in " + location + ": " + errorMsg);
            result.put("success", false);
            result.put("message", "Σφάλμα σύνδεσης με τη βάση δεδομένων.");
        } else {
            System.err.println("Error in " + location + ": " + errorMsg);
            result.put("success", false);
            result.put("message", "Σφάλμα κατά την εκτέλεση.");
        }
        e.printStackTrace();
    }

    private void handleUnexpectedError(Map<String, Object> result, Exception e, String location) {
        System.err.println("Unexpected error in " + location + ": " + e.getMessage());
        e.printStackTrace();
        result.put("success", false);
        result.put("message", "Απροσδόκητο σφάλμα.");
    }
}
