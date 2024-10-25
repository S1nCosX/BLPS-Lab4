package com.blps.lab4.repositories;

import com.blps.lab4.model.common.Status;
import com.blps.lab4.model.job.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    Page<JobPost> findAllByCreatedBy(Long createdBy, Pageable pageable);

    Optional<JobPost> findByModeratorIdAndStatus(Long moderatorId, Status status);

    @Query(value = "select * from job_posts where status = 'WAITING' order by created_at ASC limit 1 for update skip locked", nativeQuery = true)
    Optional<JobPost> findOldestWaiting();

    Page<JobPost> findAllByStatus(Status status, Pageable pageable);
}
