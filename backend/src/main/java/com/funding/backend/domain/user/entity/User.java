package com.funding.backend.domain.user.entity;

import com.funding.backend.domain.alarm.entity.Alarm;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.role.entity.Role;
import com.funding.backend.enums.UserActive;
import com.funding.backend.global.auditable.Auditable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users") // 대문자 주의
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class User extends Auditable { // Auditable 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 스키마 VARCHAR와 충돌 가능성 있음
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = true, length = 100)
    private String email;

    @Column(name = "account", nullable = true)
    private String account;

    @Column(name = "bank", nullable = true)
    private String bank;

    @Column(name = "introduce", nullable = true)
    private String introduce;

    @Column(name = "portfolio_address", nullable = true)
    private String portfolioAddress;

    @Column(name = "image", nullable = true)
    private String image;

    //oauth에서 제공된 user 식별 아이디
    @Column(name = "social_id")
    private String socialId;

    //사용된 oauth 이름, kakao, naver.
    @Column(name = "sso_provider", length = 50)
    private String ssoProvider;

    //사용자 활성상태(active/stop)
    @Column(name = "user_active", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserActive userActive = UserActive.ACTIVE;


    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Project> projectList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Alarm> alarmList = new ArrayList<>();


}