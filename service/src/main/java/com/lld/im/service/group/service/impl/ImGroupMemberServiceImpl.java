package com.lld.im.service.group.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.exception.ApplicationException;
import com.lld.im.service.group.dao.ImGroup;
import com.lld.im.service.group.dao.ImGroupMember;
import com.lld.im.service.group.enums.AddGroupMemberEnum;
import com.lld.im.service.group.enums.GroupErrorCode;
import com.lld.im.service.group.enums.GroupMemberRoleEnum;
import com.lld.im.service.group.enums.GroupTypeEnum;
import com.lld.im.service.group.model.dto.GroupMemberDTO;
import com.lld.im.service.group.model.req.*;
import com.lld.im.service.group.model.resp.AddGroupMemberResp;
import com.lld.im.service.group.model.resp.GetRoleResp;
import com.lld.im.service.group.service.ImGroupMemberService;
import com.lld.im.service.group.dao.mapper.ImGroupMemberMapper;
import com.lld.im.service.group.service.ImGroupService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
* @author Ypj
* @description 针对表【im_group_member(群成员表)】的数据库操作Service实现
* @createDate 2026-03-19 18:12:26
*/
@Service
@RequiredArgsConstructor
public class ImGroupMemberServiceImpl extends ServiceImpl<ImGroupMemberMapper, ImGroupMember>
    implements ImGroupMemberService{

    private final ImGroupMemberMapper imGroupMemberMapper;

    @Override
    public ResponseVO speak(MuteMemberReq req) {
        boolean isAdmin = false;

        ImGroup group = imGroupService.getImGroup(req.getGroupId(), req.getAppId());
        if (group == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        GetRoleResp role = getRole(req.getGroupId(), req.getOperator(), req.getAppId());
        GetRoleResp beRole = getRole(req.getGroupId(), req.getMemberId(), req.getAppId());
        if(!isAdmin){
            if(role.getRole()==GroupMemberRoleEnum.ORDINARY.getCode()){
                throw new ApplicationException(GroupErrorCode.PERMISSION_ERROR);
            }
            if(beRole.getRole()==GroupMemberRoleEnum.OWNER.getCode()){
                throw new ApplicationException(GroupErrorCode.PERMISSION_ERROR);
            }
            if(Objects.equals(role.getRole(), beRole.getRole())){
                throw new ApplicationException(GroupErrorCode.PERMISSION_ERROR);
            }
        }
        ImGroupMember imGroupMember = new ImGroupMember();
        imGroupMember.setGroupMemberId(beRole.getGroupMemberId());
        if(req.getSpeakDate()>0){
            imGroupMember.setSpeakDate(DateTime.now().getTime()+req.getSpeakDate());
        }else{
            imGroupMember.setSpeakDate(req.getSpeakDate());
        }
        imGroupMemberMapper.updateById(imGroupMember);
        return ResponseVO.successResponse();
    }

    @Resource
    @Lazy
    private ImGroupService imGroupService;

    @Override
    public List<AddGroupMemberResp> importGroupMember(ImportGroupMemberReq req) {
        ImGroup group = imGroupService.getImGroup(req.getGroupId(), req.getAppId());
        if(group == null) {
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        ArrayList<AddGroupMemberResp> resp = new ArrayList<>();

        for(GroupMemberDTO member:req.getMembers()){
            AddGroupMemberResp addGroupMemberResp = new AddGroupMemberResp();
            addGroupMemberResp.setMemberId(member.getMemberId());
            try {
                ImGroupMemberServiceImpl proxy = (ImGroupMemberServiceImpl)AopContext.currentProxy();
                proxy.doAddGroupMember(req.getGroupId(),member,req.getAppId());

                addGroupMemberResp.setResult(AddGroupMemberEnum.SUCCESS.getCode());
                addGroupMemberResp.setResultMessage(AddGroupMemberEnum.SUCCESS.getDesc());
            } catch (ApplicationException e) {
                if(e.getCode()== AddGroupMemberEnum.INSERT_ERROR.getCode()){
                    addGroupMemberResp.setResult(AddGroupMemberEnum.INSERT_ERROR.getCode());
                    addGroupMemberResp.setResultMessage(AddGroupMemberEnum.INSERT_ERROR.getDesc());
                } else if (e.getCode() == AddGroupMemberEnum.HAVE_EXIST.getCode()) {
                    addGroupMemberResp.setResult(AddGroupMemberEnum.HAVE_EXIST.getCode());
                    addGroupMemberResp.setResultMessage(AddGroupMemberEnum.HAVE_EXIST.getDesc());
                }
            }finally {
                resp.add(addGroupMemberResp);
            }
        }

        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doAddGroupMember(String groupId, GroupMemberDTO member, Integer appId) {
        if(member.getRole()!=null && member.getRole()== GroupMemberRoleEnum.OWNER.getCode()){
            LambdaQueryWrapper<ImGroupMember> eq = new LambdaQueryWrapper<ImGroupMember>()
                    .eq(ImGroupMember::getGroupId, groupId)
                    .eq(ImGroupMember::getAppId, appId)
                    .eq(ImGroupMember::getRole, GroupMemberRoleEnum.OWNER.getCode());

            Long count = imGroupMemberMapper.selectCount(eq);
            if(count>0){
                throw new ApplicationException(GroupErrorCode.GROUP_IS_HAVE_OWNER);
            }
        }

        ImGroupMember imGroupMember = BeanUtil.copyProperties(member, ImGroupMember.class);
        imGroupMember.setGroupId(groupId);
        imGroupMember.setAppId(appId);
        LambdaQueryWrapper<ImGroupMember> eq = new LambdaQueryWrapper<ImGroupMember>()
                .eq(ImGroupMember::getGroupId, groupId)
                .eq(ImGroupMember::getAppId, appId)
                .eq(ImGroupMember::getMemberId, imGroupMember.getMemberId());

        ImGroupMember groupMember = imGroupMemberMapper.selectOne(eq);
        if(groupMember==null){
            imGroupMember.setGroupMemberId(IdUtil.getSnowflakeNextId());
            try {
                imGroupMemberMapper.insert(imGroupMember);
            } catch (Exception e) {
                throw new ApplicationException(AddGroupMemberEnum.INSERT_ERROR);
            }
        }else{
            if(groupMember.getRole()!=GroupMemberRoleEnum.LEAVE.getCode()){
                throw new ApplicationException(AddGroupMemberEnum.HAVE_EXIST);
            }else{
                imGroupMember.setGroupMemberId(groupMember.getGroupMemberId());
                try {
                    imGroupMemberMapper.updateById(imGroupMember);
                } catch (Exception e) {
                    throw new ApplicationException(AddGroupMemberEnum.INSERT_ERROR);
                }
            }
        }
    }

    @Override
    public GetRoleResp getRole(String groupId, String operator, Integer appId) {
        LambdaQueryWrapper<ImGroupMember> eq = new LambdaQueryWrapper<ImGroupMember>()
                .eq(ImGroupMember::getGroupId, groupId)
                .eq(ImGroupMember::getAppId, appId)
                .eq(ImGroupMember::getMemberId, operator);
        ImGroupMember imGroupMember = imGroupMemberMapper.selectOne(eq);
        if(imGroupMember==null||imGroupMember.getRole()==GroupMemberRoleEnum.LEAVE.getCode()){
            throw new ApplicationException(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }
        return BeanUtil.copyProperties(imGroupMember, GetRoleResp.class);
    }

    @Override
    public List<GroupMemberDTO> getGroupMember(String groupId, Integer appId) {
        return imGroupMemberMapper.getGroupMember(groupId,appId);
    }

    @Override
    public List<String> getJoined(String memberId, Integer appId) {
        return imGroupMemberMapper.getJoined(memberId,appId);
    }

    @Override
    public void transfer(String groupId,String oldOwner, String newOwner, Integer appId) {
        LambdaUpdateWrapper<ImGroupMember> eq = new LambdaUpdateWrapper<ImGroupMember>()
                .eq(ImGroupMember::getMemberId, oldOwner)
                .eq(ImGroupMember::getAppId, appId)
                .eq(ImGroupMember::getGroupId, groupId);
        ImGroupMember imGroupMember = new ImGroupMember();
        imGroupMember.setRole(GroupMemberRoleEnum.ORDINARY.getCode());
        imGroupMemberMapper.update(imGroupMember, eq);

        LambdaUpdateWrapper<ImGroupMember> newEq = new LambdaUpdateWrapper<ImGroupMember>()
                .eq(ImGroupMember::getMemberId, newOwner)
                .eq(ImGroupMember::getAppId, appId)
                .eq(ImGroupMember::getGroupId, groupId);
        imGroupMember.setRole(GroupMemberRoleEnum.OWNER.getCode());
        imGroupMemberMapper.update(imGroupMember,newEq);
    }

    /**
     * 拉人入群的逻辑，只有私有群才可以调用本接口
     * @param req
     * @return
     */
    @Override
    public ResponseVO addGroupMember(AddGroupMemberReq req) {
        ImGroup group = imGroupService.getImGroup(req.getGroupId(), req.getAppId());
        if(group==null){
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        if(group.getGroupType()== GroupTypeEnum.PUBLIC.getCode()){
            throw new ApplicationException(GroupErrorCode.PUBLIC_GROUP_CAN_NOT_ADD);
        }

        for(GroupMemberDTO memberDTO:req.getMembers()){
            ImGroupMemberServiceImpl proxy = (ImGroupMemberServiceImpl)AopContext.currentProxy();
            proxy.doAddGroupMember(req.getGroupId(), memberDTO,req.getAppId());
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO removeGroupMember(RemoveGroupMemberReq req) {
        boolean isAdmin = false;

        ImGroup group = imGroupService.getImGroup(req.getGroupId(), req.getAppId());
        if(group==null){
            throw new ApplicationException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }

        if(!isAdmin){
            GetRoleResp role = getRole(req.getGroupId(), req.getOperator(), req.getAppId());
            if(role.getRole()==GroupMemberRoleEnum.ORDINARY.getCode()){
                throw new ApplicationException(GroupErrorCode.PERMISSION_ERROR);
            }
            GetRoleResp roleResp = getRole(req.getGroupId(), req.getMemberId(), req.getAppId());
            if(roleResp.getRole()==GroupMemberRoleEnum.OWNER.getCode()){
                throw new ApplicationException(GroupErrorCode.PERMISSION_ERROR);
            }
            if(Objects.equals(role.getRole(), roleResp.getRole())){
                throw new ApplicationException(GroupErrorCode.PERMISSION_ERROR);
            }
            if(group.getGroupType()== GroupTypeEnum.PRIVATE.getCode()
            && role.getRole()==GroupMemberRoleEnum.MAMAGER.getCode()){
                throw new ApplicationException(GroupErrorCode.PERMISSION_ERROR);
            }
        }

        return doRemoveGroupMember(req.getGroupId(),req.getAppId(),req.getMemberId());
    }

    public ResponseVO doRemoveGroupMember(String groupId, Integer appId, String memberId) {
        ImGroupMember imGroupMember = new ImGroupMember();
        imGroupMember.setRole(GroupMemberRoleEnum.LEAVE.getCode());
        LambdaUpdateWrapper<ImGroupMember> eq = new LambdaUpdateWrapper<ImGroupMember>()
                .eq(ImGroupMember::getGroupId, groupId)
                .eq(ImGroupMember::getAppId, appId)
                .eq(ImGroupMember::getMemberId, memberId);
        update(imGroupMember, eq);
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO exitGroupMember(ExitGroupMemberReq req) {
        return doRemoveGroupMember(req.getGroupId(),req.getAppId(),req.getOperator());
    }
}




