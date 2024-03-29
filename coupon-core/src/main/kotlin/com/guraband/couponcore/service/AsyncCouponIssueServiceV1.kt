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
    private val couponIssueService: CouponIssueService,
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
        val coupon = couponIssueService.findCoupon(couponId)

        if (!coupon.availableIssueDate()) {
            throw CouponIssueException(
                ErrorCode.INVALID_COUPON_ISSUE_DATE,
                "발급 기한 : ${coupon.dateIssueStart} ~ ${coupon.dateIssueEnd}"
            )
        }

        distributeLockExecutor.execute("lock_$couponId", 3_000, 3_000) {
            if (!couponIssueRedisService.availableTotalIssueQuantity(coupon.totalIssueQuantity, couponId)) {
                throw CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY, "수량 초과 : $coupon.totalIssueQuantity")
            }

            if (!couponIssueRedisService.availableUserIssueQuantity(couponId, userId)) {
                throw CouponIssueException(
                    ErrorCode.DUPLICATED_COUPON_ISSUE,
                    "이미 발급된 쿠폰입니다. userId : $userId, couponId : $couponId"
                )
            }

            issueRequest(couponId, userId)
        }
    }

    private fun issueRequest(couponId: Long, userId: Long) {
        val issueRequest = CouponIssueRequest(couponId, userId)
        try {
            val value = jacksonObjectMapper().writeValueAsString(issueRequest)
            redisRepository.sAdd(getIssueRequestKey(couponId), userId.toString())
            redisRepository.rPush(getIssueRequestQueueKey(), value)
        } catch (e : JsonProcessingException) {
            throw CouponIssueException(ErrorCode.FAIL_COUPON_ISSUE_REQUEST, "input : $issueRequest")
        }
    }
}