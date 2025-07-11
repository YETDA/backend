package com.funding.backend.domain.follow.service;

import com.funding.backend.domain.follow.dto.response.FollowResponseDto;
import com.funding.backend.domain.follow.entity.Follow;
import com.funding.backend.domain.follow.repository.FollowRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public void follow(Long followerId, Long followingId) {
        User follower = findUserOrThrow(followerId);
        User following = findUserOrThrow(followingId);

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_FOLLOWING);
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);
    }

    public void unfollow(Long followerId, Long followingId) {
        User follower = findUserOrThrow(followerId);
        User following = findUserOrThrow(followingId);

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.FOLLOW_NOT_FOUND));

        followRepository.delete(follow);
    }

    public List<FollowResponseDto> getFollowings(Long userId) {
        return followRepository.findFollowingsByUserId(userId);
    }

    public List<FollowResponseDto> getFollowers(Long userId) {
        return followRepository.findFollowingsByUserId(userId);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }
}
