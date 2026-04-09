package com.lld.im.service.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lld.im.service.message.dao.ImMessageHistory;
import com.lld.im.service.message.service.ImMessageHistoryService;
import com.lld.im.service.message.dao.mapper.ImMessageHistoryMapper;
import org.springframework.stereotype.Service;

/**
* @author Ypj
* @description 针对表【im_message_history(消息历史记录表)】的数据库操作Service实现
* @createDate 2026-04-08 22:49:19
*/
@Service
public class ImMessageHistoryServiceImpl extends ServiceImpl<ImMessageHistoryMapper, ImMessageHistory>
    implements ImMessageHistoryService{

}




