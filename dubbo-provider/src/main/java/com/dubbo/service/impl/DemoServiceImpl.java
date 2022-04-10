/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dubbo.service.impl;

import com.dubbo.api.DemoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;


/**
 * 功能描述:
 *
 * @param:
 * @return:
 * @auther: Zywoo Lee
 * @date: 2021/11/20 3:02 下午
 */
//选择Dubbo固定窗口限流算法
//@DubboService(filter = {"tps"},parameters = {"tps","2","tps.interval","1000"})
//选择漏桶限流算法
//@DubboService(filter = {"zywoo"},parameters = {"strategy","leakyBucket","rate","2","capacity","10"})
//选择滑动窗口限流算法(时间窗切分数量10个,时间窗长度10000(10s),代表10s只能有5个通过)
//@DubboService(filter = {"zywoo"},parameters = {"strategy","slidingWindow","limitCount","5","sampleCount","10","intervalInMs","10000"})
//选择Guava令牌桶限流算法
@DubboService(filter = "zywoo",parameters = {"strategy","guavaRateLimit","permitsPerSecond","2"})
public class DemoServiceImpl implements DemoService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * The default value of ${dubbo.application.name} is ${spring.application.name}
     */
    @Value("${dubbo.application.name}")
    private String serviceName;

    @Override
    public String sayHello(String name) {
        return String.format("[%s] : Hello, %s", serviceName, name);
    }


}