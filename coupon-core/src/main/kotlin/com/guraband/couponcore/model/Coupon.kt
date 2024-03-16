package com.guraband.couponcore.model

import com.guraband.couponcore.enums.CouponType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Coupon(
    val title: String = "",

    @Column(columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    val couponType: CouponType = CouponType.FIRST_COME_FIRST_SERVE,

    private val totalQuantity: Int? = null,

    private var issuedQuantity: Int = 0,

    val discountAmount: Int = 0,

    val minAvailableAmount: Int = 0,

    val dateIssueStart: LocalDateTime? = null,

    val dateIssueEnd: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity() {

    fun getIssuesQuantity() : Int {
        return issuedQuantity
    }

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
            throw RuntimeException("수량 오류")
        }

        if (!availableIssueDate()) {
            throw RuntimeException("날짜 오류")
        }

        issuedQuantity++
    }
}