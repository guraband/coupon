package com.guraband.couponcore.service

import com.guraband.couponcore.repository.RedisRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AsyncCouponIssueServiceV1(
    private val redisRepository: RedisRepository,
) {
    @Transactional
    fun issue(couponId: Long, userId: Long) {
        // 유저의 요청을 sorted set에 적재
        val key = "issue.request.sorted_set.couponId=$couponId"
        redisRepository.zAdd(key, userId.toString(), System.currentTimeMillis().toDouble())
    }
}