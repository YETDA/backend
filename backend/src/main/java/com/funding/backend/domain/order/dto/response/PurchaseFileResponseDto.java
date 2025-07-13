package com.funding.backend.domain.order.dto.response;


import com.funding.backend.domain.orderOption.entity.OrderOption;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseFileResponseDto {

    private String fileUrl;
    private Long purchaseOptionId;
    private String purchaseOptionName;

    public PurchaseFileResponseDto(PurchaseOption purchaseOption , OrderOption orderOption){
        this.fileUrl = purchaseOption.getFileUrl();
        this.purchaseOptionId = purchaseOption.getId();
        this.purchaseOptionName = orderOption.getOptionName();

    }

}
