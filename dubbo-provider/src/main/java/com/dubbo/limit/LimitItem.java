package com.dubbo.limit;

/**
 * @Auther: Zywoo Lee
 * @Date: 2022/4/1 21:11
 * @Description: 限流接口
 */
public interface LimitItem {

    boolean isAllowed();

}
