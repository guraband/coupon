package com.guraband.couponconsumer.component

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.guraband.couponcore.model.CouponIssueRequest
import com.guraband.couponcore.repository.RedisRepository
import com.guraband.couponcore.service.CouponIssueService
import com.guraband.couponcore.util.CouponRedisUtil.Companion.getIssueRequestQueueKey
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@EnableScheduling
@Component
class CouponIssueListener(
    private val redisRepository: RedisRepository,
    private val couponIssueService: CouponIssueService,
) {
    private val issueRequestQueueKey = getIssueRequestQueueKey()

    @Scheduled(fixedDelay = 1_000L)
    fun issue() {
        logger.info { "listening..." }
        while (existCouponIssueTarget()) {
            val target = couponIssueTarget()
            if (target != null) {
                logger.info { "발급 시작 $target" }
                couponIssueService.issue(target.couponId, target.userId)
                logger.info { "발급 종료 $target" }
            }
        }
    }

    private fun existCouponIssueTarget(): Boolean {
        return (redisRepository.lSize(issueRequestQueueKey) ?: 0) > 0
    }

    private fun couponIssueTarget(): CouponIssueRequest? {
        return redisRepository.lPop(issueRequestQueueKey)?.let {
            return jacksonObjectMapper().readValue(it, CouponIssueRequest::class.java)
        }
    }
}