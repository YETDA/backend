package com.funding.backend.domain.orderReward.repository;

import com.funding.backend.domain.orderReward.entity.OrderReward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRewardRepository extends JpaRepository<OrderReward,Long> {
}
