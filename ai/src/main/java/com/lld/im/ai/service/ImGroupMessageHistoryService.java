package com.lld.im.ai.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lld.im.ai.dao.ImGroupMessageHistory;
import com.lld.im.ai.model.AIReply;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_group_message_history(群消息历史表)】的数据库操作Service
* @createDate 2026-04-09 19:47:14
*/
public interface ImGroupMessageHistoryService extends IService<ImGroupMessageHistory> {

    List<ImGroupMessageHistory> getRecentMessage(AIReply aiReply);

}
