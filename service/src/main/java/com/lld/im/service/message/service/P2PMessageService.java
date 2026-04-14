package com.lld.im.service.message.service;

import cn.hutool.core.bean.BeanUtil;
import com.lld.im.codec.pack.message.ChatMessageAck;
import com.lld.im.codec.proto.MessageReceiveServerAckPack;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.MessageCommand;
import com.lld.im.common.enums.conversation.ConversationTypeEnum;
import com.lld.im.common.model.ClientInfo;
import com.lld.im.common.model.message.MessageContent;
import com.lld.im.common.model.message.OfflineMessageContent;
import com.lld.im.service.message.modul.req.SendMessageReq;
import com.lld.im.service.message.modul.resp.SendMessageResp;
import com.lld.im.service.message.seq.RedisSeq;
import com.lld.im.service.utils.ConversationIdGenerate;
import com.lld.im.service.utils.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class P2PMessageService {

    private final MessageProducer messageProducer;

    private final CheckSendMessageService checkSendMessageService;

    private final MessageStoreService messageStoreService;

    private final RedisSeq redisSeq;

    private ThreadPoolExecutor threadPoolExecutor;

    {
        AtomicLong num = new AtomicLong(0);
        threadPoolExecutor = new ThreadPoolExecutor(
            8,
            8,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    thread.setName("message-process-thread-"+num.incrementAndGet());
                    return thread;
                }
            }
        );
    }

    public void process(MessageContent messageContent) {
        // 前置校验 用户 是否被禁言 是否被禁用，发送方 和 接收方 是否是好友
//        ResponseVO responseVO = imServerPermission(messageContent);
//        if(responseVO.isOk()){

        MessageContent cache = messageStoreService.getMessageCache(messageContent.getAppId(),
                messageContent.getMessageId(), MessageContent.class);
        if (cache != null) {
            threadPoolExecutor.execute(()->{
                // 回 ack 给 tcp
                ack(cache, ResponseVO.successResponse());
                // 将消息同步到 发送方 的 其他在线端
                syncToSend(cache,cache);
                // 将消息发送到 接收端 所有的 在线端 返回发送成功的client
                List<ClientInfo> clientInfos = dispatchMessage(cache);
                if(clientInfos != null && clientInfos.isEmpty()) {
                    receiveAck(messageContent);
                }
            });
            return;
        }

        String key = messageContent.getAppId()+
                Constants.SeqConstants.P2PRedisSeq+
                ConversationIdGenerate.generateP2PId(messageContent.getFromId(),messageContent.getToId());
        Long seq = redisSeq.seqIncrement(key);
        messageContent.setMessageSequence(seq);

        threadPoolExecutor.execute(()->{
            // 将消息存入数据库中
            messageStoreService.p2pMessageStore(messageContent);
            // 存储离线消息
            OfflineMessageContent offlineMessageContent = BeanUtil.copyProperties(messageContent, OfflineMessageContent.class);
            offlineMessageContent.setConversationType(ConversationTypeEnum.P2P.getCode());
            messageStoreService.storeP2POfflineMessage(offlineMessageContent);
            // 回 ack 给 tcp
            ack(messageContent, ResponseVO.successResponse());
            // 将消息同步到 发送方 的 其他在线端
            syncToSend(messageContent,messageContent);
            // 将消息发送到 接收端 所有的 在线端 返回发送成功的client
            List<ClientInfo> clientInfos = dispatchMessage(messageContent);
            // 将 messageId 存入缓存中
            messageStoreService.setMessageCache(messageContent.getAppId(),messageContent.getMessageId(),messageContent);
            if(clientInfos != null && clientInfos.isEmpty()) {
                receiveAck(messageContent);
            }
        });

//        }else{
//            // 回 ack
//            ack(messageContent, responseVO);
//        }

    }

    private void receiveAck(MessageContent messageContent) {
        MessageReceiveServerAckPack messageReceiveServerAckPack = new MessageReceiveServerAckPack();
        messageReceiveServerAckPack.setMessageKey(messageContent.getMessageKey());
        messageReceiveServerAckPack.setFromId(messageContent.getToId());
        messageReceiveServerAckPack.setToId(messageContent.getFromId());
        messageReceiveServerAckPack.setMessageSequence(messageContent.getMessageSequence());
        messageReceiveServerAckPack.setServerSend(true);
        messageReceiveServerAckPack.setAppId(messageContent.getAppId());
        messageReceiveServerAckPack.setClientType(messageContent.getClientType());
        messageReceiveServerAckPack.setImei(messageContent.getImei());

        messageProducer.sendToUser(
                messageReceiveServerAckPack.getToId(),
                MessageCommand.MSG_RECIVE_ACK,
                messageReceiveServerAckPack,
                new ClientInfo(messageContent.getAppId(),messageContent.getClientType(),messageContent.getImei()),
                Constants.RocketConstants.MessageService2Im
        );

    }

    /**
     * 将消息发送到 接收端 所有的 在线端
     * @param messageContent
     * @return 返回发送成功的client集合
     */
    private List<ClientInfo> dispatchMessage(MessageContent messageContent) {
        return messageProducer.sendToUser(messageContent.getToId(),
                MessageCommand.MSG_P2P,
                messageContent,
                messageContent.getAppId(),
                Constants.RocketConstants.MessageService2Im);
    }

    private void syncToSend(MessageContent messageContent, ClientInfo clientInfo) {
        messageProducer.sendToUserExceptClient(
                messageContent.getFromId(),
                MessageCommand.MSG_P2P,
                messageContent,
                clientInfo,
                Constants.RocketConstants.MessageService2Im);
    }

    private void ack(MessageContent messageContent, ResponseVO responseVO) {
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId(), messageContent.getMessageSequence());
        responseVO.setData(chatMessageAck);
        messageProducer.sendToUser(
                messageContent.getFromId(),
                MessageCommand.MSG_ACK,
                responseVO,
                messageContent,
                Constants.RocketConstants.MessageService2Im);
    }

    public ResponseVO imServerPermission(String fromId, String toId, Integer appId) {
        ResponseVO responseVO = checkSendMessageService.checkForbidAndMute(fromId, appId);
        if(!responseVO.isOk()){
            return responseVO;
        }

        return checkSendMessageService.checkFriendAndBlack(
                fromId,
                toId,
                appId);
    }

    public SendMessageResp send(SendMessageReq req) {
        SendMessageResp resp = new SendMessageResp();
        MessageContent messageContent = new MessageContent();
        messageContent.setMessageId(req.getMessageId());
        messageContent.setFromId(req.getFromId());
        messageContent.setToId(req.getToId());
        messageContent.setMessageBody(req.getMessageBody());
        messageContent.setMessageTime(req.getMessageTime());
        messageContent.setAppId(req.getAppId());
        messageContent.setClientType(req.getClientType());
        messageContent.setImei(req.getImei());

        messageStoreService.p2pMessageStore(messageContent);
        syncToSend(messageContent,messageContent);
        dispatchMessage(messageContent);

        resp.setMessageKey(messageContent.getMessageKey());
        resp.setMessageTime(messageContent.getMessageTime());

        return resp;
    }
}
