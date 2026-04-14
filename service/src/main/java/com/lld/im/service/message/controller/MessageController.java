package com.lld.im.service.message.controller;

import com.lld.im.common.ResponseVO;
import com.lld.im.common.model.message.req.CheckSendMessageReq;
import com.lld.im.service.message.modul.req.SendGroupMessageReq;
import com.lld.im.service.message.modul.req.SendMessageReq;
import com.lld.im.service.message.modul.resp.SendMessageResp;
import com.lld.im.service.message.service.GroupMessageService;
import com.lld.im.service.message.service.P2PMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("v1/message")
public class MessageController {

    private final P2PMessageService p2PMessageService;

    private final GroupMessageService groupMessageService;

    @RequestMapping("send")
    public ResponseVO sendMessage(@RequestBody @Validated SendMessageReq req){
        SendMessageResp resp = p2PMessageService.send(req);
        return ResponseVO.successResponse(resp);
    }

    @RequestMapping("group/send")
    public ResponseVO sendGroupMessage(@RequestBody @Validated SendGroupMessageReq req){
        SendMessageResp resp = groupMessageService.send(req);
        return ResponseVO.successResponse(resp);
    }

    @RequestMapping("check")
    public ResponseVO checkSend(@RequestBody CheckSendMessageReq req){
        return p2PMessageService.imServerPermission(req.getFromId(), req.getToId(), req.getAppId());
    }

    @RequestMapping("group/check")
    public ResponseVO checkGroupSend(@RequestBody CheckSendMessageReq req){
        return groupMessageService.isServerPermission(req.getFromId(), req.getAppId(), req.getToId());
    }

}
