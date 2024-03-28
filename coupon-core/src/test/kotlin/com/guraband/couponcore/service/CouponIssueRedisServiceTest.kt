package com.guraband.couponcore.service

import com.guraband.couponcore.TestConfig
import com.guraband.couponcore.util.CouponRedisUtil.Companion.getIssueRequestKey
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate

class CouponIssueRedisServiceTest @Autowired constructor(
    val couponIssueRedisService: CouponIssueRedisService,
    val redisTemplate: RedisTemplate<String, String>,
) : TestConfig() {
    @BeforeEach
    fun clear() {
        val redisKeys = redisTemplate.keys("*")
        redisTemplate.delete(redisKeys)
    }

    @Test
    @DisplayName("쿠폰 수량 검증- 발급 가능 수량이 존재하면 true")
    fun availableTotalIssueQuantityTest1() {
        // given
        val totalIssueQuantity = 10
        val couponId = 1L

        // when
        val result = couponIssueRedisService.availableTotalIssueQuantity(totalIssueQuantity, couponId)

        // then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("쿠폰 수량 검증- 발급 가능 수량이 소진되면 false")
    fun availableTotalIssueQuantityTest2() {
        // given
        val totalIssueQuantity = 10
        val couponId = 1L
        (1..10).forEach {
            redisTemplate.opsForSet().add(getIssueRequestKey(couponId), it.toString())
        }

        // when
        val result = couponIssueRedisService.availableTotalIssueQuantity(totalIssueQuantity, couponId)

        // then
        assertThat(result).isFalse()
    }

    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하지 않으면 true")
    fun availableUserIssueQuantityTest1() {
        // given
        val couponId = 1L
        val userId = 1L

        // when
        val result = couponIssueRedisService.availableUserIssueQuantity(couponId, userId)

        // then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("쿠폰 중복 발급 검증 - 발급된 내역에 유저가 존재하면 false")
    fun availableUserIssueQuantityTest2() {
        // given
        val couponId = 1L
        val userId = 1L
        redisTemplate.opsForSet().add(getIssueRequestKey(couponId), userId.toString())

        // when
        val result = couponIssueRedisService.availableUserIssueQuantity(couponId, userId)

        // then
        assertThat(result).isFalse()
    }
}
