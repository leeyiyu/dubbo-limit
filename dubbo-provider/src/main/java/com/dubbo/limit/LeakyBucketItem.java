package com.dubbo.limit;


import java.util.concurrent.atomic.LongAdder;

/**
 * @Auther: Zywoo Lee
 * @Date: 2022/4/1 20:56
 * @Description: 漏桶限流实现类
 */
public class LeakyBucketItem implements LimitItem {

    /**
     * 服务名称
     */
    private String name;

    /**
     * 每秒处理数(出水率)
     */
    private long rate;

    /**
     * 当前剩余水量
     */
    private LongAdder currentWater;

    /**
     * 最后刷新时间
     */
    private long refreshTime;

    /**
     * 桶容量
     */
    private long capacity;

    public LeakyBucketItem(String name, long rate, long refreshTime, long capacity) {
        this.name = name;
        this.rate = rate;
        this.currentWater =  new LongAdder();
        this.refreshTime = refreshTime;
        this.capacity = capacity;
    }

    @Override
    public boolean isAllowed() {
        //当前时间
        long currentTime = System.currentTimeMillis();
        //流出的水量 = (当前时间-上次刷新时间)*出水率
        long outWater = (currentTime - refreshTime) / 1000 * rate;
        if (currentWater.longValue()<outWater){
            currentWater.reset();
        }else{
            //当前水量=原先剩余水量-流出的水量
            currentWater.add(-outWater);
        }


        //如果当前剩余水量小于桶的容量,则请求放行
        if (currentWater.longValue() < capacity) {
            currentWater.add(1);
            refreshTime = currentTime;
            System.out.println("漏桶限流算法:<接受请求>当前容量:" + capacity + "剩余容量:" + currentWater.longValue());
            return true;
        }
        System.out.println("漏桶限流算法:<拒绝请求>当前容量:" + capacity + "剩余容量:" + currentWater.longValue());
        return false;


    }

}
