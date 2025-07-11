package com.funding.backend.domain.purchaseOption.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PurchaseOptionDto {
    private Long id;
    private String title;
    private Long price;
}