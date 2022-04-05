package com.dubbo.limit;

import com.dubbo.limit.LimitItem;

/**
 * @Auther: Zywoo Lee
 * @Date: 2022/4/1 20:56
 * @Description: 漏桶限流实现类
 */
public class LeakyBucketItem implements LimitItem {

    private String name;

    //每秒处理数(出水率)
    private long rate;

    //当前剩余水量
    private long currentWater;

    //最后刷新时间
    private long refreshTime;

    //桶容量
    private long capacity;

    public LeakyBucketItem(String name, long rate, long currentWater, long refreshTime, long capacity) {
        this.name = name;
        this.rate = rate;
        this.currentWater = currentWater;
        this.refreshTime = refreshTime;
        this.capacity = capacity;
    }

    @Override
    public boolean isAllowed() {
        //当前时间
        long currentTime = System.currentTimeMillis();
        //流出的水量 = (当前时间-上次刷新时间)*出水率
        long outWater = (currentTime - refreshTime) / 1000 * rate;
        //当前水量=原先剩余水量-流出的水量
        currentWater = Math.max(0, currentWater - outWater);
        refreshTime = currentTime;

        //如果当前剩余水量小于桶的容量,则请求放行
        if (currentWater < capacity) {
            currentWater++;
            System.out.println("漏桶限流算法:<接受请求>当前容量:" + capacity + "剩余容量:" + currentWater);
            return true;
        }
        System.out.println("漏桶限流算法:<拒绝请求>当前容量:" + capacity + "剩余容量:" + currentWater);
        return false;


    }

}
