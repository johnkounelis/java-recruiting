package com.recruiting.filter;

import com.google.gson.Gson;
import com.recruiting.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Security filter for API endpoints. Enforces authentication and role-based
 * authorization on /api/* endpoints that require it.
 *
 * Public endpoints: GET /api/jobs (unauthenticated job browsing)
 * Protected endpoints: POST/PUT/DELETE /api/jobs, all /api/applications,
 * /api/users, etc.
 */
@WebFilter(filterName = "ApiSecurityFilter", urlPatterns = { "/api/*" })
public class ApiSecurityFilter implements Filter {
    private Gson gson = new Gson();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();
        String path = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String relativePath = path.substring(contextPath.length());

        // Allow public GET on /api/jobs (unauthenticated job browsing)
        if ("GET".equals(method) && relativePath.startsWith("/api/jobs")) {
            chain.doFilter(request, response);
            return;
        }

        // Allow public POST on /api/auth endpoints (login, register)
        if (relativePath.startsWith("/api/auth") || relativePath.startsWith("/api/login")
                || relativePath.startsWith("/api/register")) {
            chain.doFilter(request, response);
            return;
        }

        // Allow session check endpoint
        if (relativePath.startsWith("/api/session")) {
            chain.doFilter(request, response);
            return;
        }

        // All other endpoints require authentication
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendJsonError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Πρέπει να είστε συνδεδεμένος");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Role-based authorization for specific API paths
        // POST/PUT/DELETE on /api/jobs requires RECRUITER or ADMIN
        if (relativePath.startsWith("/api/jobs") && !"GET".equals(method)) {
            if (!"RECRUITER".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
                sendJsonError(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Μη εξουσιοδοτημένος");
                return;
            }
        }

        // /api/applications/all requires ADMIN
        if (relativePath.contains("/api/applications/all")) {
            if (!"ADMIN".equals(user.getRole())) {
                sendJsonError(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Μη εξουσιοδοτημένος");
                return;
            }
        }

        // /api/users requires ADMIN
        if (relativePath.startsWith("/api/users")) {
            if (!"ADMIN".equals(user.getRole())) {
                sendJsonError(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Μη εξουσιοδοτημένος");
                return;
            }
        }

        // /api/statistics is accessible by all authenticated users (role-specific stats)
        // No role restriction needed here - the servlet returns data based on the user's role

        chain.doFilter(request, response);
    }

    private void sendJsonError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(result));
        out.flush();
    }
}
