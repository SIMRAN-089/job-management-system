
package com.simran.job_management.service;

import com.simran.job_management.dto.ApplicationDTO;
import com.simran.job_management.entity.Application;
import com.simran.job_management.entity.ApplicationStatus;
import com.simran.job_management.entity.Job;
import com.simran.job_management.entity.User;
import com.simran.job_management.mapper.ApplicationMapper;
import com.simran.job_management.repository.ApplicationRepository;
import com.simran.job_management.repository.JobRepository;
import com.simran.job_management.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            JobRepository jobRepository) {

        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    // Apply for a job
    @Transactional
    public ApplicationDTO applyForJob(ApplicationDTO dto, String candidateEmail) {

        if (dto == null || dto.getJobId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Job ID is required"
            );
        }

        User candidate = userRepository.findByEmailIgnoreCase(candidateEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found"));
        if (candidate.getRole() != com.simran.job_management.entity.UserRole.CANDIDATE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only candidates can apply for jobs");
        }

        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Job not found"
                        ));

        boolean alreadyApplied =
                applicationRepository.existsByCandidateIdAndJobId(
                        candidate.getId(),
                        dto.getJobId()
                );

        if (alreadyApplied) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Candidate has already applied for this job"
            );
        }

        Application application =
                ApplicationMapper.toEntity(dto, candidate, job);

        // Every new application starts with APPLIED status
        application.setStatus(ApplicationStatus.APPLIED);

        Application savedApplication =
                applicationRepository.save(application);

        return ApplicationMapper.toDTO(savedApplication);
    }

    // Update an application's status
    @Transactional
    public ApplicationDTO updateApplicationStatus(
            Long applicationId,
            ApplicationStatus newStatus, String recruiterEmail) {

        Application application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Application not found"
                        ));

        if (application.getJob().getRecruiter() == null
                || !application.getJob().getRecruiter().getEmail().equalsIgnoreCase(recruiterEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only update applications for your own jobs");
        }

        if (newStatus == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Application status is required"
            );
        }

        ApplicationStatus currentStatus = application.getStatus();

        if (currentStatus == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Current application status is missing"
            );
        }

        // Validate allowed status transitions
        boolean validTransition = false;

        if (currentStatus == ApplicationStatus.APPLIED) {
            validTransition =
                    newStatus == ApplicationStatus.SHORTLISTED
                            || newStatus == ApplicationStatus.REJECTED;

        } else if (currentStatus == ApplicationStatus.SHORTLISTED) {
            validTransition =
                    newStatus == ApplicationStatus.INTERVIEW
                            || newStatus == ApplicationStatus.REJECTED;

        } else if (currentStatus == ApplicationStatus.INTERVIEW) {
            validTransition =
                    newStatus == ApplicationStatus.SELECTED
                            || newStatus == ApplicationStatus.REJECTED;
        }

        if (!validTransition) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status transition from "
                            + currentStatus + " to " + newStatus
            );
        }


        application.setStatus(newStatus);

        Application updatedApplication =
                applicationRepository.save(application);

        return ApplicationMapper.toDTO(updatedApplication);
    }

    // Let a candidate view their own applications
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getMyApplications(String candidateEmail) {
        User candidate = userRepository.findByEmailIgnoreCase(candidateEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found"));
        if (candidate.getRole() != com.simran.job_management.entity.UserRole.CANDIDATE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only candidates can view their applications");
        }
        return applicationRepository.findByCandidateId(candidate.getId()).stream()
                .map(ApplicationMapper::toDTO).collect(java.util.stream.Collectors.toList());
    }

    // Get all applications for a job
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsByJob(Long jobId, String recruiterEmail) {

        if (jobId == null || !jobRepository.existsById(jobId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Job not found"
            );
        }

        ensureRecruiterOwnsJob(jobId, recruiterEmail);
        List<Application> applications = applicationRepository.findByJobId(jobId);

        return applications.stream()
                .map(ApplicationMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    // Get applications for a job filtered by status
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsByJobAndStatus(
            Long jobId,
            ApplicationStatus status, String recruiterEmail) {

        if (jobId == null || !jobRepository.existsById(jobId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Job not found"
            );
        }

        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid application status is required"
            );
        }

        ensureRecruiterOwnsJob(jobId, recruiterEmail);
        List<Application> applications = applicationRepository.findByJobIdAndStatus(jobId, status);

        return applications.stream()
                .map(ApplicationMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }
    private void ensureRecruiterOwnsJob(Long jobId, String recruiterEmail) {
        Job job = jobRepository.findById(jobId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        if (job.getRecruiter() == null || !job.getRecruiter().getEmail().equalsIgnoreCase(recruiterEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only view applications for your own jobs");
        }
    }
}
