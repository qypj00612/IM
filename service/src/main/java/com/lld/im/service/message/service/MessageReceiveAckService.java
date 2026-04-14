package com.lld.im.service.message.service;

import cn.hutool.core.bean.BeanUtil;
import com.lld.im.codec.pack.message.MessageReadPack;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.Command;
import com.lld.im.common.enums.command.MessageCommand;
import com.lld.im.common.enums.command.group.GroupEventCommand;
import com.lld.im.common.model.message.MessageReadedContent;
import com.lld.im.common.model.message.MessageReceiveAckPack;
import com.lld.im.service.conversation.service.ImConversationSetService;
import com.lld.im.service.utils.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

/**
 * 服务端回复给tcp的消息的类
 */
@Service
@RequiredArgsConstructor
public class MessageReceiveAckService {

    private final MessageProducer messageProducer;

    private final ImConversationSetService imConversationSetService;

    /**
     * 消息收到确认
     * @param message
     */
    public void receiveAck(MessageReceiveAckPack message) {
        messageProducer.sendToUser(
                message.getToId(),
                MessageCommand.MSG_RECIVE_ACK,
                message,message.getAppId(),
                Constants.RocketConstants.MessageService2Im
        );
    }

    /**
     * 消息已读
     * @param messageReadedContent
     */
    public void readMark(MessageReadedContent messageReadedContent) {
        // 更新会话seq，将已读消息同步到其他在线端，给对方发送消息已读回执
        imConversationSetService.readMark(messageReadedContent);
        MessageReadPack messageReadPack = BeanUtil.copyProperties(messageReadedContent, MessageReadPack.class);

        syncRead(messageReadPack, messageReadedContent,MessageCommand.MSG_READED_NOTIFY);
        messageProducer.sendToUser(
                messageReadedContent.getToId(),
                MessageCommand.MSG_READED_RECEIPT,
                messageReadPack,
                messageReadedContent.getAppId(),
                Constants.RocketConstants.MessageService2Im
        );
    }

    private void syncRead(MessageReadPack messageReadPack, MessageReadedContent messageReadedContent, Command command) {
        messageProducer.sendToUserExceptClient(
                messageReadedContent.getFromId(),
                command,
                messageReadPack,
                messageReadedContent,
                Constants.RocketConstants.MessageService2Im
        );
    }

    public void readGroupMark(MessageReadedContent messageReadedContent) {
        imConversationSetService.readMark(messageReadedContent);
        MessageReadPack messageReadPack = BeanUtil.copyProperties(messageReadedContent, MessageReadPack.class);
        syncRead(messageReadPack, messageReadedContent, GroupEventCommand.MSG_GROUP_READED_NOTIFY);
        messageProducer.sendToUser(
                messageReadedContent.getToId(),
                GroupEventCommand.MSG_GROUP_READED_RECEIPT,
                messageReadPack,
                messageReadedContent.getAppId(),
                Constants.RocketConstants.MessageService2Im
        );
    }
}
