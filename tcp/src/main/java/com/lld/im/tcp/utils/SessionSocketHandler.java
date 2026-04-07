package com.lld.im.tcp.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.ImConnectStatusEnums;
import com.lld.im.common.model.UserClientDto;
import com.lld.im.common.model.UserSession;
import com.lld.im.tcp.Redis.RedisManager;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SessionSocketHandler {

    private static final Map<UserClientDto, NioSocketChannel> socketMap = new ConcurrentHashMap<>();

    public static void put(Integer appId, String userId,Integer clientType, String imei,NioSocketChannel socketChannel) {
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setUserId(userId);
        userClientDto.setAppId(appId);
        userClientDto.setClientType(clientType);
        userClientDto.setImei(imei);

        socketMap.put(userClientDto, socketChannel);
    }

    public static NioSocketChannel get(Integer appId, String userId,Integer clientType, String imei) {
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setImei(imei);
        userClientDto.setUserId(userId);
        userClientDto.setAppId(appId);
        userClientDto.setClientType(clientType);

        return socketMap.get(userClientDto);
    }

    public static void remove(Integer appId, String userId,Integer clientType, String imei) {
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setImei(imei);
        userClientDto.setUserId(userId);
        userClientDto.setAppId(appId);
        userClientDto.setClientType(clientType);

        socketMap.remove(userClientDto);
    }

    public static void remove(NioSocketChannel socketChannel) {
        socketMap.entrySet().removeIf(entry -> entry.getValue() == socketChannel);
    }

    public static void loginOut(NioSocketChannel socketChannel) {
        String userId = (String) socketChannel.attr(AttributeKey.valueOf(Constants.UserId)).get();
        Integer appId = (Integer) socketChannel.attr(AttributeKey.valueOf(Constants.AppId)).get();
        Integer clientType = (Integer) socketChannel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
        String imei = (String) socketChannel.attr(AttributeKey.valueOf(Constants.imei)).get();
        remove(appId,userId,clientType,imei);
        log.info("{} {} {}",userId,appId,clientType);

        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(appId
                + Constants.RedisConstants.UserSessionConstant + userId);
        map.remove(clientType+":"+imei);
        socketChannel.close();
    }

    public static void offLineUserSession(NioSocketChannel channel) {
        String userId = (String) channel.attr(AttributeKey.valueOf(Constants.UserId)).get();
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(Constants.AppId)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
        String imei = (String) channel.attr(AttributeKey.valueOf(Constants.imei)).get();
        remove(appId,userId,clientType,imei);
        log.info("{} {} {} {}",userId,appId,clientType,imei);

        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient.getMap(appId
                + Constants.RedisConstants.UserSessionConstant + userId);
        String s = map.get(clientType+":"+imei);
        if(StrUtil.isNotBlank(s)){
            UserSession userSession = JSONObject.parseObject(s, UserSession.class);
            userSession.setConnectState(ImConnectStatusEnums.OFF_LINE.getCode());
            map.put(clientType+":"+imei, JSONObject.toJSONString(userSession));
        }
        channel.close();
    }

    public static List<NioSocketChannel> get(Integer appId, String userId) {
        Set<UserClientDto> userClientDtos = socketMap.keySet();
        List<NioSocketChannel> channels = new ArrayList<>();
        for (UserClientDto userClientDto : userClientDtos) {
            if(userClientDto.getUserId().equals(userId)&&userClientDto.getAppId().equals(appId)) {
                channels.add(socketMap.get(userClientDto));
            }
        }
        return channels;
    }
}
