package com.recruiting.dao.impl;

import com.recruiting.dao.CompanyDAO;
import com.recruiting.model.Company;
import com.recruiting.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompanyDAOImpl implements CompanyDAO {

    private Company mapRow(ResultSet rs) throws SQLException {
        Company c = new Company();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setWebsite(rs.getString("website"));
        c.setLocation(rs.getString("location"));
        c.setIndustry(rs.getString("industry"));
        c.setCreatedBy(rs.getInt("created_by"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }

    @Override
    public Optional<Company> findById(int id) {
        String sql = "SELECT * FROM companies WHERE id = ?";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
        return Optional.empty();
    }

    @Override
    public List<Company> findAll() {
        String sql = "SELECT * FROM companies ORDER BY name ASC";
        List<Company> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
        return list;
    }

    @Override
    public List<Company> findByCreator(int userId) {
        String sql = "SELECT * FROM companies WHERE created_by = ? ORDER BY name ASC";
        List<Company> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
        return list;
    }

    @Override
    public Optional<Company> findByName(String name) {
        String sql = "SELECT * FROM companies WHERE name = ?";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
        return Optional.empty();
    }

    @Override
    public int create(Company company) {
        String sql = "INSERT INTO companies (name, description, website, location, industry, created_by) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, company.getName());
                ps.setString(2, company.getDescription());
                ps.setString(3, company.getWebsite());
                ps.setString(4, company.getLocation());
                ps.setString(5, company.getIndustry());
                ps.setInt(6, company.getCreatedBy());
                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next())
                            return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
        return -1;
    }

    @Override
    public boolean update(Company company) {
        String sql = "UPDATE companies SET name = ?, description = ?, website = ?, location = ?, industry = ? WHERE id = ?";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, company.getName());
                ps.setString(2, company.getDescription());
                ps.setString(3, company.getWebsite());
                ps.setString(4, company.getLocation());
                ps.setString(5, company.getIndustry());
                ps.setInt(6, company.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM companies WHERE id = ?";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
    }
}
