package com.lld.im.service.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.lld.im.codec.pack.MessagePack;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.Command;
import com.lld.im.common.model.ClientInfo;
import com.lld.im.common.model.UserSession;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final RocketMQTemplate rocketMQTemplate;

    private final UserSessionUtil userSessionUtil;

    public void send(Object msg, UserSession userSession , String tag) {

        String topic = Constants.RocketConstants.SERVICE_TO_IM+"_"+userSession.getBrokerId();

        rocketMQTemplate.convertAndSend(topic+":"+tag, msg);
    }

    public void sendPack(String toId, Command command, Object msg, UserSession userSession, String tag) {
        MessagePack<Object> messagePack = new MessagePack<>();
        messagePack.setUserId(userSession.getUserId());
        messagePack.setAppId(userSession.getAppId());
        messagePack.setToId(toId);
        messagePack.setClientType(userSession.getClientType());
        // messagePack.setMessageId();
        messagePack.setImei(userSession.getImei());
        messagePack.setCommand(command.getCommand());
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(msg));
        messagePack.setData(jsonObject);

        send(messagePack, userSession, tag);

    }

    // 发送给所有端的方法
    public void sendToUser(String toId, Command command, Object msg, Integer appId, String tag) {
        List<UserSession> userSessions = userSessionUtil.getUserSession(appId, toId);
        for (UserSession userSession : userSessions) {
            sendPack(toId, command, msg, userSession, tag);
        }
    }

    // 发送给所有端的方法
    public void sendToUser(String toId, Integer clientType, String imei
            , Command command, Object msg, Integer appId, String tag) {

        if(ObjectUtil.isNotNull(clientType) && StrUtil.isNotBlank(imei)) {
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setAppId(appId);
            clientInfo.setClientType(clientType);
            clientInfo.setImei(imei);
            sendToUserExceptClient(toId,command,msg,clientInfo,tag);
        }else{
            sendToUser(toId, command, msg, appId, tag);
        }

    }

    // 发送给某个用户的指定客户端
    public void sendToUser(String toId, Command command, Object msg, ClientInfo clientInfo, String tag) {
        UserSession userSession = userSessionUtil.getUserSession(clientInfo.getAppId()
                , toId
                , clientInfo.getClientType()
                , clientInfo.getImei());
        sendPack(toId, command, msg, userSession, tag);
    }

    // 发送给用户除了某一端的所有端
    public void sendToUserExceptClient(String toId, Command command, Object msg, ClientInfo clientInfo, String tag) {
        List<UserSession> userSessions = userSessionUtil.getUserSession(clientInfo.getAppId(), toId);
        for (UserSession userSession : userSessions) {
            if(isMatchClient(userSession,clientInfo)){
                sendPack(toId, command, msg, userSession, tag);
            }
        }
    }

    private boolean isMatchClient(UserSession userSession, ClientInfo clientInfo) {
        return ! ( userSession.getClientType().equals(clientInfo.getClientType())
                && userSession.getImei().equals(clientInfo.getImei()) );
    }

}
