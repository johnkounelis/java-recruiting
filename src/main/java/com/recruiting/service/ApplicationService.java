package com.recruiting.service;

import com.recruiting.dao.ApplicationDAO;
import com.recruiting.dao.impl.ApplicationDAOImpl;
import com.recruiting.model.Application;

import java.util.List;
import java.util.Optional;

import com.recruiting.model.Job;

public class ApplicationService {
    private ApplicationDAO applicationDAO = new ApplicationDAOImpl();
    private JobService jobService = new JobService();

    public List<Application> getApplicationsByJob(int jobId) {
        return applicationDAO.findByJob(jobId);
    }

    public List<Application> getApplicationsByCandidate(int candidateId) {
        return applicationDAO.findByCandidate(candidateId);
    }

    public Optional<Application> getApplicationById(int id) {
        return applicationDAO.findById(id);
    }

    public boolean applyForJob(Application application) {
        // Validation
        if (application.getJobId() <= 0) {
            return false;
        }
        if (application.getCandidateId() <= 0) {
            return false;
        }

        Optional<Job> jobOpt = jobService.getJobById(application.getJobId());
        if (!jobOpt.isPresent() || !"ACTIVE".equals(jobOpt.get().getStatus())) {
            return false; // Job does not exist or is not active
        }

        // Check if already applied
        List<Application> existing = applicationDAO.findByJob(application.getJobId());
        for (Application app : existing) {
            if (app.getCandidateId() == application.getCandidateId()) {
                return false; // Already applied
            }
        }

        // Set default status
        if (application.getStatus() == null || application.getStatus().isEmpty()) {
            application.setStatus("PENDING");
        }

        return applicationDAO.create(application) > 0;
    }

    public boolean updateApplicationStatus(int id, String status, String notes) {
        // Validation
        if (status == null || !isValidStatus(status)) {
            return false;
        }
        return applicationDAO.updateStatus(id, status, notes);
    }

    private boolean isValidStatus(String status) {
        return status.equals("PENDING") ||
                status.equals("REVIEWED") ||
                status.equals("ACCEPTED") ||
                status.equals("REJECTED");
    }

    public boolean deleteApplication(int id) {
        return applicationDAO.delete(id);
    }

    public List<Application> getAllApplications() {
        return applicationDAO.findAll();
    }

    public List<Application> searchApplicationsByCandidate(int candidateId, String keyword, String status) {
        return applicationDAO.searchByCandidate(candidateId, keyword, status);
    }

    public List<Application> searchApplicationsByCandidate(int candidateId, String keyword, String status, int limit,
            int offset) {
        return applicationDAO.searchByCandidate(candidateId, keyword, status, limit, offset);
    }

    public int countApplicationsByCandidate(int candidateId, String keyword, String status) {
        return applicationDAO.countByCandidate(candidateId, keyword, status);
    }

    public List<Application> searchApplicationsByJob(int jobId, String keyword, String status) {
        return applicationDAO.searchByJob(jobId, keyword, status);
    }

    public List<Application> searchApplicationsByJob(int jobId, String keyword, String status, int limit, int offset) {
        return applicationDAO.searchByJob(jobId, keyword, status, limit, offset);
    }

    public int countApplicationsByJob(int jobId, String keyword, String status) {
        return applicationDAO.countByJob(jobId, keyword, status);
    }

    public List<Application> searchAllApplications(String keyword, String status) {
        return applicationDAO.searchAll(keyword, status);
    }

    public List<Application> searchAllApplications(String keyword, String status, int limit, int offset) {
        return applicationDAO.searchAll(keyword, status, limit, offset);
    }

    public int countAllApplications(String keyword, String status) {
        return applicationDAO.countAll(keyword, status);
    }
}