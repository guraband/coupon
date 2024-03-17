package com.guraband.couponcore.repository

import com.guraband.couponcore.model.CouponIssue
import com.guraband.couponcore.model.QCouponIssue.couponIssue
import com.querydsl.jpa.JPQLQueryFactory
import org.springframework.stereotype.Repository

@Repository
class CouponIssueRepository(
    private val queryFactory: JPQLQueryFactory
) {
    fun findFirstCouponIssue(couponId: Long, userId: Long): CouponIssue? {
        return queryFactory.selectFrom(couponIssue)
            .where(couponIssue.couponId.eq(couponId))
            .where(couponIssue.userId.eq(userId))
            .fetchFirst()
    }
}