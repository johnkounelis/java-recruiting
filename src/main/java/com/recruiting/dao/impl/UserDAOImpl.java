package com.recruiting.dao.impl;

import com.recruiting.dao.UserDAO;
import com.recruiting.model.User;
import com.recruiting.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setName(rs.getString("name"));
        u.setRole(rs.getString("role"));
        return u;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User user = mapRow(rs);
                        return Optional.of(user);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + email);
            e.printStackTrace();
            throw new RuntimeException("Database error while finding user: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapRow(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by id: " + id);
            e.printStackTrace();
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id";
        List<User> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding all users");
            e.printStackTrace();
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return list;
    }

    @Override
    public int create(User user) {
        String sql = "INSERT INTO users (email, password, name, role) VALUES (?, ?, ?, ?)";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getPassword()); // Already hashed in UserService
                ps.setString(3, user.getName());
                ps.setString(4, user.getRole());
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            return rs.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating user: " + user.getEmail());
            e.printStackTrace();
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return -1;
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET email = ?, password = ?, name = ?, role = ? WHERE id = ?";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getPassword()); // Already hashed in service layer
                ps.setString(3, user.getName());
                ps.setString(4, user.getRole());
                ps.setInt(5, user.getId());
                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + user.getId());
            e.printStackTrace();
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + id);
            e.printStackTrace();
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return false;
    }
}