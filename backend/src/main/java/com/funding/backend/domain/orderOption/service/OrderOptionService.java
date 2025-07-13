package com.funding.backend.domain.orderOption.service;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.orderOption.entity.OrderOption;
import com.funding.backend.domain.orderOption.repository.OrderOptionRepository;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.domain.purchaseOption.repository.PurchaseOptionRepository;
import com.funding.backend.domain.purchaseOption.service.PurchaseOptionService;
import com.funding.backend.enums.ProvidingMethod;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private final PurchaseOptionService purchaseOptionService;
    private final PurchaseOptionRepository purchaseOptionRepository;

    @Transactional
    public Long saveOrderOption(Order order, List<Long> purchaseOptionIdList) {
        List<PurchaseOption> purchaseOptions = findAllByIds(purchaseOptionIdList);

        Map<Long, PurchaseOption> purchaseOptionMap = purchaseOptions.stream()
                .collect(Collectors.toMap(PurchaseOption::getId, Function.identity()));

        List<OrderOption> orderOptions = new ArrayList<>();
        long totalAmount = 0;

        for (Long id : purchaseOptionIdList) {
            PurchaseOption purchaseOption = purchaseOptionMap.get(id);
            OrderOption orderOption = createFrom(purchaseOption, order);
            orderOptions.add(orderOption);
            totalAmount += orderOption.getPrice();
        }

        orderOptionRepository.saveAll(orderOptions);
        return totalAmount;
    }


    private LocalDateTime calculateDownloadExpireDate() {
        return LocalDateTime.now().plusDays(7);
    }

    public List<PurchaseOption> findAllByIds(List<Long> ids) {
        return purchaseOptionRepository.findAllById(ids);
    }

    // OrderOption 내부에 팩토리 메서드 추가
    private OrderOption createFrom(PurchaseOption purchaseOption, Order order) {
        return OrderOption.builder()
                .optionName(purchaseOption.getTitle())
                .price(purchaseOption.getPrice())
                .providingMethod(purchaseOption.getProvidingMethod())
                .downloadCount(0)
                .purchaseOption(purchaseOption)
                .downloadExpire(
                        purchaseOption.getProvidingMethod() == ProvidingMethod.DOWNLOAD
                                ? calculateDownloadExpireDate()
                                : null
                )
                .order(order)
                .build();
    }

    public OrderOption findOrderOptionById(Long orderOptionId){
        return orderOptionRepository.findById(orderOptionId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.ORDER_OPTION_NOT_FOUND));
    }


    @Transactional
    public void saveOrderOption(OrderOption orderOption){
        orderOptionRepository.save(orderOption);
    }





}
