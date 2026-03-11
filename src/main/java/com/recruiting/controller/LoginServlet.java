package com.recruiting.controller;

import com.google.gson.Gson;
import com.recruiting.model.User;
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
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());
    private UserService userService = new UserService();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Validation
            if (email == null || password == null || email.trim().isEmpty() || password.isEmpty()) {
                result.put("success", false);
                result.put("message", "Email and password are required");
                return;
            }

            // Attempt login
            logger.info("Login attempt for email: " + email);
            Optional<User> userOpt = userService.login(email, password);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                logger.info("Successful login for user: " + user.getEmail() + " (role: " + user.getRole() + ")");
                
                // Create session - prevent session fixation
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession session = request.getSession(true);
                
                // Set session attributes
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("userRole", user.getRole());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                
                // Additional session security
                session.setAttribute("loginTime", System.currentTimeMillis());
                
                result.put("success", true);
                result.put("message", "Login successful. Welcome, " + user.getName() + "!");
                
                // Don't send full user object with password
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("email", user.getEmail());
                userData.put("name", user.getName());
                userData.put("role", user.getRole());
                result.put("user", userData);
                String redirectUrl = getRedirectUrl(user.getRole());
                result.put("redirect", redirectUrl);
            } else {
                logger.warning("Failed login attempt for email: " + email);
                result.put("success", false);
                result.put("message", "Wrong email or password. Please try again.");
            }
            
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("Database")) {
                logger.log(Level.SEVERE, "Database error during login", e);
                result.put("success", false);
                result.put("message", "Database connection error. Please check your database setup.");
            } else {
                logger.log(Level.SEVERE, "Runtime error during login: " + errorMsg, e);
                result.put("success", false);
                result.put("message", "Login error. Please try again.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during login", e);
            result.put("success", false);
            result.put("message", "Unexpected error. Please contact the administrator.");
        } finally {
            String finalResponse = gson.toJson(result);
            out.print(finalResponse);
            out.flush();
        }
    }

    private String getRedirectUrl(String role) {
        switch (role) {
            case "ADMIN":
                return "admin/dashboard.jsp";
            case "RECRUITER":
                return "recruiter/dashboard.jsp";
            case "CANDIDATE":
            default:
                return "candidate/dashboard.jsp";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}
