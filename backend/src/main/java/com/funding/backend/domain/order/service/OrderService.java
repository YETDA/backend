package com.funding.backend.domain.order.service;


import com.funding.backend.domain.order.dto.response.OrderResponseDto;
import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.repository.OrderRepository;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import com.funding.backend.security.jwt.TokenService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final TokenService tokenService;

    public Order findOrderByOrderId(String orderId){
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
    }

    public Order findOrderByOrderIdWithOptions(String orderId) {
        return orderRepository.findByOrderIdWithOptions(orderId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
    }


    @Transactional
    public void saveOrder(Order order){
        orderRepository.save(order);
    }


    @Transactional
    public void deleteOrder(Order order){
        orderRepository.delete(order);
    }

    public Page<OrderResponseDto> getUserOrderListResponse(Pageable pageable) {
        User loginUser = userService.findUserById(tokenService.getUserIdFromAccessToken());

        Page<Order> orderPage = orderRepository.findOrdersByUserAndStatus(loginUser, TossPaymentStatus.DONE, pageable);

        return orderPage.map(OrderResponseDto::from);
    }

    public Page<Order> getUserOrderList(Pageable pageable) {
        User loginUser = userService.findUserById(tokenService.getUserIdFromAccessToken());

        return orderRepository.findOrdersByUserAndStatus(loginUser, TossPaymentStatus.DONE, pageable);
    }

    //
    public Long purchaseOrderCount(Project project){
        return orderRepository.countDoneOrdersByProjectId(project.getId());
    }

    public List<Object[]> countOrdersByProjectIds( List<Long> projectIds){
        return orderRepository.countOrdersByProjectIds(projectIds);
    }

    public Long donationOrderCount(Project project) {
        return orderRepository.countDoneOrdersByProjectId(project.getId());
    }


    //프로젝트 당 특정 기간동안 생성된 주문 확인 ( 주문 완료 된 것만)
    public List<Order> findByProjectAndCreatedAtBetween(Project project,LocalDateTime from, LocalDateTime to, TossPaymentStatus tossPaymentStatus){
        return orderRepository.findByProjectAndCreatedAtBetweenAndOrderStatus(project,from,to,tossPaymentStatus);
    }

    public Order findOrderById(Long id){
        return orderRepository.findById(id)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
    }


}
