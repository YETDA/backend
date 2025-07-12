package com.funding.backend.domain.orderOption.dto.response;

import com.funding.backend.domain.orderOption.entity.OrderOption;
import com.funding.backend.enums.ProvidingMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class OrderOptionResponseDto {

    private Long id;

    private String optionName;

    private Long price;

    private ProvidingMethod providingMethod;

    private LocalDateTime downloadExpire;

    private Integer downloadCount;

    public static OrderOptionResponseDto from(OrderOption option) {
        return OrderOptionResponseDto.builder()
                .id(option.getId())
                .optionName(option.getOptionName())
                .price(option.getPrice())
                .providingMethod(option.getProvidingMethod())
                .downloadExpire(option.getDownloadExpire())
                .downloadCount(option.getDownloadCount())
                .build();
    }

}
