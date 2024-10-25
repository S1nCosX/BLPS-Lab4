package com.blps.lab4.delegates;

import com.blps.lab4.model.authentication.AuthenticationRequest;
import com.blps.lab4.model.authentication.AuthenticationResponse;
import com.blps.lab4.model.user.Role;
import com.blps.lab4.services.AuthenticationService;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import com.blps.lab4.model.register.RegisterRequest;
import org.springframework.stereotype.Service;

@Named
@Service
@AllArgsConstructor
public class RegistrationDelegate implements JavaDelegate {
    private AuthenticationService authenticationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String firstName = execution.getVariable("firstName").toString();
        String lastName = execution.getVariable("lastName").toString();
        String email = execution.getVariable("email").toString();
        String password = execution.getVariable("password").toString();
        Role role = Role.valueOf(execution.getVariable("role").toString());
        RegisterRequest register = new RegisterRequest(
                firstName,
                lastName,
                email,
                password,
                role
        );
        AuthenticationResponse response;
        try {
            response = authenticationService.register(register);
        }
        catch (Exception e) {
            execution.setVariable("response", e.getMessage());
            throw new BpmnError("UserIsAlreadyExist");
        }

        execution.setVariable("response", response.getAccessToken());
    }
}
