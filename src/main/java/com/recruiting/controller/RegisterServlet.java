package com.recruiting.controller;

import com.google.gson.Gson;
import com.recruiting.model.User;
import com.recruiting.service.UserService;
import com.recruiting.util.InputSanitizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {
    private UserService userService = new UserService();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String email = InputSanitizer.sanitizeEmail(request.getParameter("email"));
        String password = request.getParameter("password");
        String name = InputSanitizer.sanitizeName(request.getParameter("name"));
        String role = InputSanitizer.sanitizeGeneric(request.getParameter("role"));

        Map<String, Object> result = new HashMap<>();

        // Validation
        if (email == null || password == null || name == null ||
                email.trim().isEmpty() || password.isEmpty() || name.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "All fields are required");
            out.print(gson.toJson(result));
            return;
        }

        // Email validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            result.put("success", false);
            result.put("message", "Invalid email address");
            out.print(gson.toJson(result));
            return;
        }

        // Password strength validation
        if (password.length() < 8) {
            result.put("success", false);
            result.put("message", "Password must be at least 8 characters");
            out.print(gson.toJson(result));
            return;
        }

        if (!password.matches(".*[A-Z].*") || !password.matches(".*[0-9].*")) {
            result.put("success", false);
            result.put("message", "Password must contain at least one uppercase letter and one digit");
            out.print(gson.toJson(result));
            return;
        }

        // Name validation - must be at least 2 characters and contain only letters/spaces
        if (name.trim().length() < 2) {
            result.put("success", false);
            result.put("message", "Name must be at least 2 characters");
            out.print(gson.toJson(result));
            return;
        }

        if (!name.trim().matches("^[\\p{L} .'-]+$")) {
            result.put("success", false);
            result.put("message", "Name contains invalid characters");
            out.print(gson.toJson(result));
            return;
        }

        try {
            User user = new User();
            user.setEmail(email.trim().toLowerCase());
            user.setPassword(password);
            user.setName(name.trim());
            // Enforce valid role whitelist
            String validRole = "CANDIDATE";
            if (role != null && (role.equals("RECRUITER") || role.equals("CANDIDATE"))) {
                validRole = role;
            }
            user.setRole(validRole);

            boolean registered = userService.register(user);
            if (registered) {
                result.put("success", true);
                result.put("message", "Registration successful! You can now login.");
            } else {
                result.put("success", false);
                result.put("message", "Registration failed. Email may already be in use.");
            }
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("Database")) {
                System.err.println("Database error in RegisterServlet: " + errorMsg);
                result.put("success", false);
                result.put("message", "Database connection error. Please check your database setup.");
            } else {
                System.err.println("Error in RegisterServlet: " + errorMsg);
                result.put("success", false);
                result.put("message", "Registration error. Please try again.");
            }
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in RegisterServlet: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Unexpected error. Please contact the administrator.");
        }
        out.print(gson.toJson(result));
    }
}
