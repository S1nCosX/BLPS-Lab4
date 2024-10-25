package com.blps.lab4.delegates;

import com.blps.lab4.model.authentication.AuthenticationResponse;
import com.blps.lab4.model.common.Status;
import com.blps.lab4.model.common.WorkMode;
import com.blps.lab4.model.cv.Education;
import com.blps.lab4.model.cv.Resume;
import com.blps.lab4.model.cv.Sex;
import com.blps.lab4.model.cv.WorkExperience;
import com.blps.lab4.model.user.Role;
import com.blps.lab4.repositories.TokenRepository;
import com.blps.lab4.services.ResumeService;
import com.blps.lab4.services.UserService;
import com.blps.lab4.services.jwt.JwtService;
import jakarta.inject.Named;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.blps.lab4.utils.TransformationUtils.resumeToResponse;

@Named
@Service
@AllArgsConstructor
public class CreateResumeDelegate implements JavaDelegate {
    private UserService userService;
    private UserDetailsService userDetailsService;
    private ResumeService resumeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String firstName = (String) execution.getVariable("firstName");
        String lastName = (String) execution.getVariable("lastName");
        Sex sex = Sex.valueOf(execution.getVariable("sex").toString());
        String email = (String) execution.getVariable("email");
        String city = (String) execution.getVariable("city");
        String phoneNumber = (String) execution.getVariable("phoneNumber");
        String position = (String) execution.getVariable("position");

        String userEmail = (String) execution.getVariable("userEmail");
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        Long userId = userService.findByEmail(userDetails.getUsername()).getId();

        String response;

        try {
            response = resumeService.create(
                            Resume.builder()
                                    .firstName(firstName)
                                    .lastName(lastName)
                                    .sex(sex)
                                    .email(email)
                                    .city(city)
                                    .phoneNumber(phoneNumber)
                                    .position(position),
                            userId)
                    .toString();
        } catch (Exception e) {
            execution.setVariable("response", e.getMessage());
            throw new BpmnError(e.getMessage());
        }

        execution.setVariable("response", response);
    }
}