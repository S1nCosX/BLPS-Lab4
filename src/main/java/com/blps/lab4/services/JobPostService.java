package com.blps.lab4.services;

import com.blps.lab4.exceptions.NoEntitiesException;
import com.blps.lab4.model.common.Status;
import com.blps.lab4.model.emailmessage.EmailMessage;
import com.blps.lab4.model.job.JobPost;
import com.blps.lab4.repositories.JobPostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final UserService userService;
    private final EmailService emailService;
    public JobPost create(JobPost.JobPostBuilder jobPost, Long userId) {
        jobPost.createdBy(userId);
        var time = System.currentTimeMillis();
        jobPost.createdAt(time);
        jobPost.updatedAt(time);
        jobPost.status(Status.WAITING);

        var buildedJobPost = jobPost.build();
        jobPostRepository.save(buildedJobPost);
        return buildedJobPost;
    }

    public Page<JobPost> getAllJobPosts(Pageable pageable) {
        return jobPostRepository.findAllByStatus(Status.APPROVED, pageable);
    }

    public JobPost getJobPostById(Long jobPostId) {
        return jobPostRepository.findById(jobPostId).orElseThrow(() -> new NoEntitiesException("Ууупс, вакансия куда - то исчезла"));
    }

    public Page<JobPost> getByUserId(Long userId, Pageable pageable) {
        return jobPostRepository.findAllByCreatedBy(userId, pageable);
    }

    @Transactional
    public JobPost getJobPostForReview(Long moderatorId) {
        var assignedJobPost = jobPostRepository.findByModeratorIdAndStatus(moderatorId, Status.ASSIGNED);
        if (assignedJobPost.isPresent()) return assignedJobPost.get();

        var jobPost = jobPostRepository.findOldestWaiting().orElseThrow(() -> new NoEntitiesException("Новых вакансий для проверки пока нет"));
        jobPost.setStatus(Status.ASSIGNED);
        jobPost.setModeratorId(moderatorId);
        jobPost.setUpdatedAt(System.currentTimeMillis());
        jobPostRepository.save(jobPost);
        return jobPost;
    }

    @Transactional
    public void validate(Long jobPostId, Boolean isValid) {
        var jobPost = jobPostRepository.findById(jobPostId).orElseThrow();

        Status resultStatus = isValid ? Status.APPROVED : Status.ASSIGNED;

        jobPost.setStatus(resultStatus);
        jobPost.setUpdatedAt(System.currentTimeMillis());
        jobPostRepository.save(jobPost);

        var userEmail = userService.findById(jobPost.getCreatedBy()).getEmail();
        emailService.sendEmail(
                new EmailMessage(
                        userEmail,
                        "Корректность заполнения вакансии",
                        isValid ? "Ваша вакансия заполнено корректно. Объявление будет размещено в ближайшее время" : "В вашей вакансии есть некорректно заполненные данные. " +
                                "Пожалуйста, проверьте введенные данные. В случае возникновении вопросов пишите нам на email, с которого пришло это сообщение."
                )
        );
    }
}
