package com.guraband.couponcore.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.guraband.couponcore.component.DistributeLockExecutor
import com.guraband.couponcore.enums.ErrorCode
import com.guraband.couponcore.exception.CouponIssueException
import com.guraband.couponcore.model.CouponIssueRequest
import com.guraband.couponcore.repository.RedisRepository
import com.guraband.couponcore.util.CouponRedisUtil.Companion.getIssueRequestKey
import com.guraband.couponcore.util.CouponRedisUtil.Companion.getIssueRequestQueueKey
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AsyncCouponIssueServiceV1(
    private val redisRepository: RedisRepository,
    private val couponIssueRedisService: CouponIssueRedisService,
    private val distributeLockExecutor: DistributeLockExecutor,
    private val couponCacheService: CouponCacheService,
) {
    @Transactional
    fun issueUsingRedisSortedSet(couponId: Long, userId: Long) {
        // 유저의 요청을 sorted set에 적재
        val key = "issue.request.sorted_set.couponId=$couponId"
        redisRepository.zAdd(key, userId.toString(), System.currentTimeMillis().toDouble())
    }

    @Transactional
    fun issueUsingRedisSet(couponId: Long, userId: Long) {
        val coupon = couponCacheService.getCouponCache(couponId)
        coupon.validate()

        distributeLockExecutor.execute("lock_$couponId", 3_000, 3_000) {
            couponIssueRedisService.checkCouponIssueQuantity(coupon, userId)
            issueRequest(couponId, userId)
        }
    }

    private fun issueRequest(couponId: Long, userId: Long) {
        val issueRequest = CouponIssueRequest(couponId, userId)
        try {
            val value = jacksonObjectMapper().writeValueAsString(issueRequest)
            redisRepository.sAdd(getIssueRequestKey(couponId), userId.toString())
            redisRepository.rPush(getIssueRequestQueueKey(), value)
        } catch (e: JsonProcessingException) {
            throw CouponIssueException(ErrorCode.FAIL_COUPON_ISSUE_REQUEST, "input : $issueRequest")
        }
    }
}