package com.funding.backend.domain.purchaseOption.dto.request;

import com.funding.backend.enums.OptionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class PurchaseOptionUpdateRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Long price;

    @NotNull
    private OptionStatus optionStatus;

    private MultipartFile file; // 선택적 수정일 경우 @NotNull 제거
}
