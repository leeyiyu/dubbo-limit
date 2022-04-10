package com.dubbo.limit;

import com.google.common.util.concurrent.RateLimiter;

/**
 * @Auther: Zywoo Lee
 * @Date: 2022/4/10 15:00
 * @Description: Guava令牌桶限流算法
 */
public class GuavaRateLimit implements LimitItem{

    /**
     * 服务名称
     */
    String name;


    /**
     * Guava限流实现
     */
    RateLimiter limit;

    /**
     *
     * @param name 服务名称
     * @param permitsPerSecond 每秒限制通过数
     */
    public GuavaRateLimit(String name,double permitsPerSecond) {
        this.name = name;
        limit = RateLimiter.create(permitsPerSecond);
    }

    @Override
    public boolean isAllowed() {
        return limit.tryAcquire();
    }
}
