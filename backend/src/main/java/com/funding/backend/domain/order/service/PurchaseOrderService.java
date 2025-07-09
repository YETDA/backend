package com.funding.backend.domain.order.service;

import static com.funding.backend.global.utils.OrderUtils.generateOrderId;

import com.funding.backend.domain.order.dto.request.PurchaseOrderRequestDto;
import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.repository.OrderRepository;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.global.toss.enums.OrderStatus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PurchaseOrderService {
    private final OrderRepository orderRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public String createOrder(PurchaseOrderRequestDto request) {
        Project project = projectService.findProjectById(request.getProjectId());

        String orderId = generateOrderId();
        //로그인 pull받고 변경
        User user =  userRepository.findById(1L).get();

        Order order = Order.builder()
                .orderId(orderId)
                .paidAmount(request.getPrice())
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .project(project)
                .user(user)
                .projectType(request.getProjectType())
                .orderStatus(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);

        return orderId;
    }




}
