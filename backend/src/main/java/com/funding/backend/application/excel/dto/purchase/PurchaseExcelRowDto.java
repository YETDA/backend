package com.funding.backend.application.excel.dto.purchase;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "엑셀 행 단위 구매 내역 데이터")
public class PurchaseExcelRowDto {

    @Schema(description = "구매자 이름", example = "홍길동")
    private String customerName;

    @Schema(description = "구매자 이메일", example = "hong@example.com")
    private String customerEmail;

    @Schema(description = "옵션 이름", example = "Premium Package")
    private String optionName;

    @Schema(description = "지불 금액", example = "15000")
    private Long paidAmount;
}
