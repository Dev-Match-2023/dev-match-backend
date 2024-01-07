package com.example.devmatch_backend.domain.project.dto;

import lombok.Data;

@Data
public class ProjectDto {
    private String project_title;
    private String project_desc;
    private String project_tags;
    private String project_status;
    private Long project_owner;
    private int max_developers;
    private int max_planners;
    private int max_designers;
}
