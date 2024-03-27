package com.guraband.couponcore.service

import com.guraband.couponcore.repository.RedisRepository
import com.guraband.couponcore.util.CouponRedisUtil.Companion.getIssueRequestKey
import org.springframework.stereotype.Service

@Service
class CouponIssueRedisService(
    private val redisRepository: RedisRepository
) {
    fun availableTotalIssueQuantity(totalQuantity: Int?, couponId: Long): Boolean {
        if (totalQuantity == null) {
            return true
        }

        val key = getIssueRequestKey(couponId.toString())
        return totalQuantity > (redisRepository.sCard(key) ?: 0)
    }

    fun availableUserIssueQuantity(couponId: Long, userId: Long): Boolean {
        val key = getIssueRequestKey(couponId.toString())
        return !(redisRepository.sIsMember(key, userId.toString()) ?: false)
    }
}