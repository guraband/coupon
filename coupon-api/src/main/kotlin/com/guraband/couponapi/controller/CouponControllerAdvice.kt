package com.guraband.couponapi.controller

import com.guraband.couponapi.dto.CouponIssueResponse
import com.guraband.couponcore.exception.CouponIssueException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CouponControllerAdvice {

    @ExceptionHandler(CouponIssueException::class)
    fun couponIssueExceptionHandler(exception: CouponIssueException) : CouponIssueResponse {
        return CouponIssueResponse(false, exception.errorCode.message)
    }
}