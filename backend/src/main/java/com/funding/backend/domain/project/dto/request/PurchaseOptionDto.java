package com.funding.backend.domain.project.dto.request;

import com.funding.backend.enums.OptionStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseOptionDto {
    private String title;
    private String content;
    private Long price;
    private OptionStatus optionStatus;
}
