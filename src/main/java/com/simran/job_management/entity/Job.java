package com.simran.job_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String company;
    private String description;
    private String location;
    private int salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id",nullable = false)
    private User recruiter;
}
