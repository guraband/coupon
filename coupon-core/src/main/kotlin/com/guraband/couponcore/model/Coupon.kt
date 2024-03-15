package com.guraband.couponcore.model

import com.guraband.couponcore.enums.CouponType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Coupon(
    val title: String,

    @Column(columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    val couponType: CouponType,

    val totalQuantity: Int?,

    val issuedQuantity: Int,

    val discountAmount: Int,

    val minAvailableAmount: Int,

    val dateIssueStart: LocalDateTime?,

    val dateIssueEnd: LocalDateTime?,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity()