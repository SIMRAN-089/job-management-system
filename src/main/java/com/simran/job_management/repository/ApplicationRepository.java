
package com.simran.job_management.repository;

import com.simran.job_management.entity.Application;
import com.simran.job_management.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);
    List<Application> findByCandidateId(Long candidateId);


    // Get all applications for a job
    List<Application> findByJobId(Long jobId);

    // Get applications for a job with a specific status
    List<Application> findByJobIdAndStatus(
            Long jobId,
            ApplicationStatus status
    );
}
