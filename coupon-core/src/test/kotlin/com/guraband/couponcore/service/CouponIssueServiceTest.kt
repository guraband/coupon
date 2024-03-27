package com.guraband.couponcore.service

import com.guraband.couponcore.TestConfig
import com.guraband.couponcore.enums.CouponType
import com.guraband.couponcore.enums.ErrorCode
import com.guraband.couponcore.exception.CouponIssueException
import com.guraband.couponcore.model.Coupon
import com.guraband.couponcore.model.CouponIssue
import com.guraband.couponcore.repository.CouponIssueJpaRepository
import com.guraband.couponcore.repository.CouponIssueRepository
import com.guraband.couponcore.repository.CouponJpaRepository
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

class CouponIssueServiceTest @Autowired constructor(
    private val couponIssueService: CouponIssueService,
    private val couponJpaRepository: CouponJpaRepository,
    private val couponIssueJpaRepository: CouponIssueJpaRepository,
    private val couponIssueRepository: CouponIssueRepository,
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
            couponIssueService.saveCouponIssue(couponIssue.couponId, couponIssue.userId)
        }

        // then
        assertThat(e.errorCode).isEqualTo(ErrorCode.DUPLICATED_COUPON_ISSUE)
    }

    @Test
    @DisplayName("발급내역이 없으면 정상 발급된다.")
    fun saveCouponIssueTest() {
        // given
        val couponId = 1L
        val userId = 1L

        // when
        val result = couponIssueService.saveCouponIssue(couponId, userId)

        // then
        assertThat(couponIssueJpaRepository.findById(result.id!!).isPresent).isTrue()
    }

    @Test
    @DisplayName("발급수량, 기한, 중복발급에 문제가 없으면 쿠폰을 발급한다.")
    fun issueTest1() {
        // given
        val userId = 1L
        val coupon = Coupon(
            title = "선착순 쿠폰",
            couponType = CouponType.FIRST_COME_FIRST_SERVE,
            _totalQuantity = 100,
            _issuedQuantity = 0,
            dateIssueStart = LocalDateTime.now().minusDays(1),
            dateIssueEnd = LocalDateTime.now().plusDays(1),
        )
        couponJpaRepository.save(coupon)

        // when
        couponIssueService.issue(coupon.id!!, userId)

        // then
        val couponResult = couponJpaRepository.findById(coupon.id!!).get()
        assertThat(couponResult.issuedQuantity).isEqualTo(1)

        val couponIssueResult = couponIssueRepository.findFirstCouponIssue(coupon.id!!, userId)
        assertThat(couponIssueResult).isNotNull
    }

    @Test
    @DisplayName("발급수량에 문제가 있으면 예외가 발생한다.")
    fun issueTest2() {
        // given
        val userId = 1L
        val coupon = Coupon(
            title = "선착순 쿠폰",
            couponType = CouponType.FIRST_COME_FIRST_SERVE,
            _totalQuantity = 100,
            _issuedQuantity = 100,
            dateIssueStart = LocalDateTime.now().minusDays(1),
            dateIssueEnd = LocalDateTime.now().plusDays(1),
        )
        couponJpaRepository.save(coupon)

        // when & then
        val e = assertThrows<CouponIssueException> {
            couponIssueService.issue(coupon.id!!, userId)
        }
        assertThat(e.errorCode).isEqualTo(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY)
    }

    @Test
    @DisplayName("발급기한에 문제가 있으면 예외가 발생한다.")
    fun issueTest3() {
        // given
        val userId = 1L
        val coupon = Coupon(
            title = "선착순 쿠폰",
            couponType = CouponType.FIRST_COME_FIRST_SERVE,
            _totalQuantity = 100,
            _issuedQuantity = 0,
            dateIssueStart = LocalDateTime.now().minusDays(2),
            dateIssueEnd = LocalDateTime.now().minusDays(1),
        )
        couponJpaRepository.save(coupon)

        // when & then
        val e = assertThrows<CouponIssueException> {
            couponIssueService.issue(coupon.id!!, userId)
        }
        assertThat(e.errorCode).isEqualTo(ErrorCode.INVALID_COUPON_ISSUE_DATE)
    }


    @Test
    @DisplayName("중복 발급시 예외가 발생한다.")
    fun issueTest4() {
        // given
        val userId = 1L
        val coupon = Coupon(
            title = "선착순 쿠폰",
            couponType = CouponType.FIRST_COME_FIRST_SERVE,
            _totalQuantity = 100,
            _issuedQuantity = 0,
            dateIssueStart = LocalDateTime.now().minusDays(1),
            dateIssueEnd = LocalDateTime.now().plusDays(1),
        )
        couponJpaRepository.save(coupon)

        val couponIssue = CouponIssue(coupon.id!!, userId)
        couponIssueJpaRepository.save(couponIssue)

        // when & then
        val e = assertThrows<CouponIssueException> {
            couponIssueService.issue(coupon.id!!, userId)
        }
        assertThat(e.errorCode).isEqualTo(ErrorCode.DUPLICATED_COUPON_ISSUE)
    }

    @Test
    @DisplayName("쿠폰이 존재하지 않으면 예외가 발생한다.")
    fun issueTest5() {
        // given
        val couponId = 1L
        val userId = 1L

        // when & then
        val e = assertThrows<CouponIssueException> {
            couponIssueService.issue(couponId, userId)
        }
        assertThat(e.errorCode).isEqualTo(ErrorCode.COUPON_NOT_EXIST)
    }
}