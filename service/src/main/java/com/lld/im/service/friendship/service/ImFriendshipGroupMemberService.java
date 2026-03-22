package com.lld.im.service.friendship.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lld.im.common.ResponseVO;
import com.lld.im.service.friendship.dao.ImFriendshipGroupMember;
import com.lld.im.service.friendship.model.req.AddFriendshipGroupMemberReq;
import com.lld.im.service.friendship.model.req.DeleteFriendshipGroupMemberReq;
import com.lld.im.service.friendship.model.resp.AddFriendshipGroupMemberResp;
import com.lld.im.service.friendship.model.resp.DeleteFriendshipGroupMemberResp;

/**
* @author Ypj
* @description 针对表【im_friendship_group_member(好友分组成员表)】的数据库操作Service
* @createDate 2026-03-18 21:07:09
*/
public interface ImFriendshipGroupMemberService extends IService<ImFriendshipGroupMember> {

    AddFriendshipGroupMemberResp addGroupMember(AddFriendshipGroupMemberReq req);

    DeleteFriendshipGroupMemberResp delGroupMember(DeleteFriendshipGroupMemberReq req);
}
