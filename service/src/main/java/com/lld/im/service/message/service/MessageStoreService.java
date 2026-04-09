package com.lld.im.service.message.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.hutool.core.util.IdUtil;
import com.lld.im.common.enums.DelFlagEnum;
import com.lld.im.service.message.dao.ImGroupMessageHistory;
import com.lld.im.service.message.dao.ImMessageBody;
import com.lld.im.service.message.dao.ImMessageHistory;
import com.lld.im.service.message.modul.GroupMessageContent;
import com.lld.im.service.message.modul.MessageContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageStoreService {

    private final ImMessageHistoryService imMessageHistoryService;

    private final ImMessageBodyService imMessageBodyService;

    private final ImGroupMessageHistoryService imGroupMessageHistoryService;

    @Transactional(rollbackFor = Exception.class)
    public void p2pMessageStore(MessageContent messageContent) {
        ImMessageBody imMessageBody = extractMessageBody(messageContent);
        imMessageBodyService.save(imMessageBody);

        List<ImMessageHistory> imMessageHistories = extractMessageHistory(messageContent, imMessageBody);
        imMessageHistoryService.saveBatch(imMessageHistories);

        messageContent.setMessageKey(imMessageBody.getMessageKey());
    }

    @Transactional(rollbackFor = Exception.class)
    public void groupMessageStore(GroupMessageContent messageContent) {
        ImMessageBody imMessageBody = extractMessageBody(messageContent);
        imMessageBodyService.save(imMessageBody);

        ImGroupMessageHistory imGroupMessageHistory = extractGroupMessageHistory(messageContent, imMessageBody);
        imGroupMessageHistoryService.save(imGroupMessageHistory);

        messageContent.setMessageKey(imMessageBody.getMessageKey());
    }

    private ImGroupMessageHistory extractGroupMessageHistory(GroupMessageContent messageContent, ImMessageBody imMessageBody) {
        ImGroupMessageHistory imGroupMessageHistory = new ImGroupMessageHistory();
        imGroupMessageHistory.setMessageKey(imMessageBody.getMessageKey());
        imGroupMessageHistory.setAppId(messageContent.getAppId());
        imGroupMessageHistory.setFromId(messageContent.getFromId());
        imGroupMessageHistory.setGroupId(messageContent.getGroupId());
        imGroupMessageHistory.setSequence(IdUtil.getSnowflakeNextId());
        // imGroupMessageHistory.setMessageRandom();
        imGroupMessageHistory.setMessageTime(messageContent.getMessageTime());
        imGroupMessageHistory.setCreateTime(DateTime.now().getTime());
        return imGroupMessageHistory;
    }

    private ImMessageBody extractMessageBody(MessageContent messageContent) {
        ImMessageBody imMessageBody = new ImMessageBody();
        imMessageBody.setMessageKey(IdUtil.getSnowflakeNextId());
        imMessageBody.setAppId(messageContent.getAppId());
        imMessageBody.setMessageBody(messageContent.getMessageBody());
        imMessageBody.setSecurityKey("");
        imMessageBody.setMessageTime(messageContent.getMessageTime());
        imMessageBody.setCreateTime(DateTime.now().getTime());
        imMessageBody.setExtra(messageContent.getExtra());
        imMessageBody.setDelFlag(DelFlagEnum.NORMAL.getCode());

        return imMessageBody;

    }

    private List<ImMessageHistory> extractMessageHistory(MessageContent messageContent, ImMessageBody imMessageBody) {
        List<ImMessageHistory> imMessageHistories = new ArrayList<>();
        ImMessageHistory fromHistory = new ImMessageHistory();
        fromHistory.setAppId(messageContent.getAppId());
        fromHistory.setOwnerId(messageContent.getFromId());
        fromHistory.setSequence(IdUtil.getSnowflakeNextId());
        fromHistory.setFromId(messageContent.getFromId());
        fromHistory.setToId(messageContent.getToId());
        fromHistory.setMessageKey(imMessageBody.getMessageKey());
        //fromHistory.setMessageRandom();
        fromHistory.setMessageTime(imMessageBody.getMessageTime());
        fromHistory.setCreateTime(DateTime.now().getTime());

        ImMessageHistory toHistory = new ImMessageHistory();
        toHistory.setAppId(messageContent.getAppId());
        toHistory.setOwnerId(messageContent.getToId());
        toHistory.setSequence(IdUtil.getSnowflakeNextId());
        toHistory.setFromId(messageContent.getFromId());
        toHistory.setToId(messageContent.getToId());
        toHistory.setMessageKey(imMessageBody.getMessageKey());
        //toHistory.setMessageRandom();
        toHistory.setMessageTime(imMessageBody.getMessageTime());
        toHistory.setCreateTime(DateTime.now().getTime());

        imMessageHistories.add(fromHistory);
        imMessageHistories.add(toHistory);

        return imMessageHistories;
    }

}
