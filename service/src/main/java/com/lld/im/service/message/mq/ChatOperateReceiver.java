package com.lld.im.service.message.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.MessageCommand;
import com.lld.im.service.message.modul.MessageContent;
import com.lld.im.service.message.service.P2PMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "im-to-service-consumer-p2p-group",
        topic = Constants.RocketConstants.IM_TO_SERVICE,
        selectorExpression = Constants.RocketConstants.Im2MessageService)
@RequiredArgsConstructor
public class ChatOperateReceiver implements RocketMQListener<MessageExt> {

    private final P2PMessageService p2PMessageService;

    @Override
    public void onMessage(MessageExt message) {
        try {
            String s = new String(message.getBody());
            log.info("收到消息：{}",s);
            JSONObject jsonObject = JSON.parseObject(s);
            Integer command = jsonObject.getInteger("command");
            if(command.equals(MessageCommand.MSG_P2P.getCommand())){
                // 处理消息
                MessageContent msg = JSONObject.toJavaObject(jsonObject, MessageContent.class);
                p2PMessageService.process(msg);
                log.info("消息：{}已处理", msg);
            }
        } catch (Exception e) {
            log.error("error:{}",e.getMessage());
        }
    }
}
