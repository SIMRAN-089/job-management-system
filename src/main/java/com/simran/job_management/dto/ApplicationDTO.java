
package com.simran.job_management.dto;

import com.simran.job_management.entity.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {

    private Long id;

    private Long candidateId;
    private String candidateName;
    private String candidateEmail;

    private Long jobId;
    private String jobTitle;
    private String company;

    private LocalDateTime appliedAt;
    private ApplicationStatus status;
}
