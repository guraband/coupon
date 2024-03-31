package com.guraband.couponconsumer.component

import com.guraband.couponconsumer.TestConfig
import com.guraband.couponcore.repository.RedisRepository
import com.guraband.couponcore.service.CouponIssueService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.InOrder
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.RedisTemplate

class CouponIssueListenerTest @Autowired constructor(
    val couponIssueListener: CouponIssueListener,
    val redisTemplate: RedisTemplate<String, String>,
    val redisRepository: RedisRepository,
    @MockBean val couponIssueService: CouponIssueService,
) : TestConfig() {

    @BeforeEach
    fun clear() {
        val redisKeys = redisTemplate.keys("*")
        redisTemplate.delete(redisKeys)
    }

    @Test
    @DisplayName("쿠폰 발급 큐에 대상이 없으면 처리하지 않는다")
    fun issue1() {
        couponIssueListener.issue()

        verify(couponIssueService, never()).issue(anyLong(), anyLong())
    }

    @Test
    @DisplayName("쿠폰 발급 큐에 대상이 있으면 발급한다")
    fun issue2() {
        // given
        val couponId = 1L
        val userId = 100L
        val totalQuantity = Int.MAX_VALUE

        redisRepository.issueRequest(couponId, userId, totalQuantity)

        // when
        couponIssueListener.issue()

        // then
        verify(couponIssueService, times(1)).issue(anyLong(), anyLong())
    }


    @Test
    @DisplayName("쿠폰 발급이 순서대로 처리된다.")
    fun issue3() {
        // given
        val couponId = 1L
        val userId1 = 100L
        val userId2 = 101L
        val userId3 = 102L
        val totalQuantity = Int.MAX_VALUE

        redisRepository.issueRequest(couponId, userId1, totalQuantity)
        redisRepository.issueRequest(couponId, userId2, totalQuantity)
        redisRepository.issueRequest(couponId, userId3, totalQuantity)

        // when
        couponIssueListener.issue()

        // then
        val inOrder : InOrder = inOrder(couponIssueService)
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId1)
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId2)
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId3)
    }
}