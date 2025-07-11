package com.funding.backend.domain.orderOption.repository;

import com.funding.backend.domain.orderOption.entity.OrderOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderOptionRepository extends JpaRepository<OrderOption,Long> {
}
