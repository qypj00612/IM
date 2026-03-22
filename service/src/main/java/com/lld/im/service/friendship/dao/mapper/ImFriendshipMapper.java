package com.lld.im.service.friendship.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lld.im.service.friendship.dao.ImFriendship;
import com.lld.im.service.friendship.model.req.CheckFriendShipReq;
import com.lld.im.service.friendship.model.resp.CheckFriendShipResp;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_friendship(IM好友关系表)】的数据库操作Mapper
* @createDate 2026-03-15 22:22:41
* @Entity com/lld/im/service/friendship.dao.ImFriendship
*/
public interface ImFriendshipMapper extends BaseMapper<ImFriendship> {

    List<CheckFriendShipResp> singleCheck(CheckFriendShipReq checkFriendShipReq);

    List<CheckFriendShipResp> bothCheck(CheckFriendShipReq checkFriendShipReq);

    List<CheckFriendShipResp> singleBlack(CheckFriendShipReq req);

    List<CheckFriendShipResp> bothBlack(CheckFriendShipReq req);
}




