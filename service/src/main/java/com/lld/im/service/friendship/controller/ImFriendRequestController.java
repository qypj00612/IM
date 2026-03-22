package com.lld.im.service.friendship.controller;

import com.lld.im.common.ResponseVO;
import com.lld.im.service.friendship.dao.ImFriendshipRequest;
import com.lld.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.lld.im.service.friendship.model.req.GetFriendshipRequestReq;
import com.lld.im.service.friendship.model.req.ReadFriendshipRequestReq;
import com.lld.im.service.friendship.service.ImFriendshipRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/friendshipRequest")
@RequiredArgsConstructor
public class ImFriendRequestController {

    private final ImFriendshipRequestService imFriendshipRequestService;

    @RequestMapping("approve")
    public ResponseVO approve(@RequestBody @Valid ApproveFriendRequestReq req){
        return imFriendshipRequestService.approve(req);
    }

    @RequestMapping("read")
    public ResponseVO read(@RequestBody @Valid ReadFriendshipRequestReq req){
        return imFriendshipRequestService.read(req);
    }

    @RequestMapping("getFriendshipRequest")
    public ResponseVO<List<ImFriendshipRequest>> getFriendshipRequest(@RequestBody @Valid GetFriendshipRequestReq req){
        List<ImFriendshipRequest> resp = imFriendshipRequestService.getFriendshipRequest(req);
        return ResponseVO.successResponse(resp);
    }
}
