package com.guraband.couponcore.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.guraband.couponcore.enums.CouponType
import com.guraband.couponcore.enums.ErrorCode
import com.guraband.couponcore.exception.CouponIssueException
import java.time.LocalDateTime

class CouponRedisEntity(
    coupon: Coupon
) {
    val id = coupon.id!!
    val couponType = coupon.couponType
    val totalQuantity = coupon.totalIssueQuantity

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val dateIssueStart: LocalDateTime? = coupon.dateIssueStart

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val dateIssueEnd: LocalDateTime? = coupon.dateIssueEnd

    private fun availableIssueDate(): Boolean {
        val now = LocalDateTime.now()
        return (dateIssueStart == null || dateIssueStart.isBefore(now))
                && (dateIssueEnd == null || dateIssueEnd.isAfter(now))
    }

    fun checkIssuableCoupon() {
        if (!availableIssueDate()) {
            throw CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_DATE, "발급 기한 : $dateIssueStart ~ $dateIssueEnd")
        }
    }
}
