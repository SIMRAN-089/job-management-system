package com.simran.job_management.dto;

import com.simran.job_management.entity.InterviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InterviewDTO {
    private Long id;
    @NotNull
    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private String candidateName;
    @NotNull
    private LocalDateTime scheduledAt;
    private String meetingLink;
    private String feedback;
    private InterviewStatus status;
}
