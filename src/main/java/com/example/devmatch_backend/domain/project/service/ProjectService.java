package com.example.devmatch_backend.domain.project.service;

import com.example.devmatch_backend.domain.project.dto.ProjectDto;
import com.example.devmatch_backend.domain.project.entity.Matching;
import com.example.devmatch_backend.domain.project.entity.Project;
import com.example.devmatch_backend.domain.project.repository.MatchingRepository;
import com.example.devmatch_backend.domain.project.repository.ProjectRepository;
import com.example.devmatch_backend.domain.user.entity.User;
import com.example.devmatch_backend.domain.user.repository.UserRepository;
import com.example.devmatch_backend.exception.CustomException;
import com.example.devmatch_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final MatchingRepository matchingRepository;
    private final UserRepository userRepository;

    // 키워드로 프로젝트 리스트 조회 로직
    public List<Project> searchProjects(String keyword) {
        // 프로젝트 제목 또는 태그에서 키워드를 포함하는 프로젝트를 검색
        List<Project> projects = projectRepository.findByProjectTitleContainingOrProjectTagsContaining(keyword, keyword);
        return projects;
    }

    // 프로젝트 생성 로직
    public Project createProject(ProjectDto projectDto) {
        // 프로젝트 생성 및 저장 로직 구현
        Project project = Project.toEntity(projectDto);

        return projectRepository.save(project);
    }

    public Project getProject(Long projectId) {
        // 프로젝트 생성 및 저장 로직 구현
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND, "해당 프로젝트를 찾을 수 없습니다: " + projectId));

        return projectRepository.save(project);
    }

    // 프로젝트 참여 신청 로직
    public Matching applyToProject(Long projectId, Long participantId) {
        // 프로젝트 및 사용자 검색 로직 구현
        Project project = projectRepository.findById(projectId).orElse(null);
        User participant = userRepository.findById(participantId).orElse(null);

        if (project != null && participant != null) {
            // 매칭 생성 및 저장 로직 구현
            Matching matching = new Matching();
            matching.setProject(project);
            matching.setParticipant(participant);
            return matchingRepository.save(matching);
        } else {
            // 프로젝트 또는 사용자가 존재하지 않는 경우 처리 로직 구현
            throw new IllegalArgumentException("Project or participant not found");
        }
    }

    // 프로젝트 주인이 신청자를 수락하는 로직
    public void approveApplication(Long matchingId) {
        // 매칭 검색 및 업데이트 로직 구현
        Matching matching = matchingRepository.findById(matchingId).orElse(null);

        if (matching != null) {
            // 매칭을 수락 상태로 업데이트
            matching.setAccepted(true);
            matchingRepository.save(matching);
        } else {
            // 매칭이 존재하지 않는 경우 처리 로직 구현
            throw new IllegalArgumentException("Matching not found");
        }
    }

    // 프로젝트 주인이 신청자를 거절하는 로직
    public void rejectApplication(Long matchingId) {
        // 매칭 검색 및 삭제 로직 구현
        Matching matching = matchingRepository.findById(matchingId).orElse(null);

        if (matching != null) {
            // 매칭 삭제
            matching.setAccepted(false);
            matchingRepository.save(matching);
        } else {
            // 매칭이 존재하지 않는 경우 처리 로직 구현
            throw new IllegalArgumentException("Matching not found");
        }
    }
}
