package com.lld.im.service.message.service;

import com.lld.im.common.ResponseVO;
import com.lld.im.common.config.AppConfig;
import com.lld.im.common.enums.MessageErrorCode;
import com.lld.im.common.enums.friendship.FriendshipErrorCode;
import com.lld.im.common.enums.friendship.FriendshipStatusEnum;
import com.lld.im.common.enums.group.GroupErrorCode;
import com.lld.im.common.enums.group.GroupMemberRoleEnum;
import com.lld.im.common.enums.group.GroupMuteTypeEnum;
import com.lld.im.common.enums.user.UserErrorCode;
import com.lld.im.common.enums.user.UserForbiddenFlagEnum;
import com.lld.im.common.enums.user.UserSilentFlagEnum;
import com.lld.im.common.exception.ApplicationException;
import com.lld.im.service.friendship.dao.ImFriendship;
import com.lld.im.service.friendship.model.req.GetRelationshipReq;
import com.lld.im.service.friendship.service.ImFriendshipService;
import com.lld.im.service.group.dao.ImGroup;
import com.lld.im.service.group.model.resp.GetRoleResp;
import com.lld.im.service.group.service.ImGroupMemberService;
import com.lld.im.service.group.service.ImGroupService;
import com.lld.im.service.user.dao.ImUserData;
import com.lld.im.service.user.service.ImUserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckSendMessageService {

    private final ImUserDataService imUserDataService;

    private final ImFriendshipService imFriendshipService;

    private final ImGroupService imGroupService;

    private final ImGroupMemberService imGroupMemberService;

    private final AppConfig appConfig;

    // 用户是否被 禁用 和 禁言
    public ResponseVO checkForbidAndMute(String from, Integer appId) {
        ImUserData singleUserInfo = imUserDataService.getSingleUserInfo(from, appId);
        if (singleUserInfo == null) {
            return ResponseVO.errorResponse(UserErrorCode.USER_IS_NOT_EXIST);
        }
        if(singleUserInfo.getForbiddenFlag()== UserForbiddenFlagEnum.FORBIBBEN.getCode()){
            return ResponseVO.errorResponse(MessageErrorCode.FROMER_IS_FORBIDDEN);
        }
        if(singleUserInfo.getSilentFlag()== UserSilentFlagEnum.MUTE.getCode()){
            return ResponseVO.errorResponse(MessageErrorCode.FROMER_IS_MUTE);
        }
        return ResponseVO.successResponse();
    }

    public ResponseVO checkFriendAndBlack(String from, String toId, Integer appId) {
        // 校验是否是好友
        if(appConfig.isSendMessageCheckFriend()){
            GetRelationshipReq fromReq = new GetRelationshipReq();
            fromReq.setFromId(from);
            fromReq.setToId(toId);
            fromReq.setAppId(appId);

            ResponseVO<ImFriendship> fromShip = imFriendshipService.getRelationShip(fromReq);

            GetRelationshipReq toReq = new GetRelationshipReq();
            toReq.setFromId(toId);
            toReq.setToId(from);
            toReq.setAppId(appId);
            ResponseVO<ImFriendship> toShip = imFriendshipService.getRelationShip(toReq);

            // 是否在黑名单
            if(appConfig.isSendMessageCheckBlack()){
                if(fromShip.getData().getBlack()==FriendshipStatusEnum.BLACK_STATUS_BLACKED.getCode()){
                    return ResponseVO.errorResponse(FriendshipErrorCode.FRIEND_IS_BLACK);
                }
                if(toShip.getData().getBlack()== FriendshipStatusEnum.BLACK_STATUS_BLACKED.getCode()){
                    return ResponseVO.errorResponse(FriendshipErrorCode.TARGET_IS_BLACK_YOU);
                }
            }
        }

        return ResponseVO.successResponse();
    }

    // 群消息发送前的校验
    public ResponseVO checkGroupMessage(String from, String groupId, Integer appId) {
        // 用户是否被 禁用 和 禁言
        ResponseVO responseVO = checkForbidAndMute(from, appId);
        if(!responseVO.isOk()){
            return responseVO;
        }

        // 判断用户是否在群内
        GetRoleResp role = imGroupMemberService.getRole(groupId, from, appId);
        // 判断用户是否被禁言
        if(role.getSpeakDate()!=null&&role.getSpeakDate()>System.currentTimeMillis()){
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_MEMBER_IS_SPEAK);
        }

        // 判断该群是否禁言
        // 禁言则只有群主和管理员才能发言
        ImGroup group = imGroupService.getImGroup(groupId, appId);
        if(group.getMute()!=null&&
                group.getMute()== GroupMuteTypeEnum.MUTE.getCode()&&
                role.getRole()== GroupMemberRoleEnum.ORDINARY.getCode()){
            return ResponseVO.errorResponse(GroupErrorCode.THIS_GROUP_IS_MUTE);
        }

        return ResponseVO.successResponse();
    }

}
