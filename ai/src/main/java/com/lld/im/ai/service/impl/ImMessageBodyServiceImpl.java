package com.lld.im.ai.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lld.im.ai.dao.ImMessageBody;
import com.lld.im.ai.dao.mapper.ImMessageBodyMapper;
import com.lld.im.ai.service.ImMessageBodyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_message_body(消息内容表)】的数据库操作Service实现
* @createDate 2026-04-08 22:47:43
*/
@Service
@RequiredArgsConstructor
public class ImMessageBodyServiceImpl extends ServiceImpl<ImMessageBodyMapper, ImMessageBody>
    implements ImMessageBodyService {

    private final ImMessageBodyMapper imMessageBodyMapper;

    @Override
    public List<ImMessageBody> getRecentMessage(List<Long> messageKeyList) {

        LambdaQueryWrapper<ImMessageBody> in = new LambdaQueryWrapper<ImMessageBody>()
                .in(ImMessageBody::getMessageKey, messageKeyList);

        return imMessageBodyMapper.selectList(in);

    }

}




