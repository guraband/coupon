package com.guraband.couponcore.service

import com.guraband.couponcore.repository.RedisRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AsyncCouponIssueServiceV2(
    private val redisRepository: RedisRepository,
    private val couponCacheService: CouponCacheService,
) {
    @Transactional
    fun issueUsingRedisSet(couponId: Long, userId: Long) {
        val coupon = couponCacheService.getCouponCache(couponId)
        coupon.validate()
        issueRequest(couponId, userId, coupon.totalQuantity)
    }

    private fun issueRequest(couponId: Long, userId: Long, totalQuantity: Int?) {
        redisRepository.issueRequest(couponId, userId, totalQuantity ?: Int.MAX_VALUE)
    }
}