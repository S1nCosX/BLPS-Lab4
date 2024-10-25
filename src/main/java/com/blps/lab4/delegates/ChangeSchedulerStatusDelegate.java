package com.blps.lab4.delegates;

import com.blps.lab4.services.ResumeService;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Named
@Service
@AllArgsConstructor
public class ChangeSchedulerStatusDelegate implements JavaDelegate {
    private ResumeService resumeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Boolean status = (Boolean) execution.getVariable("status");

        resumeService.setIsSchedulerActive(status);
    }
}
