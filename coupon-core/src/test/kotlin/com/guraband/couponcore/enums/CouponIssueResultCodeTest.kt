package com.guraband.couponcore.enums

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CouponIssueResultCodeTest {

    @Test
    fun enumTest() {
        val resultCode = "1"

        val couponIssueResultCode = CouponIssueResultCode.find(resultCode)

        assertThat(couponIssueResultCode).isEqualTo(CouponIssueResultCode.SUCCESS)
    }

    @Test
    @DisplayName("존재하지 않는 코드를 변환할 경우 예외 발생")
    fun enumTest2() {
        val resultCode = "4"

        assertThrows<IllegalArgumentException> {
            CouponIssueResultCode.find(resultCode)
        }
    }
}