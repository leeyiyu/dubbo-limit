package com.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.filter.tps.TPSLimiter;

/**
 * @Auther: Zywoo Lee
 * @Date: 2022/4/1 20:46
 * @Description: 自定义限流实现
 */
@Activate(group = CommonConstants.PROVIDER, value = "zywoo")
public class ZywooLimitFilter implements Filter {

    //Dubbo默认实现
    //private final TPSLimiter tpsLimiter = new DefaultTPSLimiter();

    //自定义实现
    private final TPSLimiter tpsLimiter = new ZywooTPSLimiter();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (!tpsLimiter.isAllowable(invoker.getUrl(), invocation)) {
            throw new RpcException(
                    "Failed to invoke service " +
                            invoker.getInterface().getName() +
                            "." +
                            invocation.getMethodName() +
                            " because exceed max service tps.");
        }

        return invoker.invoke(invocation);
    }
}
