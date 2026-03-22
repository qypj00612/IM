package com.lld.im.service.friendship.service;

import com.lld.im.common.ResponseVO;
import com.lld.im.service.friendship.dao.ImFriendshipGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lld.im.service.friendship.model.req.AddFriendshipGroupReq;
import com.lld.im.service.friendship.model.req.DeleteFriendshipGroupReq;
import com.lld.im.service.friendship.model.resp.AddFriendshipGroupMemberResp;
import com.lld.im.service.friendship.model.resp.AddFriendshipGroupResp;
import com.lld.im.service.friendship.model.resp.DeleteFriendshipGroupMemberResp;

/**
* @author Ypj
* @description 针对表【im_friendship_group(好友分组表)】的数据库操作Service
* @createDate 2026-03-18 21:11:07
*/
public interface ImFriendshipGroupService extends IService<ImFriendshipGroup> {

    ImFriendshipGroup getFriendGroup(String groupName, String fromId, Integer appId);

    AddFriendshipGroupResp addGroup(AddFriendshipGroupReq req);

    ResponseVO delGroup(DeleteFriendshipGroupReq req);
}
