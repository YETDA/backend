package com.funding.backend.domain.review.controller;

import com.funding.backend.domain.review.dto.request.ReviewRequestDto;
import com.funding.backend.domain.review.dto.response.ReviewResponseDto;
import com.funding.backend.domain.review.service.ReviewService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "후기", description = "후기(review) 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    //프로젝트별 후기 조회
    @Operation(summary = "프로젝트별 후기 목록 조회", description = "특정 프로젝트의 후기 목록을 조회")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponseDto>>> getReviewsByProject(
            @PathVariable Long projectId, Pageable pageable) {

        Page<ReviewResponseDto> response = reviewService.getReviewsByProject(projectId, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "프로젝트별 후기 목록 조회 성공", response));
    }

    //후기 상세 조회
    @Operation(summary = "후기 상세 조회", description = "후기 ID로 상세 조회")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> getReview(@PathVariable Long reviewId) {

        ReviewResponseDto response = reviewService.getReview(reviewId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "후기 상세 조회 성공", response));

    }

    //후기 작성
    @Operation(summary = "후기 작성", description = "새로운 후기를 작성")
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(
            @Valid @RequestBody ReviewRequestDto requestDto) {

        ReviewResponseDto response = reviewService.createReview(requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "후기 작성 성공", response));
    }

    //후기 수정
    @Operation(summary = "후기 수정", description = "기존 후기를 수정")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> updateReview(
            @PathVariable Long reviewId, @Valid @RequestBody ReviewRequestDto requestDto) {

        ReviewResponseDto response = reviewService.updateReview(reviewId, requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "후기 수정 성공", response));
    }

    // 후기 삭제
    @Operation(summary = "후기 삭제", description = "후기 삭제")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId){

        reviewService.deleteReview(reviewId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "후기 삭제 성공"));
    }


}
