package com.funding.backend.domain.purchase.dto.request;

import com.funding.backend.enums.OptionStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseOptionRequestDto {
    private String title;
    private String content;
    private Long price;
    private String fileUrl;
    private OptionStatus optionStatus;
}
