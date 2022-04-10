package com.dubbo.limit;

import cn.hutool.core.date.DateUtil;
import com.dubbo.limit.slidingWindow.LeapArray;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;


/**
 * @Auther: Zywoo Lee
 * @Date: 2022/4/5 14:39
 * @Description:滑动窗口限流实现类
 */
public class SlidingWindowItem implements LimitItem {

    /**
     * 服务名称
     */
    private String name;


    /**
     * 限流次数
     */
    private int limitCount;

    /**
     * 时间窗
     */
    private final LeapArray data;

    /**
     * @param limitCount   限流次数
     * @param sampleCount  时间窗数量
     * @param intervalInMs 时间窗长度
     */
    public SlidingWindowItem(String name, int limitCount, int sampleCount, int intervalInMs) {
        this.name = name;
        this.limitCount = limitCount;
        this.data = new LeapArray(sampleCount, intervalInMs);
    }

    @Override
    public boolean isAllowed() {
        if (canPass()) {
            add(1);
            return true;
        }
        return false;
    }

    public boolean canPass() {
        //return getSum() + 1 <= limitCount;

        //打印调试用
        long sum = getSum();
        boolean canPass = getSum() + 1 <= limitCount;

        //时间窗下标 10为时间窗数量
        long idx = ((long) System.currentTimeMillis() / data.getWindowLengthInMs()) % 10;
        System.out.println("[" + idx + "]时间窗访问数:" + data.currentWindow().value().intValue() + ","
                + "访问总数:" + sum + ","
                + (canPass ? "<通过>" : "<拒绝>")
                + "当前时间:" + DateUtil.now());
        return canPass;
    }

    public void add(int x) {
        data.currentWindow().value().add(x);
    }


    public long getSum() {
        data.currentWindow();
        long success = 0;

        List<LongAdder> list = data.values();
        for (LongAdder window : list) {
            success += window.sum();
        }
        return success;
    }

}
