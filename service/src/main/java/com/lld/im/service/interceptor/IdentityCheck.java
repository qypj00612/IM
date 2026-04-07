package com.lld.im.service.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.config.AppConfig;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.ApplicationExceptionEnum;
import com.lld.im.common.enums.BaseCode;
import com.lld.im.common.enums.GateWayErrorCode;
import com.lld.im.common.utils.SigAPI;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdentityCheck {

    private final AppConfig appConfig;

    private final StringRedisTemplate stringRedisTemplate;

    public ApplicationExceptionEnum checkUserSign(String appId, String userId, String sign){

        String key = appId + Constants.RedisConstants.UserSign + userId + sign;
        String cacheTime = stringRedisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(cacheTime) && Long.parseLong(cacheTime) > System.currentTimeMillis()/1000){
            return BaseCode.SUCCESS;
        }

        String privateKey = appConfig.getPrivateKey();
        JSONObject jsonObject = SigAPI.decodeUserSig(sign);
        Long expireTime = 0L;
        Long expireSec = 0L;
        String decoderAppId = "";
        String decoderUserId = "";
        try {
            decoderUserId = jsonObject.getString("TLS.operator");
            decoderAppId = jsonObject.getString("TLS.appId");
            String expireTimeStr = jsonObject.getString("TLS.expireTime");
            String expireSecStr = jsonObject.getString("TLS.expire");
            expireSec = Long.parseLong(expireSecStr);
            expireTime = Long.parseLong(expireTimeStr)+expireSec;
        }catch (Exception e){
            e.printStackTrace();
            log.error("checkSign error:{}",e.getMessage());
        }
        if(!appId.equals(decoderAppId)){
            return GateWayErrorCode.USERSIGN_IS_ERROR;
        }
        if(!userId.equals(decoderUserId)){
            return GateWayErrorCode.USERSIGN_OPERATE_NOT_MATE;
        }
        if(expireTime < System.currentTimeMillis()/1000){
            return GateWayErrorCode.USERSIGN_IS_EXPIRED;
        }

        Long time = expireTime - System.currentTimeMillis()/1000;
        stringRedisTemplate.opsForValue().set(key,expireTime.toString(),time, TimeUnit.SECONDS);

        return BaseCode.SUCCESS;
    }
}
