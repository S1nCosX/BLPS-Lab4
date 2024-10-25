package com.blps.lab4.delegates;

import com.blps.lab4.repositories.TokenRepository;
import com.blps.lab4.repositories.UserRepository;
import com.blps.lab4.services.ResumeService;
import com.blps.lab4.services.UserService;
import com.blps.lab4.services.jwt.JwtService;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Named
@Service
@AllArgsConstructor
public class SetResumeValidationStatusDelegate implements JavaDelegate {
    private ResumeService resumeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long resumeId = (Long) execution.getVariable("resume_ID");
        Boolean isValid = (Boolean) execution.getVariable("is_valid");

        resumeService.validate(resumeId, isValid);
    }
}
