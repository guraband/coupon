package com.guraband.couponcore.util

class CouponRedisUtil {
    companion object {
        fun getIssueRequestKey(key: Long) : String {
            return "issue.request.couponId:$key"
        }

        fun getIssueRequestQueueKey() : String {
            return "issue.request"
        }
    }
}