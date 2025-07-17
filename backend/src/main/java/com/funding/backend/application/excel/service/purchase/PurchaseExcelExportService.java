package com.funding.backend.application.excel.service.purchase;

import com.funding.backend.application.excel.dto.purchase.PurchaseExcelRowDto;
import com.funding.backend.domain.orderOption.repository.OrderOptionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseExcelExportService {

    private final OrderOptionRepository orderOptionRepository;

    public List<PurchaseExcelRowDto> getRowsByProject(Long projectId) {
        return orderOptionRepository.findByOrder_Project_Id(projectId).stream()
                .map(o -> new PurchaseExcelRowDto(
                        o.getOrder().getCustomerName(),
                        o.getOrder().getCustomerEmail(),
                        o.getOptionName(),
                        o.getPrice()
                ))
                .toList();
    }
}
