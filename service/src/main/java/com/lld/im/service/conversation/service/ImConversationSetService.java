package com.lld.im.service.conversation.service;

import com.lld.im.common.ResponseVO;
import com.lld.im.common.model.message.MessageReadedContent;
import com.lld.im.common.model.req.SyncReq;
import com.lld.im.common.model.resp.SyncResp;
import com.lld.im.service.conversation.dao.ImConversationSet;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lld.im.service.conversation.model.DeleteConversationReq;
import com.lld.im.service.conversation.model.UpdateConversationReq;

/**
* @author Ypj
* @description 针对表【im_conversation_set(会话设置表)】的数据库操作Service
* @createDate 2026-04-13 22:06:35
*/
public interface ImConversationSetService extends IService<ImConversationSet> {

    void readMark(MessageReadedContent messageReadedContent);

    /**
     * 删除会话
     * @param deleteConversationReq
     * @return
     */
    ResponseVO delete(DeleteConversationReq deleteConversationReq);

    /**
     * 更新会话
     * @param req
     * @return
     */
    ResponseVO update(UpdateConversationReq req);

    SyncResp<ImConversationSet> syncConversation(SyncReq req);
}
