package com.blps.lab4.services.jms;

import com.blps.lab4.model.cv.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;

@Service
public class ResumeProducerService {
/*
    private ResumeXMPPProducer xmppProducer;

    public String sendResumeForModeration(@RequestBody Resume resume) {
        try {
            xmppProducer = new ResumeXMPPProducer("activemq", 61222, "admin", "admin");
            xmppProducer.sendMessage("resume.queue", resume.toString());
            xmppProducer.disconnect();
            return "Resume sent for moderation with ID: " + resume.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send resume for moderation.";
        }
    }

 */
    @Autowired
    private JmsTemplate jmsTemplate;
    public void sendMessage(String queueName, String message) {
        jmsTemplate.convertAndSend(queueName, message);
        System.out.println("Message sent to queue " + queueName + ": " + message);
    }
}
