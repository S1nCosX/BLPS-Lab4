package com.blps.lab4.controllers;

import com.blps.lab4.exceptions.PermissionDeniedException;
import com.blps.lab4.model.job.JobApplication;
import com.blps.lab4.model.job.JobPostEntity;
import com.blps.lab4.model.cv.ResumeEntity;
import com.blps.lab4.services.AccessRightsService;
import com.blps.lab4.services.JobApplicationService;
import com.blps.lab4.services.JobPostService;
import com.blps.lab4.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static com.blps.lab4.utils.TransformationUtils.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/job_posts")
public class JobPostController {
    private final JobPostService jobPostService;
    private final UserService userService;
    private final JobApplicationService jobApplicationService;
    private final AccessRightsService accessRightsService;

    @GetMapping("/all_job_posts")
    public ResponseEntity<Page<JobPostEntity>> getAllJobPosts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(jobPostPageToResponse(jobPostService.getAllJobPosts(PageRequest.of(page, size))));
    }

    @GetMapping("/view")
    public ResponseEntity<JobPostEntity> getJobPost(
            @RequestParam Long jobPostId
    ) {
        return ResponseEntity.ok(jobPostToResponse(jobPostService.getJobPostById(jobPostId)));
    }

    @GetMapping("/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> apply(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam Long jobPostId
    ) {
        var userId = userService.findByEmail(userDetails.getUsername()).getId();
        if (!accessRightsService.checkIsResumeApproved(userId)) throw new PermissionDeniedException("Дождитесь, пока резюме не будет проверено модератором");

        jobApplicationService.create(
            JobApplication.builder()
                .userId(userService.findByEmail(userDetails.getUsername()).getId())
                .createdAt(System.currentTimeMillis())
                .jobPostId(jobPostId)
                .build()
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<JobPostEntity> create(
        @RequestBody JobPostEntity request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        request.validate();

        var jobPost = request.fromRequest();
        var userId = userService.findByEmail(userDetails.getUsername()).getId();

        return ResponseEntity.ok(jobPostToResponse(jobPostService.create(jobPost, userId)));
    }

    @GetMapping("/my_job_posts")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Page<JobPostEntity>> getMyJobPosts(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        var userId = userService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(jobPostPageToResponse(jobPostService.getByUserId(userId, PageRequest.of(page, size))));
    }

    @GetMapping("/review")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<JobPostEntity> review(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        var userId = userService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(jobPostToResponse(jobPostService.getJobPostForReview(userId)));
    }

    @GetMapping("/validate")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Void> validate(
        @RequestParam Long jobPostId,
        @RequestParam Boolean isValid,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        var moderatorId = userService.findByEmail(userDetails.getUsername()).getId();
        if (!accessRightsService.checkIsJobPostAssignedTo(moderatorId, jobPostId)) throw new PermissionDeniedException("У вас нет прав на выполнение этой операции");

        jobPostService.validate(jobPostId, isValid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/applicants")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Collection<ResumeEntity>> getApplicants(
            @RequestParam Long jobPostId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        var hrId = userService.findByEmail(userDetails.getUsername()).getId();
        if(!accessRightsService.checkIsJobPostCreatedBy(hrId, jobPostId)) throw new PermissionDeniedException("У вас нет прав на выполнение этой операции");

        var resumes = jobApplicationService.getApplicants(jobPostId);

        return ResponseEntity.ok(resumeCollectionToResponse(resumes));
    }
}
