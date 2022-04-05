package com.dubbo.limit.slidingWindow;

import java.util.concurrent.atomic.LongAdder;

/**
 * @Auther: Zywoo Lee
 * @Date: 2022/4/5 14:09
 * @Description:
 */
public class WindowWrap {

    /**
     * 样本窗口长度
     */
    private final long windowLengthInMs;

    /**
     * 样本窗口的起始时间戳
     */
    private long windowStart;

    /**
     * 当前样本窗口中的统计数据
     */
    private LongAdder value;


    public WindowWrap(long windowLengthInMs, long windowStart,LongAdder value) {
        this.windowLengthInMs = windowLengthInMs;
        this.windowStart = windowStart;
        this.value = value;
    }

    /**
     *
     * 功能描述:重置设置此窗口的开始时间戳
     *
     * @param:
     * @return:
     * @auther: Zywoo Lee
     * @date: 2022/4/5 14:11
     */
    public WindowWrap resetTo(long startTime){
        this.windowStart = startTime;
        return this;
    }

    /**
     *
     * 功能描述:判断时间戳是否在此时间窗口内
     *
     * @param:
     * @return:
     * @auther: Zywoo Lee
     * @date: 2022/4/5 14:11
     */
    public boolean isTimeInWindow(long timeMillis) {
        return windowStart <= timeMillis && timeMillis < windowStart + windowLengthInMs;
    }


    public long windowStart() {
        return windowStart;
    }


    public LongAdder value() {
        return value;
    }


}
