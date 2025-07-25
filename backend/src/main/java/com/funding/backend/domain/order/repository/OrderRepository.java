package com.funding.backend.domain.order.repository;


import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.toss.enums.TossPaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);

    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.orderStatus = :status")
    Page<Order> findOrdersByUserAndStatus(
            @Param("user") User user,
            @Param("status") TossPaymentStatus status,
            Pageable pageable
    );

    @Query("SELECT o FROM Order o JOIN FETCH o.orderOptionList WHERE o.orderId = :orderId")
    Optional<Order> findByOrderIdWithOptions(String orderId);



    //해당 프로젝트 구매 개수
    @Query("SELECT COUNT(o) FROM Order o WHERE o.project.id = :projectId AND o.orderStatus = 'DONE'")
    Long countDoneOrdersByProjectId(@Param("projectId") Long projectId);

    @Query("""
                SELECT o.project.id, COUNT(o)
                FROM Order o
                WHERE o.project.id IN :projectIds
                GROUP BY o.project.id
            """)
    List<Object[]> countOrdersByProjectIds(@Param("projectIds") List<Long> projectIds);


    //정산에 포함되어야 할 주문은 '결제가 완료된 주문'만 포함
    List<Order> findByProjectAndCreatedAtBetweenAndOrderStatus(
            Project project,
            LocalDateTime from,
            LocalDateTime to,
            TossPaymentStatus orderStatus
    );

    List<Order> findByPurchaseSuccessTimeBetweenAndOrderStatusAndSettlementIsNull(
            LocalDateTime from,
            LocalDateTime to,
            TossPaymentStatus orderStatus
    );

    List<Order> findByPurchaseSuccessTimeBetweenAndOrderStatusAndProjectTypeAndSettlementIsNull(
            LocalDateTime start,
            LocalDateTime end,
            TossPaymentStatus orderStatus,
            ProjectType projectType
    );



    long countDistinctByUser_IdAndProjectType(
            @Param("userId") Long userId,
            @Param("projectType") ProjectType projectType
    );

    @Query("""
                SELECT COALESCE(SUM(o.paidAmount), 0)
                FROM Order o
                WHERE o.user.id = :userId
                  AND o.projectType = :projectType
            """)
    Long sumPaidAmountByUserIdAndProjectType(
            @Param("userId") Long userId,
            @Param("projectType") ProjectType projectType
    );
    List<Order> findByProject_Donation_EndDateBefore(LocalDateTime now);
}
