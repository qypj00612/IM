package com.lld.im.ai.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lld.im.ai.dao.ImMessageBody;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_message_body(消息内容表)】的数据库操作Service
* @createDate 2026-04-08 22:47:43
*/
public interface ImMessageBodyService extends IService<ImMessageBody> {

    List<ImMessageBody> getRecentMessage(List<Long> messageKeyList);

}
