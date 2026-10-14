
package com.simran.job_management.controller;

import com.simran.job_management.dto.ApplicationDTO;
import com.simran.job_management.entity.ApplicationStatus;
import com.simran.job_management.service.ApplicationService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // Apply for a job
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CANDIDATE')")
    public ApplicationDTO applyForJob(@RequestBody ApplicationDTO dto, Principal principal) {
        return applicationService.applyForJob(dto, principal.getName());
    }

    // Candidate views only their own applications
    @GetMapping("/my")
    @PreAuthorize("hasRole('CANDIDATE')")
    public List<ApplicationDTO> getMyApplications(Principal principal) {
        return applicationService.getMyApplications(principal.getName());
    }

    // Update application status
    @PutMapping("/{applicationId}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ApplicationDTO updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody StatusUpdateRequest request, Principal principal) {

        return applicationService.updateApplicationStatus(
                applicationId, request.getStatus(), principal.getName());
    }

    // Get all applications for a job
    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    public List<ApplicationDTO> getApplicationsByJob(
            @PathVariable Long jobId, Principal principal) {

        return applicationService.getApplicationsByJob(jobId, principal.getName());
    }

    // Filter applications for a job by status
    @GetMapping("/job/{jobId}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public List<ApplicationDTO> getApplicationsByJobAndStatus(
            @PathVariable Long jobId,
            @RequestParam ApplicationStatus status, Principal principal) {

        return applicationService.getApplicationsByJobAndStatus(jobId, status, principal.getName());
    }

    // Request body for status updates
    public static class StatusUpdateRequest {

        private ApplicationStatus status;

        public ApplicationStatus getStatus() {
            return status;
        }

        public void setStatus(ApplicationStatus status) {
            this.status = status;
        }
    }
}
