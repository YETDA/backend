package com.funding.backend.domain.follow.service;

import com.funding.backend.domain.follow.dto.response.FollowCountResponse;
import com.funding.backend.domain.follow.dto.response.FollowResponseDto;
import com.funding.backend.domain.follow.entity.Follow;
import com.funding.backend.domain.follow.repository.FollowRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserService userService;

    public void follow(Long followerId, Long followingId) {
        User follower = userService.getUserOrThrow(followerId);
        User following = userService.getUserOrThrow(followingId);

        // 중복 팔로우 금지
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_FOLLOWING);
        }

        // 자기 자신 팔로우 금지
        if (followerId.equals(followingId)) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_FOLLOW_SELF);
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);
    }

    public void unfollow(Long followerId, Long followingId) {
        User follower = userService.getUserOrThrow(followerId);
        User following = userService.getUserOrThrow(followingId);

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.FOLLOW_NOT_FOUND));

        followRepository.delete(follow);
    }

    public List<FollowResponseDto> getFollowings(Long userId) {
        return followRepository.findFollowingsByUserId(userId);
    }

    public List<FollowResponseDto> getFollowers(Long userId) {
        return followRepository.findFollowersByUserId(userId);
    }

    public long countFollowers(Long userId) {
        return followRepository.countByFollowingId(userId);
    }

    public long countFollowings(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    public FollowCountResponse getFollowCounts(Long userId) {
        return new FollowCountResponse(
                countFollowers(userId),
                countFollowings(userId)
        );
    }

}
