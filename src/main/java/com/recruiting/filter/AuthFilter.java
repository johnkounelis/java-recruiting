package com.recruiting.filter;

import com.recruiting.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/candidate/*", "/recruiter/*", "/admin/*"})
public class AuthFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }
    
    @Override
    public void destroy() {
        // Cleanup if needed
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        boolean isAjax = "XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With"));

        if (session == null || session.getAttribute("user") == null) {
            if (isAjax) {
                // Return 401 for AJAX requests so client-side can handle redirect
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"error\":\"Session expired\",\"redirect\":\"login.jsp\"}");
            } else {
                // Store the originally requested URL so we can redirect back after login
                String requestedUrl = httpRequest.getRequestURI();
                String queryString = httpRequest.getQueryString();
                if (queryString != null) {
                    requestedUrl += "?" + queryString;
                }
                httpRequest.getSession(true).setAttribute("redirectAfterLogin", requestedUrl);
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?expired=true");
            }
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String requestPath = httpRequest.getRequestURI();
        
        // Check role-based access
        if (requestPath.contains("/admin/") && !user.getRole().equals("ADMIN")) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        
        if (requestPath.contains("/recruiter/") && 
            !user.getRole().equals("RECRUITER") && !user.getRole().equals("ADMIN")) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        
        // Allow all authenticated users to access the profile page
        if (requestPath.contains("/candidate/profile.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        if (requestPath.contains("/candidate/") &&
            !user.getRole().equals("CANDIDATE") && !user.getRole().equals("ADMIN")) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        
        chain.doFilter(request, response);
    }
}
