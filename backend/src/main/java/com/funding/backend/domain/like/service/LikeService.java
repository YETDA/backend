package com.funding.backend.domain.like.service;

import com.funding.backend.domain.like.entity.Like;
import com.funding.backend.domain.like.repository.LikeRepository;
import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    // TODO: UserService 구현시 주석 해제 후 Repository 제거
//    private final UserService userService;
    private final UserRepository userRepository;
    private final ProjectService projectService;

    /**
     * 프로젝트에 대한 좋아요를 추가하거나 취소합니다.
     *
     * @param userId 사용자 ID
     * @param projectId 프로젝트 ID
     * @return 좋아요 상태 (true: 좋아요 추가, false: 좋아요 취소)
     */
    @Transactional
    public boolean toggleLike(Long userId, Long projectId) {
        boolean likedByUser = isLikedByUser(userId, projectId);

        if (likedByUser) {
            likeRepository.delete(likeRepository.findByUserIdAndProjectId(userId, projectId).get());
        } else {
            // TODO: UserService 생성 시 주석 해제 후 new User() 부분 삭제
//            User user = userService.findUserById(userId);
            User user = userRepository.findById(userId).get();
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
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 좋아요한 프로젝트 목록
     */
    public Page<ProjectResponseDto> getLikedProjects(Long userId, Pageable pageable) {
        Page<Like> likes = likeRepository.findByUserId(userId, pageable);

        if (likes.isEmpty()) {
            return new PageImpl<>(List.of());
        }

        // TODO: ProjectResponseDto 구현 뒤 빈 배열 return 삭제 후 아래 주석 해제
        return new PageImpl<>(List.of());
//        return likes.map(like -> new ProjectResponseDto(like.getProject()));
    }

    /**
     * 사용자가 특정 프로젝트에 좋아요를 눌렀는지 여부를 확인합니다.
     *
     * @param userId 사용자 ID
     * @param projectId 프로젝트 ID
     * @return 좋아요 여부 (true: 좋아요, false: 좋아요 아님)
     */
    public boolean isLikedByUser(Long userId, Long projectId) {
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
