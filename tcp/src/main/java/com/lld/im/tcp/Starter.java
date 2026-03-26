package com.lld.im.tcp;


import com.alibaba.nacos.api.naming.NamingService;
import com.lld.im.codec.config.BootstrapConfig;
import com.lld.im.tcp.Redis.RedisManager;
import com.lld.im.tcp.receiver.MqMessageReceiver;
import com.lld.im.tcp.register.NacosManager;
import com.lld.im.tcp.register.NacosRegister;
import com.lld.im.tcp.server.ImWebServer;
import com.lld.im.tcp.server.ImServer;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;

public class Starter {
    public static void main(String[] args) {
        if (args.length >0) {
            start(args[0]);
        }
    }

    public static void start(String path){
        try {
            Yaml yaml = new Yaml();
            FileInputStream fileInputStream = new FileInputStream(path);
            BootstrapConfig bootstrapConfig = yaml.loadAs(fileInputStream, BootstrapConfig.class);

            RedisManager.init(bootstrapConfig);

            nacosRegister(bootstrapConfig);

            MqMessageReceiver.startReceiver(bootstrapConfig);

            new ImServer(bootstrapConfig.getIm()).start();
            new ImWebServer(bootstrapConfig.getIm()).start();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(500);
        }
    }

    private static void nacosRegister(BootstrapConfig bootstrapConfig) {
        NacosManager nacosManager = new NacosManager();
        nacosManager.create(bootstrapConfig);
        NamingService namingService = nacosManager.getNamingService();
        NacosRegister nacosRegister = new NacosRegister(namingService, bootstrapConfig);
        nacosRegister.register();
    }
}
