package com.recruiting.controller;

import com.google.gson.Gson;
import com.recruiting.model.Company;
import com.recruiting.model.User;
import com.recruiting.service.CompanyService;
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

@WebServlet("/api/companies/*")
public class CompanyServlet extends HttpServlet {
    private CompanyService companyService = new CompanyService();
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
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Company> companies = companyService.getAllCompanies();
                result.put("success", true);
                result.put("companies", companies);
            } else {
                try {
                    int companyId = Integer.parseInt(pathInfo.substring(1));
                    Optional<Company> companyOpt = companyService.getCompanyById(companyId);
                    if (companyOpt.isPresent()) {
                        result.put("success", true);
                        result.put("company", companyOpt.get());
                    } else {
                        result.put("success", false);
                        result.put("message", "Η εταιρεία δεν βρέθηκε");
                    }
                } catch (NumberFormatException e) {
                    result.put("success", false);
                    result.put("message", "Λάθος ID εταιρείας");
                }
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Σφάλμα κατά τη φόρτωση");
            e.printStackTrace();
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
        Map<String, Object> result = new HashMap<>();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            result.put("success", false);
            result.put("message", "Πρέπει να είστε συνδεδεμένος");
            out.print(gson.toJson(result));
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"RECRUITER".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
            result.put("success", false);
            result.put("message", "Μη εξουσιοδοτημένος");
            out.print(gson.toJson(result));
            return;
        }

        try {
            String name = InputSanitizer.sanitizeCompany(request.getParameter("name"));
            String description = InputSanitizer.sanitizeDescription(request.getParameter("description"));
            String website = InputSanitizer.sanitizeGeneric(request.getParameter("website"));
            String location = InputSanitizer.sanitizeLocation(request.getParameter("location"));
            String industry = InputSanitizer.sanitizeGeneric(request.getParameter("industry"));

            if (name == null || name.trim().length() < 2) {
                result.put("success", false);
                result.put("message", "Το όνομα εταιρείας πρέπει να έχει τουλάχιστον 2 χαρακτήρες");
                out.print(gson.toJson(result));
                return;
            }

            Company company = new Company();
            company.setName(name.trim());
            company.setDescription(description != null ? description.trim() : "");
            company.setWebsite(website != null ? website.trim() : "");
            company.setLocation(location != null ? location.trim() : "");
            company.setIndustry(industry != null ? industry.trim() : "");
            company.setCreatedBy(user.getId());

            boolean created = companyService.createCompany(company);
            if (created) {
                result.put("success", true);
                result.put("message", "Η εταιρεία δημιουργήθηκε επιτυχώς");
            } else {
                result.put("success", false);
                result.put("message", "Η δημιουργία απέτυχε. Η εταιρεία μπορεί να υπάρχει ήδη.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Σφάλμα κατά τη δημιουργία");
            e.printStackTrace();
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
        Map<String, Object> result = new HashMap<>();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            result.put("success", false);
            result.put("message", "Πρέπει να είστε συνδεδεμένος");
            out.print(gson.toJson(result));
            return;
        }

        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            result.put("success", false);
            result.put("message", "Company ID είναι υποχρεωτικό");
            out.print(gson.toJson(result));
            return;
        }

        try {
            int companyId = Integer.parseInt(pathInfo.substring(1));
            String name = InputSanitizer.sanitizeCompany(request.getParameter("name"));
            String description = InputSanitizer.sanitizeDescription(request.getParameter("description"));
            String website = InputSanitizer.sanitizeGeneric(request.getParameter("website"));
            String location = InputSanitizer.sanitizeLocation(request.getParameter("location"));
            String industry = InputSanitizer.sanitizeGeneric(request.getParameter("industry"));

            Company company = new Company();
            company.setId(companyId);
            company.setName(name != null ? name.trim() : "");
            company.setDescription(description != null ? description.trim() : "");
            company.setWebsite(website != null ? website.trim() : "");
            company.setLocation(location != null ? location.trim() : "");
            company.setIndustry(industry != null ? industry.trim() : "");

            boolean updated = companyService.updateCompany(company, user.getId());
            if (updated) {
                result.put("success", true);
                result.put("message", "Η εταιρεία ενημερώθηκε");
            } else {
                result.put("success", false);
                result.put("message", "Η ενημέρωση απέτυχε");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Λάθος ID εταιρείας");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Σφάλμα κατά την ενημέρωση");
            e.printStackTrace();
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
        Map<String, Object> result = new HashMap<>();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            result.put("success", false);
            result.put("message", "Πρέπει να είστε συνδεδεμένος");
            out.print(gson.toJson(result));
            return;
        }

        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            result.put("success", false);
            result.put("message", "Company ID είναι υποχρεωτικό");
            out.print(gson.toJson(result));
            return;
        }

        try {
            int companyId = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = companyService.deleteCompany(companyId, user.getId());
            if (deleted) {
                result.put("success", true);
                result.put("message", "Η εταιρεία διαγράφηκε");
            } else {
                result.put("success", false);
                result.put("message", "Η διαγραφή απέτυχε");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Λάθος ID εταιρείας");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Σφάλμα κατά τη διαγραφή");
            e.printStackTrace();
        } finally {
            out.print(gson.toJson(result));
        }
    }
}
