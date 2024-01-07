package com.example.devmatch_backend.domain.project.repository;


import com.example.devmatch_backend.domain.project.entity.Matching;
import com.example.devmatch_backend.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MatchingRepository extends JpaRepository<Matching, Long> {

}