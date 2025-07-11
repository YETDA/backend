package com.funding.backend.domain.purchaseOption.dto.request;

import com.funding.backend.enums.OptionStatus;
import com.funding.backend.enums.ProvidingMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseOptionCreateRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;

    @NotNull
    private Long price;

    @NotNull
    private ProvidingMethod providingMethod;// DOWNLOAD, EMAIL 등

    @NotNull
    private OptionStatus optionStatus;

    private MultipartFile file;
}
