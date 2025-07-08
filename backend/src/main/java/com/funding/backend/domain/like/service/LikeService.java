package com.funding.backend.domain.like.service;

import com.funding.backend.domain.like.entity.Like;
import com.funding.backend.domain.like.repository.LikeRepository;
import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
//    private final UserService userService;
    private final ProjectService projectService;

    @Transactional
    public boolean toggleLike(/* Long userId, */Long projectId) {
        // TODO: UserService 생성 시 주석 해제 후 new User() 부분 삭제
//        User user = userService.findUserById(userId);
        User user = new User();
        Project project = projectService.findProjectById(projectId);

        Optional<Like> like = likeRepository.findByUserAndProject(user, project);

        if (like.isPresent()) {
            likeRepository.delete(like.get());
            return false; // 좋아요가 취소됨
        } else {
            Like newLike = Like.builder()
                    .user(user)
                    .project(project)
                    .build();
            likeRepository.save(newLike);
            return true; // 좋아요가 추가됨
        }
    }

    public Page<ProjectResponseDto> getLikedProjects(/* Long userId, */ Pageable pageable) {
        // TODO: UserService 생성 시 주석 해제 후 new User() 부분 삭제
//        User user = userService.findUserById(userId);
        User user = new User();

        Page<Like> likes = likeRepository.findByUser(user, pageable);

        if (likes.isEmpty()) {
            return new PageImpl<>(List.of());
        }

        // TODO: ProjectResponseDto 구현 뒤 주석 해제
        return new PageImpl<>(List.of());
//        return likes.map(like -> new ProjectResponseDto(like.getProject()));
    }
}
