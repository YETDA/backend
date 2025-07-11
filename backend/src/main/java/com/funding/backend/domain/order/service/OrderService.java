package com.funding.backend.domain.order.service;


import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.repository.OrderRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    public Order findOrderByOrderId(String orderId){
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
    }

    @Transactional
    public void saveOrder(Order order){
        orderRepository.save(order);
    }
}
