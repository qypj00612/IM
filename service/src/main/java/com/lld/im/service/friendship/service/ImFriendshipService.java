package com.lld.im.service.friendship.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lld.im.common.ResponseVO;
import com.lld.im.service.friendship.dao.ImFriendship;
import com.lld.im.service.friendship.model.req.*;
import com.lld.im.service.friendship.model.resp.CheckFriendShipResp;
import com.lld.im.service.friendship.model.resp.ImportFriendshipResp;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_friendship(IM好友关系表)】的数据库操作Service
* @createDate 2026-03-15 22:22:41
*/
public interface ImFriendshipService extends IService<ImFriendship> {

    ImportFriendshipResp importFriendship(ImportFriendshipReq importFriendshipReq);

    ResponseVO addFriend(AddFriendReq addFriendReq);

    ResponseVO updateFriend(UpdateFriendReq updateFriendReq);

    ResponseVO deleteFriend(DeleteFriendReq deleteFriendReq);

    ResponseVO deleteAllFriend(DeleteFriendReq deleteFriendReq);

    ResponseVO getAllFriend(GetAllFriendshipReq getAllFriendReq);

    ResponseVO getRelationShip(GetRelationshipReq getRelationshipReq);

    List<CheckFriendShipResp> checkFriendShip(CheckFriendShipReq checkFriendShipReq);

    ResponseVO addBlack(AddBlackReq addBlackReq);

    ResponseVO deleteBlack(DeleteBlackReq deleteBlackReq);

    List<CheckFriendShipResp> checkBlack(CheckFriendShipReq checkFriendShipReq);
}
