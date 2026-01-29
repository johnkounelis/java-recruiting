package com.recruiting.controller;

import com.google.gson.Gson;
import com.recruiting.filter.CsrfFilter;
import com.recruiting.model.User;

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

@WebServlet("/api/session")
public class SessionServlet extends HttpServlet {
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

            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                result.put("loggedIn", true);
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("email", user.getEmail());
                userData.put("name", user.getName());
                userData.put("role", user.getRole());
                result.put("user", userData);
                // Include CSRF token
                String csrfToken = CsrfFilter.getToken(request);
                if (csrfToken != null) {
                    result.put("csrfToken", csrfToken);
                }
            } else {
                result.put("loggedIn", false);
            }
        } catch (Exception e) {
            System.err.println("Error in SessionServlet: " + e.getMessage());
            e.printStackTrace();
            result.put("loggedIn", false);
            result.put("error", "Session check failed");
        } finally {
            out.print(gson.toJson(result));
        }
    }
}
