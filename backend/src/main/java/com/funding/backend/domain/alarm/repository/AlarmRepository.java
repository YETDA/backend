package com.funding.backend.domain.alarm.repository;

import com.funding.backend.domain.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AlarmRepository extends JpaRepository<Alarm, Long> {


}
