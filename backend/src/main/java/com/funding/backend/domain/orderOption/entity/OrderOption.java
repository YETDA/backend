package com.funding.backend.domain.orderOption.entity;


import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.enums.ProvidingMethod;
import com.funding.backend.global.auditable.Auditable;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "order_options")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OrderOption extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 주문 (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 구매 옵션 (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private PurchaseOption purchaseOption;

    @Column(name = "option_name", length = 100, nullable = false)
    private String optionName; // 구매 당시 옵션명

    @Column(name = "price", nullable = false)
    private Long price; // 구매 당시 가격

    @Enumerated(EnumType.STRING)
    @Column(name = "providing_method", nullable = false)
    private ProvidingMethod providingMethod; // 다운로드, 이메일 등

    // 다운로드 마감일 (nullable: 후원형은 없을 수도 있음)
    @Column(name = "download_expire")
    private LocalDateTime downloadExpire;

    // 다운로드 횟수 (기본값 0, null 가능)
    @Column(name = "download_count")
    private  Integer downloadCount = 0;

}