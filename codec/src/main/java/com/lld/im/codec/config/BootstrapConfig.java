package com.lld.im.codec.config;

import lombok.*;

@Data
public class BootstrapConfig {
    private TcpConfig im;

    @Data
    public static class TcpConfig{
        private int tcpPort;
        private int webSocketPort;
        private int bossThreadSize;
        private int workerThreadSize;
        private Long hearBeatTime;

        private Integer brokerId;
        private Integer loginModel;
        /**
         * redis配置
         */
        private RedisConfig redis;

        private NacosConfig nacos;

        private RocketMQConfig rocketmq;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RedisConfig{
        /**
         * 单机模式：single 哨兵模式：sentinel 集群模式：cluster
         */
        private String mode;
        /**
         * 数据库
         */
        private Integer database;
        /**
         * 密码
         */
        private String password;
        /**
         * 超时时间
         */
        private Integer timeout;
        /**
         * 最小空闲数
         */
        private Integer poolMinIdle;
        /**
         * 连接超时时间(毫秒)
         */
        private Integer poolConnTimeout;
        /**
         * 连接池大小
         */
        private Integer poolSize;

        /**
         * redis单机配置
         */
        private RedisSingle single;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedisSingle {
        /**
         * 地址
         */
        private String address;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NacosConfig {
        private String address;
        private String namespace;
        private String username;
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RocketMQConfig {
        private String address;
    }
}
