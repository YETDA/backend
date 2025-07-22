package com.funding.backend.domain.settlement.entity;


import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.enums.SettlementStatus;
import com.funding.backend.global.auditable.Auditable;
import jakarta.persistence.CascadeType;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "settlements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Settlement extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // 해당 정산이 포함하는 기간 (예: 6월분 정산)
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;

    // 정산 금액 정보
    // 총 주문 금액
    private Long totalOrderAmount;

    //수수료 금액
    private Long feeAmount;

    //실제 정산 지급 금액
    //totalOrderAmount - feeAmount
    private Long payoutAmount;


    //정산 완료 시간
    private LocalDateTime settledAt;

    @Enumerated(EnumType.STRING)
    private SettlementStatus status; // WAITING, COMPLETED, FAILED 등

    // 어떤 프로젝트에 대한 정산인지
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Order> orderList = new ArrayList<>();

}
