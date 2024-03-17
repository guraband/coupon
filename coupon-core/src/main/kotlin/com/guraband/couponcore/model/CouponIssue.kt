package com.guraband.couponcore.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class CouponIssue(
    val couponId: Long,

    val userId: Long,

    val dateIssued: LocalDateTime = LocalDateTime.now(),

    val dateUsed: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity()