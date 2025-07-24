package com.funding.backend.domain.alarm.repository;

import com.funding.backend.domain.alarm.entity.Alarm;
import com.funding.backend.domain.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    // 특정 유저의 읽지 않은 알림 목록 조회
    Page<Alarm> findByUserAndReadStatus(User user, boolean readStatus,Pageable pageable);

    Page<Alarm> findAllByUser(User user, Pageable pageable);



}
