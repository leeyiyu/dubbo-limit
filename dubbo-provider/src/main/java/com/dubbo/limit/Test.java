package com.dubbo.limit;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * @Auther: Zywoo Lee
 * @Date: 2022/4/5 22:21
 * @Description: 测试类
 */
public class Test {


    /**
     *
     * 功能描述:滑动窗口算法测试
     *
     * @param:
     * @return:
     * @auther: Zywoo Lee
     * @date: 2022/4/6 09:08
     */
    @org.junit.Test
    public void SlidingWindowTest() throws InterruptedException {
        //时间窗长度为1s,分割为10个滑动窗口,时间窗内只能有150个放行
        LimitItem slidingWindowItem = new SlidingWindowItem("SlidingWindowTest",150,10,1000);
        AtomicInteger allowed = new AtomicInteger(0);
        AtomicInteger limited = new AtomicInteger(0);
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < 2000; i++) {
            new Thread(() -> {
                //System.out.println(Thread.currentThread().getName()+":"+leakyBucketItem.isAllowed());
                if (slidingWindowItem.isAllowed()) {
                    allowed.addAndGet(1);
                } else {
                    limited.addAndGet(1);
                }
            }, i + "").start();
            //每20次休眠100毫秒
            if (i % 20 == 0) {
                Thread.sleep(100);
            }
        }
        long endTime = System.currentTimeMillis();
        //等待1s让所有线程都请求完再展示结果
        Thread.sleep(1000);
        System.out.println("执行时间:" + (endTime - beginTime) + "毫秒");
        System.out.println("限制次数:" + limited.get() + "通过次数:" + allowed.get());
    }


    /**
     * 功能描述:漏桶算法测试
     *
     * @param:
     * @return:
     * @auther: Zywoo Lee
     * @date: 2022/4/5 22:29
     */
    @org.junit.Test
    public void LeakyBucketTest() throws InterruptedException {
        // 桶容量10,每秒流出速率5
        LimitItem leakyBucketItem = new LeakyBucketItem("ZywooTest", 5, System.currentTimeMillis(), 10);
        AtomicInteger allowed = new AtomicInteger(0);
        AtomicInteger limited = new AtomicInteger(0);
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                //System.out.println(Thread.currentThread().getName()+":"+leakyBucketItem.isAllowed());
                if (leakyBucketItem.isAllowed()) {
                    allowed.addAndGet(1);
                } else {
                    limited.addAndGet(1);
                }
            }, i + "").start();
            //每50次休眠500毫秒
            if (i % 50 == 0) {
                Thread.sleep(500);
            }
        }
        long endTime = System.currentTimeMillis();
        //等待1s让所有线程都请求完再展示结果
        Thread.sleep(1000);
        System.out.println("执行时间:" + (endTime - beginTime) + "毫秒");
        System.out.println("限制次数:" + limited.get() + "通过次数:" + allowed.get());

    }


    @org.junit.Test
    public void GuavaRateLimitTest(){
        long num =  NANOSECONDS.toMicros(100L);

        //服务每秒允许的TPS
        RateLimiter limit = RateLimiter.create(0.2);
        //limit.tryAcquire()
        while (true) {
            System.out.println("get 1 tokens: " + limit.acquire(1) + "s");
        }
    }



}
