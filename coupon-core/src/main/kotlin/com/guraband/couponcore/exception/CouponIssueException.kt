package com.guraband.couponcore.exception

import com.guraband.couponcore.enums.ErrorCode

class CouponIssueException(
    val errorCode: ErrorCode,
    message: String,
) : RuntimeException(message)