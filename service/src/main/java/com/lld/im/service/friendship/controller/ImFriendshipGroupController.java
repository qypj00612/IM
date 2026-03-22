package com.lld.im.service.friendship.controller;

import com.lld.im.common.ResponseVO;
import com.lld.im.service.friendship.model.req.AddFriendshipGroupMemberReq;
import com.lld.im.service.friendship.model.req.AddFriendshipGroupReq;
import com.lld.im.service.friendship.model.req.DeleteFriendshipGroupMemberReq;
import com.lld.im.service.friendship.model.req.DeleteFriendshipGroupReq;
import com.lld.im.service.friendship.model.resp.AddFriendshipGroupMemberResp;
import com.lld.im.service.friendship.model.resp.AddFriendshipGroupResp;
import com.lld.im.service.friendship.model.resp.DeleteFriendshipGroupMemberResp;
import com.lld.im.service.friendship.service.ImFriendshipGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/friendshipGroup")
@RequiredArgsConstructor
public class ImFriendshipGroupController {

    private final ImFriendshipGroupService imFriendshipGroupService;

    @RequestMapping("add")
    public ResponseVO<AddFriendshipGroupResp> addGroup(@RequestBody @Valid AddFriendshipGroupReq req) {
        AddFriendshipGroupResp resp = imFriendshipGroupService.addGroup(req);
        return ResponseVO.successResponse(resp);
    }

    @RequestMapping("del")
    public ResponseVO delGroup(@RequestBody @Valid DeleteFriendshipGroupReq req){
        return imFriendshipGroupService.delGroup(req);
    }

}
