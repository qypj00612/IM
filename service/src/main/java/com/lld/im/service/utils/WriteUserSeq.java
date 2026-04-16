package com.lld.im.service.utils;

import com.lld.im.common.constant.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WriteUserSeq {

    private final RedisTemplate redisTemplate;

    public void writeUserSeq(Integer appId, String userId, String type, Long seq) {
        String key = appId + Constants.RedisConstants.UserSeq + userId;
        redisTemplate.opsForHash().put(key, type, seq);
    }

}
