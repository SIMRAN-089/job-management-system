
package com.simran.job_management.repository;

import com.simran.job_management.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    // Search jobs by title and location
    List<Job> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(
            String title, String location);

    // Filter jobs by minimum salary
    List<Job> findBySalaryGreaterThanEqual(int minSalary);

    // Filter jobs by location only
    List<Job> findByLocationContainingIgnoreCase(String location);
}
