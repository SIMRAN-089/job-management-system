
package com.simran.job_management.mapper;

import com.simran.job_management.dto.ApplicationDTO;
import com.simran.job_management.entity.Application;
import com.simran.job_management.entity.User;
import com.simran.job_management.entity.Job;

public class ApplicationMapper {

    public static ApplicationDTO toDTO(Application application) {
        ApplicationDTO dto = new ApplicationDTO();

        dto.setId(application.getId());

        // Candidate details
        dto.setCandidateId(application.getCandidate().getId());
        dto.setCandidateName(application.getCandidate().getName());
        dto.setCandidateEmail(application.getCandidate().getEmail());

        // Job details
        dto.setJobId(application.getJob().getId());
        dto.setJobTitle(application.getJob().getTitle());
        dto.setCompany(application.getJob().getCompany());

        // Application details
        dto.setAppliedAt(application.getAppliedAt());
        dto.setStatus(application.getStatus());

        return dto;
    }

    public static Application toEntity(
            ApplicationDTO dto, User candidate, Job job) {

        Application application = new Application();

        application.setCandidate(candidate);
        application.setJob(job);
        application.setStatus(dto.getStatus());

        return application;
    }
}
