package com.guraband.couponcore.repository

import com.guraband.couponcore.model.CouponIssue
import org.springframework.data.jpa.repository.JpaRepository

interface CouponIssueJpaRepository : JpaRepository<CouponIssue, Long> {
}