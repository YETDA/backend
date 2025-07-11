package com.funding.backend.domain.follow.repository;

import com.funding.backend.domain.follow.dto.response.FollowResponseDto;
import com.funding.backend.domain.follow.entity.Follow;
import com.funding.backend.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // 내가 팔로우한 사람 (팔로잉)
    @Query("SELECT new com.funding.backend.domain.follow.dto.response.FollowResponseDto(f.following.id, f.following.name) "
            +
            "FROM Follow f WHERE f.follower.id = :userId")
    List<FollowResponseDto> findFollowingsByUserId(@Param("userId") Long userId);

    // 나를 팔로우한 사람 (팔로워)
    @Query("SELECT new com.funding.backend.domain.follow.dto.response.FollowResponseDto(f.follower.id, f.follower.name) "
            +
            "FROM Follow f WHERE f.following.id = :userId")
    List<FollowResponseDto> findFollowersByUserId(@Param("userId") Long userId);

    long countByFollowerId(Long followerId);

    long countByFollowingId(Long followingId);
}