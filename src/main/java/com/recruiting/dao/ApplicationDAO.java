package com.recruiting.dao;

import com.recruiting.model.Application;
import java.util.List;
import java.util.Optional;

public interface ApplicationDAO {
    Optional<Application> findById(int id);

    List<Application> findByJob(int jobId);

    List<Application> findByCandidate(int candidateId);

    List<Application> findAll();

    List<Application> searchByCandidate(int candidateId, String keyword, String status);

    List<Application> searchByCandidate(int candidateId, String keyword, String status, int limit, int offset);

    int countByCandidate(int candidateId, String keyword, String status);

    List<Application> searchByJob(int jobId, String keyword, String status);

    List<Application> searchByJob(int jobId, String keyword, String status, int limit, int offset);

    int countByJob(int jobId, String keyword, String status);

    List<Application> searchAll(String keyword, String status);

    List<Application> searchAll(String keyword, String status, int limit, int offset);

    int countAll(String keyword, String status);

    int create(Application application);

    boolean updateStatus(int id, String status, String notes);

    boolean delete(int id);
}