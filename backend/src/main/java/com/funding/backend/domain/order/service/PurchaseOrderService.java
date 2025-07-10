package com.funding.backend.domain.order.service;

import static com.funding.backend.global.utils.OrderUtils.generateOrderId;

import com.funding.backend.domain.order.dto.request.PurchaseOrderRequestDto;
import com.funding.backend.domain.order.dto.response.PurchaseOrderResponseDto;
import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.repository.OrderRepository;
import com.funding.backend.domain.orderOption.service.OrderOptionService;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.purchaseOption.service.PurchaseOptionService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.toss.enums.OrderStatus;
import com.funding.backend.security.jwt.TokenService;
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
    private final TokenService tokenService;
    private final UserService userService;
    private final OrderOptionService orderOptionService;


    public PurchaseOrderResponseDto createOrder(PurchaseOrderRequestDto request) {
        Project project = projectService.findProjectById(request.getProjectId());

        String orderId = generateOrderId();
        User user =  userService.findUserById(tokenService.getUserIdFromAccessToken());

        if(request.getProjectType().equals(ProjectType.DONATION)){
            throw new BusinessLogicException(ExceptionCode.UNSUPPORTED_PROJECT_TYPE_ORDER);
        }

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
        Order saveOrder = orderRepository.save(order);
        orderOptionService.saveOrderOption(saveOrder, request.getPurchaseOptions());
        return new PurchaseOrderResponseDto(saveOrder);
    }

}
