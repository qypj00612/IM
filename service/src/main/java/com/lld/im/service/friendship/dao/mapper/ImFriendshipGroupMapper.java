package com.lld.im.service.friendship.dao.mapper;

import com.lld.im.service.friendship.dao.ImFriendshipGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Ypj
* @description 针对表【im_friendship_group(好友分组表)】的数据库操作Mapper
* @createDate 2026-03-18 21:11:07
* @Entity com.lld.im.service.friendship.dao.ImFriendshipGroup
*/
public interface ImFriendshipGroupMapper extends BaseMapper<ImFriendshipGroup> {

    ImFriendshipGroup getFriendshipGroup(String groupId);
}




