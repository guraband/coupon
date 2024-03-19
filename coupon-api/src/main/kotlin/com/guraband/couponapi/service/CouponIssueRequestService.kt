package com.guraband.couponapi.service

import com.guraband.couponapi.dto.CouponIssueRequest
import com.guraband.couponcore.service.CouponIssueService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CouponIssueRequestService(
    private val couponIssueService: CouponIssueService,
) {
    fun issueRequestV1(request: CouponIssueRequest) {
        couponIssueService.issue(request.couponId, request.userId)

        logger.info { "[쿠폰 발급 완료] couponId : ${request.couponId}, ${request.userId}"}
    }
}