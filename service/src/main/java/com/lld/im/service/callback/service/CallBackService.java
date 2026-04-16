package com.lld.im.service.callback.service;

import com.lld.im.common.ResponseVO;

public interface CallBackService {
    ResponseVO sendMessageBeforeCallback(Integer appId, String sendMessageBefore, String jsonString);
}
