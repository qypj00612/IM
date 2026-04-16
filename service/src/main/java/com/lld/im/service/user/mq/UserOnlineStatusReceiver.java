package com.lld.im.service.user.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lld.im.codec.pack.user.UserStatusChangeNotifyPack;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.SystemCommand;
import com.lld.im.common.enums.command.user.UserEventCommand;
import com.lld.im.common.model.user.UserStatusModifyContent;
import com.lld.im.service.user.service.ImUserStatueService;
import com.lld.im.service.utils.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RocketMQMessageListener(
        consumerGroup = "user-online-status-consumer-group",
        topic = Constants.RocketConstants.IM_TO_SERVICE,
        selectorExpression = Constants.RocketConstants.Im2UserService
)
@Slf4j
@RequiredArgsConstructor
public class UserOnlineStatusReceiver implements RocketMQListener<MessageExt> {

    private final ImUserStatueService imUserStatueService;

    @Override
    public void onMessage(MessageExt message) {
        try {
            String s = new String(message.getBody());
            JSONObject jsonObject = JSON.parseObject(s);
            Integer command = jsonObject.getInteger("command");
            log.info("接收到用户状态变更");

            UserStatusModifyContent userStatus = jsonObject.toJavaObject(UserStatusModifyContent.class);
            if(command.equals(UserEventCommand.USER_ONLINE_STATUS_CHANGE.getCommand())){
                log.info("用户{}登录状态改变", userStatus.getUserId());
                imUserStatueService.processUserOnlineStatusNotify(userStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("用户在线状态通知出现异常");
        }
    }
}
