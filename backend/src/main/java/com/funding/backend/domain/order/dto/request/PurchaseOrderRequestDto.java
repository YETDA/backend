package com.funding.backend.domain.order.dto.request;

import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.enums.ProjectType;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseOrderRequestDto {

    private Long projectId;
    private ProjectType projectType;
    //만약 메일 형식으로 받는다면 필요해서 넣음
    private String customerEmail;

    private List<Long> purchaseOptions;
}
