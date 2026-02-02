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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
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

        User currentUser = (User) session.getAttribute("user");
        if (!currentUser.getRole().equals("ADMIN")) {
            result.put("success", false);
            result.put("message", "Μη εξουσιοδοτημένος");
            out.print(gson.toJson(result));
            return;
        }

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all users
                List<User> users = userService.getAllUsers();
                // Remove passwords from response
                for (User u : users) {
                    u.setPassword(null);
                }
                result.put("success", true);
                result.put("users", users);
            } else {
                // Get user by ID
                try {
                    int userId = Integer.parseInt(pathInfo.substring(1));
                    Optional<User> userOpt = userService.getUserById(userId);
                    if (userOpt.isPresent()) {
                        User u = userOpt.get();
                        u.setPassword(null);
                        result.put("success", true);
                        result.put("user", u);
                    } else {
                        result.put("success", false);
                        result.put("message", "Ο χρήστης δεν βρέθηκε");
                    }
                } catch (NumberFormatException e) {
                    result.put("success", false);
                    result.put("message", "Λάθος User ID");
                }
            }
        } catch (Exception e) {
            System.err.println("Error in UserServlet GET: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Σφάλμα κατά τη φόρτωση χρηστών.");
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

        User currentUser = (User) session.getAttribute("user");
        if (!currentUser.getRole().equals("ADMIN")) {
            result.put("success", false);
            result.put("message", "Μη εξουσιοδοτημένος");
            out.print(gson.toJson(result));
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            result.put("success", false);
            result.put("message", "User ID είναι υποχρεωτικό");
            out.print(gson.toJson(result));
            return;
        }

        try {
            int userId = Integer.parseInt(pathInfo.substring(1));
            String newRole = request.getParameter("role");

            if (newRole == null || newRole.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "Ο ρόλος είναι υποχρεωτικός");
                out.print(gson.toJson(result));
                return;
            }

            if (!newRole.equals("CANDIDATE") && !newRole.equals("RECRUITER") && !newRole.equals("ADMIN")) {
                result.put("success", false);
                result.put("message", "Μη έγκυρος ρόλος");
                out.print(gson.toJson(result));
                return;
            }

            // Prevent admin from changing their own role
            if (userId == currentUser.getId()) {
                result.put("success", false);
                result.put("message", "Δεν μπορείτε να αλλάξετε τον δικό σας ρόλο");
                out.print(gson.toJson(result));
                return;
            }

            Optional<User> userOpt = userService.getUserById(userId);
            if (!userOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "Ο χρήστης δεν βρέθηκε");
                out.print(gson.toJson(result));
                return;
            }

            User userToUpdate = userOpt.get();
            userToUpdate.setRole(newRole);
            boolean updated = userService.updateUser(userToUpdate);

            if (updated) {
                result.put("success", true);
                result.put("message", "Ο ρόλος ενημερώθηκε επιτυχώς");
            } else {
                result.put("success", false);
                result.put("message", "Η ενημέρωση απέτυχε");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Λάθος User ID");
        } catch (Exception e) {
            System.err.println("Error in UserServlet PUT: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Σφάλμα κατά την ενημέρωση.");
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

        User currentUser = (User) session.getAttribute("user");
        if (!currentUser.getRole().equals("ADMIN")) {
            result.put("success", false);
            result.put("message", "Μη εξουσιοδοτημένος");
            out.print(gson.toJson(result));
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            result.put("success", false);
            result.put("message", "User ID είναι υποχρεωτικό");
            out.print(gson.toJson(result));
            return;
        }

        try {
            int userId = Integer.parseInt(pathInfo.substring(1));

            // Prevent admin from deleting themselves
            if (userId == currentUser.getId()) {
                result.put("success", false);
                result.put("message", "Δεν μπορείτε να διαγράψετε τον εαυτό σας");
                out.print(gson.toJson(result));
                return;
            }

            boolean deleted = userService.deleteUser(userId);
            if (deleted) {
                result.put("success", true);
                result.put("message", "Ο χρήστης διαγράφηκε επιτυχώς");
            } else {
                result.put("success", false);
                result.put("message", "Η διαγραφή απέτυχε");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Λάθος User ID");
        } catch (Exception e) {
            System.err.println("Error in UserServlet DELETE: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Σφάλμα κατά τη διαγραφή.");
        } finally {
            out.print(gson.toJson(result));
        }
    }
}
