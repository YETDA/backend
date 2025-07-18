package com.funding.backend.domain.donationReward.entity;

import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.orderReward.entity.OrderReward;
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
@Table(name = "donation_reward")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DonationReward extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

    @Column(name = "title", length = 100, nullable = false)
    private String title; // 리워드 이름

    @Column(name = "content", length = 100, nullable = false)
    private String content;

    @Column(name = "price", nullable = false)
    private Long price;

    @OneToMany(mappedBy = "donationReward")
    private List<OrderReward> orderRewardList = new ArrayList<>();

}
