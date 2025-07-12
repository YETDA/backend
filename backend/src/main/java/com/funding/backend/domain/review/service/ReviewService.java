package com.funding.backend.domain.review.service;

import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchase.service.PurchaseService;
import com.funding.backend.domain.review.dto.request.ReviewRequestDto;
import com.funding.backend.domain.review.dto.response.ReviewResponseDto;
import com.funding.backend.domain.review.entity.Review;
import com.funding.backend.domain.review.repository.ReviewRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PurchaseService purchaseService;
    private final UserService userService;
    private final TokenService tokenService;

    private Review findReviewById(Long reviewId){
        return reviewRepository.findById(reviewId)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND));
    }

    private Long getCurrentUserId(){
        try {
            return  tokenService.getUserIdFromAccessToken();
        }catch (BusinessLogicException e){
           throw new BusinessLogicException(ExceptionCode.ACCESS_TOKEN_NOT_FOUND);
        }
    }



    //구매자 검증 메서드
    private void validatePurchaseAccess(Long userId, Long purchaseId){
        Purchase purchase = purchaseService.findByProject(purchaseId);

        if(!purchase.getProject().getUser().getId().equals(userId)){
            throw new BusinessLogicException(ExceptionCode.REVIEW_ACCESS_DENIED);
        }
    }

    //작성자 검증 메서드
    private void validateReviewOwner(Review review, Long userId){
        if (!review.getUser().getId().equals(userId)){
            throw new BusinessLogicException(ExceptionCode.REVIEW_ACCESS_DENIED);
        }
    }

    //프로젝트별 후기 목록 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviewsByProject(Long projectId, Pageable pageable){
        Page<Review> reviews = reviewRepository.findByPurchaseProjectId(projectId, pageable);
        return reviews.map(ReviewResponseDto::from);
    }

    //후기 상세 조회
    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long reviewId){
        Review review = findReviewById(reviewId);
        return ReviewResponseDto.from(review);
    }

    //후기 작성
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto requestDto){
        Long currentUserId = getCurrentUserId();

        validatePurchaseAccess(currentUserId,requestDto.getPurchaseId());

        if(reviewRepository.existsByPurchaseId(requestDto.getPurchaseId())) {
            throw new BusinessLogicException(ExceptionCode.REVIEW_ALREADY_EXISTS);
        }

        User user = userService.findUserById(currentUserId);
        Purchase purchase = purchaseService.findByIdWithProjectAndUser(requestDto.getPurchaseId());

        Review review = Review.builder()
                .user(user)
                .purchase(purchase)
                .content(requestDto.getContent())
                .rating(requestDto.getRating())
                .imageUrl(requestDto.getImageUrl())
                .build();
        return ReviewResponseDto.from(reviewRepository.save(review));
    }

    //후기 수정
    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto requestDto){
        Long currentUserId = getCurrentUserId();
        Review review = findReviewById(reviewId);
        validateReviewOwner(review,currentUserId);

        review.setContent(requestDto.getContent());
        review.setRating(requestDto.getRating());
        review.setImageUrl(requestDto.getImageUrl());

        return ReviewResponseDto.from(review);
    }

    //후기 삭제
    @Transactional
    public void deleteReview(Long reviewId){
        Long currentUserId = getCurrentUserId();
        Review review = findReviewById(reviewId);
        validateReviewOwner(review,currentUserId);

        reviewRepository.delete(review);
    }

    }

