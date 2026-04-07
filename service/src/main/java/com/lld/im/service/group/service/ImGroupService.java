package com.lld.im.service.group.service;

import com.lld.im.common.ResponseVO;
import com.lld.im.service.group.dao.ImGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lld.im.service.group.model.req.*;
import com.lld.im.service.group.model.resp.GetGroupResp;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_group(群信息表)】的数据库操作Service
* @createDate 2026-03-19 18:10:25
*/
public interface ImGroupService extends IService<ImGroup> {

    ResponseVO importGroup(ImportGroupReq importGroupReq);

    ImGroup getImGroup(String groupId, Integer appId);

    ResponseVO createGroup(CreateGroupReq req);

    ResponseVO updateGroup(UpdateGroupReq req);

    GetGroupResp getInfo(GetGroupInfoReq req);

    List<ImGroup> getJoined(GetJoinedGroupReq req);

    ResponseVO destroy(DestroyGroupReq req);

    ResponseVO transfer(TransferGroupReq req);

    ResponseVO mute(MuteGroupReq req);

}
