package com.funding.backend.domain.purchase.dto.request;

import com.funding.backend.enums.ProvidingMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 🔹 구매형 프로젝트 DTO
@Getter
@Setter
@NoArgsConstructor
public class PurchaseProjectDetail {
    @Pattern(regexp = "^(DOWNLOAD|EMAIL)$", message = "전송 방식은 DOWNLOAD 또는 EMAIL이어야 합니다.")
    private ProvidingMethod providingMethod;// DOWNLOAD, EMAIL 등

    @NotBlank(message = "Git 주소는 필수입니다.")
    private String gitAddress;

    @NotNull(message = "구매 카테고리는 필수입니다.")
    private Long purchaseCategoryId;

    @NotNull(message = "평균 전송 소용시간은 필수입니다." )
    @Size(max = 20, message = "평균시간은 작성 길이는 20자 이내여야 합니다.")
    private String getAverageDeliveryTime;

    private List<PurchaseOptionRequestDto> purchaseOptionList;



}
