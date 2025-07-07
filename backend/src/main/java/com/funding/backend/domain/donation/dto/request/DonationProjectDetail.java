package com.funding.backend.domain.donation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DonationProjectDetail {
    @NotNull(message = "메인 카테고리는 필수입니다.")
    private Long mainCategoryId;

    @Size(max = 2, message = "상세 카테고리는 최대 2개까지 선택 가능합니다.")
    private List<Long> subCategoryIds;

    @NotNull(message = "목표 금액은 필수입니다.")
    private Long goalAmount;

    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수입니다.")
    private LocalDate endDate;

    @NotBlank(message = "배포 주소는 필수입니다.")
    private String deployAddress;

    private String gitAddress;
}
