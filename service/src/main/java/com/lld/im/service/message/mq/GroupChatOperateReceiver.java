package com.lld.im.service.message.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.group.GroupEventCommand;
import com.lld.im.service.message.modul.GroupMessageContent;
import com.lld.im.service.message.service.GroupMessageServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(consumerGroup = "im-to-service-consumer-group",
        topic = Constants.RocketConstants.IM_TO_SERVICE,
        selectorExpression = Constants.RocketConstants.Im2GroupService)
@RequiredArgsConstructor
@Slf4j
public class GroupChatOperateReceiver implements RocketMQListener<MessageExt> {

    private final GroupMessageServer groupMessageServer;

    @Override
    public void onMessage(MessageExt message) {
        try {
            String s = new String(message.getBody());
            log.info("收到群聊消息:{}",s);
            JSONObject parse = JSON.parseObject(s);
            Integer command = parse.getInteger("command");
            if(command.equals(GroupEventCommand.MSG_GROUP.getCommand())){
                GroupMessageContent javaObject = JSONObject.toJavaObject(parse, GroupMessageContent.class);
                groupMessageServer.process(javaObject);
                log.info("群聊消息：{}已处理",javaObject);
            }
        } catch (Exception e) {
            log.error("error:{}",e.getMessage());
        }
    }
}
