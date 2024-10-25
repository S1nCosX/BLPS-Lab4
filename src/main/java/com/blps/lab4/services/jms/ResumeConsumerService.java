package com.blps.lab4.services.jms;

import com.blps.lab4.model.common.Status;
import com.blps.lab4.repositories.ResumeRepository;
import com.blps.lab4.model.cv.Resume;
import com.blps.lab4.services.PremoderationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class ResumeConsumerService {

    @Autowired
    private PremoderationService premoderationService;

    @Autowired
    private ResumeRepository resumeRepository;

    @JmsListener(destination = "resume.queue")
    public void receiveMessage(String message) {

        // Обновление информации в базе данных
        try {
            Resume resume = Resume.deserialize(message);
            boolean containsOffensiveLanguage = premoderationService.containsOffensiveLanguage(resume.toString());
            Resume content = resumeRepository.findById(resume.getId()).orElseThrow();
            content.setPremoderationStatus(!containsOffensiveLanguage ? Status.APPROVED : Status.WAITING);
            resumeRepository.save(content);

            System.out.println("Processed resume with ID: " + resume.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
