package com.lld.im.service.friendship.controller;

import com.lld.im.common.ResponseVO;
import com.lld.im.service.friendship.dao.ImFriendshipGroupMember;
import com.lld.im.service.friendship.model.req.AddFriendshipGroupMemberReq;
import com.lld.im.service.friendship.model.req.DeleteFriendshipGroupMemberReq;
import com.lld.im.service.friendship.model.resp.AddFriendshipGroupMemberResp;
import com.lld.im.service.friendship.model.resp.DeleteFriendshipGroupMemberResp;
import com.lld.im.service.friendship.service.ImFriendshipGroupMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/friendshipGroupMember")
@RequiredArgsConstructor
public class ImFriendshipGroupMemberController {

    private final ImFriendshipGroupMemberService imFriendshipGroupMemberService;

    @RequestMapping("add")
    public ResponseVO<AddFriendshipGroupMemberResp> addGroupMember(@RequestBody @Valid AddFriendshipGroupMemberReq req) {
        AddFriendshipGroupMemberResp resp = imFriendshipGroupMemberService.addGroupMember(req);
        return ResponseVO.successResponse(resp);
    }

    @RequestMapping("del")
    public ResponseVO<DeleteFriendshipGroupMemberResp> delGroupMember(@RequestBody @Valid DeleteFriendshipGroupMemberReq req){
        DeleteFriendshipGroupMemberResp resp = imFriendshipGroupMemberService.delGroupMember(req);
        return ResponseVO.successResponse(resp);
    }
}
