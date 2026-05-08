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
import com.lld.im.service.utils.ConversationIdGenerate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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

        // 消息持久化
        rocketMQTemplate.convertAndSend(
                Constants.RocketConstants.MESSAGE_STORE+":"+Constants.RocketConstants.StoreP2PMessage,
                doStoreP2PMessage);

        VectorMessageDto vectorMessageDto = new VectorMessageDto();
        vectorMessageDto.setAppId(messageContent.getAppId());
        vectorMessageDto.setContent(imMessageBody.getMessageBody());
        vectorMessageDto.setMessageKey(imMessageBody.getMessageKey());
        vectorMessageDto.setConversationId(ConversationIdGenerate.genP2PConversationId(messageContent.getFromId(),messageContent.getToId()));
        vectorMessageDto.setMessageTime(imMessageBody.getMessageTime());

        // 消息向量化
        rocketMQTemplate.convertAndSend(
                Constants.RocketConstants.MESSAGE_STORE+":"+Constants.RocketConstants.VectorMessage,
                vectorMessageDto);
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

        VectorMessageDto vectorMessageDto = new VectorMessageDto();
        vectorMessageDto.setAppId(messageContent.getAppId());
        vectorMessageDto.setContent(imMessageBody.getMessageBody());
        vectorMessageDto.setMessageKey(imMessageBody.getMessageKey());
        vectorMessageDto.setConversationId(ConversationIdGenerate.genGroupConversationId(messageContent.getToId()));
        vectorMessageDto.setMessageTime(imMessageBody.getMessageTime());

        // 消息向量化
        rocketMQTemplate.convertAndSend(
                Constants.RocketConstants.MESSAGE_STORE+":"+Constants.RocketConstants.VectorMessage,
                vectorMessageDto);

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

        String conversationId = imConversationSetService.genConversationId(
                ConversationTypeEnum.P2P.getCode(),
                offlineMessageContent.getFromId(),
                offlineMessageContent.getToId()
        );

        // 设置离线消息的会话id
        offlineMessageContent.setConversationId(conversationId);
//        String conversationKey = offlineMessageContent.getAppId()+
//                Constants.RedisConstants.OfflineConstantConversationIndex+
//                // 优化后的会话 id 不需要 userId 了
//                // offlineMessageContent.getFromId()+":"+
//                offlineMessageContent.getConversationId();

        // 找到from队列
        String fromKey = offlineMessageContent.getAppId()+Constants.RedisConstants.OfflineConstantIndex +offlineMessageContent.getFromId();
//        String fromConversationKey = offlineMessageContent.getAppId()+
//                Constants.RedisConstants.OfflineConstantConversationIndex+
//                // 优化后的会话 id 不需要 userId 了
//                // offlineMessageContent.getFromId()+":"+
//                offlineMessageContent.getConversationId();
        String fromHash = offlineMessageContent.getAppId()+Constants.RedisConstants.OfflineConstant + offlineMessageContent.getFromId();
        ZSetOperations<String, String> zset = stringRedisTemplate.opsForZSet();
        HashOperations<String, Object, Object> hash= stringRedisTemplate.opsForHash();
        // 判断队列中的数据是否超过设定值
        if(zset.zCard(fromKey)>appConfig.getOfflineMessageCount()){

            //zset.removeRange(fromKey,0,0);

            // 先获取要被删除的 messageKey（最旧的一条）
            Set<String> deleteKeys = zset.range(fromKey, 0, 0);
            if (deleteKeys != null && !deleteKeys.isEmpty()) {
                //String delKey = deleteKeys.iterator().next();
                // 删除 ZSet
                zset.removeRange(fromKey, 0, 0);
                // 删除 Hash 中对应的消息体

                // 优化 全局离线消息超出上线后不删除具体的消息
                //hash.delete(fromHash, delKey);
            }
        }

//        // 会话队列
//        if(zset.zCard(conversationKey)>200){
//            Set<String> deleteKeys = zset.range(conversationKey, 0, 0);
//            if (deleteKeys != null && !deleteKeys.isEmpty()) {
//                String delKey = deleteKeys.iterator().next();
//                // 删除 ZSet
//                zset.removeRange(conversationKey, 0, 0);
//                // 删除 Hash 中对应的消息体
//                // 优化 只有会话离线消息超出上限后才删除消息
//                hash.delete(fromHash, delKey);
//            }
//        }

