package com.lld.im.service.group.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.exception.ApplicationException;
import com.lld.im.service.group.dao.ImGroup;
import com.lld.im.service.group.dao.ImGroupMember;
import com.lld.im.service.group.enums.GroupErrorCode;
import com.lld.im.service.group.enums.GroupMemberRoleEnum;
import com.lld.im.service.group.enums.GroupStatusEnum;
import com.lld.im.service.group.enums.GroupTypeEnum;
import com.lld.im.service.group.model.dto.GroupMemberDTO;
import com.lld.im.service.group.model.req.*;
import com.lld.im.service.group.model.resp.GetGroupResp;
import com.lld.im.service.group.model.resp.GetRoleResp;
import com.lld.im.service.group.service.ImGroupMemberService;
import com.lld.im.service.group.service.ImGroupService;
import com.lld.im.service.group.dao.mapper.ImGroupMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author Ypj
* @description 针对表【im_group(群信息表)】的数据库操作Service实现
* @createDate 2026-03-19 18:10:25
*/
@Service
@RequiredArgsConstructor
public class ImGroupServiceImpl extends ServiceImpl<ImGroupMapper, ImGroup>
    implements ImGroupService{

    private final ImGroupMapper imGroupMapper;

    @Resource
    @Lazy
    private ImGroupMemberService imGroupMemberService;

    @Override
    public ResponseVO importGroup(ImportGroupReq req) {

        ImGroup imGroup = BeanUtil.copyProperties(req, ImGroup.class);

        if(imGroup.getGroupId()!=null){
            ImGroup group = getImGroup(req.getGroupId(), req.getAppId());

            if(group!=null){
                if(group.getStatus()== GroupStatusEnum.NORMAL.getCode()){
                    throw new ApplicationException(GroupErrorCode.GROUP_IS_EXIST);
                }
                imGroup.setCreateTime(DateTime.now().getTime());
                imGroup.setUpdateTime(DateTime.now().getTime());
                imGroup.setStatus(GroupStatusEnum.NORMAL.getCode());
                imGroupMapper.updateById(imGroup);
            }else{
                imGroup.setCreateTime(DateTime.now().getTime());
                imGroup.setUpdateTime(DateTime.now().getTime());
                imGroup.setStatus(GroupStatusEnum.NORMAL.getCode());
                imGroupMapper.insert(imGroup);
            }
        }else{
            imGroup.setGroupId(UUID.randomUUID().toString().replace("-", ""));
            imGroup.setStatus(GroupStatusEnum.NORMAL.getCode());
            imGroup.setCreateTime(DateTime.now().getTime());
            imGroup.setUpdateTime(DateTime.now().getTime());
            imGroupMapper.insert(imGroup);
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ImGroup getImGroup(String groupId, Integer appId) {
        LambdaQueryWrapper<ImGroup> eq = new LambdaQueryWrapper<ImGroup>()
                .eq(ImGroup::getGroupId, groupId)
                .eq(ImGroup::getAppId, appId)
                .eq(ImGroup::getStatus, GroupStatusEnum.NORMAL.getCode());
        return imGroupMapper.selectOne(eq);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO createGroup(CreateGroupReq req) {
        boolean isAdmin = false;
        if(!isAdmin){
            req.setOwnerId(req.getOperator());
        }
        if(req.getGroupType()==GroupTypeEnum.PUBLIC.getCode()&&StrUtil.isNotBlank(req.getOwnerId())){
            throw new ApplicationException(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }

        if(StrUtil.isBlank(req.getGroupId())){
            req.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        }else{
            ImGroup group = getImGroup(req.getGroupId(), req.getAppId());
            if(group==null){
                throw new ApplicationException(GroupErrorCode.GROUP_IS_EXIST);
            }
        }

        ImGroup imGroup = BeanUtil.copyProperties(req, ImGroup.class);
        imGroup.setCreateTime(DateTime.now().getTime());
        imGroup.setUpdateTime(DateTime.now().getTime());
        imGroup.setStatus(GroupStatusEnum.NORMAL.getCode());
        try {
            imGroupMapper.insert(imGroup);
        } catch (Exception e) {
            throw new ApplicationException(GroupErrorCode.GROUP_CREATE_ERROR);
        }

        GroupMemberDTO groupMemberDTO = new GroupMemberDTO();
        groupMemberDTO.setGroupId(req.getGroupId());
        groupMemberDTO.setMemberId(req.getOwnerId());
        groupMemberDTO.setRole(GroupMemberRoleEnum.OWNER.getCode());

        imGroupMemberService.doAddGroupMember(req.getGroupId(), groupMemberDTO, req.getAppId());

        if(CollUtil.isEmpty(req.getMember())){
            throw new ApplicationException(GroupErrorCode.GROUP_MEMBER_NULL);
        }

        for(GroupMemberDTO memberDTO:req.getMember()){
            imGroupMemberService.doAddGroupMember(req.getGroupId(), memberDTO, req.getAppId());
        }

        return ResponseVO.successResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO updateGroup(UpdateGroupReq req) {
        boolean isAdmin = false;

        ImGroup group = getImGroup(req.getGroupId(), req.getAppId());
        if(group==null){
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        if(!isAdmin){
            GetRoleResp roleResp = imGroupMemberService.getRole(req.getGroupId(),req.getOperator(),req.getAppId());
            int role=roleResp.getRole();
            boolean isMan= role==GroupMemberRoleEnum.MAMAGER.getCode();
            boolean isOwner= role==GroupMemberRoleEnum.OWNER.getCode();
            if(group.getGroupType()== GroupTypeEnum.PUBLIC.getCode()&&!(isOwner||isMan)){
                throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }

        ImGroup update = BeanUtil.copyProperties(req, ImGroup.class);
        update.setUpdateTime(DateTime.now().getTime());
        if(StrUtil.isNotBlank(req.getGroupIntroduction())){
            update.setIntroduction(req.getGroupIntroduction());
        }
        int count = imGroupMapper.updateById(update);
        if(count!=1){
            throw new ApplicationException(GroupErrorCode.UPDATE_GROUP_BASE_INFO_ERROR);
        }
        return ResponseVO.successResponse();
    }

    @Override
    public GetGroupResp getInfo(GetGroupInfoReq req) {
        ImGroup group = getImGroup(req.getGroupId(), req.getAppId());
        if(group==null){
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        GetGroupResp getRoleResp = BeanUtil.copyProperties(group, GetGroupResp.class);
        List<GroupMemberDTO> memberList=imGroupMemberService.getGroupMember(req.getGroupId(),req.getAppId());
        getRoleResp.setMemberList(memberList);
        return getRoleResp;
    }

    @Override
    public List<ImGroup> getJoined(GetJoinedGroupReq req) {

        List<String> groups = imGroupMemberService.getJoined(req.getMemberId(),req.getAppId());

        LambdaQueryWrapper<ImGroup> eq = new LambdaQueryWrapper<ImGroup>()
                .eq(ImGroup::getAppId, req.getAppId())
                .eq(ImGroup::getStatus, GroupStatusEnum.NORMAL.getCode());
        if(CollUtil.isNotEmpty(req.getGroupType())){
            eq.in(ImGroup::getGroupType, req.getGroupType());
        }
        eq.in(ImGroup::getGroupId,groups);

        return imGroupMapper.selectList(eq);
    }

    @Override
    public ResponseVO mute(MuteGroupReq req) {

        boolean isAdmin = false;
        ImGroup group = getImGroup(req.getGroupId(), req.getAppId());
        if(group==null){
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        if(!isAdmin){
            GetRoleResp role = imGroupMemberService.getRole(req.getGroupId(), req.getOperator(), req.getAppId());
            boolean isMan= role.getRole()==GroupMemberRoleEnum.OWNER.getCode()
                    || role.getRole()==GroupMemberRoleEnum.MAMAGER.getCode();
            if(!isMan){
                throw new ApplicationException(GroupErrorCode.PERMISSION_ERROR);
            }
        }

        ImGroup imGroup = new ImGroup();
        imGroup.setUpdateTime(DateTime.now().getTime());
        imGroup.setMute(req.getMute());
        imGroup.setGroupId(req.getGroupId());
        imGroupMapper.updateById(imGroup);

        return ResponseVO.successResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO destroy(DestroyGroupReq req) {
        boolean isAdmin = false;
        ImGroup group = getImGroup(req.getGroupId(), req.getAppId());

        if(group==null){
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        GetRoleResp role = imGroupMemberService.getRole(req.getGroupId(), req.getOperator(), req.getAppId());
        boolean isOwner= role.getRole()==GroupMemberRoleEnum.OWNER.getCode();

        if(group.getGroupType()== GroupTypeEnum.PRIVATE.getCode()&&!isAdmin){
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_APPMANAGER_ROLE);
        }

        if(group.getGroupType()== GroupTypeEnum.PUBLIC.getCode()&&!(isOwner||isAdmin)){
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }

        group.setUpdateTime(DateTime.now().getTime());
        group.setStatus(GroupStatusEnum.DESTROY.getCode());
        try {
            imGroupMapper.updateById(group);
        } catch (Exception e) {
            throw new ApplicationException(GroupErrorCode.DESTROY_ERROR);
        }

        return ResponseVO.successResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO transfer(TransferGroupReq req) {
        ImGroup group = getImGroup(req.getGroupId(), req.getAppId());
        if(group==null){
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        GetRoleResp role = imGroupMemberService.getRole(req.getGroupId(), req.getOperator(), req.getAppId());
        if(role.getRole()!=GroupMemberRoleEnum.OWNER.getCode()){
            throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }
        GetRoleResp newRole = imGroupMemberService.getRole(req.getGroupId(), req.getOwnerId(), req.getAppId());

        group.setUpdateTime(DateTime.now().getTime());
        group.setOwnerId(req.getOwnerId());
        imGroupMapper.updateById(group);
        imGroupMemberService.transfer(group.getGroupId(),req.getOperator(),req.getOwnerId(),req.getAppId());
        return ResponseVO.successResponse();
    }
}




