package com.funding.backend.domain.pricingPlan.service;

import com.funding.backend.domain.pricingPlan.entity.PricingPlan;
import com.funding.backend.domain.pricingPlan.repository.PricingRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PricingService {

    private final PricingRepository pricingRepository;

    public PricingPlan findById(Long id){
       return pricingRepository.findById(id).orElseThrow(
                ()-> new BusinessLogicException(ExceptionCode.PRICING_PLAN_NOT_FOUND)
        );
    }
}
