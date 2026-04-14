package com.lld.im.tcp.feign;

import com.lld.im.common.ResponseVO;
import com.lld.im.common.model.message.req.CheckSendMessageReq;
import feign.Headers;
import feign.RequestLine;

public interface FeignMessageService {

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    @RequestLine("POST /message/check")
    ResponseVO checkP2pSendMessage(CheckSendMessageReq checkSendMessage);

    @Headers({
            "Content-Type: application/json",
            "Accept: application/json"
    })
    @RequestLine("POST /message/group/check")
    ResponseVO checkGroupSendMessage(CheckSendMessageReq checkSendMessage);

}
