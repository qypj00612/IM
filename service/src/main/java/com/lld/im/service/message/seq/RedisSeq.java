package com.lld.im.service.message.seq;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSeq {

    private final StringRedisTemplate stringRedisTemplate;

    public Long seqIncrement(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

}
