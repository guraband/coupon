package com.guraband.couponcore.model

import com.guraband.couponcore.enums.CouponType
import com.guraband.couponcore.enums.ErrorCode
import com.guraband.couponcore.exception.CouponIssueException
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Coupon(
    val title: String = "",

    @Column(columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    val couponType: CouponType = CouponType.FIRST_COME_FIRST_SERVE,

    private val totalQuantity: Int? = null,

    @Column(name = "issuedQuantity")
    private var _issuedQuantity: Int = 0,

    val discountAmount: Int = 0,

    val minAvailableAmount: Int = 0,

    val dateIssueStart: LocalDateTime? = null,

    val dateIssueEnd: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity() {

    val issuedQuantity: Int
        get() = _issuedQuantity

    fun availableIssueQuantity(): Boolean {
        if (totalQuantity == null) {
            return true
        }
        return issuedQuantity < totalQuantity
    }

    fun availableIssueDate(): Boolean {
        val now = LocalDateTime.now()

        if (dateIssueStart != null && dateIssueStart.isAfter(now)) {
            return false
        }

        if (dateIssueEnd != null && dateIssueEnd.isBefore(now)) {
            return false
        }

        return true
    }

    fun issue() {
        if (!availableIssueQuantity()) {
            throw CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY, "수량 : $issuedQuantity / $totalQuantity")
        }

        if (!availableIssueDate()) {
            throw CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_DATE, "발급 기한 : $dateIssueStart ~ $dateIssueEnd")
        }

        _issuedQuantity++
    }
}