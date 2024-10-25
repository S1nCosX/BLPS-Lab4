package com.blps.lab4.services;

import com.blps.lab4.model.common.Status;
import com.blps.lab4.repositories.JobPostRepository;
import com.blps.lab4.repositories.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@SuppressWarnings("OptionalGetWithoutIsPresent")
@Service
@RequiredArgsConstructor
public class AccessRightsService {
    private final JobPostRepository jobPostRepository;
    private final ResumeRepository resumeRepository;

    public boolean checkIsResumeAssignedTo(Long moderatorId, Long resumeId) {
        var assignee = resumeRepository.findById(resumeId).get().getModeratorId();

        return assignee.equals(moderatorId);
    }

    public boolean checkIsJobPostAssignedTo(Long moderatorId, Long jobPostId) {
        var assignee = jobPostRepository.findById(jobPostId).get().getModeratorId();

        return assignee.equals(moderatorId);
    }

    public boolean checkIsJobPostCreatedBy(Long hrId, Long jobPostId) {
        var createdBy = jobPostRepository.findById(jobPostId).get().getCreatedBy();

        return createdBy.equals(hrId);
    }

    public boolean checkIsResumeApproved(Long userId) {
        var resume = resumeRepository.findByCreatedBy(userId);

        return resume.map(value -> value.getStatus().equals(Status.APPROVED)).orElse(false);
    }

}
