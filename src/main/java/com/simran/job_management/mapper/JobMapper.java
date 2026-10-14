package com.simran.job_management.mapper;


import com.simran.job_management.dto.JobDTO;
import com.simran.job_management.entity.Job;

public class JobMapper {
    //Entity to DTO
    public static JobDTO toDTO(Job job) {
        JobDTO dto = new JobDTO();

        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setCompany(job.getCompany());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setSalary(job.getSalary());

        if (job.getRecruiter() != null) {
            dto.setRecruiterId(job.getRecruiter().getId());
        }

        return dto;

    }
    // DTO → Entity
    public static Job toEntity(JobDTO dto) {
        Job job = new Job();

        job.setTitle(dto.getTitle());
        job.setCompany(dto.getCompany());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setSalary(dto.getSalary());

        return job;
    }
}
