package com.guraband.couponcore.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.guraband.couponcore.enums.CouponIssueResultCode
import com.guraband.couponcore.enums.ErrorCode
import com.guraband.couponcore.exception.CouponIssueException
import com.guraband.couponcore.model.CouponIssueRequest
import com.guraband.couponcore.util.CouponRedisUtil.Companion.getIssueRequestKey
import com.guraband.couponcore.util.CouponRedisUtil.Companion.getIssueRequestQueueKey
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Repository

private val logger = KotlinLogging.logger {}

@Repository
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private val issueScript = issueRedisScript()
    private val issueRequestQueueKey = getIssueRequestQueueKey()

    fun zAdd(key: String, value: String, score: Double): Boolean? {
        return redisTemplate.opsForZSet().addIfAbsent(key, value, score)
    }

    fun sAdd(key: String, value: String): Long? {
        return redisTemplate.opsForSet().add(key, value)
    }

    fun sCard(key: String): Long? {
        return redisTemplate.opsForSet().size(key)
    }

    fun sIsMember(key: String, value: String): Boolean? {
        return redisTemplate.opsForSet().isMember(key, value)
    }

    fun rPush(key: String, value: String): Long? {
        return redisTemplate.opsForList().rightPush(key, value)
    }

    fun lSize(key: String): Long? {
        return redisTemplate.opsForList().size(key)
    }

    fun lIndex(key: String, index: Long): String? {
        return redisTemplate.opsForList().index(key, index)
    }

    fun lPop(key: String): String? {
        return redisTemplate.opsForList().leftPop(key)
    }

    fun issueRequest(couponId: Long, userId: Long, totalIssueQuantity: Int?) {
        try {
            val issueRequestKey = getIssueRequestKey(couponId)
            val issueRequest = CouponIssueRequest(couponId, userId)
            val value = jacksonObjectMapper().writeValueAsString(issueRequest)

            val resultCode = redisTemplate.execute(
                issueScript,
                listOf(issueRequestKey, issueRequestQueueKey),
                userId.toString(), totalIssueQuantity.toString(), value
            )

            CouponIssueResultCode.validate(CouponIssueResultCode.find(resultCode))
        } catch (ce: CouponIssueException) {
            throw ce
        } catch (e: Exception) {
            logger.error(e) { "${e.message}" }
            throw CouponIssueException(
                ErrorCode.FAIL_COUPON_ISSUE_REQUEST,
                "쿠폰 발급에 실패했습니다. couponId : $couponId / userId : $userId"
            )
        }
    }

    private fun issueRedisScript(): RedisScript<String> {
        val script = """
            if redis.call('SISMEMBER', KEYS[1], ARGV[1]) == 1 then
                return '2'
            end
            
            if tonumber(ARGV[2]) > redis.call('SCARD', KEYS[1]) then
                redis.call('SADD', KEYS[1], ARGV[1])
                redis.call('RPUSH', KEYS[2], ARGV[3])
                return '1'
            end
            
            return '3'
        """
        return RedisScript.of(script, String::class.java)
    }
}