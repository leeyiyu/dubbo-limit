package com.dubbo.controller;

import com.dubbo.api.DemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: Zywoo Lee
 * @Date: 2021/11/20 15:35
 * @Description:
 */
@RestController
@RequestMapping("/test")
@Api(tags = "Dubbo服务测试类")
public class DemoController {

    @DubboReference(timeout = 1000000,retries=0)
    private DemoService demoService;

    @ApiOperation(value = "服务调用测试", notes = "")
    @GetMapping("sayHello")
    public String sayHello() {
        String name = demoService.sayHello("ZywooLee-debug");
        return name;
    }


}
