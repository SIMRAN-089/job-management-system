
package com.simran.job_management.controller;

import com.simran.job_management.dto.JobDTO;
import com.simran.job_management.entity.Job;
import com.simran.job_management.mapper.JobMapper;
import com.simran.job_management.service.JobService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Get all jobs
    @GetMapping
    public List<JobDTO> getAllJobs() {
        return jobService.getAllJobs()
                .stream()
                .map(JobMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Search jobs by title and location
    @GetMapping("/search")
    public List<JobDTO> searchJobs(
            @RequestParam String title,
            @RequestParam String location) {

        return jobService.searchJobs(title, location)
                .stream()
                .map(JobMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Create a job
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('RECRUITER')")
    public JobDTO createJob(@RequestBody JobDTO dto, Principal principal) {
        return jobService.createJob(dto, principal.getName());
    }

    // Get one job by ID
    @GetMapping("/{id}")
    public JobDTO getJobById(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(JobMapper::toDTO)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Job not found"
                        )
                );
    }

    // Update a job
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public JobDTO updateJob(
            @PathVariable Long id,
            @RequestBody JobDTO dto, Principal principal) {

        Job updatedJob = jobService.updateJob(id, dto, principal.getName());
        return JobMapper.toDTO(updatedJob);
    }

    // Delete a job
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('RECRUITER')")
    public void deleteJob(@PathVariable Long id, Principal principal) {
        jobService.deleteJob(id, principal.getName());
    }
    @GetMapping("/salary")
    public List<JobDTO> filterJobsBySalary(
            @RequestParam int minSalary) {

        return jobService.filterJobsBySalary(minSalary)
                .stream()
                .map(JobMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/location")
    public List<JobDTO> filterJobsByLocation(
            @RequestParam String location) {

        return jobService.filterJobsByLocation(location)
                .stream()
                .map(JobMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/paginated")
    public Page<JobDTO> getJobsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return jobService.getJobsWithPagination(page, size)
                .map(JobMapper::toDTO);
    }


}
