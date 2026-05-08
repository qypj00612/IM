package com.lld.im.ai.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lld.im.ai.dao.ImMessageHistory;
import com.lld.im.ai.dao.mapper.ImMessageHistoryMapper;
import com.lld.im.ai.model.AIReply;
import com.lld.im.ai.service.ImMessageHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_message_history(消息历史记录表)】的数据库操作Service实现
* @createDate 2026-04-08 22:49:19
*/
@Service
@RequiredArgsConstructor
public class ImMessageHistoryServiceImpl extends ServiceImpl<ImMessageHistoryMapper, ImMessageHistory>
    implements ImMessageHistoryService {

    private final ImMessageHistoryMapper imMessageHistoryMapper;

    @Override
    public List<ImMessageHistory> getRecentMessage(AIReply aiReply) {
        Page<ImMessageHistory> imMessageHistoryPage = new Page<>(1, aiReply.getRecentMessageNum());

        LambdaQueryWrapper<ImMessageHistory> eq = new LambdaQueryWrapper<ImMessageHistory>()
                .eq(ImMessageHistory::getAppId, aiReply.getAppId())
                .eq(ImMessageHistory::getOwnerId, aiReply.getFromId())
                .orderByDesc(ImMessageHistory::getSequence);

        imMessageHistoryMapper.selectPage(imMessageHistoryPage,eq);

        return imMessageHistoryPage.getRecords();
    }
}




