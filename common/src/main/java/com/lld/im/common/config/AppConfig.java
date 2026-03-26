package com.lld.im.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "nacos")
public class AppConfig {
    private String address;
    private String namespace;
    private String username;
    private String password;
}
