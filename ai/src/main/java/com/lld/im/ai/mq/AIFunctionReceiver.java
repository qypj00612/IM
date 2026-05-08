package com.lld.im.ai.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lld.im.ai.model.AIReply;
import com.lld.im.ai.service.AIService;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.ai.AIEventCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQMessageListener(
        consumerGroup = Constants.RocketConstants.Im2AI,
        topic = Constants.RocketConstants.IM_TO_SERVICE,
        selectorExpression = Constants.RocketConstants.Im2AI
)
@RequiredArgsConstructor
public class AIFunctionReceiver implements RocketMQListener<MessageExt> {

    private final AIService aiService;

    @Override
    public void onMessage(MessageExt message) {
        String s = new String(message.getBody());
        log.info("AI功能收到消息:{}",s);
        JSONObject jsonObject = JSON.parseObject(s);
        Integer command = jsonObject.getInteger("command");
        AIReply javaObject = JSON.toJavaObject(jsonObject, AIReply.class);
        if(command == AIEventCommand.AI_INTELLIGENT_SEARCH.getCommand()){
            aiService.IntelligenceSearch(javaObject);
        } else if (command == AIEventCommand.AI_INTELLIGENT_REPLY.getCommand()) {
            aiService.intelligenceReply(javaObject);
        } else if (command == AIEventCommand.AI_GROUP_CHAT_SUMMARY.getCommand()) {
            aiService.IntelligenceSummary(javaObject);
        }
    }
}
