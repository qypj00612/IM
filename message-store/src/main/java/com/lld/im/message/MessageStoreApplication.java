package com.lld.im.message;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lld.im.message.**.mapper")
public class MessageStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageStoreApplication.class, args);
    }
}
