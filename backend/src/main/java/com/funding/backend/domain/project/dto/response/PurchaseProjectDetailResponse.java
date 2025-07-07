package com.funding.backend.domain.project.dto.response;

import com.funding.backend.enums.ProvidingMethod;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PurchaseProjectDetailResponse {
    private ProvidingMethod providingMethod;
    private String gitAddress;
    private Long purchaseCategoryId;
    private String averageDeliveryTime;
    private List<PurchaseOptionResponseDto> purchaseOptionList;
}
