package com.example.devmatch_backend.domain.project;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_title", nullable = false)
    private String projectTitle;

    @Column(name = "project_desc")
    private String projectDescription;

    @Column(name = "project_tags")
    private String projectTags;

    @Column(name = "project_status", nullable = false)
    private String projectStatus;

    @Column(name = "project_owner")
    private Long projectOwner;

    @Column(name = "max_developers")
    private Integer maxDevelopers;

    @Column(name = "max_planners")
    private Integer maxPlanners;

    @Column(name = "max_designers")
    private Integer maxDesigners;

}

