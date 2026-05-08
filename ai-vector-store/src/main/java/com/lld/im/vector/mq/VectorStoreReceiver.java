package com.lld.im.vector.mq;

import com.alibaba.fastjson.JSONObject;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.model.message.VectorMessageDto;
import com.lld.im.vector.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        consumerGroup = Constants.RocketConstants.VectorMessage,
        topic = Constants.RocketConstants.MESSAGE_STORE,
        selectorExpression = Constants.RocketConstants.VectorMessage
)
@RequiredArgsConstructor
@Slf4j
public class VectorStoreReceiver implements RocketMQListener<MessageExt> {

    private final VectorStoreService vectorStoreService;

    @Override
    public void onMessage(MessageExt message) {
        String body = new String(message.getBody());
        log.info("消息向量化收到消息：{}",body);
        VectorMessageDto vectorMessageDto = JSONObject.parseObject(body, VectorMessageDto.class);
        log.info("{}",vectorMessageDto);
        vectorStoreService.vectorMessage(vectorMessageDto);
    }
}
