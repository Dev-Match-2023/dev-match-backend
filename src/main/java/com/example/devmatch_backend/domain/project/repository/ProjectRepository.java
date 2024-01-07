package com.example.devmatch_backend.domain.project.repository;


import com.example.devmatch_backend.domain.project.entity.Project;
import com.example.devmatch_backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByProjectTitleContainingOrProjectTagsContaining(String keyword1, String keyword2);

}