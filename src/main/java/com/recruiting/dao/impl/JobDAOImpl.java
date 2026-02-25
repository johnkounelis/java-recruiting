package com.recruiting.dao.impl;

import com.recruiting.dao.JobDAO;
import com.recruiting.model.Job;
import com.recruiting.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JobDAOImpl implements JobDAO {

    private Job mapRow(ResultSet rs) throws SQLException {
        Job j = new Job();
        j.setId(rs.getInt("id"));
        j.setTitle(rs.getString("title"));
        j.setDescription(rs.getString("description"));
        j.setCompany(rs.getString("company"));
        j.setLocation(rs.getString("location"));
        j.setCategory(rs.getString("category"));
        j.setPostedBy(rs.getInt("posted_by"));
        j.setPostedDate(rs.getTimestamp("posted_date"));
        j.setStatus(rs.getString("status"));
        return j;
    }

    @Override
    public Optional<Job> findById(int id) {
        String sql = "SELECT * FROM jobs WHERE id = ?";
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
            System.err.println("Error finding job by id: " + id);
            e.printStackTrace();
            throw new RuntimeException("Database error while finding job: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Job> findAllActive() {
        String sql = "SELECT * FROM jobs WHERE status = 'ACTIVE' ORDER BY posted_date DESC";
        List<Job> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all active jobs");
            e.printStackTrace();
            throw new RuntimeException("Database error while finding active jobs: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return list;
    }

    @Override
    public List<Job> findAllActive(int limit, int offset) {
        String sql = "SELECT * FROM jobs WHERE status = 'ACTIVE' ORDER BY posted_date DESC LIMIT ? OFFSET ?";
        List<Job> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, limit);
                ps.setInt(2, offset);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding all active jobs with pagination");
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
        return list;
    }

    @Override
    public int countAllActive() {
        String sql = "SELECT COUNT(*) FROM jobs WHERE status = 'ACTIVE'";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
        return 0;
    }

    @Override
    public List<Job> findByRecruiter(int recruiterId) {
        String sql = "SELECT * FROM jobs WHERE posted_by = ? ORDER BY posted_date DESC";
        List<Job> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, recruiterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding jobs by recruiter: " + recruiterId);
            e.printStackTrace();
            throw new RuntimeException("Database error while finding jobs by recruiter: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return list;
    }

    /**
     * Escape special SQL LIKE pattern characters to prevent injection
     * through wildcard manipulation in search queries.
     */
    private String escapeLikePattern(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                     .replace("%", "\\%")
                     .replace("_", "\\_");
    }

    @Override
    public List<Job> searchJobs(String keyword) {
        String sql = "SELECT * FROM jobs WHERE status = 'ACTIVE' AND " +
                "(title LIKE ? OR description LIKE ? OR company LIKE ? OR location LIKE ? OR category LIKE ?) " +
                "ORDER BY posted_date DESC";
        List<Job> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                String searchPattern = "%" + escapeLikePattern(keyword) + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                ps.setString(3, searchPattern);
                ps.setString(4, searchPattern);
                ps.setString(5, searchPattern);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching jobs: " + keyword);
            e.printStackTrace();
            throw new RuntimeException("Database error while searching jobs: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return list;
    }

    @Override
    public List<Job> searchJobs(String keyword, int limit, int offset) {
        String sql = "SELECT * FROM jobs WHERE status = 'ACTIVE' AND " +
                "(title LIKE ? OR description LIKE ? OR company LIKE ? OR location LIKE ? OR category LIKE ?) " +
                "ORDER BY posted_date DESC LIMIT ? OFFSET ?";
        List<Job> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                String searchPattern = "%" + escapeLikePattern(keyword) + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                ps.setString(3, searchPattern);
                ps.setString(4, searchPattern);
                ps.setString(5, searchPattern);
                ps.setInt(6, limit);
                ps.setInt(7, offset);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching jobs with pagination: " + keyword);
            e.printStackTrace();
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
        return list;
    }

    @Override
    public int countSearchJobs(String keyword) {
        String sql = "SELECT COUNT(*) FROM jobs WHERE status = 'ACTIVE' AND " +
                "(title LIKE ? OR description LIKE ? OR company LIKE ? OR location LIKE ? OR category LIKE ?)";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                String searchPattern = "%" + escapeLikePattern(keyword) + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                ps.setString(3, searchPattern);
                ps.setString(4, searchPattern);
                ps.setString(5, searchPattern);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
        return 0;
    }

    @Override
    public int create(Job job) {
        String sql = "INSERT INTO jobs (title, description, company, location, category, posted_by, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, job.getTitle());
                ps.setString(2, job.getDescription());
                ps.setString(3, job.getCompany());
                ps.setString(4, job.getLocation());
                ps.setString(5, job.getCategory() != null ? job.getCategory() : "OTHER");
                ps.setInt(6, job.getPostedBy());
                ps.setString(7, job.getStatus());
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
            System.err.println("Error creating job");
            e.printStackTrace();
            throw new RuntimeException("Database error while creating job: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return -1;
    }

    @Override
    public boolean update(Job job) {
        String sql = "UPDATE jobs SET title = ?, description = ?, company = ?, location = ?, category = ?, status = ? WHERE id = ?";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, job.getTitle());
                ps.setString(2, job.getDescription());
                ps.setString(3, job.getCompany());
                ps.setString(4, job.getLocation());
                ps.setString(5, job.getCategory() != null ? job.getCategory() : "OTHER");
                ps.setString(6, job.getStatus());
                ps.setInt(7, job.getId());
                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating job: " + job.getId());
            e.printStackTrace();
            throw new RuntimeException("Database error while updating job: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
    }

    @Override
    public boolean closeJob(int id) {
        String sql = "UPDATE jobs SET status = 'CLOSED' WHERE id = ?";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error closing job: " + id);
            e.printStackTrace();
            throw new RuntimeException("Database error while closing job: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM jobs WHERE id = ?";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting job: " + id);
            e.printStackTrace();
            throw new RuntimeException("Database error while deleting job: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
    }
}