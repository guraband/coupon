package com.guraband.couponcore.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedisConfiguration(
) {
    @Value("\${spring.data.redis.host}")
    private val host : String? = null

    @Value("\${spring.data.redis.port}")
    private val port : Int? = null

    @Bean
    fun redissonClient() : RedissonClient {
        val config = Config()
        val address = "redis://$host:$port"
        config.useSingleServer().address = address
        return Redisson.create(config)
    }
}