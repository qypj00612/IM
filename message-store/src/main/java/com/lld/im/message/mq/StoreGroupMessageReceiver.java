package com.lld.im.message.mq;

import com.alibaba.fastjson.JSONObject;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.model.message.DoStoreGroupMessage;
import com.lld.im.common.model.message.GroupMessageContent;
import com.lld.im.common.model.message.ImMessageBodyDto;
import com.lld.im.message.service.MessageStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(
        consumerGroup = Constants.RocketConstants.StoreGroupMessage,
        topic = Constants.RocketConstants.MESSAGE_STORE,
        selectorExpression = Constants.RocketConstants.StoreGroupMessage
)
@RequiredArgsConstructor
@Slf4j
public class StoreGroupMessageReceiver implements RocketMQListener<MessageExt> {

    private final MessageStoreService messageStoreService;

    @Override
    public void onMessage(MessageExt message) {
        try {
            String s = new String(message.getBody());
            log.info("收到群聊持久化消息:{}",s);
            JSONObject jsonObject = JSONObject.parseObject(s);
            GroupMessageContent groupMessageContent = JSONObject.parseObject(jsonObject.getString("groupMessageContent"), GroupMessageContent.class);
            ImMessageBodyDto imMessageBodyDto = JSONObject.parseObject(jsonObject.getString("imMessageBodyDto"), ImMessageBodyDto.class);
            DoStoreGroupMessage doStoreGroupMessage = new DoStoreGroupMessage();
            doStoreGroupMessage.setGroupMessageContent(groupMessageContent);
            doStoreGroupMessage.setImMessageBodyDto(imMessageBodyDto);
            messageStoreService.doGroupMessageStore(doStoreGroupMessage);
        } catch (Exception e) {
            log.error("群聊消息持久化出现错误：{}",e.getMessage());
        }

    }
}
