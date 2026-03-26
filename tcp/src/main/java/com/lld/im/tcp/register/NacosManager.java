package com.lld.im.tcp.register;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.resolver.InetNameResolver;
import com.lld.im.codec.config.BootstrapConfig;
import com.lld.im.common.constant.Constants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.util.Properties;

@Slf4j
public class NacosManager {

    @Getter
    private NamingService namingService;

    public void create(BootstrapConfig config){
        Properties properties = new Properties();
        properties.put("serverAddr", config.getIm().getNacos().getAddress());
        properties.put("namespace", config.getIm().getNacos().getNamespace());
        properties.put("username", config.getIm().getNacos().getUsername());
        properties.put("password", config.getIm().getNacos().getPassword());

        try {
            namingService = NacosFactory.createNamingService(properties);
        } catch (NacosException e) {
            log.error("nacos实例注册失败");
        }

    }

}
