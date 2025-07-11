package com.funding.backend.domain.order.service;

import static com.funding.backend.global.utils.OrderUtils.generateOrderId;

import com.funding.backend.domain.order.dto.request.PurchaseOrderRequestDto;
import com.funding.backend.domain.order.dto.response.PurchaseOrderResponseDto;
import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.repository.OrderRepository;
import com.funding.backend.domain.orderOption.service.OrderOptionService;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.purchaseOption.dto.response.PurchaseOptionDto;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.toss.enums.OrderStatus;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import com.funding.backend.security.jwt.TokenService;
import java.util.List;
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


    //주문 내역서 생성
    @Transactional
    public PurchaseOrderResponseDto createOrder(PurchaseOrderRequestDto request) {
        validateRequest(request);

        Project project = projectService.findProjectById(request.getProjectId());
        User user = userService.findUserById(tokenService.getUserIdFromAccessToken());

        Order order = createOrderEntity(request, project, user);
        orderRepository.save(order);

        Long totalAmount = orderOptionService.saveOrderOption(order, request.getPurchaseOptions());
        order.setPaidAmount(totalAmount);
        // DTO 변환 후 응답
        List<PurchaseOptionDto> purchaseOptionDtoList = orderOptionService.findAllByIds(request.getPurchaseOptions())
                .stream()
                .map(po -> PurchaseOptionDto.builder()
                        .id(po.getId())
                        .title(po.getTitle())
                        .price(po.getPrice())
                        .build())
                .toList();


        return PurchaseOrderResponseDto.from(order, totalAmount, purchaseOptionDtoList);

    }

    private void validateRequest(PurchaseOrderRequestDto request) {
        if (request.getProjectType() == ProjectType.DONATION) {
            throw new BusinessLogicException(ExceptionCode.UNSUPPORTED_PROJECT_TYPE_ORDER);
        }
    }

    private Order createOrderEntity(PurchaseOrderRequestDto request, Project project, User user) {
        return Order.builder()
                .orderId(generateOrderId())
                .customerName(user.getName())
                .paidAmount(0L)
                .customerEmail(
                        request.getCustomerEmail() != null ? request.getCustomerEmail() : user.getEmail()
                )
                .project(project)
                .user(user)
                .projectType(request.getProjectType())
                .orderStatus(TossPaymentStatus.READY)
                .build();
    }

    private PurchaseOrderResponseDto buildResponse(Order order, Long totalAmount) {
        return PurchaseOrderResponseDto.builder()
                .totalAmount(totalAmount)
                .orderId(order.getOrderId())
                .orderName(order.getProject().getTitle())
                .customerEmail(order.getCustomerEmail())
                .customerName(order.getCustomerName())
                .createDate(order.getCreatedAt().toString())
                //.paySuccessYn(order.getOrderStatus() == OrderStatus.COMPLETED ? "Y" : "N")
                .build();
    }






}
