package com.simran.job_management.controller;

import com.simran.job_management.dto.InterviewDTO;
import com.simran.job_management.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@PreAuthorize("hasRole('RECRUITER')")
public class InterviewController {
    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) { this.interviewService = interviewService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewDTO schedule(@Valid @RequestBody InterviewDTO dto, Principal principal) {
        return interviewService.schedule(dto, principal.getName());
    }

    @GetMapping("/application/{applicationId}")
    public List<InterviewDTO> list(@PathVariable Long applicationId, Principal principal) {
        return interviewService.listForApplication(applicationId, principal.getName());
    }

    @PutMapping("/{interviewId}")
    public InterviewDTO update(@PathVariable Long interviewId, @RequestBody InterviewDTO dto, Principal principal) {
        return interviewService.update(interviewId, dto, principal.getName());
    }
}
