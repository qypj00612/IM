package com.lld.im.service.conversation.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.lld.im.service.conversation.dao.ImConversationSet;
import com.lld.im.service.conversation.model.DeleteConversationReq;
import com.lld.im.service.conversation.model.UpdateConversationReq;
import com.lld.im.service.conversation.service.ImConversationSetService;
import com.lld.im.service.conversation.dao.mapper.ImConversationSetMapper;
import com.lld.im.service.utils.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public String genConversationId(Integer type, String fromId, String toId){
        return type + "_" + fromId + "_" + toId;
    }

    @Override
    public void readMark(MessageReadedContent messageReadedContent) {
        String toId = messageReadedContent.getToId();
        if(messageReadedContent.getConversationType() == ConversationTypeEnum.GROUP.getCode()){
            toId = messageReadedContent.getGroupId();
        }
        String id = genConversationId(messageReadedContent.getConversationType(), messageReadedContent.getFromId(), toId);
        LambdaQueryWrapper<ImConversationSet> eq = new LambdaQueryWrapper<ImConversationSet>()
                .eq(ImConversationSet::getConversationId, id);
        ImConversationSet imConversationSetEntity = imConversationSetMapper.selectOne(eq);
        ImConversationSet imConversationSet = new ImConversationSet();

        if(ObjectUtil.isNull(imConversationSetEntity)){

            imConversationSet.setConversationId(id);
            imConversationSet.setConversationType(messageReadedContent.getConversationType());
            imConversationSet.setFromId(messageReadedContent.getFromId());
            imConversationSet.setToId(toId);
//            imConversationSet.setIsMute();
//            imConversationSet.setIsTop();
//            imConversationSet.setSequence();
            imConversationSet.setReadSequence(messageReadedContent.getMessageSequence());
            imConversationSet.setAppId(messageReadedContent.getAppId());
            imConversationSetMapper.insert(imConversationSet);

        }else{
            imConversationSetMapper.readMark(id, messageReadedContent.getAppId(), messageReadedContent.getMessageSequence());
        }
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
        if(ObjectUtil.isNotNull(imConversationSet)){
            imConversationSet.setIsMute(0);
            imConversationSet.setIsTop(0);
            imConversationSetMapper.updateById(imConversationSet);
        }
        // 同步到其他端
        if(appConfig.getDeleteConversationSyncMode()==1){
            DeleteConversationPack deleteConversationPack = new DeleteConversationPack();
            deleteConversationPack.setConversationId(req.getConversationId());
            messageProducer.sendToUserExceptClient(
                    req.getFromId(),
                    ConversationEventCommand.CONVERSATION_DELETE,
                    deleteConversationPack,
                    new ClientInfo(req.getAppId(),req.getClientType(),req.getImei()),
                    Constants.RocketConstants.MessageService2Im
            );
        }
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
        if(ObjectUtil.isNotNull(imConversationSet)){
            ImConversationSet update = new ImConversationSet();
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
                pack.setSequence(imConversationSet.getSequence());

                messageProducer.sendToUserExceptClient(
                        req.getFromId(),
                        ConversationEventCommand.CONVERSATION_UPDATE,
                        pack,
                        new ClientInfo(req.getAppId(),req.getClientType(),req.getImei()),
                        Constants.RocketConstants.MessageService2Im
                );

            }
        }
        return ResponseVO.successResponse();
    }
}




