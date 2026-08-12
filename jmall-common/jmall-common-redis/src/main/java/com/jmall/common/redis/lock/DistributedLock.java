package com.jmall.common.redis.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁工具
 * <p>
 * 基于 Redisson 实现的分布式锁，支持公平锁、可重入锁、自动续期。
 * 提供模板方法，自动处理加锁/解锁逻辑，避免忘记释放锁。
 * </p>
 *
 * @author jmall
 */
@Component
public class DistributedLock {

    private final RedissonClient redissonClient;

    public DistributedLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取锁（不等待，立即返回）
     *
     * @param lockKey 锁Key
     * @return RLock 实例，需手动调用 lock.unlock()
     */
    public RLock tryGetLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 尝试加锁（指定等待时间和持有时间，自动续期）
     *
     * @param lockKey      锁Key
     * @param waitTime     等待获取锁的最长时间（秒）
     * @param leaseTime    持有锁的最长时间（秒），-1 表示自动续期
     * @return true=加锁成功
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        try {
            RLock lock = redissonClient.getLock(lockKey);
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁Key
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 执行带分布式锁的操作（模板方法）
     * <p>
     * 自动加锁、执行业务、释放锁。推荐使用此方法，避免忘记解锁。
     * </p>
     *
     * @param lockKey   锁Key
     * @param waitTime  等待获取锁的最长时间（秒）
     * @param leaseTime 持有锁的最长时间（秒），-1 表示自动续期
     * @param supplier  业务逻辑
     * @return 业务执行结果
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (!locked) {
                throw new IllegalStateException("获取分布式锁失败: " + lockKey);
            }
            return supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("获取分布式锁被中断: " + lockKey, e);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 执行带分布式锁的无返回值操作
     *
     * @param lockKey   锁Key
     * @param waitTime  等待获取锁的最长时间（秒）
     * @param leaseTime 持有锁的最长时间（秒）
     * @param runnable  业务逻辑
     */
    public void executeWithLock(String lockKey, long waitTime, long leaseTime, Runnable runnable) {
        executeWithLock(lockKey, waitTime, leaseTime, () -> {
            runnable.run();
            return null;
        });
    }

    /**
     * 执行带分布式锁的操作（默认等待10秒，自动续期）
     *
     * @param lockKey  锁Key
     * @param supplier 业务逻辑
     * @return 业务执行结果
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
        return executeWithLock(lockKey, 10L, -1L, supplier);
    }
}
