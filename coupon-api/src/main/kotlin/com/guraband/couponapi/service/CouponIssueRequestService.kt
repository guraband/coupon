package com.guraband.couponapi.service

import com.guraband.couponapi.dto.CouponIssueRequest
import com.guraband.couponcore.component.DistributeLockExecutor
import com.guraband.couponcore.service.AsyncCouponIssueServiceV1
import com.guraband.couponcore.service.AsyncCouponIssueServiceV2
import com.guraband.couponcore.service.CouponIssueService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CouponIssueRequestService(
    private val couponIssueService: CouponIssueService,
    private val asyncCouponIssueServiceV1: AsyncCouponIssueServiceV1,
    private val asyncCouponIssueServiceV2: AsyncCouponIssueServiceV2,
    private val distributeLockExecutor: DistributeLockExecutor,
) {
    fun issueRequestV1(request: CouponIssueRequest) {
        distributeLockExecutor.execute("lock_coupon_" + request.couponId, 10_000, 10_000) {
            couponIssueService.issue(request.couponId, request.userId)
        }

        logger.info { "[쿠폰 발급 완료] couponId : ${request.couponId}, ${request.userId}" }
    }

    fun issueRequestV2(request: CouponIssueRequest) {
        couponIssueService.issueWithDBLock(request.couponId, request.userId)
        logger.info { "[쿠폰 발급 완료] couponId : ${request.couponId}, ${request.userId}" }
    }

    fun asyncIssueRequestUsingSortedSet(request: CouponIssueRequest) {
        asyncCouponIssueServiceV1.issueUsingRedisSortedSet(request.couponId, request.userId)
        logger.info { "[쿠폰 발급 완료] couponId : ${request.couponId}, ${request.userId}" }
    }

    fun asyncIssueRequestUsingSetV1(request: CouponIssueRequest) {
        asyncCouponIssueServiceV1.issueUsingRedisSet(request.couponId, request.userId)
        logger.info { "[쿠폰 발급 완료] couponId : ${request.couponId}, ${request.userId}" }
    }

    fun asyncIssueRequestUsingSetV2(request: CouponIssueRequest) {
        asyncCouponIssueServiceV2.issueUsingRedisSet(request.couponId, request.userId)
        logger.info { "[쿠폰 발급 완료] couponId : ${request.couponId}, ${request.userId}" }
    }
}