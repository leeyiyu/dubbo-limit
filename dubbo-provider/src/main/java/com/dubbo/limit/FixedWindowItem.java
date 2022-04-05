package com.dubbo.limit;

/**
 * @Auther: Zywoo Lee
 * @Date: 2022/4/1 21:48
 * @Description: 固定窗口限流算法
 */
public class FixedWindowItem implements LimitItem {

    private String name;

    //滑动频率 单位:毫秒
    private long windowUnit;

    //最后刷新时间
    private long refreshTime;

    //计算器
    private int counter;

    //窗口访问阈值
    private int threshold;

    public FixedWindowItem(String name, long windowUnit, long refreshTime, int counter, int threshold) {
        this.name = name;
        this.windowUnit = windowUnit;
        this.refreshTime = refreshTime;
        this.counter = counter;
        this.threshold = threshold;
    }

    @Override
    public boolean isAllowed() {
        //获取当前系统时间
        long currentTime = System.currentTimeMillis();
        if (currentTime - refreshTime > windowUnit) {
            counter = 0;
            refreshTime = currentTime;
        }
        //小于阈值
        if (counter < threshold) {
            counter++;
            System.out.println("固定滑动窗口算法:<接受请求>窗口访问次数:" + counter);
            return true;
        }
        System.out.println("固定滑动窗口算法:<拒绝请求>窗口访问次数:" + counter);
        return false;
    }
}
