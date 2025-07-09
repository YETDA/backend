package com.funding.backend.domain.purchase.dto.request;

import com.funding.backend.enums.OptionStatus;
import com.funding.backend.enums.ProvidingMethod;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//프로젝트 생성할때 쓰이는 optionRequestDto
public class PurchaseOptionRequestDto {
    private String title;
    private String content;
    private Long price;
    private String fileUrl;
    private OptionStatus optionStatus;
    private String fileIdentifier;

    // 추가 정보 -> 매핑을 위한 필드
    private String originalFileName;
    private Long fileSize;
    private String fileType;

    @Pattern(regexp = "^(DOWNLOAD|EMAIL)$", message = "전송 방식은 DOWNLOAD 또는 EMAIL이어야 합니다.")
    private ProvidingMethod providingMethod;// DOWNLOAD, EMAIL 등

}
