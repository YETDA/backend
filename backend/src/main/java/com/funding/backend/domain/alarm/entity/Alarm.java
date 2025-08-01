package com.funding.backend.domain.alarm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.funding.backend.domain.alarm.enums.AlarmType;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.global.auditable.Auditable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "alarms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Alarm extends Auditable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_type", nullable = false, length = 30)
    private AlarmType alarmType;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;


    @Column(name = "read_status", nullable = false)
    private boolean readStatus;


    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User user;


}
