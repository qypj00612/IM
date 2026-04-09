package com.lld.im.service.message.service;

import com.lld.im.codec.pack.message.ChatMessageAck;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.group.GroupEventCommand;
import com.lld.im.common.model.ClientInfo;
import com.lld.im.service.group.service.ImGroupMemberService;
import com.lld.im.service.message.modul.GroupMessageContent;
import com.lld.im.service.message.modul.req.SendGroupMessageReq;
import com.lld.im.service.message.modul.resp.SendMessageResp;
import com.lld.im.service.utils.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupMessageServer {

    private final CheckSendMessageService checkSendMessageService;

    private final MessageProducer messageProducer;

    private final ImGroupMemberService imGroupMemberService;

    private final MessageStoreService messageStoreService;

    public void process(GroupMessageContent messageContent){
        ResponseVO responseVO = isServerPermission(messageContent);
        if(responseVO.isOk()){

            messageStoreService.groupMessageStore(messageContent);

            ack(messageContent,responseVO);
            syncToSend(messageContent,messageContent);
            groupDispatch(messageContent);
        }else{
            ack(messageContent,responseVO);
        }
    }

    private void groupDispatch(GroupMessageContent messageContent){
        List<String> groupMemberIds = imGroupMemberService.getGroupMemberIds(messageContent.getGroupId(), messageContent.getAppId());
        for(String groupMember : groupMemberIds){
            if(!messageContent.getFromId().equals(groupMember)){
                messageProducer.sendToUser(
                        groupMember,
                        GroupEventCommand.MSG_GROUP,
                        messageContent,
                        messageContent.getAppId(),
                        Constants.RocketConstants.MessageService2Im
                );
            }
        }
    }

    private void syncToSend(GroupMessageContent messageContent, ClientInfo clientInfo){
        messageProducer.sendToUserExceptClient(messageContent.getFromId(),
                GroupEventCommand.MSG_GROUP,
                messageContent,
                clientInfo,
                Constants.RocketConstants.MessageService2Im);
    }

    private void ack(GroupMessageContent messageContent, ResponseVO responseVO){
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
        responseVO.setData(chatMessageAck);
        messageProducer.sendToUser(messageContent.getFromId(),
                GroupEventCommand.GROUP_MSG_ACK,
                responseVO,
                messageContent,
                Constants.RocketConstants.MessageService2Im);
    }

    private ResponseVO isServerPermission(GroupMessageContent messageContent){
        ResponseVO responseVO = checkSendMessageService.checkForbidAndMute(messageContent.getFromId(), messageContent.getAppId());
        if(!responseVO.isOk()){
            return responseVO;
        }
        return checkSendMessageService.checkGroupMessage(messageContent.getFromId(), messageContent.getGroupId(), messageContent.getAppId());
    }

    public SendMessageResp send(SendGroupMessageReq req) {
        GroupMessageContent groupMessageContent = new GroupMessageContent();
        groupMessageContent.setGroupId(req.getGroupId());
        groupMessageContent.setMessageId(req.getMessageId());
        groupMessageContent.setFromId(req.getFromId());
        groupMessageContent.setMessageBody(req.getMessageBody());
        groupMessageContent.setMessageTime(req.getMessageTime());
        groupMessageContent.setAppId(req.getAppId());
        groupMessageContent.setClientType(req.getClientType());
        groupMessageContent.setImei(req.getImei());

        messageStoreService.groupMessageStore(groupMessageContent);
        syncToSend(groupMessageContent,groupMessageContent);
        groupDispatch(groupMessageContent);

        SendMessageResp resp = new SendMessageResp();
        resp.setMessageTime(groupMessageContent.getMessageTime());
        resp.setMessageKey(groupMessageContent.getMessageKey());

        return resp;

    }
}
