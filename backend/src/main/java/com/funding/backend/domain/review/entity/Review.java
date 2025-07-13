package com.funding.backend.domain.review.entity;

import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.global.auditable.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Review extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    //미구현 상태
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "purchase_category_id", nullable = false)
//    private PurchaseCategory purchaseCategory;

    @Column(name = "content",nullable = false)
    private String content;

    @Column(name = "rating",nullable = false)
    private Float rating;

    @Column(name = "image_url")
    private String imageUrl;


}
