package com.guraband.couponcore.repository

import com.guraband.couponcore.model.Coupon
import org.springframework.data.jpa.repository.JpaRepository

interface CouponJpaRepository : JpaRepository<Coupon, Long> {
}