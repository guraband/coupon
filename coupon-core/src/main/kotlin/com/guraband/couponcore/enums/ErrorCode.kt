package com.guraband.couponcore.enums

enum class ErrorCode(
    val message: String,
) {
    INVALID_COUPON_ISSUE_QUANTITY("발급 가능한 수량을 초과했습니다."),
    INVALID_COUPON_ISSUE_DATE("발급 기한이 유효하지 않습니다."),
    COUPON_NOT_EXIST("쿠폰이 존재하지 않습니다."),
    DUPLICATED_COUPON_ISSUE("이미 발급된 쿠폰입니다."),
    FAIL_COUPON_ISSUE_REQUEST("쿠폰 발급에 실패했습니다."),
}