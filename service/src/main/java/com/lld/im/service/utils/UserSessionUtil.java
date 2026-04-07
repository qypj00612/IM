package com.lld.im.service.utils;

import com.alibaba.fastjson.JSONObject;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.ImConnectStatusEnums;
import com.lld.im.common.model.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserSessionUtil {

    private final StringRedisTemplate stringRedisTemplate;

    // 1. 获取用户的所有session
    public List<UserSession> getUserSession(Integer appId, String userId) {
        List<UserSession> userSessions = new ArrayList<>();

        String key = appId + Constants.RedisConstants.UserSessionConstant + userId;
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        Collection<Object> values = entries.values();

        for (Object value : values) {
            UserSession userSession = JSONObject.parseObject(value.toString(), UserSession.class);
            if(Objects.equals(userSession.getConnectState(), ImConnectStatusEnums.ON_LINE.getCode())){
                userSessions.add(userSession);
            }
        }
        return userSessions;
    }
    // 2. 获取除了本端的所有session




    public UserSession getUserSession(Integer appId, String userId, Integer clientType, String imei) {
        String key = appId + Constants.RedisConstants.UserSessionConstant + userId;
        Object o = stringRedisTemplate.opsForHash().get(key, clientType + ":" + imei);
        UserSession userSession = JSONObject.parseObject(o.toString(), UserSession.class);
        return userSession;
    }

}
