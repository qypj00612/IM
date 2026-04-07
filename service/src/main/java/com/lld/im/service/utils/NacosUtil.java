package com.lld.im.service.utils;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.lld.im.common.config.AppConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@RequiredArgsConstructor
@Component
@Slf4j
public class NacosUtil {

    private static AppConfig staticAppConfig;

    private final AppConfig appConfig;

    @PostConstruct
    public void init() {
        staticAppConfig = this.appConfig;
    }

    public static List<Instance> getAllImNode(String name){
        List<Instance> imNode = List.of();
        Properties properties = new Properties();
        properties.put("serverAddr", staticAppConfig.getServerAddr());
        properties.put("namespace", staticAppConfig.getNamespace());
        properties.put("username", staticAppConfig.getUsername());
        properties.put("password", staticAppConfig.getPassword());

        try {
            NamingService namingService = NacosFactory.createNamingService(properties);
            imNode = namingService.getAllInstances(name);
        } catch (NacosException e) {
            e.printStackTrace();
            log.error("获取nacos中Im节点出现异常");
        }

        return imNode;
    }

}
