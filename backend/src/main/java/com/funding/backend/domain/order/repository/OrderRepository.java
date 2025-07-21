package com.funding.backend.domain.order.repository;


import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    Optional<Order> findByOrderId(String orderId);

    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.orderStatus = :status")
    Page<Order> findOrdersByUserAndStatus(
            @Param("user") User user,
            @Param("status") TossPaymentStatus status,
            Pageable pageable
    );


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


}
