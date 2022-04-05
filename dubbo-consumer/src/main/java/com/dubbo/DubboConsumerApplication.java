package com.dubbo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@EnableDubbo
public class DubboConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboConsumerApplication.class, args);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

//    @DubboReference(
//        version = "${demo.service.version}",
//        group = "testGroup",
//        timeout = 3000,
//        methods = {
//            @Method(name = "sayHello", timeout = 3000)
//        }
//    )
//    private DemoService demoService;

//    @Bean
//    public ApplicationRunner runner() {
//        return args -> {
//            //参数token隐式传递
////            RpcContext.getContext().setAttachment("token","123456");
//            String result = demoService.sayHello("mercyblitz");
//            logger.info(result);
//        };
//    }

}
