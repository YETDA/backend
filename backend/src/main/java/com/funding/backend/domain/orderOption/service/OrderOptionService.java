package com.funding.backend.domain.orderOption.service;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.orderOption.entity.OrderOption;
import com.funding.backend.domain.orderOption.repository.OrderOptionRepository;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.enums.ProvidingMethod;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderOptionService {
    private final OrderOptionRepository orderOptionRepository;

    @Transactional
    public void saveOrderOption(Order order, List<PurchaseOption> purchaseOptionList){
        for(PurchaseOption purchaseOption : purchaseOptionList){
            OrderOption orderOption = OrderOption.builder()
                    .optionName(purchaseOption.getTitle())
                    .price(purchaseOption.getPrice())
                    .providingMethod(purchaseOption.getProvidingMethod())
                    .downloadCount(0)
                    .purchaseOption(purchaseOption)
                    .downloadExpire(
                            purchaseOption.getProvidingMethod() == ProvidingMethod.DOWNLOAD ? calculateDownloadExpireDate() : null
                    )

                    .order(order)
                    .build();
            orderOptionRepository.save(orderOption);

        }
    }

    private LocalDateTime calculateDownloadExpireDate() {
        return LocalDateTime.now().plusDays(7);
    }

}
