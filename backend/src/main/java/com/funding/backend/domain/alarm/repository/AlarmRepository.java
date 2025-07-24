package com.funding.backend.domain.alarm.repository;

import com.funding.backend.domain.alarm.entity.Alarm;
import com.funding.backend.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    // 특정 유저의 읽지 않은 알림 목록 조회
    Page<Alarm> findByUserAndReadStatus(User user, boolean readStatus,Pageable pageable);

    Page<Alarm> findAllByUser(User user, Pageable pageable);

    List<Alarm> findByUserAndReadStatus(User user, boolean readStatus);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Alarm a WHERE a.user = :user")
    void deleteAlarmByUser(@Param("user") User user);


    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Alarm a WHERE a.user = :user AND a.readStatus = :readStatus")
    void deleteAlarmByUserAndReadStatus(@Param("user") User user, @Param("readStatus") boolean readStatus);




}
