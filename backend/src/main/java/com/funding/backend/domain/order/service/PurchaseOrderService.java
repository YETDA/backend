package com.funding.backend.domain.order.service;

import static com.funding.backend.global.utils.OrderUtils.generateOrderId;

import com.funding.backend.domain.order.dto.request.PurchaseOrderRequestDto;
import com.funding.backend.domain.order.dto.response.OrderResponseDto;
import com.funding.backend.domain.order.dto.response.PurchaseFileResponseDto;
import com.funding.backend.domain.order.dto.response.PurchaseOrderResponseDto;
import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.repository.OrderRepository;
import com.funding.backend.domain.orderOption.entity.OrderOption;
import com.funding.backend.domain.orderOption.service.OrderOptionService;
import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import com.funding.backend.domain.project.dto.response.PurchaseProjectResponseDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchase.service.PurchaseService;
import com.funding.backend.domain.purchaseOption.dto.response.PurchaseOptionDto;
import com.funding.backend.domain.purchaseOption.dto.response.PurchaseOptionResponseDto;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.domain.purchaseOption.service.PurchaseOptionService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PurchaseOrderService {
    private final OrderRepository orderRepository;


    private final UserService userService;
    private final OrderOptionService orderOptionService;
    private final ProjectService projectService;
    private final TokenService tokenService;
    private final OrderService orderService;
    private final PurchaseService purchaseService;
    private final PurchaseOptionService purchaseOptionService;


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
                .orderName(project.getTitle())
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


    public Page<ProjectResponseDto> getPurchaseProjectList(Pageable pageable) {
        Page<Order> orderPage = orderService.getUserOrderList(pageable);

        Page<ProjectResponseDto> projectDtoPage = orderPage.map(order -> {
            Project project = projectService.findProjectById(order.getProject().getId());
            return purchaseService.createPurchaseProjectResponse(project);
        });

        return projectDtoPage;
    }


    //orderOption을 id로 받아서 조회
    //이 메서드가 호출되면 orderOption의 최소 다운로드 횟수를 확인
    public PurchaseFileResponseDto getUserPurchasedFile(Long orderOptionId){
        OrderOption orderOption = orderOptionService.findOrderOptionById(orderOptionId);
        validPurchaseUser(userService.findUserById(tokenService.getUserIdFromAccessToken()),orderOption);

        if(orderOption.getDownloadCount()>3){
            throw new BusinessLogicException(ExceptionCode.DOWNLOAD_LIMIT_EXCEEDED);
        }
        //다운로드 횟수 증가시키고 다시 db에 저장
        orderOption.setDownloadCount(orderOption.getDownloadCount()+1);
        orderOptionService.saveOrderOption(orderOption);

        PurchaseOption purchaseOption = purchaseOptionService.findPurchaseOptionById(orderOption.getPurchaseOption().getId());

        return new PurchaseFileResponseDto(purchaseOption,orderOption);
    }

    public void validPurchaseUser(User user, OrderOption orderOption){
        if(!user.equals(orderOption.getOrder().getUser())){
            throw new BusinessLogicException(ExceptionCode.NOT_PURCHASED_OPTION_OWNER);
        }
    }














}
