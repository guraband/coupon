package com.guraband.couponcore.service

import com.guraband.couponcore.TestConfig
import com.guraband.couponcore.enums.ErrorCode
import com.guraband.couponcore.exception.CouponIssueException
import com.guraband.couponcore.model.CouponIssue
import com.guraband.couponcore.repository.CouponIssueJpaRepository
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class CouponIssueServiceTest @Autowired constructor(
    private val couponIssueService: CouponIssueService,
    private val couponJpaRepository: CouponIssueJpaRepository,
    private val couponIssueJpaRepository: CouponIssueJpaRepository,
    private val couponIssueRepository: CouponIssueJpaRepository,
) : TestConfig() {
    @BeforeEach
    fun clean() {
        couponJpaRepository.deleteAllInBatch()
        couponIssueJpaRepository.deleteAllInBatch()
    }

    @Test
    @DisplayName("중복 발급시 예외가 발생한다.")
    fun duplicateIssueTest() {
        // given
        val couponIssue = CouponIssue(1L, 1L)
        couponIssueJpaRepository.save(couponIssue)

        // when
        val e = assertThrows<CouponIssueException> {
            couponIssueService.issue(couponIssue.couponId, couponIssue.userId)
        }

        // then
        assertThat(e.errorCode).isEqualTo(ErrorCode.DUPLICATED_COUPON_ISSUE)
    }
}