package com.lld.im.service.message.service;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.lld.im.common.config.AppConfig;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.DelFlagEnum;
import com.lld.im.common.enums.conversation.ConversationTypeEnum;
import com.lld.im.common.model.message.*;
import com.lld.im.service.conversation.service.impl.ImConversationSetServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageStoreService {

    private final RocketMQTemplate rocketMQTemplate;

    private final StringRedisTemplate stringRedisTemplate;

    private final AppConfig appConfig;

    private final ImConversationSetServiceImpl imConversationSetService;

    public void p2pMessageStore(MessageContent messageContent) {
        ImMessageBodyDto imMessageBody = extractMessageBody(messageContent);
        messageContent.setMessageKey(imMessageBody.getMessageKey());

        DoStoreP2PMessage doStoreP2PMessage = new DoStoreP2PMessage();
        doStoreP2PMessage.setImMessageBodyDto(imMessageBody);
        doStoreP2PMessage.setMessageContent(messageContent);

        rocketMQTemplate.convertAndSend(
                Constants.RocketConstants.MESSAGE_STORE+":"+Constants.RocketConstants.StoreP2PMessage,
                doStoreP2PMessage);

//        imMessageBodyService.save(imMessageBody);
//
//        List<ImMessageHistory> imMessageHistories = extractMessageHistory(messageContent, imMessageBody);
//        imMessageHistoryService.saveBatch(imMessageHistories);

//        messageContent.setMessageKey(imMessageBody.getMessageKey());
    }

    public void groupMessageStore(GroupMessageContent messageContent) {
        ImMessageBodyDto imMessageBody = extractMessageBody(messageContent);
        messageContent.setMessageKey(imMessageBody.getMessageKey());

        DoStoreGroupMessage doStoreGroupMessage = new DoStoreGroupMessage();
        doStoreGroupMessage.setGroupMessageContent(messageContent);
        doStoreGroupMessage.setImMessageBodyDto(imMessageBody);

        rocketMQTemplate.convertAndSend(
                Constants.RocketConstants.MESSAGE_STORE+":"+Constants.RocketConstants.StoreGroupMessage,
                doStoreGroupMessage
        );

        //imMessageBodyService.save(imMessageBody);

        //ImGroupMessageHistory imGroupMessageHistory = extractGroupMessageHistory(messageContent, imMessageBody);
        //imGroupMessageHistoryService.save(imGroupMessageHistory);

        //messageContent.setMessageKey(imMessageBody.getMessageKey());
    }

    private ImMessageBodyDto extractMessageBody(MessageContent messageContent) {
        ImMessageBodyDto imMessageBody = new ImMessageBodyDto();
        imMessageBody.setMessageKey(IdUtil.getSnowflakeNextId());
        imMessageBody.setAppId(messageContent.getAppId());
        imMessageBody.setMessageBody(messageContent.getMessageBody());
        imMessageBody.setSecurityKey("");
        imMessageBody.setMessageTime(messageContent.getMessageTime());
        imMessageBody.setCreateTime(DateTime.now().getTime());
        imMessageBody.setExtra(messageContent.getExtra());
        imMessageBody.setDelFlag(DelFlagEnum.NORMAL.getCode());

        return imMessageBody;

    }

    public <T> T getMessageCache(Integer appId, String messageId, Class<T> clazz){
        String key = appId + Constants.RedisConstants.MessageConstant +messageId;
        String s = stringRedisTemplate.opsForValue().get(key);
        if(StrUtil.isBlank(s)){
            return null;
        }
        return JSONObject.parseObject(s, clazz);
    }

    public void setMessageCache(Integer appId, String messageId, Object messageContent) {
        String key = appId + Constants.RedisConstants.MessageConstant +messageId;
        String jsonString = JSONObject.toJSONString(messageContent);
        stringRedisTemplate.opsForValue().set(
                key,
                jsonString,
                5,
                TimeUnit.MINUTES
        );
    }

    public void storeP2POfflineMessage(OfflineMessageContent offlineMessageContent) {
        // 找到from队列
        String fromKey = offlineMessageContent.getAppId()+Constants.RedisConstants.OfflineConstant+offlineMessageContent.getFromId();
        ZSetOperations<String, String> zset = stringRedisTemplate.opsForZSet();
        // 判断队列中的数据是否超过设定值
        if(zset.zCard(fromKey)>appConfig.getOfflineMessageCount()){
            zset.removeRange(fromKey,0,0);
        }
        // 设置离线消息的会话id
        offlineMessageContent.setConversationId(imConversationSetService.genConversationId(
                ConversationTypeEnum.P2P.getCode(),
                offlineMessageContent.getFromId(),
                offlineMessageContent.getToId()
        ));
        // 插入数据，将messageKey作为分值
        zset.add(
                fromKey,
                JSONObject.toJSONString(offlineMessageContent),
                offlineMessageContent.getMessageKey()
        );

        // 找到to的队列
        String toKey = offlineMessageContent.getAppId()+Constants.RedisConstants.OfflineConstant+offlineMessageContent.getToId();
        // 判断队列中的数据是否超过设定值
        if(zset.zCard(toKey)>appConfig.getOfflineMessageCount()){
            zset.reverseRange(toKey,0,0);
        }
        // 设置离线消息的会话id
        offlineMessageContent.setConversationId(imConversationSetService.genConversationId(
                ConversationTypeEnum.P2P.getCode(),
                offlineMessageContent.getToId(),
                offlineMessageContent.getFromId()
        ));
        // 插入数据，将messageKey作为分值
        zset.add(
                toKey,
                JSONObject.toJSONString(offlineMessageContent),
                offlineMessageContent.getMessageKey()
        );

    }

    public void storeGroupOfflineMessage(OfflineMessageContent offlineMessageContent, List<String> members) {
        ZSetOperations<String, String> zset = stringRedisTemplate.opsForZSet();
        // 遍历所有群成员
        for(String memberId : members){
            // 获取群成员的队列名
            String key = offlineMessageContent.getAppId()+Constants.RedisConstants.OfflineConstant+memberId;
            // 判断队列中的数据是否超过设定值
            if(zset.zCard(key)>appConfig.getOfflineMessageCount()){
                zset.removeRange(key,0,0);
            }
            // 设置离线消息的会话id
            offlineMessageContent.setConversationId(imConversationSetService.genConversationId(
                    ConversationTypeEnum.GROUP.getCode(),
                    memberId,
                    offlineMessageContent.getToId()
            ));
            // 插入数据，将messageKey作为分值
            zset.add(
                    key,
                    JSONObject.toJSONString(offlineMessageContent),
                    offlineMessageContent.getMessageKey()
            );
        }

    }
}
