package com.lld.im.ai.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lld.im.ai.dao.ImMessageHistory;
import com.lld.im.ai.model.AIReply;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_message_history(消息历史记录表)】的数据库操作Service
* @createDate 2026-04-08 22:49:19
*/
public interface ImMessageHistoryService extends IService<ImMessageHistory> {

    List<ImMessageHistory> getRecentMessage(AIReply aiReply);
}
