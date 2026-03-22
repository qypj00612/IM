package com.lld.im.service.group.service;

import com.lld.im.common.ResponseVO;
import com.lld.im.service.group.dao.ImGroupMember;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lld.im.service.group.model.dto.GroupMemberDTO;
import com.lld.im.service.group.model.req.*;
import com.lld.im.service.group.model.resp.AddGroupMemberResp;
import com.lld.im.service.group.model.resp.GetRoleResp;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_group_member(群成员表)】的数据库操作Service
* @createDate 2026-03-19 18:12:26
*/
public interface ImGroupMemberService extends IService<ImGroupMember> {

    List<AddGroupMemberResp> importGroupMember(ImportGroupMemberReq req);

    void doAddGroupMember(String groupId, GroupMemberDTO member, Integer appId);

    GetRoleResp getRole(String groupId, String operator, Integer appId);

    List<GroupMemberDTO> getGroupMember(String groupId, Integer appId);

    List<String> getJoined(String memberId, Integer appId);

    void transfer(String groupId,String oldOwner, String newOwner, Integer appId);

    ResponseVO addGroupMember(AddGroupMemberReq req);

    ResponseVO removeGroupMember(RemoveGroupMemberReq req);

    ResponseVO exitGroupMember(ExitGroupMemberReq req);

    ResponseVO doRemoveGroupMember(String groupId, Integer appId, String memberId);

    ResponseVO speak(MuteMemberReq req);
}
