package com.guraband.couponcore.enums

import com.guraband.couponcore.exception.CouponIssueException

enum class CouponIssueResultCode(
    val resultCode: Int
) {
    SUCCESS(1),
    DUPLICATED_COUPON_ISSUE(2),
    INVALID_COUPON_ISSUE_QUANTITY(3);

    companion object {
        fun find(code: String): CouponIssueResultCode {
            val codeValue = code.toInt()
            return entries.find {
                it.resultCode == codeValue
            } ?: throw IllegalArgumentException("존재하지 않는 코드입니다.")
        }

        fun validate(code: CouponIssueResultCode) {
            when (code) {
                DUPLICATED_COUPON_ISSUE -> throw CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE)
                INVALID_COUPON_ISSUE_QUANTITY -> throw CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY)
                else -> return
            }
        }
    }
}