package com.funding.backend.domain.order.service;

import com.funding.backend.domain.order.dto.request.DonationOrderRequestDto;
import com.funding.backend.domain.order.dto.response.DonationOrderResponseDto;
import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.repository.OrderRepository;
import com.funding.backend.domain.project.dto.response.ProjectInfoResponseDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import com.funding.backend.global.utils.OrderUtils;
import com.funding.backend.security.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationOrderService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final UserService userService;
    private final TokenService tokenService;
    private final ProjectService projectService;

    @Transactional
    public DonationOrderResponseDto createOrder(DonationOrderRequestDto request) {
        User user = userService.findUserById(tokenService.getUserIdFromAccessToken());
        Project project = projectService.findProjectById(request.getProjectId());

        Order order = Order.builder()
//                .payType(request.getPayType())
                .paidAmount(request.getPrice())
                .projectType(ProjectType.DONATION)
                .orderId(OrderUtils.generateOrderId())
                .customerEmail(request.getEmail())
                .customerName(user.getName())
                .orderName(project.getTitle())
//                .paymentKey(request.getPaymentKey())
                .orderStatus(TossPaymentStatus.READY)
                .user(user)
                .project(project)
                .orderOptionList(new ArrayList<>())
                .build();

        Order savedOrder = orderRepository.save(order);

        return new DonationOrderResponseDto(savedOrder);
    }

    @Transactional
    public boolean cancelOrder(String orderId) {
        Order order = orderService.findOrderByOrderId(orderId);

        if (order.getOrderStatus() != TossPaymentStatus.READY) {
            throw new BusinessLogicException(ExceptionCode.PAYMENT_CANCEL_FAILED, "주문 상태가 결제 준비 상태가 아닙니다.");
        }

        order.setOrderStatus(TossPaymentStatus.CANCELED);
        orderRepository.save(order);

        return true;
    }

    public List<Order> getSettlementOrders() {
        return orderRepository.findByProject_Donation_EndDateBefore(LocalDateTime.now().minusDays(1));
    }

    @Transactional(readOnly = true)
    public Page<ProjectInfoResponseDto> getDonationProjectList(Pageable pageable) {
        Page<Order> orderPage = orderService.getUserOrderList(pageable);

        return orderPage.map(order -> {
            Project project = projectService.findProjectById(order.getProject().getId());
            return new ProjectInfoResponseDto(project);
        });
    }
}
