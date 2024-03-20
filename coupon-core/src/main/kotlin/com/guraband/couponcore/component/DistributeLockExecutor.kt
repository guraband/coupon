package com.guraband.couponcore.component

import io.github.oshai.kotlinlogging.KotlinLogging
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

@Component
class DistributeLockExecutor(
    private val redissonClient: RedissonClient
) {
    fun execute(lockName: String, waitMilliSecond: Long, leaseMilliSecond: Long, logic: Runnable) {
        val lock = redissonClient.getLock(lockName)
        try {
            val isLocked = lock.tryLock(waitMilliSecond, leaseMilliSecond, TimeUnit.MILLISECONDS)
            if (!isLocked) {
                throw IllegalStateException("[$lockName] lock 획득 실패")
            }
            logic.run()
        } catch (e: InterruptedException) {
            logger.error(e) { "${e.message}" }
            throw RuntimeException(e)
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}