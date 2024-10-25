package com.blps.lab4.services;

import com.blps.lab4.exceptions.DidNotCompletePremoderationExeption;
import com.blps.lab4.exceptions.NoEntitiesException;
import com.blps.lab4.exceptions.PermissionDeniedException;
import com.blps.lab4.model.common.Status;
import com.blps.lab4.model.emailmessage.EmailMessage;
import com.blps.lab4.model.cv.Resume;
import com.blps.lab4.repositories.ResumeRepository;
import com.blps.lab4.services.jms.ResumeProducerService;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeProducerService producerService;
    private final ResumeRepository resumeRepository;
    private final EmailService emailService;
    private final UserService userService;
    private final PremoderationService premoderationService;
    private Boolean isSchedulerActive;

    @Transactional
    public Resume create(Resume.ResumeBuilder resume, Long userId) {
        resume.createdBy(userId);
        Long time = System.currentTimeMillis();
        resume.createdAt(time);
        resume.updatedAt(time);
        var buildedResume = resume.build();
        resumeRepository.save(buildedResume);
        try {
            producerService.sendMessage("resume.queue", buildedResume.serialize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buildedResume;
    }

    @Transactional
    public Resume getResumeForReview(Long moderatorId) {
        var assignedResume = resumeRepository.findByModeratorIdAndStatus(moderatorId, Status.ASSIGNED);
        if (assignedResume.isPresent()) return assignedResume.get();

        var resume = resumeRepository.findOldestWaiting().orElseThrow(() -> new NoEntitiesException("Новых резюме для проверки пока нет"));
        resume.setStatus(Status.ASSIGNED);
        resume.setModeratorId(moderatorId);
        resume.setUpdatedAt(System.currentTimeMillis());

        resumeRepository.save(resume);
        return resume;
    }

    public Resume getByUserId(Long userId) {
        return resumeRepository.findByCreatedBy(userId).orElseThrow(() -> new NoEntitiesException("Вы еще не создали резюме"));
    }

    public Collection<Resume> getAllByUserIds(Collection<Long> userIds) {
        return resumeRepository.findAllById(userIds);
    }

    @Transactional
    public void validate(Long resumeId, Boolean isValid) {
        var resume = resumeRepository.findById(resumeId).orElseThrow();

        Status resultStatus = isValid ? Status.APPROVED : Status.ASSIGNED;

        if (resume.getPremoderationStatus() != Status.APPROVED)
            throw new DidNotCompletePremoderationExeption("Resume did not complete premoderation for offensive words.");

        resume.setStatus(resultStatus);
        resume.setUpdatedAt(System.currentTimeMillis());
        resumeRepository.save(resume);
    }

    @Scheduled(cron = "0 * * * * ?")
    void notifyUsersForResume() {
        if (isSchedulerActive == null || !isSchedulerActive) return;

        for (Resume resume : resumeRepository.findAll()){
            if (resume.getPremoderationStatus().equals(Status.WAITING))
                System.out.println(resume.getId() + " содержит ненормативную лексику");
        }
    }

    public void setIsSchedulerActive(boolean isSchedulerActive) {
        this.isSchedulerActive = isSchedulerActive;
    }
}
