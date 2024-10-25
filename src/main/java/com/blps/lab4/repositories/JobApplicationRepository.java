package com.blps.lab4.repositories;

import com.blps.lab4.model.job.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    Collection<JobApplication> findByJobPostId(Long jobPostId);
}
