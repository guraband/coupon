package com.guraband.couponcore.service

import com.guraband.couponcore.enums.ErrorCode
import com.guraband.couponcore.exception.CouponIssueException
import com.guraband.couponcore.model.Coupon
import com.guraband.couponcore.model.CouponIssue
import com.guraband.couponcore.repository.CouponIssueJpaRepository
import com.guraband.couponcore.repository.CouponIssueRepository
import com.guraband.couponcore.repository.CouponJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CouponIssueService(
    private val couponJpaRepository: CouponJpaRepository,
    private val couponIssueJpaRepository: CouponIssueJpaRepository,
    private val couponIssueRepository: CouponIssueRepository,
) {

    @Transactional
    fun issue(couponId: Long, userId: Long) {
        val coupon = findCoupon(couponId)
        coupon.issue()
        saveCouponIssue(couponId, userId)
    }

    @Transactional(readOnly = true)
    fun findCoupon(couponId: Long): Coupon {
        return couponJpaRepository.findById(couponId).orElseThrow {
            throw CouponIssueException(ErrorCode.COUPON_NOT_EXIST, "쿠폰이 존재하지 않습니다. ($couponId)")
        }
    }

    @Transactional
    fun saveCouponIssue(couponId: Long, userId: Long) {
        checkCouponAlreadyIssued(couponId, userId)

        val couponIssue = CouponIssue(couponId, userId)
        couponIssueJpaRepository.save(couponIssue)
    }

    private fun checkCouponAlreadyIssued(couponId: Long, userId: Long) {
        couponIssueRepository.findFirstCouponIssue(couponId, userId)
            ?: throw CouponIssueException(
                ErrorCode.DUPLICATED_COUPON_ISSUE,
                "이미 발급된 쿠폰입니다. userId : $userId, couponId : $couponId"
            )
    }
}