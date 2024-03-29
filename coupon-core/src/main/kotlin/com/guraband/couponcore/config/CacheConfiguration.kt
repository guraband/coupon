package com.guraband.couponcore.config

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration


@Configuration
class CacheConfiguration(
    private val redisConnectionFactory: RedisConnectionFactory
) {
    companion object {
        const val CACHE_60SECONDS = "cache60s"
        const val CACHE_10MINUTES = "cache10m"
    }

    @Bean
    fun redisCacheManager(): CacheManager {
        val redisCacheConfigMap : Map<String, RedisCacheConfiguration> = mapOf(
            CACHE_60SECONDS to createCacheConfig(60),
            CACHE_10MINUTES to createCacheConfig(60 * 10),
        )

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
            .withInitialCacheConfigurations(redisCacheConfigMap)
            .build()
    }

    private fun createCacheConfig(seconds:Long) : RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJackson2JsonRedisSerializer(objectMapper())
                )
            )
            .entryTtl(Duration.ofSeconds(seconds))
    }

    /*
        참고 : Spring에서 Redis 동적 데이터 저장하기 ( https://monny.tistory.com/263 )
     */
    private fun objectMapper(): ObjectMapper {
        val ptv: PolymorphicTypeValidator = BasicPolymorphicTypeValidator
            .builder()
            .allowIfSubType(Any::class.java)
            .build()
        return jacksonObjectMapper()
            .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.EVERYTHING)
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
            .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(JavaTimeModule())   // jdk8 LocalDateTime 호환
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)    // LocalDateTime이 배열형태로 저장되는 것을 방지
    }
}