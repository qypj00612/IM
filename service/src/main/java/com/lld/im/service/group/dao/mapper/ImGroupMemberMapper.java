package com.lld.im.service.group.dao.mapper;

import com.lld.im.service.group.dao.ImGroupMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lld.im.service.group.model.dto.GroupMemberDTO;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_group_member(群成员表)】的数据库操作Mapper
* @createDate 2026-03-19 18:12:26
* @Entity com.lld.im.service.group.dao.ImGroupMember
*/
public interface ImGroupMemberMapper extends BaseMapper<ImGroupMember> {

    List<GroupMemberDTO> getGroupMember(String groupId, Integer appId);

    List<String> getJoined(String memberId, Integer appId);

    List<String> getGroupMemberIds(String groupId, Integer appId);

    List<String> getGroupManagers(String groupId, Integer appId);
}




