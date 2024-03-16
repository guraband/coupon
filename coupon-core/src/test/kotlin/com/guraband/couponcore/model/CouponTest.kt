package com.guraband.couponcore.model

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CouponTest {
    @Test
    @DisplayName("발급수량이 남아있다면 true 반환")
    fun availableIssueQuantityTest1() {
        // given
        val coupon = Coupon(totalQuantity = 100, issuedQuantity = 99)

        // when
        val result = coupon.availableIssueQuantity()

        // then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("발급수량이 소진되었다면 false 반환")
    fun availableIssueQuantityTest2() {
        // given
        val coupon = Coupon(totalQuantity = 100, issuedQuantity = 100)

        // when
        val result = coupon.availableIssueQuantity()

        // then
        assertThat(result).isFalse()
    }

    @Test
    @DisplayName("발급수량이 null이면 true 반환")
    fun availableIssueQuantityTest3() {
        // given
        val coupon = Coupon(issuedQuantity = 100)

        // when
        val result = coupon.availableIssueQuantity()

        // then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("발급기한이 시작되지 않았다면 false")
    fun availableIssueDateTest1() {
        // given
        val coupon = Coupon(
            dateIssueStart = LocalDateTime.now().plusDays(1),
            dateIssueEnd = LocalDateTime.now().plusDays(2)
        )

        // when
        val result = coupon.availableIssueDate()

        // then
        assertThat(result).isFalse()
    }

    @Test
    @DisplayName("발급기한이면 true")
    fun availableIssueDateTest2() {
        // given
        val coupon = Coupon(
            dateIssueStart = LocalDateTime.now().plusDays(-1),
            dateIssueEnd = LocalDateTime.now().plusDays(2)
        )

        // when
        val result = coupon.availableIssueDate()

        // then
        assertThat(result).isTrue()
    }

    @Test
    @DisplayName("발급기한이 종료되었다면 false")
    fun availableIssueDateTest3() {
        // given
        val coupon = Coupon(
            dateIssueStart = LocalDateTime.now().plusDays(-2),
            dateIssueEnd = LocalDateTime.now().plusDays(-1)
        )

        // when
        val result = coupon.availableIssueDate()

        // then
        assertThat(result).isFalse()
    }

    @Test
    @DisplayName("발급 테스트")
    fun issueTest1() {
        // given
        val coupon = Coupon(
            totalQuantity = 100,
            issuedQuantity = 99,
            dateIssueStart = LocalDateTime.now().plusDays(-2),
            dateIssueEnd = LocalDateTime.now().plusDays(1)
        )

        // when
        coupon.issue()

        // then
        assertThat(coupon.getIssuesQuantity()).isEqualTo(100)
    }
}