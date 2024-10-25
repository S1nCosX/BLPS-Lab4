package com.blps.lab4.delegates;

import com.blps.lab4.model.authentication.AuthenticationRequest;
import com.blps.lab4.model.authentication.AuthenticationResponse;
import com.blps.lab4.model.register.RegisterRequest;
import com.blps.lab4.model.user.Role;
import com.blps.lab4.services.AuthenticationService;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Named
@Service
@AllArgsConstructor
public class AuthorizationDelegate implements JavaDelegate {
    private AuthenticationService authenticationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String email = (String) execution.getVariable("email");
        String password = (String) execution.getVariable("password");
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                email,
                password
        );
        AuthenticationResponse response;
        try {
            response = authenticationService.authenticate(authenticationRequest);
        }
        catch (Exception e) {
            execution.setVariable("response", e.getMessage());
            throw new BpmnError("UserIsAlreadyExist");
        }

        execution.setVariable("response", response.getAccessToken());
    }
}
