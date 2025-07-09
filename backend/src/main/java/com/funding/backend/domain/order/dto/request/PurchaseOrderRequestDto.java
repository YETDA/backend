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
    private Long price;
    private String customerName;
    private String customerEmail;

    private List<PurchaseOption> purchaseOptions;
}
