package com.guraband.couponcore.service

import com.guraband.couponcore.enums.ErrorCode
import com.guraband.couponcore.exception.CouponIssueException
import com.guraband.couponcore.model.CouponRedisEntity
import com.guraband.couponcore.repository.RedisRepository
import com.guraband.couponcore.util.CouponRedisUtil.Companion.getIssueRequestKey
import org.springframework.stereotype.Service

@Service
class CouponIssueRedisService(
    private val redisRepository: RedisRepository
) {
    fun checkCouponIssueQuantity(coupon : CouponRedisEntity, userId: Long) {
        if (!availableTotalIssueQuantity(coupon.totalQuantity, coupon.id)) {
            throw CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY, "수량 초과 : $coupon.totalIssueQuantity")
        }

        if (!availableUserIssueQuantity(coupon.id, userId)) {
            throw CouponIssueException(
                ErrorCode.DUPLICATED_COUPON_ISSUE,
                "이미 발급된 쿠폰입니다. userId : $userId, couponId : ${coupon.id}"
            )
        }
    }

    fun availableTotalIssueQuantity(totalQuantity: Int?, couponId: Long): Boolean {
        if (totalQuantity == null) {
            return true
        }

        val key = getIssueRequestKey(couponId)
        return totalQuantity > (redisRepository.sCard(key) ?: 0)
    }

    fun availableUserIssueQuantity(couponId: Long, userId: Long): Boolean {
        val key = getIssueRequestKey(couponId)
        return !(redisRepository.sIsMember(key, userId.toString()) ?: false)
    }
}