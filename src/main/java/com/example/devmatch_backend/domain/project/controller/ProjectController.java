package com.example.devmatch_backend.domain.project.controller;

import com.example.devmatch_backend.domain.project.dto.ProjectDto;
import com.example.devmatch_backend.domain.project.entity.Matching;
import com.example.devmatch_backend.domain.project.entity.Project;
import com.example.devmatch_backend.domain.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/search")
    public ResponseEntity<List<Project>> searchProjects(@RequestParam String keyword) {
        List<Project> projects = projectService.searchProjects(keyword);
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }
    @PostMapping("/create")
    public ResponseEntity<Project> createProject(@RequestBody ProjectDto project) {
        // 프로젝트 생성 로직 구현 및 저장
        Project createdProject = projectService.createProject(project);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProject(@PathVariable Long projectId) {
        // 프로젝트 생성 로직 구현 및 저장
        Project project = projectService.getProject(projectId);
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }

    @PostMapping("/apply")
    public ResponseEntity<Matching> applyToProject(@RequestParam Long projectId, @RequestParam Long participantId) {
        // 프로젝트 참여 신청 로직 구현 및 저장
        Matching matching = projectService.applyToProject(projectId, participantId);
        return new ResponseEntity<>(matching, HttpStatus.CREATED);
    }

    @PostMapping("/approve")
    public ResponseEntity<String> approveApplication(@RequestParam Long matchingId) {
        // 프로젝트 주인이 신청자를 수락하는 로직 구현
        projectService.approveApplication(matchingId);
        return new ResponseEntity<>("Application approved", HttpStatus.OK);
    }

    @PostMapping("/reject")
    public ResponseEntity<String> rejectApplication(@RequestParam Long matchingId) {
        // 프로젝트 주인이 신청자를 거절하는 로직 구현
        projectService.rejectApplication(matchingId);
        return new ResponseEntity<>("Application rejected", HttpStatus.OK);
    }
}

