package com.guraband.couponapi.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class CouponIssueRequest(
    val userId: Long,
    val couponId: Long,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CouponIssueResponse(
    val isSuccess: Boolean,
    val message: String? = null,
)