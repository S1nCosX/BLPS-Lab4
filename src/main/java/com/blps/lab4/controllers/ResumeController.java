package com.blps.lab4.controllers;

import com.blps.lab4.exceptions.PermissionDeniedException;
import com.blps.lab4.model.cv.ResumeEntity;
import com.blps.lab4.services.AccessRightsService;
import com.blps.lab4.services.PremoderationService;
import com.blps.lab4.services.ResumeService;
import com.blps.lab4.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.blps.lab4.utils.TransformationUtils.resumeToResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resume")
public class ResumeController {
    private final UserService userService;
    private final ResumeService resumeService;
    private final AccessRightsService accessRightsService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResumeEntity> create(
            @Valid @RequestBody ResumeEntity request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        request.validate();

        var resume = request.fromRequest();
        var userId = userService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(resumeToResponse(resumeService.create(resume, userId)));
    }

    @GetMapping("/my_resume")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ResumeEntity> getMyResume(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var userId = userService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(resumeToResponse(resumeService.getByUserId(userId)));
    }

    @GetMapping("/review")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<ResumeEntity> review(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var userId = userService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(resumeToResponse(resumeService.getResumeForReview(userId)));
    }

    @GetMapping("/validate")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<Void> validate(
            @RequestParam Long resumeId,
            @RequestParam Boolean isValid,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var moderatorId = userService.findByEmail(userDetails.getUsername()).getId();
        if (!accessRightsService.checkIsResumeAssignedTo(moderatorId, resumeId)) throw new PermissionDeniedException("У вас нет прав на выполнение этой операции");

        resumeService.validate(resumeId, isValid);
        return ResponseEntity.noContent().build();
    }
}
