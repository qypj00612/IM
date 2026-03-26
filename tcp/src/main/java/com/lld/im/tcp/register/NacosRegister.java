package com.lld.im.tcp.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.lld.im.codec.config.BootstrapConfig;
import com.lld.im.common.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@AllArgsConstructor
@Slf4j
public class NacosRegister{

    private NamingService namingService;

    private BootstrapConfig config;

    public void register() {
        try {
            namingService.registerInstance(
                    Constants.NacosConstants.NacosTcpServer
                    , InetAddress.getLocalHost().getHostAddress()
                    , config.getIm().getTcpPort());

            namingService.registerInstance(
                    Constants.NacosConstants.NacosWebServer
                    , InetAddress.getLocalHost().getHostAddress()
                    , config.getIm().getWebSocketPort());
        } catch (Exception e) {
            log.error("服务注册失败");
        }
    }
}
