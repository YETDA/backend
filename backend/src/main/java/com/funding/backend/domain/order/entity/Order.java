package com.funding.backend.domain.order.entity;

import com.funding.backend.domain.orderOption.entity.OrderOption;
import com.funding.backend.domain.pricingPlan.entity.PricingPlan;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.auditable.Auditable;
import com.funding.backend.global.toss.enums.OrderStatus;
import com.funding.backend.global.toss.enums.PayType;
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
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class Order extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "pay_type")
	private PayType payType;

	@Column(nullable = false)
	private Long paidAmount;

	//download, gmail 인지 선택
	@Enumerated(EnumType.STRING)
	@Column(name = "project_type")
	private ProjectType projectType;

	//중복 데이터 저장되면 안됨 (주문 번호)
	@Column(nullable = false, unique = true)
	private String orderId;

	//구매한 시점의 이메일, 이름 저장을 위한 필드
	@Column(nullable = false)
	private String customerEmail;

	@Column(nullable = false)
	private String customerName;

	//Toss 결제 승인 시 받은 고유 키
	@Column(name = "payment_key", unique = true)
	private String paymentKey;


	//주문 상태
	@Enumerated(EnumType.STRING)
	@Column(name = "order_status", nullable = false)
	private OrderStatus orderStatus;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;

	@OneToMany(mappedBy = "order")
	private List<OrderOption> orderOptionList = new ArrayList<>();

}