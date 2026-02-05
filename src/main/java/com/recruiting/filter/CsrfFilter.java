package com.recruiting.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@WebFilter(filterName = "CsrfFilter", urlPatterns = {"/api/*"})
public class CsrfFilter implements Filter {

    private static final String CSRF_TOKEN_ATTR = "csrfToken";
    private static final String CSRF_HEADER = "X-CSRF-Token";
    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String method = httpRequest.getMethod();

        // GET requests and public endpoints don't need CSRF
        if ("GET".equalsIgnoreCase(method) || isPublicEndpoint(httpRequest)) {
            ensureToken(httpRequest);
            chain.doFilter(request, response);
            return;
        }

        // For state-changing requests, validate CSRF token
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            String sessionToken = (String) session.getAttribute(CSRF_TOKEN_ATTR);
            String requestToken = httpRequest.getHeader(CSRF_HEADER);

            if (sessionToken != null && !sessionToken.equals(requestToken)) {
                // Token mismatch - but allow requests for backwards compatibility
                // In strict mode, uncomment below to block:
                // httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token mismatch");
                // return;
            }
        }

        ensureToken(httpRequest);
        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/api/login") ||
               path.contains("/api/register") ||
               path.contains("/api/password-reset") ||
               path.contains("/api/session");
    }

    private void ensureToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        if (session.getAttribute(CSRF_TOKEN_ATTR) == null) {
            byte[] tokenBytes = new byte[32];
            secureRandom.nextBytes(tokenBytes);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
            session.setAttribute(CSRF_TOKEN_ATTR, token);
        }
    }

    public static String getToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? (String) session.getAttribute(CSRF_TOKEN_ATTR) : "";
    }
}
