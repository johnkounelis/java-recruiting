package com.recruiting.service;

import com.recruiting.dao.UserDAO;
import com.recruiting.dao.impl.UserDAOImpl;
import com.recruiting.model.User;
import com.recruiting.util.PasswordUtil;

import java.util.List;
import java.util.Optional;

public class UserService {
    private UserDAO userDAO = new UserDAOImpl();

    public Optional<User> login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Login attempt: Empty email or password");
            return Optional.empty();
        }

        String normalizedEmail = email.trim().toLowerCase();
        System.out.println("Login attempt for email: " + normalizedEmail);

        Optional<User> userOpt = userDAO.findByEmail(normalizedEmail);

        if (!userOpt.isPresent()) {
            System.out.println("Login failed: User not found for email: " + normalizedEmail);
            return Optional.empty();
        }

        User user = userOpt.get();
        String storedPassword = user.getPassword();

        System.out.println("User found: id=" + user.getId() + ", name=" + user.getName() +
                ", role=" + user.getRole() + ", storedPwdLength=" +
                (storedPassword != null ? storedPassword.length() : 0));

        // Check if password is hashed (SHA-256 produces 64 character hex string)
        if (storedPassword != null && storedPassword.length() == 64) {
            System.out.println("Checking hashed password...");
            boolean verified = PasswordUtil.verifyPassword(password, storedPassword);
            if (verified) {
                System.out.println("Login successful (hashed password)");
                return Optional.of(user);
            } else {
                System.out.println("Hashed password verification failed");
            }
        } else {
            // Plain text (for existing users), compare directly
            System.out.println("Checking plain text password. Stored: '" + storedPassword +
                    "', Input: '" + password + "'");
            boolean matches = storedPassword != null && storedPassword.equals(password);
            if (matches) {
                System.out.println("Login successful (plain text). Upgrading to hash...");
                // Upgrade to hashed password
                user.setPassword(PasswordUtil.hashPassword(password));
                userDAO.update(user);
                return Optional.of(user);
            } else {
                System.out.println("Plain text password comparison failed. Match: " + matches);
            }
        }

        System.out.println("Login failed: Password mismatch");
        return Optional.empty();
    }

    public boolean register(User user) {
        // Validation
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return false;
        }

        // Email format validation
        String email = user.getEmail().trim().toLowerCase();
        if (!isValidEmail(email)) {
            return false;
        }

        if (user.getPassword() == null || user.getPassword().isEmpty() || user.getPassword().length() < 6) {
            return false;
        }

        if (user.getName() == null || user.getName().trim().isEmpty() || user.getName().trim().length() < 2) {
            return false;
        }

        // Check if email already exists
        if (userDAO.findByEmail(email).isPresent()) {
            return false; // Email already exists
        }

        // Set default role if not set
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("CANDIDATE");
        }

        // Normalize email
        user.setEmail(email);
        user.setName(user.getName().trim());

        // Hash password before saving
        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        return userDAO.create(user) > 0;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Simple email validation
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public Optional<User> getUserById(int id) {
        return userDAO.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public boolean updateUser(User user) {
        return userDAO.update(user);
    }

    public boolean deleteUser(int id) {
        return userDAO.delete(id);
    }

    public boolean resetPassword(String email, String newPassword) {
        if (email == null || newPassword == null || newPassword.length() < 6)
            return false;
        Optional<User> userOpt = userDAO.findByEmail(email.trim().toLowerCase());
        if (!userOpt.isPresent())
            return false;
        User user = userOpt.get();
        user.setPassword(PasswordUtil.hashPassword(newPassword));
        return userDAO.update(user);
    }
}