package com.lld.im.service.message.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lld.im.codec.pack.message.MessageReadPack;
import com.lld.im.codec.pack.message.ReCallMessageNotifyPack;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.DelFlagEnum;
import com.lld.im.common.enums.command.Command;
import com.lld.im.common.enums.command.MessageCommand;
import com.lld.im.common.enums.command.group.GroupEventCommand;
import com.lld.im.common.enums.conversation.ConversationTypeEnum;
import com.lld.im.common.enums.message.MessageErrorCode;
import com.lld.im.common.model.ClientInfo;
import com.lld.im.common.model.message.MessageReadedContent;
import com.lld.im.common.model.message.MessageReceiveAckPack;
import com.lld.im.common.model.message.OfflineMessageContent;
import com.lld.im.common.model.message.ReCallMessageContent;
import com.lld.im.common.model.req.SyncReq;
import com.lld.im.common.model.resp.SyncResp;
import com.lld.im.service.conversation.service.ImConversationSetService;
import com.lld.im.service.message.dao.ImMessageBody;
import com.lld.im.service.message.dao.mapper.ImMessageBodyMapper;
import com.lld.im.service.message.seq.RedisSeq;
import com.lld.im.service.utils.ConversationIdGenerate;
import com.lld.im.service.utils.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 服务端回复给tcp的消息的类
 */
@Service
@RequiredArgsConstructor
public class MessageReceiveAckService {

    private final MessageProducer messageProducer;

    private final ImConversationSetService imConversationSetService;

    private final StringRedisTemplate stringRedisTemplate;

    private final ImMessageBodyMapper imMessageBodyMapper;

    private final RedisSeq redisSeq;

    /**
     * 消息收到确认
     * @param message
     */
    public void receiveAck(MessageReceiveAckPack message) {
        messageProducer.sendToUser(
                message.getToId(),
                MessageCommand.MSG_RECIVE_ACK,
                message,message.getAppId(),
                Constants.RocketConstants.MessageService2Im
        );
    }

    /**
     * 消息已读
     * @param messageReadedContent
     */
    public void readMark(MessageReadedContent messageReadedContent) {
        // 更新会话seq，将已读消息同步到其他在线端，给对方发送消息已读回执
        imConversationSetService.readMark(messageReadedContent);
        MessageReadPack messageReadPack = BeanUtil.copyProperties(messageReadedContent, MessageReadPack.class);

        syncRead(messageReadPack, messageReadedContent,MessageCommand.MSG_READED_NOTIFY);
        messageProducer.sendToUser(
                messageReadedContent.getToId(),
                MessageCommand.MSG_READED_RECEIPT,
                messageReadPack,
                messageReadedContent.getAppId(),
                Constants.RocketConstants.MessageService2Im
        );
    }

    private void syncRead(MessageReadPack messageReadPack, MessageReadedContent messageReadedContent, Command command) {
        messageProducer.sendToUserExceptClient(
                messageReadedContent.getFromId(),
                command,
                messageReadPack,
                messageReadedContent,
                Constants.RocketConstants.MessageService2Im
        );
    }

    public void readGroupMark(MessageReadedContent messageReadedContent) {
        imConversationSetService.readMark(messageReadedContent);
        MessageReadPack messageReadPack = BeanUtil.copyProperties(messageReadedContent, MessageReadPack.class);
        syncRead(messageReadPack, messageReadedContent, GroupEventCommand.MSG_GROUP_READED_NOTIFY);
        messageProducer.sendToUser(
                messageReadedContent.getToId(),
                GroupEventCommand.MSG_GROUP_READED_RECEIPT,
                messageReadPack,
                messageReadedContent.getAppId(),
                Constants.RocketConstants.MessageService2Im
        );
    }

    /**
     * 增量拉取离线消息
     * @param req
     * @return
     */
    public ResponseVO syncOfflineMessage(SyncReq req) {
        SyncResp<OfflineMessageContent> resp = new SyncResp<>();
        String key = req.getAppId()+Constants.RedisConstants.OfflineConstant+req.getOperator();
        ZSetOperations<String, String> stringStringZSetOperations = stringRedisTemplate.opsForZSet();

        // 逆序取第一条消息（获取 score 最大的那条消息，也就是最新消息）
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringStringZSetOperations.reverseRangeWithScores(key, 0, 0);
        if(CollUtil.isNotEmpty(typedTuples)){
            ArrayList<ZSetOperations.TypedTuple<String>> list = new ArrayList<>(typedTuples);
            ZSetOperations.TypedTuple<String> max = list.get(0);
            // 获取最大的messageKey
            Double maxMessageKey = max.getScore();

            List<OfflineMessageContent> respData = new ArrayList<>();

            // 范围查询
            Set<ZSetOperations.TypedTuple<String>> query = stringStringZSetOperations.rangeByScoreWithScores(
                    key,
                    req.getLastSeq(), // 从这个seq开始
                    maxMessageKey, // 到最大seq结束
                    0, // 偏移量（不用管）
                    req.getMaxLimit() // 最多拉几条
            );

            // 把 ZSet 里的 value（JSON字符串） 转成离线消息实体
            for (ZSetOperations.TypedTuple<String> data : query) {
                OfflineMessageContent content = JSONObject.parseObject(data.getValue(), OfflineMessageContent.class);
                respData.add(content);
            }
            resp.setDataList(respData);

            if(CollUtil.isNotEmpty(respData)){
                // 获取拉取消息中messageKey最大的那条消息
                OfflineMessageContent offlineMessageContent = respData.get(respData.size() - 1);
                // 判断是否拉取完成
                resp.setCompleted(offlineMessageContent.getMessageKey()>=maxMessageKey);
                // 设置本次拉取消息中最大的messageKey
                resp.setMaxSeq(offlineMessageContent.getMessageKey());
            }else{
                resp.setCompleted(true);
            }

        }else{
            resp.setCompleted(true);
        }
        return ResponseVO.successResponse(resp);
    }

