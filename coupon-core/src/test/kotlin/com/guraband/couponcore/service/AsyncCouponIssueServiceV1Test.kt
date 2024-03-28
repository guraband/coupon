package com.guraband.couponcore.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.guraband.couponcore.TestConfig
import com.guraband.couponcore.enums.CouponType
import com.guraband.couponcore.enums.ErrorCode
import com.guraband.couponcore.exception.CouponIssueException
import com.guraband.couponcore.model.Coupon
import com.guraband.couponcore.model.CouponIssueRequest
import com.guraband.couponcore.repository.CouponJpaRepository
import com.guraband.couponcore.util.CouponRedisUtil.Companion.getIssueRequestKey
import com.guraband.couponcore.util.CouponRedisUtil.Companion.getIssueRequestQueueKey
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import java.time.LocalDateTime

class AsyncCouponIssueServiceV1Test @Autowired constructor(
    private val asyncCouponIssueService: AsyncCouponIssueServiceV1,
    private val redisTemplate: RedisTemplate<String, String>,
    private val couponJpaRepository: CouponJpaRepository,
) : TestConfig() {
    @BeforeEach
    fun clear() {
        val redisKeys = redisTemplate.keys("*")
        redisTemplate.delete(redisKeys)
        couponJpaRepository.deleteAllInBatch()
    }

    @Test
    @DisplayName("쿠폰 발급 - 쿠폰이 존재하지 않으면 예외 발생")
    fun couponIssueTest() {
        // given
        val couponId = 1L
        val userId = 1L

        // when & then
        val e = assertThrows<CouponIssueException> {
            asyncCouponIssueService.issueUsingRedisSet(couponId, userId)
        }
        assertThat(e.errorCode).isEqualTo(ErrorCode.COUPON_NOT_EXIST)
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 가능한 수량이 없으면 예외 발생")
    fun couponIssueTest2() {
        // given
        val userId = 1000L

        val coupon = Coupon(
            "선착순 테스트 쿠폰",
            CouponType.FIRST_COME_FIRST_SERVE,
            10,
            0,
            dateIssueStart = LocalDateTime.now().minusDays(1),
            dateIssueEnd = LocalDateTime.now().plusDays(1)
        )

        couponJpaRepository.save(coupon)
        val couponId = coupon.id!!

        (1..10).forEach {
            redisTemplate.opsForSet().add(getIssueRequestKey(couponId), it.toString())
        }

        // when & then
        val e = assertThrows<CouponIssueException> {
            asyncCouponIssueService.issueUsingRedisSet(couponId, userId)
        }
        assertThat(e.errorCode).isEqualTo(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY)
    }

    @Test
    @DisplayName("쿠폰 발급 - 중복 발급 시도시 예외 발생")
    fun couponIssueTest3() {
        // given
        val userId = 10L

        val coupon = Coupon(
            "선착순 테스트 쿠폰",
            CouponType.FIRST_COME_FIRST_SERVE,
            100,
            0,
            dateIssueStart = LocalDateTime.now().minusDays(1),
            dateIssueEnd = LocalDateTime.now().plusDays(1)
        )

        couponJpaRepository.save(coupon)
        val couponId = coupon.id!!

        (1..10).forEach {
            redisTemplate.opsForSet().add(getIssueRequestKey(couponId), it.toString())
        }

        // when & then
        val e = assertThrows<CouponIssueException> {
            asyncCouponIssueService.issueUsingRedisSet(couponId, userId)
        }
        assertThat(e.errorCode).isEqualTo(ErrorCode.DUPLICATED_COUPON_ISSUE)
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 기한에 유효하지 않으면 예외 발생")
    fun couponIssueTest4() {
        // given
        val userId = 10L

        val coupon = Coupon(
            "선착순 테스트 쿠폰",
            CouponType.FIRST_COME_FIRST_SERVE,
            10,
            0,
            dateIssueStart = LocalDateTime.now().minusDays(2),
            dateIssueEnd = LocalDateTime.now().minusDays(1)
        )

        couponJpaRepository.save(coupon)
        val couponId = coupon.id!!

        // when & then
        val e = assertThrows<CouponIssueException> {
            asyncCouponIssueService.issueUsingRedisSet(couponId, userId)
        }
        assertThat(e.errorCode).isEqualTo(ErrorCode.INVALID_COUPON_ISSUE_DATE)
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 후 캐시에 존재하는지 확인")
    fun couponIssueTest5() {
        // given
        val userId = 10L

        val coupon = Coupon(
            "선착순 테스트 쿠폰",
            CouponType.FIRST_COME_FIRST_SERVE,
            10,
            0,
            dateIssueStart = LocalDateTime.now().minusDays(2),
            dateIssueEnd = LocalDateTime.now().plusDays(1)
        )
        couponJpaRepository.save(coupon)
        val couponId = coupon.id!!

        // when
        asyncCouponIssueService.issueUsingRedisSet(couponId, userId)

        // then
        assertThat(
            redisTemplate.opsForSet().isMember(getIssueRequestKey(couponId), userId.toString())
        ).isTrue()

        val queue = redisTemplate.opsForList().leftPop(getIssueRequestQueueKey())
        val couponRequest = CouponIssueRequest(couponId, userId)
        val value = jacksonObjectMapper().writeValueAsString(couponRequest)
        assertThat(queue).isEqualTo(value)
    }
}