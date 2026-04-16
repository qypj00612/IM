package com.lld.im.service.callback.service.impl;

import com.lld.im.common.ResponseVO;
import com.lld.im.service.callback.service.CallBackService;
import org.springframework.stereotype.Service;

@Service
public class CallBackServiceImpl implements CallBackService {
    @Override
    public ResponseVO sendMessageBeforeCallback(Integer appId, String sendMessageBefore, String jsonString) {
        return ResponseVO.successResponse();
    }
}
