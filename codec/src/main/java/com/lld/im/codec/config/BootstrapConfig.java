package com.lld.im.codec.config;

import lombok.Data;

@Data
public class BootstrapConfig {
    private TcpConfig im;

    @Data
    public static class TcpConfig{
        private int tcpPort;
        private int webSocketPort;
        private int bossThreadSize;
        private int workerThreadSize;
    }
}
