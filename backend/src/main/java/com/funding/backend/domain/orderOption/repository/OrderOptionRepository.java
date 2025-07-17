package com.funding.backend.domain.orderOption.repository;

import com.funding.backend.application.excel.dto.purchase.PurchaseExcelRowDto;
import com.funding.backend.domain.orderOption.entity.OrderOption;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderOptionRepository extends JpaRepository<OrderOption, Long> {

    List<OrderOption> findByOrder_Project_Id(Long projectId);

    @Query("""
            select new com.funding.backend.application.excel.dto.purchase.PurchaseExcelRowDto(
                o.order.customerName,
                o.order.customerEmail,
                o.optionName,
                o.price
            )
            from OrderOption o
            where o.order.project.id = :projectId
            """)
    List<PurchaseExcelRowDto> findExcelRowsByProjectId(@Param("projectId") Long projectId);
}
