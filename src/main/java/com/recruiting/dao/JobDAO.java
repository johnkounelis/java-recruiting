package com.recruiting.dao;

import com.recruiting.model.Job;
import java.util.List;
import java.util.Optional;

public interface JobDAO {
    Optional<Job> findById(int id);

    List<Job> findAllActive();

    List<Job> findAllActive(int limit, int offset);

    int countAllActive();

    List<Job> findByRecruiter(int recruiterId);

    List<Job> searchJobs(String keyword);

    List<Job> searchJobs(String keyword, int limit, int offset);

    int countSearchJobs(String keyword);

    int create(Job job);

    boolean update(Job job);

    boolean closeJob(int id);

    boolean delete(int id);
}