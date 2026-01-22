package com.recruiting.dao.impl;

import com.recruiting.dao.ApplicationDAO;
import com.recruiting.model.Application;
import com.recruiting.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplicationDAOImpl implements ApplicationDAO {

    private Application mapRow(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setId(rs.getInt("id"));
        a.setJobId(rs.getInt("job_id"));
        a.setCandidateId(rs.getInt("candidate_id"));
        a.setStatus(rs.getString("status"));
        a.setAppliedDate(rs.getTimestamp("applied_date"));
        a.setNotes(rs.getString("notes"));
        try { a.setResumeFilename(rs.getString("resume_filename")); } catch (SQLException ignored) {}
        return a;
    }

    @Override
    public Optional<Application> findById(int id) {
        String sql = "SELECT * FROM applications WHERE id = ?";
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
            System.err.println("Error finding application by id: " + id);
            e.printStackTrace();
            throw new RuntimeException("Database error while finding application: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Application> findByJob(int jobId) {
        String sql = "SELECT * FROM applications WHERE job_id = ? ORDER BY applied_date DESC";
        List<Application> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, jobId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding applications by job: " + jobId);
            e.printStackTrace();
            throw new RuntimeException("Database error while finding applications by job: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return list;
    }

    @Override
    public List<Application> findByCandidate(int candidateId) {
        String sql = "SELECT * FROM applications WHERE candidate_id = ? ORDER BY applied_date DESC";
        List<Application> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, candidateId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding applications by candidate: " + candidateId);
            e.printStackTrace();
            throw new RuntimeException("Database error while finding applications by candidate: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return list;
    }

    @Override
    public List<Application> findAll() {
        String sql = "SELECT a.*, j.title AS job_title, j.company AS job_company, u.name AS candidate_name " +
                "FROM applications a " +
                "JOIN jobs j ON a.job_id = j.id " +
                "JOIN users u ON a.candidate_id = u.id " +
                "ORDER BY a.applied_date DESC";
        List<Application> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRowWithJoins(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding all applications");
            e.printStackTrace();
            throw new RuntimeException("Database error while finding all applications: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return list;
    }

    @Override
    public List<Application> searchByCandidate(int candidateId, String keyword, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.*, j.title AS job_title, j.company AS job_company, u.name AS candidate_name " +
                        "FROM applications a " +
                        "JOIN jobs j ON a.job_id = j.id " +
                        "JOIN users u ON a.candidate_id = u.id " +
                        "WHERE a.candidate_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(candidateId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND a.status = ?");
            params.add(status.trim());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(j.title) LIKE ? OR LOWER(j.company) LIKE ? OR LOWER(a.notes) LIKE ?)");
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        sql.append(" ORDER BY a.applied_date DESC");

        List<Application> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    Object p = params.get(i);
                    if (p instanceof Integer)
                        ps.setInt(i + 1, (Integer) p);
                    else
                        ps.setString(i + 1, (String) p);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRowWithJoins(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching applications by candidate: " + candidateId);
            e.printStackTrace();
            throw new RuntimeException("Database error while searching applications: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return list;
    }

    @Override
    public List<Application> searchByCandidate(int candidateId, String keyword, String status, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.*, j.title AS job_title, j.company AS job_company, u.name AS candidate_name " +
                        "FROM applications a " +
                        "JOIN jobs j ON a.job_id = j.id " +
                        "JOIN users u ON a.candidate_id = u.id " +
                        "WHERE a.candidate_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(candidateId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND a.status = ?");
            params.add(status.trim());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(j.title) LIKE ? OR LOWER(j.company) LIKE ? OR LOWER(a.notes) LIKE ?)");
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        sql.append(" ORDER BY a.applied_date DESC LIMIT ? OFFSET ?");

        List<Application> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                for (Object p : params) {
                    if (p instanceof Integer)
                        ps.setInt(paramIndex++, (Integer) p);
                    else
                        ps.setString(paramIndex++, (String) p);
                }
                ps.setInt(paramIndex++, limit);
                ps.setInt(paramIndex++, offset);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRowWithJoins(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
        return list;
    }

    @Override
    public int countByCandidate(int candidateId, String keyword, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM applications a JOIN jobs j ON a.job_id = j.id WHERE a.candidate_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(candidateId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND a.status = ?");
            params.add(status.trim());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(j.title) LIKE ? OR LOWER(j.company) LIKE ? OR LOWER(a.notes) LIKE ?)");
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                for (Object p : params) {
                    if (p instanceof Integer)
                        ps.setInt(paramIndex++, (Integer) p);
                    else
                        ps.setString(paramIndex++, (String) p);
                }
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
    public List<Application> searchByJob(int jobId, String keyword, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.*, j.title AS job_title, j.company AS job_company, u.name AS candidate_name " +
                        "FROM applications a " +
                        "JOIN jobs j ON a.job_id = j.id " +
                        "JOIN users u ON a.candidate_id = u.id " +
                        "WHERE a.job_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(jobId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND a.status = ?");
            params.add(status.trim());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(u.name) LIKE ? OR LOWER(u.email) LIKE ? OR LOWER(a.notes) LIKE ?)");
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        sql.append(" ORDER BY a.applied_date DESC");

        List<Application> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    Object p = params.get(i);
                    if (p instanceof Integer)
                        ps.setInt(i + 1, (Integer) p);
                    else
                        ps.setString(i + 1, (String) p);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRowWithJoins(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching applications by job: " + jobId);
            e.printStackTrace();
            throw new RuntimeException("Database error while searching applications: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return list;
    }

    @Override
    public List<Application> searchByJob(int jobId, String keyword, String status, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.*, j.title AS job_title, j.company AS job_company, u.name AS candidate_name " +
                        "FROM applications a " +
                        "JOIN jobs j ON a.job_id = j.id " +
                        "JOIN users u ON a.candidate_id = u.id " +
                        "WHERE a.job_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(jobId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND a.status = ?");
            params.add(status.trim());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(u.name) LIKE ? OR LOWER(u.email) LIKE ? OR LOWER(a.notes) LIKE ?)");
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        sql.append(" ORDER BY a.applied_date DESC LIMIT ? OFFSET ?");

        List<Application> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                for (Object p : params) {
                    if (p instanceof Integer)
                        ps.setInt(paramIndex++, (Integer) p);
                    else
                        ps.setString(paramIndex++, (String) p);
                }
                ps.setInt(paramIndex++, limit);
                ps.setInt(paramIndex++, offset);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRowWithJoins(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
        return list;
    }

    @Override
    public int countByJob(int jobId, String keyword, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM applications a JOIN users u ON a.candidate_id = u.id WHERE a.job_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(jobId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND a.status = ?");
            params.add(status.trim());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(u.name) LIKE ? OR LOWER(u.email) LIKE ? OR LOWER(a.notes) LIKE ?)");
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                for (Object p : params) {
                    if (p instanceof Integer)
                        ps.setInt(paramIndex++, (Integer) p);
                    else
                        ps.setString(paramIndex++, (String) p);
                }
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
    public List<Application> searchAll(String keyword, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.*, j.title AS job_title, j.company AS job_company, u.name AS candidate_name " +
                        "FROM applications a " +
                        "JOIN jobs j ON a.job_id = j.id " +
                        "JOIN users u ON a.candidate_id = u.id " +
                        "WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND a.status = ?");
            params.add(status.trim());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(
                    " AND (LOWER(j.title) LIKE ? OR LOWER(j.company) LIKE ? OR LOWER(u.name) LIKE ? OR LOWER(a.notes) LIKE ?)");
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        sql.append(" ORDER BY a.applied_date DESC");

        List<Application> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setString(i + 1, (String) params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRowWithJoins(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching all applications");
            e.printStackTrace();
            throw new RuntimeException("Database error while searching applications: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return list;
    }

    @Override
    public List<Application> searchAll(String keyword, String status, int limit, int offset) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.*, j.title AS job_title, j.company AS job_company, u.name AS candidate_name " +
                        "FROM applications a " +
                        "JOIN jobs j ON a.job_id = j.id " +
                        "JOIN users u ON a.candidate_id = u.id " +
                        "WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND a.status = ?");
            params.add(status.trim());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(
                    " AND (LOWER(j.title) LIKE ? OR LOWER(j.company) LIKE ? OR LOWER(u.name) LIKE ? OR LOWER(a.notes) LIKE ?)");
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        sql.append(" ORDER BY a.applied_date DESC LIMIT ? OFFSET ?");

        List<Application> list = new ArrayList<>();
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                for (Object p : params) {
                    ps.setString(paramIndex++, (String) p);
                }
                ps.setInt(paramIndex++, limit);
                ps.setInt(paramIndex++, offset);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        list.add(mapRowWithJoins(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null)
                DatabaseConnection.closeConnection(con);
        }
        return list;
    }

    @Override
    public int countAll(String keyword, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM applications a JOIN jobs j ON a.job_id = j.id JOIN users u ON a.candidate_id = u.id WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND a.status = ?");
            params.add(status.trim());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(
                    " AND (LOWER(j.title) LIKE ? OR LOWER(j.company) LIKE ? OR LOWER(u.name) LIKE ? OR LOWER(a.notes) LIKE ?)");
            String kw = "%" + keyword.trim().toLowerCase() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                for (Object p : params) {
                    ps.setString(paramIndex++, (String) p);
                }
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

    private Application mapRowWithJoins(ResultSet rs) throws SQLException {
        Application a = mapRow(rs);
        try {
            a.setJobTitle(rs.getString("job_title"));
        } catch (SQLException ignore) {
        }
        try {
            a.setJobCompany(rs.getString("job_company"));
        } catch (SQLException ignore) {
        }
        try {
            a.setCandidateName(rs.getString("candidate_name"));
        } catch (SQLException ignore) {
        }
        return a;
    }

    @Override
    public int create(Application application) {
        String sql = "INSERT INTO applications (job_id, candidate_id, status, notes, resume_filename) VALUES (?, ?, ?, ?, ?)";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, application.getJobId());
                ps.setInt(2, application.getCandidateId());
                ps.setString(3, application.getStatus());
                ps.setString(4, application.getNotes());
                ps.setString(5, application.getResumeFilename());
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
            System.err.println("Error creating application");
            e.printStackTrace();
            throw new RuntimeException("Database error while creating application: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
        return -1;
    }

    @Override
    public boolean updateStatus(int id, String status, String notes) {
        String sql = "UPDATE applications SET status = ?, notes = ? WHERE id = ?";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setString(2, notes != null ? notes : "");
                ps.setInt(3, id);
                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error updating application status: " + id);
            e.printStackTrace();
            throw new RuntimeException("Database error while updating application status: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM applications WHERE id = ?";
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting application: " + id);
            e.printStackTrace();
            throw new RuntimeException("Database error while deleting application: " + e.getMessage(), e);
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
    }
}