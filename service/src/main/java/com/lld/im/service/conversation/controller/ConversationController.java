package com.lld.im.service.conversation.controller;

import com.lld.im.common.ResponseVO;
import com.lld.im.common.model.req.SyncReq;
import com.lld.im.common.model.resp.SyncResp;
import com.lld.im.service.conversation.dao.ImConversationSet;
import com.lld.im.service.conversation.model.DeleteConversationReq;
import com.lld.im.service.conversation.model.UpdateConversationReq;
import com.lld.im.service.conversation.service.ImConversationSetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/conversation")
@RequiredArgsConstructor
public class ConversationController {

    private final ImConversationSetService imConversationSetService;

    @RequestMapping("delete")
    public ResponseVO deleteConversation(@RequestBody @Validated DeleteConversationReq deleteConversationReq) {
        return imConversationSetService.delete(deleteConversationReq);
    }

    @RequestMapping("update")
    public ResponseVO updateConversation(@RequestBody @Validated UpdateConversationReq req) {
        return imConversationSetService.update(req);
    }

    @RequestMapping("sync")
    public ResponseVO syncConversation(@RequestBody @Valid SyncReq req) {
        SyncResp<ImConversationSet> resp = imConversationSetService.syncConversation(req);
        return ResponseVO.successResponse(resp);
    }

}
