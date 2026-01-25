package com.recruiting.service;

import com.recruiting.dao.JobDAO;
import com.recruiting.dao.impl.JobDAOImpl;
import com.recruiting.model.Job;

import java.util.List;
import java.util.Optional;

public class JobService {
    private JobDAO jobDAO = new JobDAOImpl();

    public List<Job> getAllActiveJobs() {
        return jobDAO.findAllActive();
    }

    public List<Job> getAllActiveJobs(int limit, int offset) {
        return jobDAO.findAllActive(limit, offset);
    }

    public int countAllActiveJobs() {
        return jobDAO.countAllActive();
    }

    public Optional<Job> getJobById(int id) {
        return jobDAO.findById(id);
    }

    public List<Job> getJobsByRecruiter(int recruiterId) {
        return jobDAO.findByRecruiter(recruiterId);
    }

    public boolean createJob(Job job) {
        // Validation
        if (job.getTitle() == null || job.getTitle().trim().isEmpty()) {
            return false;
        }
        if (job.getDescription() == null || job.getDescription().trim().isEmpty()) {
            return false;
        }
        if (job.getCompany() == null || job.getCompany().trim().isEmpty()) {
            return false;
        }
        if (job.getLocation() == null || job.getLocation().trim().isEmpty()) {
            return false;
        }
        if (job.getPostedBy() <= 0) {
            return false;
        }

        // Set default status
        if (job.getStatus() == null || job.getStatus().isEmpty()) {
            job.setStatus("ACTIVE");
        }

        return jobDAO.create(job) > 0;
    }

    public boolean updateJob(Job job, int recruiterId) {
        // Verify job exists and belongs to recruiter
        Optional<Job> existingJobOpt = jobDAO.findById(job.getId());
        if (!existingJobOpt.isPresent()) {
            return false;
        }
        Job existingJob = existingJobOpt.get();
        if (existingJob.getPostedBy() != recruiterId) {
            return false; // Job doesn't belong to this recruiter
        }
        return jobDAO.update(job);
    }

    public boolean closeJob(int id) {
        return jobDAO.closeJob(id);
    }

    public boolean deleteJob(int id) {
        return jobDAO.delete(id);
    }

    public List<Job> searchJobs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveJobs();
        }
        return jobDAO.searchJobs(keyword.trim());
    }

    public List<Job> searchJobs(String keyword, int limit, int offset) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveJobs(limit, offset);
        }
        return jobDAO.searchJobs(keyword.trim(), limit, offset);
    }

    public int countSearchJobs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return countAllActiveJobs();
        }
        return jobDAO.countSearchJobs(keyword.trim());
    }
}