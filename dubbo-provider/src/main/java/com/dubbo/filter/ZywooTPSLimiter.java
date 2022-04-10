package com.dubbo.filter;

import com.dubbo.limit.GuavaRateLimit;
import com.dubbo.limit.LeakyBucketItem;
import com.dubbo.limit.LimitItem;
import com.dubbo.limit.SlidingWindowItem;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.filter.tps.TPSLimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @Auther: Zywoo Lee
 * @Date: 2022/4/1 21:03
 * @Description:
 */
public class ZywooTPSLimiter implements TPSLimiter {

    private final ConcurrentMap<String, LimitItem> stats = new ConcurrentHashMap<>();

    @Override
    public boolean isAllowable(URL url, Invocation invocation) {
        String serviceKey = url.getServiceKey();
        //选择限流的策略
        String strategy = url.getParameter("strategy");
        //获取限流实现类
        LimitItem limitItem = stats.get(serviceKey);
        if (limitItem == null) {
            switch (strategy) {
                //漏桶限流
                case "leakyBucket": {
                    long rate = url.getParameter("rate", -1L);
                    long capacity = url.getParameter("capacity", -1L);
                    long currentTime = System.currentTimeMillis();
                    stats.putIfAbsent(serviceKey, new LeakyBucketItem(serviceKey, rate, currentTime, capacity));
                    break;
                }
                //滑动窗口限流
                case "slidingWindow": {
                    //限流个数
                    int limitCount = url.getParameter("limitCount", 1);
                    //时间窗数量
                    int sampleCount = url.getParameter("sampleCount", 10);
                    //时间窗长度
                    int intervalInMs = url.getParameter("intervalInMs", 1000);
                    stats.putIfAbsent(serviceKey, new SlidingWindowItem(serviceKey, limitCount, sampleCount, intervalInMs));
                    break;
                }
                //Guava令牌桶限流
                case "guavaRateLimit": {
                    //每秒限流个数
                    double permitsPerSecond = url.getParameter("permitsPerSecond", 1D);
                    stats.putIfAbsent(serviceKey,new GuavaRateLimit(serviceKey,permitsPerSecond));
                    break;
                }
                //无获取到限流算法,直接放行
                default: {
                    return true;
                }
            }
            limitItem = stats.get(serviceKey);
        }
        return limitItem.isAllowed();
    }
}
