package com.lld.im.service.message.service;

import cn.hutool.core.bean.BeanUtil;
import com.lld.im.codec.pack.message.ChatMessageAck;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.group.GroupEventCommand;
import com.lld.im.common.enums.conversation.ConversationTypeEnum;
import com.lld.im.common.model.ClientInfo;
import com.lld.im.common.model.message.GroupMessageContent;
import com.lld.im.common.model.message.OfflineMessageContent;
import com.lld.im.service.group.service.ImGroupMemberService;
import com.lld.im.service.message.modul.req.SendGroupMessageReq;
import com.lld.im.service.message.modul.resp.SendMessageResp;
import com.lld.im.service.message.seq.RedisSeq;
import com.lld.im.service.utils.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupMessageService {

    private final CheckSendMessageService checkSendMessageService;

    private final MessageProducer messageProducer;

    private final ImGroupMemberService imGroupMemberService;

    private final MessageStoreService messageStoreService;

    private final RedisSeq redisSeq;

    private ThreadPoolExecutor threadPoolExecutor;

    {
        AtomicInteger num = new AtomicInteger(0);
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
                        thread.setName("group-message-group-thread-" + num.getAndIncrement());
                        return thread;
                    }
                }
        );
    }

    public void process(GroupMessageContent groupMessageContent){

        GroupMessageContent cache = messageStoreService.getMessageCache(groupMessageContent.getAppId(), groupMessageContent.getMessageId(), GroupMessageContent.class);
        List<String> groupMemberIds = imGroupMemberService.getGroupMemberIds(groupMessageContent.getGroupId(), groupMessageContent.getAppId());
        if(cache!=null){
            threadPoolExecutor.execute(()->{
                ack(groupMessageContent,ResponseVO.successResponse());
                syncToSend(groupMessageContent,groupMessageContent);
                groupDispatch(groupMessageContent,groupMemberIds);
            });
            return;
        }

        String key = groupMessageContent.getAppId()+Constants.SeqConstants.GroupRedisSeq+groupMessageContent.getGroupId();
        Long l = redisSeq.seqIncrement(key);
        groupMessageContent.setMessageSequence(l);
        threadPoolExecutor.execute(()->{
            messageStoreService.groupMessageStore(groupMessageContent);

            // 离线消息存储
            OfflineMessageContent offlineMessageContent = BeanUtil.copyProperties(groupMessageContent, OfflineMessageContent.class);
            offlineMessageContent.setToId(groupMessageContent.getGroupId());
            offlineMessageContent.setConversationType(ConversationTypeEnum.GROUP.getCode());
            messageStoreService.storeGroupOfflineMessage(offlineMessageContent,groupMemberIds);

            ack(groupMessageContent,ResponseVO.successResponse());
            syncToSend(groupMessageContent,groupMessageContent);
            groupDispatch(groupMessageContent, groupMemberIds);
            messageStoreService.setMessageCache(groupMessageContent.getAppId(),groupMessageContent.getMessageId(),groupMessageContent);
        });
    }

    /**
     * // 将消息发送到 接收端 所有的 在线端
     * @param messageContent
     */
    private void groupDispatch(GroupMessageContent messageContent, List<String> groupMemberIds){
        for(String groupMember : groupMemberIds){
            if(!messageContent.getFromId().equals(groupMember)){
                messageProducer.sendToUser(
                        groupMember,
                        GroupEventCommand.MSG_GROUP,
                        messageContent,
                        messageContent.getAppId(),
                        Constants.RocketConstants.MessageService2Im
                );
            }
        }
    }

    /**
     * 将消息同步到 发送方 的 其他在线端
     * @param messageContent
     * @param clientInfo
     */
    private void syncToSend(GroupMessageContent messageContent, ClientInfo clientInfo){
        messageProducer.sendToUserExceptClient(messageContent.getFromId(),
                GroupEventCommand.MSG_GROUP,
                messageContent,
                clientInfo,
                Constants.RocketConstants.MessageService2Im);
    }

    /**
     * 回ack给tcp
     * @param messageContent
     * @param responseVO
     */
    private void ack(GroupMessageContent messageContent, ResponseVO responseVO){
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
        responseVO.setData(chatMessageAck);
        messageProducer.sendToUser(messageContent.getFromId(),
                GroupEventCommand.GROUP_MSG_ACK,
                responseVO,
                messageContent,
                Constants.RocketConstants.MessageService2Im);
    }

    public ResponseVO isServerPermission(String fromId, Integer appId, String groupId){
        ResponseVO responseVO = checkSendMessageService.checkForbidAndMute(fromId, appId);
        if(!responseVO.isOk()){
            return responseVO;
        }
        return checkSendMessageService.checkGroupMessage(fromId, groupId, appId);
    }

    public SendMessageResp send(SendGroupMessageReq req) {
        GroupMessageContent groupMessageContent = new GroupMessageContent();
        groupMessageContent.setGroupId(req.getGroupId());
        groupMessageContent.setMessageId(req.getMessageId());
        groupMessageContent.setFromId(req.getFromId());
        groupMessageContent.setMessageBody(req.getMessageBody());
        groupMessageContent.setMessageTime(req.getMessageTime());
        groupMessageContent.setAppId(req.getAppId());
        groupMessageContent.setClientType(req.getClientType());
        groupMessageContent.setImei(req.getImei());

        //messageStoreService.groupMessageStore(groupMessageContent);
        syncToSend(groupMessageContent,groupMessageContent);
        List<String> groupMemberIds = imGroupMemberService.getGroupMemberIds(groupMessageContent.getGroupId(), groupMessageContent.getAppId());
        groupDispatch(groupMessageContent,groupMemberIds);

        SendMessageResp resp = new SendMessageResp();
        resp.setMessageTime(groupMessageContent.getMessageTime());
        resp.setMessageKey(groupMessageContent.getMessageKey());

        return resp;

    }
}
