package com.lld.im.service.conversation.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lld.im.codec.pack.conversation.DeleteConversationPack;
import com.lld.im.codec.pack.conversation.UpdateConversationPack;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.config.AppConfig;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.conversation.ConversationEventCommand;
import com.lld.im.common.enums.conversation.ConversationErrorCode;
import com.lld.im.common.enums.conversation.ConversationTypeEnum;
import com.lld.im.common.exception.ApplicationException;
import com.lld.im.common.model.ClientInfo;
import com.lld.im.common.model.message.MessageReadedContent;
import com.lld.im.common.model.req.SyncReq;
import com.lld.im.common.model.resp.SyncResp;
import com.lld.im.service.conversation.dao.ImConversationSet;
import com.lld.im.service.conversation.model.DeleteConversationReq;
import com.lld.im.service.conversation.model.UpdateConversationReq;
import com.lld.im.service.conversation.service.ImConversationSetService;
import com.lld.im.service.conversation.dao.mapper.ImConversationSetMapper;
import com.lld.im.service.message.seq.RedisSeq;
import com.lld.im.service.utils.MessageProducer;
import com.lld.im.service.utils.WriteUserSeq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_conversation_set(会话设置表)】的数据库操作Service实现
* @createDate 2026-04-13 22:06:35
*/
@Service
@RequiredArgsConstructor
public class ImConversationSetServiceImpl extends ServiceImpl<ImConversationSetMapper, ImConversationSet>
    implements ImConversationSetService{

    private final ImConversationSetMapper imConversationSetMapper;

    private final MessageProducer messageProducer;

    private final AppConfig appConfig;

    private final RedisSeq redisSeq;

    private final WriteUserSeq writeUserSeq;

    public String genConversationId(Integer type, String fromId, String toId){

        // 单聊会话 大的 在前 小的 在后
        if(type == ConversationTypeEnum.P2P.getCode()){
            if(fromId.compareTo(toId) < 0){
                return type + "_" + toId + "_" + fromId;
            }else{
                return type + "_" + fromId + "_" + toId;
            }
        } else {
            // 群聊会话
            return type + "_" + toId;
        }

    }

    public String genConversationId(Integer type, String toId){
        // 群聊会话
        return type + "_" + toId;
    }

    @Override
    public void readMark(MessageReadedContent messageReadedContent) {
        String toId = messageReadedContent.getToId();
        if(messageReadedContent.getConversationType() == ConversationTypeEnum.GROUP.getCode()){
            toId = messageReadedContent.getGroupId();
        }

        String conversationId = genConversationId(messageReadedContent.getConversationType(), messageReadedContent.getFromId(), toId);
        // 优化会话id
        // String id = genConversationId(messageReadedContent.getConversationType(), messageReadedContent.getFromId(), toId);
        LambdaQueryWrapper<ImConversationSet> eq = new LambdaQueryWrapper<ImConversationSet>()
                .eq(ImConversationSet::getConversationId, conversationId);
        ImConversationSet imConversationSetEntity = imConversationSetMapper.selectOne(eq);
        ImConversationSet imConversationSet = new ImConversationSet();

        long seq = redisSeq.seqIncrement(messageReadedContent.getAppId()+":"+Constants.SeqConstants.ConversationSeq);

        if(ObjectUtil.isNull(imConversationSetEntity)){

            imConversationSet.setConversationId(conversationId);
            imConversationSet.setConversationType(messageReadedContent.getConversationType());
            imConversationSet.setFromId(messageReadedContent.getFromId());
            imConversationSet.setToId(toId);
//            imConversationSet.setIsMute();
//            imConversationSet.setIsTop();
//            imConversationSet.setSequence();
            imConversationSet.setReadSequence(messageReadedContent.getMessageSequence());
            imConversationSet.setAppId(messageReadedContent.getAppId());
            imConversationSet.setSequence(seq);
            imConversationSetMapper.insert(imConversationSet);

        }else{
            imConversationSetMapper.readMark(conversationId, messageReadedContent.getAppId(), messageReadedContent.getMessageSequence(),seq);
        }

        writeUserSeq.writeUserSeq(messageReadedContent.getAppId(), messageReadedContent.getFromId(), Constants.SeqConstants.ConversationSeq, seq);
    }

    /**
     * 删除会话
     * @param req
     * @return
     */
    @Override
    public ResponseVO delete(DeleteConversationReq req) {
        LambdaQueryWrapper<ImConversationSet> eq = new LambdaQueryWrapper<ImConversationSet>()
                .eq(ImConversationSet::getConversationId, req.getConversationId())
                .eq(ImConversationSet::getAppId, req.getAppId());

        ImConversationSet imConversationSet = imConversationSetMapper.selectOne(eq);

        long seq = redisSeq.seqIncrement(req.getAppId()+":"+Constants.SeqConstants.ConversationSeq);

        if(ObjectUtil.isNotNull(imConversationSet)){
            imConversationSet.setIsMute(0);
            imConversationSet.setIsTop(0);
            imConversationSet.setSequence(seq);
            imConversationSetMapper.updateById(imConversationSet);
        }
        // 同步到其他端
        if(appConfig.getDeleteConversationSyncMode()==1){
            DeleteConversationPack deleteConversationPack = new DeleteConversationPack();
            deleteConversationPack.setConversationId(req.getConversationId());
            deleteConversationPack.setSequence(seq);
            messageProducer.sendToUserExceptClient(
                    req.getFromId(),
                    ConversationEventCommand.CONVERSATION_DELETE,
                    deleteConversationPack,
                    new ClientInfo(req.getAppId(),req.getClientType(),req.getImei()),
                    Constants.RocketConstants.MessageService2Im
            );
        }

        writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.ConversationSeq, seq);

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO update(UpdateConversationReq req) {
        if(req.getIsMute()==null&&req.getIsTop()==null){
            throw new ApplicationException(ConversationErrorCode.CONVERSATION_UPDATE_PARAM_ERROR);
        }
        LambdaQueryWrapper<ImConversationSet> eq = new LambdaQueryWrapper<ImConversationSet>()
                .eq(ImConversationSet::getConversationId, req.getConversationId())
                .eq(ImConversationSet::getAppId, req.getAppId());
        ImConversationSet imConversationSet = imConversationSetMapper.selectOne(eq);
        long seq = redisSeq.seqIncrement(req.getAppId()+":"+Constants.SeqConstants.ConversationSeq);
        if(ObjectUtil.isNotNull(imConversationSet)){
            ImConversationSet update = new ImConversationSet();
            update.setSequence(seq);
            update.setConversationId(req.getConversationId());
            if(req.getIsTop()!=null){
                update.setIsMute(req.getIsMute());
                imConversationSet.setIsTop(req.getIsTop());
            }
            if(req.getIsMute()!=null){
                update.setIsMute(req.getIsMute());
                imConversationSet.setIsMute(req.getIsMute());
            }
            imConversationSetMapper.updateById(update);
            if(appConfig.getDeleteConversationSyncMode()==1){
                UpdateConversationPack pack = new UpdateConversationPack();
                pack.setConversationId(req.getConversationId());
                pack.setIsMute(imConversationSet.getIsMute());
                pack.setIsTop(imConversationSet.getIsTop());
                pack.setConversationType(imConversationSet.getConversationType());
                pack.setSequence(seq);

                messageProducer.sendToUserExceptClient(
                        req.getFromId(),
                        ConversationEventCommand.CONVERSATION_UPDATE,
                        pack,
                        new ClientInfo(req.getAppId(),req.getClientType(),req.getImei()),
                        Constants.RocketConstants.MessageService2Im
                );

            }
        }

        writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.ConversationSeq, seq);

        return ResponseVO.successResponse();
    }

    @Override
    public SyncResp<ImConversationSet> syncConversation(SyncReq req) {
        if(req.getMaxLimit()>100){
            req.setMaxLimit(100);
        }
        Page<ImConversationSet> page = new Page<>(1, req.getMaxLimit());
        LambdaQueryWrapper<ImConversationSet> eq = new LambdaQueryWrapper<ImConversationSet>()
                .eq(ImConversationSet::getAppId, req.getAppId())
                .eq(ImConversationSet::getFromId, req.getOperator())
                .gt(ImConversationSet::getSequence, req.getLastSeq())
                .orderByAsc(ImConversationSet::getSequence);
        imConversationSetMapper.selectPage(page,eq);
        List<ImConversationSet> records = page.getRecords();
        SyncResp<ImConversationSet> resp = new SyncResp<>();
        if(CollUtil.isNotEmpty(records)){
            ImConversationSet max = records.get(records.size() - 1);
            resp.setMaxSeq(max.getSequence());
            resp.setDataList(records);
            long masSeq = imConversationSetMapper.getMaxSequence(req.getAppId(),req.getOperator());
            resp.setCompleted(max.getSequence()>=masSeq);
        }else{
            resp.setCompleted(true);
        }
        return resp;
    }
}