    /**
     * 消息撤回
     * @param content
     */
    public void recallMessage(ReCallMessageContent content) {
        // 修改历史消息的状态
        // 修改离线消息的状态
        // 回ack给发送方
        // 同步给发送方其他端
        // 分发给接收方

        Long seq = redisSeq.seqIncrement(
                content.getAppId()+
                        Constants.SeqConstants.P2PRedisSeq+
                        ConversationIdGenerate.generateSeqP2PId(content.getFromId(), content.getToId()));

        ReCallMessageNotifyPack pack = new ReCallMessageNotifyPack();
        pack.setFromId(content.getFromId());
        pack.setToId(content.getToId());
        pack.setMessageKey(content.getMessageKey());
        pack.setMessageSequence(content.getMessageSequence());

        Long messageTime = content.getMessageTime();
        Long now = DateTime.now().getTime();
        if(now - messageTime > 12000L){
            ack(pack, ResponseVO.errorResponse(MessageErrorCode.MESSAGE_RECALL_TIME_OUT), content);
            return;
        }

        LambdaQueryWrapper<ImMessageBody> eq = new LambdaQueryWrapper<ImMessageBody>()
                .eq(ImMessageBody::getAppId, content.getAppId())
                .eq(ImMessageBody::getMessageKey, content.getMessageKey());
        ImMessageBody imMessageBody = imMessageBodyMapper.selectOne(eq);
        if(imMessageBody == null){
            ack(pack, ResponseVO.errorResponse(MessageErrorCode.MESSAGEBODY_IS_NOT_EXIST), content);
        }
        if(imMessageBody.getDelFlag()== DelFlagEnum.DELETE.getCode()){
            ack(pack, ResponseVO.errorResponse(MessageErrorCode.MESSAGE_IS_RECALLED), content);
        }

        ImMessageBody body = new ImMessageBody();
        body.setMessageKey(imMessageBody.getMessageKey());
        body.setDelFlag(DelFlagEnum.DELETE.getCode());
        imMessageBodyMapper.updateById(body);

        if(content.getConversationType() == ConversationTypeEnum.P2P.getCode()){

            String fromKey = content.getAppId() + Constants.RedisConstants.OfflineConstant+content.getFromId();
            String fromConversationId = ConversationIdGenerate.genConversationId(content.getConversationType(), content.getFromId(), content.getToId());

            String toKey = content.getAppId() + Constants.RedisConstants.OfflineConstant+content.getToId();
            String toConversationId = ConversationIdGenerate.genConversationId(content.getConversationType(), content.getToId(), content.getFromId());

            long messageKey = IdUtil.getSnowflakeNextId();

            OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
            offlineMessageContent.setAppId(content.getAppId());
            offlineMessageContent.setMessageKey(messageKey);
            offlineMessageContent.setMessageBody(imMessageBody.getMessageBody());
            offlineMessageContent.setMessageTime(imMessageBody.getMessageTime());
            offlineMessageContent.setDelFlag(DelFlagEnum.DELETE.getCode());
            offlineMessageContent.setFromId(content.getFromId());
            offlineMessageContent.setToId(content.getToId());
            offlineMessageContent.setMessageSequence(seq);
            offlineMessageContent.setConversationType(ConversationTypeEnum.P2P.getCode());

            offlineMessageContent.setConversationId(fromConversationId);
            stringRedisTemplate.opsForZSet().add(fromKey, JSONObject.toJSONString(offlineMessageContent), messageKey);

            offlineMessageContent.setConversationId(toConversationId);
            stringRedisTemplate.opsForZSet().add(toKey, JSONObject.toJSONString(offlineMessageContent), messageKey);

            ack(pack, ResponseVO.successResponse(), content);
            messageProducer.sendToUserExceptClient(
                    content.getFromId(),
                    MessageCommand.MSG_RECALL_NOTIFY,
                    ResponseVO.successResponse(),
                    content,
                    Constants.RocketConstants.MessageService2Im
            );

            messageProducer.sendToUser(
                    content.getToId(),
                    MessageCommand.MSG_RECALL_NOTIFY,
                    content,
                    content.getAppId(),
                    Constants.RocketConstants.MessageService2Im
            );
        }else{

        }

    }

    private void ack(ReCallMessageNotifyPack pack, ResponseVO responseVO, ClientInfo content) {
        responseVO.setData(pack);
        messageProducer.sendToUser(
                pack.getFromId(),
                MessageCommand.MSG_RECALL_ACK,
                responseVO,
                content,
                Constants.RocketConstants.MessageService2Im
        );
    }
}
