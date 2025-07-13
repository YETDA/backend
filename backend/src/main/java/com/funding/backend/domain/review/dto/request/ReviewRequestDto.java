package com.funding.backend.domain.review.dto.request;

import com.funding.backend.domain.review.entity.Review;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDto {

    @NotNull(message = "구매 ID는 필수입니다.")
    private Long purchaseId;

    @NotNull(message = "후기 내용은 필수입니다.")
    @Size(min = 5, max = 500, message = "후기 내용은 5자 이상 500자 이하로 작성해주세요.")
    private String content;

    @NotNull(message = "평점은 필수입니다.")
    @DecimalMin(value = "0.5")@DecimalMax(value = "5.0")
    private Float rating;

    private String imageUrl;

}
