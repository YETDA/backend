package com.funding.backend.domain.purchase.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PurchaseResponseDto {
    Long projectId;

    public PurchaseResponseDto(Long projectId){
        this.projectId = projectId;
    }
}
