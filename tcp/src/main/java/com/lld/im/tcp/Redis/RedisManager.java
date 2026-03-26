package com.lld.im.tcp.Redis;


import com.lld.im.codec.config.BootstrapConfig;
import org.redisson.api.RedissonClient;

public class RedisManager {
    private static RedissonClient redisson;
    public static void init(BootstrapConfig config){
        SingleClientStrategy singleClientStrategy = new SingleClientStrategy();
        redisson = singleClientStrategy.getRedissonClient(config.getIm().getRedis());
    }

    public static RedissonClient getRedissonClient(){
        return redisson;
    }
}
