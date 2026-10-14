
package com.simran.job_management.service;

import com.simran.job_management.dto.JobDTO;
import com.simran.job_management.entity.Job;
import com.simran.job_management.entity.User;
import com.simran.job_management.mapper.JobMapper;
import com.simran.job_management.repository.JobRepository;
import com.simran.job_management.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository,
                      UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // Get all jobs
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // Get jobs with pagination
    public Page<Job> getJobsWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jobRepository.findAll(pageable);
    }

    // Search jobs by title and location
    public List<Job> searchJobs(String title, String location) {
        return jobRepository
                .findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(
                        title, location);
    }

    // Filter jobs by minimum salary
    public List<Job> filterJobsBySalary(int minSalary) {
        return jobRepository
                .findBySalaryGreaterThanEqual(minSalary);
    }

    // Filter jobs by location
    public List<Job> filterJobsByLocation(String location) {
        return jobRepository
                .findByLocationContainingIgnoreCase(location);
    }

    // Get job by ID
    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    // Create a job
    public JobDTO createJob(JobDTO dto, String recruiterEmail) {
        User recruiter = userRepository.findByEmailIgnoreCase(recruiterEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter not found"));
        if (recruiter.getRole() != com.simran.job_management.entity.UserRole.RECRUITER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only recruiters can create jobs");
        }

        Job job = JobMapper.toEntity(dto);
        job.setRecruiter(recruiter);

        Job savedJob = jobRepository.save(job);

        return JobMapper.toDTO(savedJob);
    }

    // Update a job
    public Job updateJob(Long id, JobDTO dto, String recruiterEmail) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        ensureOwner(job, recruiterEmail);

        job.setTitle(dto.getTitle());
        job.setCompany(dto.getCompany());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setSalary(dto.getSalary());

        return jobRepository.save(job);
    }

    // Delete a job
    public void deleteJob(Long id, String recruiterEmail) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        ensureOwner(job, recruiterEmail);
        jobRepository.delete(job);
    }

    private void ensureOwner(Job job, String recruiterEmail) {
        if (job.getRecruiter() == null || !job.getRecruiter().getEmail().equalsIgnoreCase(recruiterEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage jobs you created");
        }
    }
}
