package com.blps.lab4.delegates;

import com.blps.lab4.model.cv.Resume;
import com.blps.lab4.model.cv.Sex;
import com.blps.lab4.services.ResumeService;
import com.blps.lab4.services.UserService;
import com.blps.lab4.services.jwt.JwtService;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Named
@Service
@AllArgsConstructor
public class GetResumeForReviewDelegate implements JavaDelegate {
    private UserService userService;
    private ResumeService resumeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String email = (String) execution.getVariable("email");

        Long moderatorID = userService.findByEmail(email).getId();
        String response;
        try {
            response = resumeService.getResumeForReview(moderatorID).toString();
        } catch (Exception e) {
            execution.setVariable("response", e.getMessage());
            throw new BpmnError(e.getMessage());
        }

        execution.setVariable("response", response);
    }
}