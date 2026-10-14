package com.simran.job_management.service;

import com.simran.job_management.dto.InterviewDTO;
import com.simran.job_management.entity.*;
import com.simran.job_management.repository.ApplicationRepository;
import com.simran.job_management.repository.InterviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;

    public InterviewService(InterviewRepository interviewRepository, ApplicationRepository applicationRepository) {
        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public InterviewDTO schedule(InterviewDTO dto, String recruiterEmail) {
        Application application = getApplication(dto.getApplicationId());
        ensureRecruiterOwnsApplication(application, recruiterEmail);
        if (application.getStatus() != ApplicationStatus.SHORTLISTED
                && application.getStatus() != ApplicationStatus.INTERVIEW) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only shortlisted or interview-stage applications can be scheduled");
        }
        Interview interview = new Interview();
        interview.setApplication(application);
        interview.setScheduledAt(dto.getScheduledAt());
        interview.setMeetingLink(dto.getMeetingLink());
        interview.setFeedback(dto.getFeedback());
        interview.setStatus(InterviewStatus.SCHEDULED);
        application.setStatus(ApplicationStatus.INTERVIEW);
        return toDTO(interviewRepository.save(interview));
    }

    @Transactional(readOnly = true)
    public List<InterviewDTO> listForApplication(Long applicationId, String recruiterEmail) {
        Application application = getApplication(applicationId);
        ensureRecruiterOwnsApplication(application, recruiterEmail);
        return interviewRepository.findByApplicationId(applicationId).stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public InterviewDTO update(Long interviewId, InterviewDTO dto, String recruiterEmail) {
        Interview interview = interviewRepository.findById(interviewId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Interview not found"));
        ensureRecruiterOwnsApplication(interview.getApplication(), recruiterEmail);
        if (dto.getScheduledAt() != null) interview.setScheduledAt(dto.getScheduledAt());
        if (dto.getMeetingLink() != null) interview.setMeetingLink(dto.getMeetingLink());
        if (dto.getFeedback() != null) interview.setFeedback(dto.getFeedback());
        if (dto.getStatus() != null) {
            interview.setStatus(dto.getStatus());
            if (dto.getStatus() == InterviewStatus.COMPLETED) {
                interview.getApplication().setStatus(ApplicationStatus.INTERVIEW);
            }
        }
        return toDTO(interviewRepository.save(interview));
    }

    private Application getApplication(Long id) {
        return applicationRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    private void ensureRecruiterOwnsApplication(Application application, String email) {
        if (application.getJob().getRecruiter() == null
                || !application.getJob().getRecruiter().getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only manage interviews for applications to your jobs");
        }
    }

    private InterviewDTO toDTO(Interview interview) {
        InterviewDTO dto = new InterviewDTO();
        dto.setId(interview.getId());
        dto.setApplicationId(interview.getApplication().getId());
        dto.setJobId(interview.getApplication().getJob().getId());
        dto.setJobTitle(interview.getApplication().getJob().getTitle());
        dto.setCandidateName(interview.getApplication().getCandidate().getName());
        dto.setScheduledAt(interview.getScheduledAt());
        dto.setMeetingLink(interview.getMeetingLink());
        dto.setFeedback(interview.getFeedback());
        dto.setStatus(interview.getStatus());
        return dto;
    }
}
