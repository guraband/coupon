package com.guraband.couponcore.repository

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {

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
}