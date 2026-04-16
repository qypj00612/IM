package com.lld.im.service.conversation.dao.mapper;

import com.lld.im.service.conversation.dao.ImConversationSet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Ypj
* @description 针对表【im_conversation_set(会话设置表)】的数据库操作Mapper
* @createDate 2026-04-13 22:06:35
* @Entity com.lld.im.service.conversation.dao.ImConversationSet
*/
public interface ImConversationSetMapper extends BaseMapper<ImConversationSet> {

    void readMark(String id, Integer appId, Long messageSequence, Long seq);

    long getMaxSequence(Integer appId, String operator);
}




