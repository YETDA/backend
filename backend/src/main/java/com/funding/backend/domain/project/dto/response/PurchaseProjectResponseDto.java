package com.funding.backend.domain.project.dto.response;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.enums.OptionStatus;
import com.funding.backend.enums.ProvidingMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PurchaseProjectResponseDto implements ProjectResponseDto {
    private Long projectId;
    private String title;
    private String introduce;
    private String content;
    private String gitAddress;
    private ProvidingMethod providingMethod;
    private Long purchaseCategoryId;
    private String purchaseCategoryName;
    private String averageDeliveryTime;
    private List<String> contentImageUrls;
    private List<PurchaseOptionResponseDto> purchaseOptions;

    @Getter
    @Setter
    public static class PurchaseOptionResponseDto {
        private String title;
        private String content;
        private Long price;
        private String fileUrl;
        private OptionStatus optionStatus;
    }

    public PurchaseProjectResponseDto(Project project, PurchaseOptionResponseDto purchaseOptionResponseDto, Purchase purchase){
        this.projectId = project.getId();
    }




}
