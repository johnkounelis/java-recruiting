package com.recruiting.controller;

import com.recruiting.dao.UserDAO;
import com.recruiting.dao.impl.UserDAOImpl;
import com.recruiting.model.User;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/test-db")
public class TestDBServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAOImpl();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Check if users table has data
            List<User> allUsers = userDAO.findAll();
            result.put("success", true);
            result.put("totalUsers", allUsers.size());
            
            Map<String, Object> usersInfo = new HashMap<>();
            for (User user : allUsers) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("email", user.getEmail());
                userInfo.put("name", user.getName());
                userInfo.put("role", user.getRole());
                userInfo.put("passwordLength", user.getPassword() != null ? user.getPassword().length() : 0);
                userInfo.put("passwordPreview", user.getPassword() != null && user.getPassword().length() > 0 
                    ? user.getPassword().substring(0, Math.min(10, user.getPassword().length())) 
                    : "");
                usersInfo.put(user.getEmail(), userInfo);
            }
            result.put("users", usersInfo);
            
            // Check admin user specifically
            result.put("adminUserExists", userDAO.findByEmail("admin@recruiting.com").isPresent());
            
            if (userDAO.findByEmail("admin@recruiting.com").isPresent()) {
                User adminUser = userDAO.findByEmail("admin@recruiting.com").get();
                Map<String, Object> adminInfo = new HashMap<>();
                adminInfo.put("id", adminUser.getId());
                adminInfo.put("email", adminUser.getEmail());
                adminInfo.put("passwordLength", adminUser.getPassword() != null ? adminUser.getPassword().length() : 0);
                adminInfo.put("password", adminUser.getPassword()); // Show actual password for debugging
                adminInfo.put("passwordMatches", "admin123".equals(adminUser.getPassword()));
                result.put("adminUser", adminInfo);
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        String jsonResponse = gson.toJson(result);
        out.print(jsonResponse);
        out.flush();
    }
}
