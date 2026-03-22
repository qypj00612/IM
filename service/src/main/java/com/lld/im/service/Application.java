package com.lld.im.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.lld.im.service.**.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        // 验证 UserDataController 是否注册
        boolean hasController = context.containsBean("userController");
        System.out.println("UserDataController 注册状态：" + hasController);
        // 输出 true → 正常；输出 false → 包路径错误，需调整 Controller 包名
    }
}
