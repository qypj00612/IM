package com.lld.im.service.message.service;

import com.lld.im.codec.pack.message.ChatMessageAck;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.MessageCommand;
import com.lld.im.common.model.ClientInfo;
import com.lld.im.service.friendship.service.ImFriendshipService;
import com.lld.im.service.message.modul.MessageContent;
import com.lld.im.service.message.modul.req.SendMessageReq;
import com.lld.im.service.message.modul.resp.SendMessageResp;
import com.lld.im.service.user.service.ImUserDataService;
import com.lld.im.service.utils.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class P2PMessageService {

    private final MessageProducer messageProducer;

    private final CheckSendMessageService checkSendMessageService;

    private final MessageStoreService messageStoreService;

    public void process(MessageContent messageContent) {
        // 前置校验 用户 是否被禁言 是否被禁用，发送方 和 接收方 是否是好友
        ResponseVO responseVO = imServerPermission(messageContent);
        if(responseVO.isOk()){
            // 将消息存入数据库中
            messageStoreService.p2pMessageStore(messageContent);
            // 回 ack 给 tcp
            ack(messageContent, responseVO);
            // 将消息同步到 发送方 的 其他在线端
            syncToSend(messageContent,messageContent);
            // 将消息发送到 接收端 所有的 在线端
            dispatchMessage(messageContent);
        }else{
            // 回 ack
            ack(messageContent, responseVO);
        }

    }

    private void dispatchMessage(MessageContent messageContent) {
        messageProducer.sendToUser(messageContent.getToId(),
                MessageCommand.MSG_P2P,
                messageContent,
                messageContent.getAppId(),
                Constants.RocketConstants.MessageService2Im);
    }

    private void syncToSend(MessageContent messageContent, ClientInfo clientInfo) {
        messageProducer.sendToUserExceptClient(
                messageContent.getFromId(),
                MessageCommand.MSG_P2P,
                messageContent,
                clientInfo,
                Constants.RocketConstants.MessageService2Im);
    }

    private void ack(MessageContent messageContent, ResponseVO responseVO) {
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
        responseVO.setData(chatMessageAck);
        messageProducer.sendToUser(
                messageContent.getFromId(),
                MessageCommand.MSG_ACK,
                responseVO,
                messageContent,
                Constants.RocketConstants.MessageService2Im);
    }

    private ResponseVO imServerPermission(MessageContent messageContent) {
        ResponseVO responseVO = checkSendMessageService.checkForbidAndMute(messageContent.getFromId(), messageContent.getAppId());
        if(!responseVO.isOk()){
            return responseVO;
        }

        return checkSendMessageService.checkFriendAndBlack(
                messageContent.getFromId(),
                messageContent.getToId(),
                messageContent.getAppId());
    }

    public SendMessageResp send(SendMessageReq req) {
        SendMessageResp resp = new SendMessageResp();
        MessageContent messageContent = new MessageContent();
        messageContent.setMessageId(req.getMessageId());
        messageContent.setFromId(req.getFromId());
        messageContent.setToId(req.getToId());
        messageContent.setMessageBody(req.getMessageBody());
        messageContent.setMessageTime(req.getMessageTime());
        messageContent.setAppId(req.getAppId());
        messageContent.setClientType(req.getClientType());
        messageContent.setImei(req.getImei());

        messageStoreService.p2pMessageStore(messageContent);
        syncToSend(messageContent,messageContent);
        dispatchMessage(messageContent);

        resp.setMessageKey(messageContent.getMessageKey());
        resp.setMessageTime(messageContent.getMessageTime());

        return resp;
    }
}
