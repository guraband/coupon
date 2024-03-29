package com.guraband.couponcore.service

import com.guraband.couponcore.config.CacheConfiguration
import com.guraband.couponcore.model.CouponRedisEntity
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class CouponCacheService(
    private val couponIssueService: CouponIssueService
) {
    @Cacheable(cacheNames = [CacheConfiguration.CACHE_10MINUTES], key = "'coupon:' + #couponId")
    fun getCouponCache(couponId : Long) : CouponRedisEntity {
        val coupon = couponIssueService.findCoupon(couponId)
        return CouponRedisEntity(coupon)
    }
}