package com.funding.backend.domain.like.service;

import com.funding.backend.domain.like.entity.Like;
import com.funding.backend.domain.like.repository.LikeRepository;
import com.funding.backend.domain.project.dto.response.ProjectInfoResponseDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.security.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final TokenService tokenService;

    /**
     * 프로젝트에 대한 좋아요를 추가하거나 취소합니다.
     *
     * @param projectId 프로젝트 ID
     * @return 좋아요 상태 (true: 좋아요 추가, false: 좋아요 취소)
     */
    @Transactional
    public boolean toggleLike(Long projectId) {
        Long userId = tokenService.getUserIdFromAccessToken();
        boolean likedByUser = isLikedByUser(projectId);

        if (likedByUser) {
            likeRepository.delete(likeRepository.findByUserIdAndProjectId(userId, projectId).get());
        } else {
            User user = userService.getUserOrThrow(userId);
            Project project = projectService.findProjectById(projectId);

            Like newLike = Like.builder()
                    .user(user)
                    .project(project)
                    .build();
            likeRepository.save(newLike);
        }

        return !likedByUser;
    }

    /**
     * 사용자가 좋아요한 프로젝트 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 좋아요한 프로젝트 목록
     */
    public Page<ProjectInfoResponseDto> getLikedProjects(Pageable pageable) {
        Long userId = tokenService.getUserIdFromAccessToken();
        Page<Like> likes = likeRepository.findByUserId(userId, pageable);

        if (likes.isEmpty()) {
            return new PageImpl<>(List.of());
        }

        // TODO: ProjectResponseDto 구현 뒤 빈 배열 return 삭제 후 아래 주석 해제
        return likes.map(like -> new ProjectInfoResponseDto(like.getProject()));
    }

    /**
     * 사용자가 특정 프로젝트에 좋아요를 눌렀는지 여부를 확인합니다.
     *
     * @param projectId 프로젝트 ID
     * @return 좋아요 여부 (true: 좋아요, false: 좋아요 아님)
     */
    public boolean isLikedByUser(Long projectId) {
        Long userId = tokenService.getUserIdFromAccessToken();
        return likeRepository.findByUserIdAndProjectId(userId, projectId).isPresent();
    }

    /**
     * 특정 프로젝트에 대한 좋아요 수를 조회합니다.
     *
     * @param projectId 프로젝트 ID
     * @return 좋아요 수
     */
    public int countLikesByProjectId(Long projectId) {
        return likeRepository.countByProjectId(projectId);
    }
}
