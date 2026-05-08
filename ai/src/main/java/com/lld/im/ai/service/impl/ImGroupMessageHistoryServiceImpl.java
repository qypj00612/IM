package com.lld.im.ai.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lld.im.ai.dao.ImGroupMessageHistory;
import com.lld.im.ai.dao.mapper.ImGroupMessageHistoryMapper;
import com.lld.im.ai.model.AIReply;
import com.lld.im.ai.service.ImGroupMessageHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_group_message_history(群消息历史表)】的数据库操作Service实现
* @createDate 2026-04-09 19:47:14
*/
@Service
@RequiredArgsConstructor
public class ImGroupMessageHistoryServiceImpl extends ServiceImpl<ImGroupMessageHistoryMapper, ImGroupMessageHistory>
    implements ImGroupMessageHistoryService {

    private final ImGroupMessageHistoryMapper imGroupMessageHistoryMapper;

    @Override
    public List<ImGroupMessageHistory> getRecentMessage(AIReply aiReply) {

        Page<ImGroupMessageHistory> page = new Page<>(1, aiReply.getRecentMessageNum());

        LambdaQueryWrapper<ImGroupMessageHistory> eq = new LambdaQueryWrapper<ImGroupMessageHistory>()
                .eq(ImGroupMessageHistory::getAppId, aiReply.getAppId())
                .eq(ImGroupMessageHistory::getGroupId, aiReply.getToId())
                .orderByDesc(ImGroupMessageHistory::getSequence);

        imGroupMessageHistoryMapper.selectPage(page, eq);
        return page.getRecords();
    }
}




