package com.recruiting.controller;

import com.google.gson.Gson;
import com.recruiting.model.User;
import com.recruiting.service.UserService;
import com.recruiting.util.DatabaseConnection;
import com.recruiting.util.InputSanitizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@WebServlet("/api/password-reset/*")
public class PasswordResetServlet extends HttpServlet {
    private UserService userService = new UserService();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.equals("/request")) {
                handleResetRequest(request, result);
            } else if (pathInfo != null && pathInfo.equals("/reset")) {
                handlePasswordReset(request, result);
            } else {
                result.put("success", false);
                result.put("message", "Μη έγκυρο endpoint");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Σφάλμα κατά την επεξεργασία");
            System.err.println("Error in PasswordResetServlet: " + e.getMessage());
            e.printStackTrace();
        } finally {
            out.print(gson.toJson(result));
        }
    }

    private void handleResetRequest(HttpServletRequest request, Map<String, Object> result) {
        String email = InputSanitizer.sanitizeEmail(request.getParameter("email"));

        if (email == null || email.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Το email είναι υποχρεωτικό");
            return;
        }

        if (!InputSanitizer.isValidEmail(email)) {
            result.put("success", false);
            result.put("message", "Μη έγκυρο email");
            return;
        }

        email = email.trim().toLowerCase();

        // Look up user by email
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (!userOpt.isPresent()) {
            // Don't reveal whether the email exists - always show success message
            result.put("success", true);
            result.put("message", "Αν το email υπάρχει στο σύστημα, θα λάβετε οδηγίες επαναφοράς κωδικού.");
            return;
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + (60 * 60 * 1000)); // 1 hour

        // Store token in database
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();

            // Invalidate old tokens for this user
            String invalidateSql = "UPDATE password_reset_tokens SET used = TRUE WHERE user_id = ? AND used = FALSE";
            try (PreparedStatement ps = con.prepareStatement(invalidateSql)) {
                ps.setInt(1, user.getId());
                ps.executeUpdate();
            }

            // Insert new token
            String insertSql = "INSERT INTO password_reset_tokens (user_id, token, expires_at) VALUES (?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setInt(1, user.getId());
                ps.setString(2, token);
                ps.setTimestamp(3, expiresAt);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error storing reset token: " + e.getMessage());
            result.put("success", false);
            result.put("message", "Σφάλμα βάσης δεδομένων");
            return;
        } finally {
            DatabaseConnection.closeConnection(con);
        }

        result.put("success", true);
        result.put("message", "Αν το email υπάρχει στο σύστημα, θα λάβετε οδηγίες επαναφοράς κωδικού.");
        // Demo mode: return token directly (in production, send via email only)
        result.put("resetToken", token);
        result.put("note", "Demo: Σε production, το token θα στελνόταν μέσω email.");
    }

    private void handlePasswordReset(HttpServletRequest request, Map<String, Object> result) {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");

        if (token == null || token.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Το token είναι υποχρεωτικό");
            return;
        }

        if (newPassword == null || newPassword.length() < 6) {
            result.put("success", false);
            result.put("message", "Ο νέος κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες");
            return;
        }

        // Look up token in database
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            String sql = "SELECT prt.user_id, prt.expires_at, u.email " +
                    "FROM password_reset_tokens prt " +
                    "JOIN users u ON prt.user_id = u.id " +
                    "WHERE prt.token = ? AND prt.used = FALSE";

            String email = null;
            Timestamp expiresAt = null;

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, token.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        email = rs.getString("email");
                        expiresAt = rs.getTimestamp("expires_at");
                    }
                }
            }

            if (email == null) {
                result.put("success", false);
                result.put("message", "Μη έγκυρο ή ληγμένο token");
                return;
            }

            if (expiresAt.before(new Timestamp(System.currentTimeMillis()))) {
                // Mark as used
                markTokenUsed(con, token.trim());
                result.put("success", false);
                result.put("message", "Το token έχει λήξει");
                return;
            }

            // Reset password
            boolean updated = userService.resetPassword(email, newPassword);
            markTokenUsed(con, token.trim());

            if (updated) {
                result.put("success", true);
                result.put("message", "Ο κωδικός ενημερώθηκε επιτυχώς");
            } else {
                result.put("success", false);
                result.put("message", "Η ενημέρωση κωδικού απέτυχε");
            }

        } catch (SQLException e) {
            System.err.println("Error during password reset: " + e.getMessage());
            result.put("success", false);
            result.put("message", "Σφάλμα βάσης δεδομένων");
        } finally {
            DatabaseConnection.closeConnection(con);
        }
    }

    private void markTokenUsed(Connection con, String token) throws SQLException {
        String sql = "UPDATE password_reset_tokens SET used = TRUE WHERE token = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.executeUpdate();
        }
    }
}
