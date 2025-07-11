package com.funding.backend.domain.follow.controller;

import com.funding.backend.domain.follow.dto.response.FollowCountResponse;
import com.funding.backend.domain.follow.dto.response.FollowResponseDto;
import com.funding.backend.domain.follow.service.FollowService;
import com.funding.backend.global.utils.ApiResponse;
import com.funding.backend.security.jwt.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/follow")
@RequiredArgsConstructor
@Tag(name = "팔로우 기능 API")
public class FollowController {

    private final FollowService followService;
    private final TokenService tokenService;

    @PostMapping("/{followingId}")
    @Operation(summary = "팔로우 하기", description = "다른 사용자를 팔로우합니다.")
    public ResponseEntity<ApiResponse<Void>> follow(@PathVariable(name = "followingId") Long followingId) {
        Long userId = tokenService.getUserIdFromAccessToken();
        followService.follow(userId, followingId);
        return ResponseEntity.ok(ApiResponse.of(200, "팔로우 성공", null));
    }

    @DeleteMapping("/{followingId}")
    @Operation(summary = "언팔로우 하기", description = "다른 사용자 팔로우를 취소합니다.")
    public ResponseEntity<ApiResponse<Void>> unfollow(@PathVariable(name = "followingId") Long followingId) {
        Long userId = tokenService.getUserIdFromAccessToken();
        followService.unfollow(userId, followingId);
        return ResponseEntity.ok(ApiResponse.of(200, "언팔로우 성공", null));
    }

    @GetMapping("/following")
    @Operation(summary = "내가 팔로우한 사람 목록", description = "팔로잉 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FollowResponseDto>>> getFollowing() {
        Long userId = tokenService.getUserIdFromAccessToken();
        List<FollowResponseDto> followings = followService.getFollowings(userId);
        return ResponseEntity.ok(ApiResponse.of(200, "팔로잉 목록 조회 성공", followings));
    }

    @GetMapping("/followers")
    @Operation(summary = "나를 팔로우한 사람 목록", description = "팔로워 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FollowResponseDto>>> getFollowers() {
        Long userId = tokenService.getUserIdFromAccessToken();
        List<FollowResponseDto> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(ApiResponse.of(200, "팔로워 목록 조회 성공", followers));
    }

    @GetMapping("/count/followers")
    @Operation(summary = "팔로워 수 조회", description = "나를 팔로우하는 사람 수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> countFollowers() {
        Long userId = tokenService.getUserIdFromAccessToken();
        long count = followService.countFollowers(userId);
        return ResponseEntity.ok(ApiResponse.of(200, "팔로워 수 조회 성공", count));
    }

    @GetMapping("/count/followings")
    @Operation(summary = "팔로잉 수 조회", description = "내가 팔로우한 사람 수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> countFollowings() {
        Long userId = tokenService.getUserIdFromAccessToken();
        long count = followService.countFollowings(userId);
        return ResponseEntity.ok(ApiResponse.of(200, "팔로잉 수 조회 성공", count));
    }

    @GetMapping("/count")
    @Operation(summary = "팔로워/팔로잉 수 동시 조회")
    public ResponseEntity<ApiResponse<FollowCountResponse>> getFollowCounts() {
        Long userId = tokenService.getUserIdFromAccessToken();
        FollowCountResponse response = followService.getFollowCounts(userId);
        return ResponseEntity.ok(ApiResponse.of(200, "팔로우 수 동시 조회 성공", response));
    }
}