//        // 插入数据，将messageKey作为分值
//        zset.add(
//                fromKey,
//                JSONObject.toJSONString(offlineMessageContent),
//                offlineMessageContent.getMessageKey()
//        );

        hash.put(
                fromHash,
                offlineMessageContent.getMessageKey().toString(),
                JSONObject.toJSONString(offlineMessageContent)
        );

        // 全局离线消息的索引存储
        zset.add(
                fromKey,
                offlineMessageContent.getMessageKey().toString(),
                offlineMessageContent.getMessageKey()
        );

        // 找到to的队列
        String toKey = offlineMessageContent.getAppId()+Constants.RedisConstants.OfflineConstantIndex +offlineMessageContent.getToId();
//        String toConversationKey = offlineMessageContent.getAppId()+
//                Constants.RedisConstants.OfflineConstantConversationIndex+
//                offlineMessageContent.getToId()+":"+
//                offlineMessageContent.getConversationId();
        String toHash = offlineMessageContent.getAppId()+Constants.RedisConstants.OfflineConstant + offlineMessageContent.getToId();

        // 判断队列中的数据是否超过设定值
        if(zset.zCard(toKey)>appConfig.getOfflineMessageCount()){

            //zset.removeRange(toKey,0,0);

            // 先获取要被删除的 messageKey（最旧的一条）
            Set<String> deleteKeys = zset.range(toKey, 0, 0);
            if (deleteKeys != null && !deleteKeys.isEmpty()) {
                String delKey = deleteKeys.iterator().next();
                // 删除 ZSet
                zset.removeRange(toKey, 0, 0);
                // 删除 Hash 中对应的消息体
                hash.delete(toHash, delKey);
            }
        }

//        // 插入数据，将messageKey作为分值
//        zset.add(
//                toKey,
//                JSONObject.toJSONString(offlineMessageContent),
//                offlineMessageContent.getMessageKey()
//        );

        hash.put(
                toHash,
                offlineMessageContent.getMessageKey().toString(),
                JSONObject.toJSONString(offlineMessageContent)
        );

        zset.add(
                toKey,
                offlineMessageContent.getMessageKey().toString(),
                offlineMessageContent.getMessageKey()
        );

//        zset.add(
//                conversationKey,
//                offlineMessageContent.getMessageKey().toString(),
//                offlineMessageContent.getMessageKey()
//        );

    }

    public void storeGroupOfflineMessage(OfflineMessageContent offlineMessageContent, List<String> members) {
        ZSetOperations<String, String> zset = stringRedisTemplate.opsForZSet();
        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();

        // 设置离线消息的会话id
        offlineMessageContent.setConversationId(imConversationSetService.genConversationId(
                ConversationTypeEnum.GROUP.getCode(),
                offlineMessageContent.getToId()
        ));

//        String conversationKey = offlineMessageContent.getAppId()+
//                Constants.RedisConstants.OfflineConstantConversationIndex +
//                offlineMessageContent.getConversationId();

        //String delKey = "";

//        if(zset.zCard(conversationKey)>200){
//            // 先获取要被删除的 messageKey（最旧的一条）
//            Set<String> deleteKeys = zset.range(conversationKey, 0, 0);
//            if (deleteKeys != null && !deleteKeys.isEmpty()) {
//                delKey = deleteKeys.iterator().next();
//                // 删除 ZSet
//                zset.removeRange(conversationKey, 0, 0);
//            }
//        }

        // 遍历所有群成员
        for(String memberId : members){

            // 获取群成员的队列名
            String key = offlineMessageContent.getAppId()+Constants.RedisConstants.OfflineConstantIndex +memberId;

            String hashKey = offlineMessageContent.getAppId()+Constants.RedisConstants.OfflineConstant + memberId;
            // 判断队列中的数据是否超过设定值
            if(zset.zCard(key)>appConfig.getOfflineMessageCount()){
                //zset.removeRange(key,0,0);

                // 先获取要被删除的 messageKey（最旧的一条）
                Set<String> deleteKeys = zset.range(key, 0, 0);
                if (deleteKeys != null && !deleteKeys.isEmpty()) {
                     String delKey = deleteKeys.iterator().next();
//                     删除 ZSet
                    zset.removeRange(key, 0, 0);
//                    // 删除 Hash 中对应的消息体
                    hash.delete(hashKey, delKey);
                }

            }

//            if(StrUtil.isNotBlank(delKey)){
//                // 删除 Hash 中对应的消息体
//                hash.delete(hashKey, delKey);
//            }

            // 插入数据，将messageKey作为分值
            zset.add(
                    key,
                    offlineMessageContent.getMessageKey().toString(),
                    offlineMessageContent.getMessageKey()
            );

            hash.put(
                    hashKey,
                    offlineMessageContent.getMessageKey().toString(),
                    JSONObject.toJSONString(offlineMessageContent)
            );
        }

//        // 添加会话索引
//        zset.add(
//                conversationKey,
//                offlineMessageContent.getMessageKey().toString(),
//                offlineMessageContent.getMessageKey()
//        );
    }
}
