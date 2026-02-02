package com.recruiting.controller;

import com.google.gson.Gson;
import com.recruiting.model.User;
import com.recruiting.service.UserService;
import com.recruiting.util.PasswordUtil;

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

@WebServlet("/api/profile")
public class ProfileServlet extends HttpServlet {
    private UserService userService = new UserService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole());

        result.put("success", true);
        result.put("profile", profile);
        out.print(gson.toJson(result));
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
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");

        try {
            if (name == null || name.trim().length() < 2) {
                result.put("success", false);
                result.put("message", "Το όνομα πρέπει να έχει τουλάχιστον 2 χαρακτήρες");
                out.print(gson.toJson(result));
                return;
            }

            if (email == null || email.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Το email είναι υποχρεωτικό");
                out.print(gson.toJson(result));
                return;
            }

            // Check if email is already taken by another user
            String normalizedEmail = email.trim().toLowerCase();
            if (!normalizedEmail.equals(user.getEmail())) {
                if (userService.getUserByEmail(normalizedEmail).isPresent()) {
                    result.put("success", false);
                    result.put("message", "Αυτό το email χρησιμοποιείται ήδη");
                    out.print(gson.toJson(result));
                    return;
                }
            }

            // Handle password change
            if (newPassword != null && !newPassword.isEmpty()) {
                if (currentPassword == null || currentPassword.isEmpty()) {
                    result.put("success", false);
                    result.put("message", "Πρέπει να εισάγετε τον τρέχον κωδικό");
                    out.print(gson.toJson(result));
                    return;
                }

                // Verify current password
                String storedPassword = user.getPassword();
                boolean passwordValid = false;
                if (storedPassword != null && storedPassword.length() == 64) {
                    passwordValid = PasswordUtil.verifyPassword(currentPassword, storedPassword);
                } else {
                    passwordValid = storedPassword != null && storedPassword.equals(currentPassword);
                }

                if (!passwordValid) {
                    result.put("success", false);
                    result.put("message", "Λάθος τρέχων κωδικός");
                    out.print(gson.toJson(result));
                    return;
                }

                if (newPassword.length() < 6) {
                    result.put("success", false);
                    result.put("message", "Ο νέος κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες");
                    out.print(gson.toJson(result));
                    return;
                }

                user.setPassword(PasswordUtil.hashPassword(newPassword));
            }

            user.setName(name.trim());
            user.setEmail(normalizedEmail);

            boolean updated = userService.updateUser(user);
            if (updated) {
                // Update session with new user data
                session.setAttribute("user", user);
                result.put("success", true);
                result.put("message", "Το προφίλ ενημερώθηκε επιτυχώς");
            } else {
                result.put("success", false);
                result.put("message", "Η ενημέρωση απέτυχε");
            }
        } catch (Exception e) {
            System.err.println("Error in ProfileServlet POST: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Σφάλμα κατά την ενημέρωση του προφίλ.");
        } finally {
            out.print(gson.toJson(result));
        }
    }
}
