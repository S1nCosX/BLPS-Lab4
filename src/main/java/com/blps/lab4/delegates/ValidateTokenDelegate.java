package com.blps.lab4.delegates;

import com.blps.lab4.model.authentication.AuthenticationResponse;
import com.blps.lab4.repositories.TokenRepository;
import com.blps.lab4.repositories.UserRepository;
import com.blps.lab4.services.AuthenticationService;
import com.blps.lab4.services.UserService;
import com.blps.lab4.services.jwt.JwtService;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Named
@Service
@AllArgsConstructor
public class ValidateTokenDelegate implements JavaDelegate {
    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private TokenRepository tokenRepository;
    private UserRepository userRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String token = (String) execution.getVariable("token");

        String userEmail = jwtService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        var isTokenValid = tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);

        execution.setVariable("user_email", userEmail);
        execution.setVariable("role", userRepository.findByEmail(userEmail).get().getRole());
        execution.setVariable("response", jwtService.isTokenValid(token, userDetails) && isTokenValid ? "Ok" : "Invalid token");
        execution.setVariable("token_validation_response", jwtService.isTokenValid(token, userDetails) && isTokenValid);
    }
}
