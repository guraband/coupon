package com.guraband.couponcore.util

class CouponRedisUtil {
    companion object {
        fun getIssueRequestKey(key: String) : String {
            return "issue.request.couponId:$key"
        }

        fun getIssueRequestQueueKey() : String {
            return "issue.request"
        }
    }
}