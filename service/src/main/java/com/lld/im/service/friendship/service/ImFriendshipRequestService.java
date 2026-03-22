package com.lld.im.service.friendship.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lld.im.common.ResponseVO;
import com.lld.im.service.friendship.dao.ImFriendshipRequest;
import com.lld.im.service.friendship.dto.FriendshipDTO;
import com.lld.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.lld.im.service.friendship.model.req.GetFriendshipRequestReq;
import com.lld.im.service.friendship.model.req.ReadFriendshipRequestReq;
import jakarta.validation.Valid;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_friendship_request(好友申请表)】的数据库操作Service
* @createDate 2026-03-18 19:07:43
*/
public interface ImFriendshipRequestService extends IService<ImFriendshipRequest> {

    ResponseVO addFriendRequest(String fromId, FriendshipDTO toItem, Integer appId);

    ResponseVO approve(ApproveFriendRequestReq req);

    ResponseVO read(ReadFriendshipRequestReq req);

    List<ImFriendshipRequest> getFriendshipRequest(GetFriendshipRequestReq req);

}
