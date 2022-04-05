package com.dubbo.limit.slidingWindow;


import com.dubbo.limit.LimitItem;
import com.dubbo.limit.slidingWindow.WindowWrap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Auther: Zywoo Lee
 * @Date: 2022/4/2 15:57
 * @Description: 滑动窗口限流算法
 */
public class LeapArray implements LimitItem {
    // 样本窗口长度
    private int windowLengthInMs;
    // 一个时间窗中包含的时间窗数量
    private int sampleCount;
    // 时间窗长度
    private int intervalInMs;
    //元素为WindowWrap样本窗口
    private final AtomicReferenceArray<WindowWrap> array;

    //修改锁
    private final ReentrantLock updateLock = new ReentrantLock();

    /**
     *
     * 功能描述: 构造函数
     *
     * @param: sampleCount 时间窗数量 intervalInMs 时间窗长度
     * @return:
     * @auther: Zywoo Lee
     * @date: 2022/4/5 14:19
     */
    public LeapArray(int sampleCount, int intervalInMs){
        this.windowLengthInMs = intervalInMs / sampleCount;
        this.intervalInMs = intervalInMs;
        this.sampleCount = sampleCount;
        this.array = new AtomicReferenceArray(sampleCount);
    }

    public WindowWrap currentWindow(){
        // 获取当前时间点所在的样本窗口
        return currentWindow(System.currentTimeMillis());
    }


    @Override
    public boolean isAllowed() {
        //获取当前时间段在哪个滑动窗口

        return true;
    }


    public WindowWrap currentWindow(long timeMillis){
        if (timeMillis < 0) {
            return null;
        }

        // 计算当前时间所在的样本窗口id，即在计算数组LeapArray中的索引
        int idx = calculateTimeIdx(timeMillis);

        // 计算当前样本窗口的开始时间点
        long windowStart = calculateWindowStart(timeMillis);

        while(true){
            WindowWrap old = array.get(idx);
            // 若当前时间所在样本窗口为null，说明该样本窗口还不存在，则创建一个
            if (old==null){
                WindowWrap window = new WindowWrap(windowLengthInMs, windowStart,new LongAdder());
                if (array.compareAndSet(idx,null,window)){
                    return window;
                }else{
                    Thread.yield();
                }
            }
            // 若当前样本窗口的起始时间点与计算出的样本窗口起始时间点相同，
            // 则说明这两个是同一个样本窗口
            else if(windowStart==old.windowStart()){
                return old;
            }
            // 若当前样本窗口的起始时间点 大于 计算出的样本窗口起始时间点，
            // 说明计算出的样本窗口已经过时了，需要将原来的样本窗口替换
            else if (windowStart > old.windowStart()){
                if (updateLock.tryLock()) {
                    try {
                        // 替换掉老的样本窗口
                        return resetWindowTo(old, windowStart);
                    } finally {
                        updateLock.unlock();
                    }
                } else {
                    // Contention failed, the thread will yield its time slice to wait for bucket available.
                    Thread.yield();
                }
            }
            // 当前样本窗口的起始时间点 小于 计算出的样本窗口起始时间点，
            // 这种情况一般不会出现，因为时间不会倒流。除非人为修改了系统时钟
            else if (windowStart < old.windowStart()){
                return new WindowWrap(windowLengthInMs, windowStart,new LongAdder());
            }
        }
    }


    public int calculateTimeIdx(long timeMillis) {
        // 计算出当前时间在哪个样本窗口
        long timeId = timeMillis / windowLengthInMs;
        // Calculate current index so we can map the timestamp to the leap array.
        return (int)(timeId % array.length());
    }

    private long calculateWindowStart(long timeMillis) {
        return timeMillis - timeMillis % windowLengthInMs;
    }


    private WindowWrap resetWindowTo(WindowWrap w, long startTime) {
        System.out.println("重置时间窗");
        // 更新窗口起始时间
        w.resetTo(startTime);
        // 将访问次数数据清零
        w.value().reset();
        return w;
    }


    public List values() {
        return values(System.currentTimeMillis());
    }

    public List values(long timeMillis) {
        if (timeMillis < 0) {
            return new ArrayList();
        }
        int size = array.length();
        List result = new ArrayList(size);

        // 逐个遍历array中的每一个样本窗口实例
        for (int i = 0; i < size; i++) {
            WindowWrap windowWrap = array.get(i);
            // 若当前遍历实例为空或已经过时，则继续下一个
            if (windowWrap == null || isWindowDeprecated(timeMillis, windowWrap)) {
                continue;
            }
            // 将当前遍历的样本窗口统计的数据记录到result中
            result.add(windowWrap.value());
        }
        return result;
    }

    public boolean isWindowDeprecated(long time, WindowWrap windowWrap) {
        // 当前时间与当前样本窗口的时间差 大于 时间窗长度，
        // 说明当前样本窗口已经过时
        return time - windowWrap.windowStart() > intervalInMs;
    }

    public int getWindowLengthInMs() {
        return windowLengthInMs;
    }
}
