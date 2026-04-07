package com.lld.im.service.utils;

import com.lld.im.common.ResponseVO;
import com.lld.im.common.config.AppConfig;
import com.lld.im.common.utils.HttpRequestUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CallBackUtil {
    private final HttpRequestUtils requestUtils;
    private final AppConfig appConfig;

    public void callBack(Integer appId, String command, String json){
        try {
            requestUtils.doPost(appConfig.getCallBackUrl(),Object.class,buildUrlParams(appId,command),json,null);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("回调出现异常");
        }
    }

    public ResponseVO beforeCallBack(Integer appId, String command, String json){
        try {
            ResponseVO responseVO = requestUtils.doPost(appConfig.getCallBackUrl()
                    , ResponseVO.class
                    , buildUrlParams(appId, command)
                    , json
                    , null);

            return responseVO;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("回调出现异常");
            return ResponseVO.successResponse();
        }
    }

    public Map buildUrlParams(Integer appId, String command){
        Map params = new HashMap();
        params.put("appId", appId);
        params.put("command", command);
        return params;
    }
}
