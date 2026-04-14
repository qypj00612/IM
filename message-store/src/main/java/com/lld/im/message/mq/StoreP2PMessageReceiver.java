package com.lld.im.message.mq;

import com.alibaba.fastjson.JSONObject;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.model.message.DoStoreP2PMessage;
import com.lld.im.common.model.message.ImMessageBodyDto;
import com.lld.im.common.model.message.MessageContent;
import com.lld.im.message.service.MessageStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(
        consumerGroup = Constants.RocketConstants.StoreP2PMessage,
        topic = Constants.RocketConstants.MESSAGE_STORE,
        selectorExpression = Constants.RocketConstants.StoreP2PMessage
)
@Slf4j
@RequiredArgsConstructor
public class StoreP2PMessageReceiver implements RocketMQListener<MessageExt> {

    private final MessageStoreService messageStoreService;

    @Override
    public void onMessage(MessageExt message) {
        try {
            String s = new String(message.getBody());
            log.info("接收到持久化信息:{}",s);
            JSONObject json = JSONObject.parseObject(s);
            MessageContent messageContent = JSONObject.parseObject(json.getString("messageContent"), MessageContent.class);
            ImMessageBodyDto imMessageBodyDto = JSONObject.parseObject(json.getString("imMessageBodyDto"), ImMessageBodyDto.class);
            DoStoreP2PMessage doStoreP2PMessage = new DoStoreP2PMessage();
            doStoreP2PMessage.setMessageContent(messageContent);
            doStoreP2PMessage.setImMessageBodyDto(imMessageBodyDto);
            messageStoreService.p2pMessageStore(doStoreP2PMessage);
        } catch (Exception e) {
            log.error("持久化异常:{}",e.getMessage());
        }
    }
}
