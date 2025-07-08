package com.funding.backend.domain.project.dto.response;

import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.enums.OptionStatus;
import com.funding.backend.enums.ProvidingMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PurchaseOptionResponseDto {
    private String title;
    private String content;
    private Long price;
    private String fileUrl;
    private OptionStatus optionStatus;
    private ProvidingMethod providingMethod;


    public PurchaseOptionResponseDto(PurchaseOption purchaseOption){
        this.title= purchaseOption.getTitle();
        this.content = purchaseOption.getContent();
        this.price = purchaseOption.getPrice();
        this.fileUrl = purchaseOption.getFileUrl();
        this.optionStatus = purchaseOption.getOptionStatus();
        this.providingMethod = purchaseOption.getProvidingMethod();
    }
}
