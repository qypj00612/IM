package com.lld.im.service.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.enums.BaseCode;
import com.lld.im.common.exception.ApplicationException;
import com.lld.im.service.friendship.dao.ImFriendship;
import com.lld.im.service.friendship.dao.ImFriendshipGroup;
import com.lld.im.service.friendship.dao.ImFriendshipGroupMember;
import com.lld.im.service.friendship.dao.mapper.ImFriendshipGroupMapper;
import com.lld.im.service.friendship.dao.mapper.ImFriendshipGroupMemberMapper;
import com.lld.im.service.friendship.model.req.AddFriendshipGroupMemberReq;
import com.lld.im.service.friendship.model.req.DeleteFriendshipGroupMemberReq;
import com.lld.im.service.friendship.model.resp.AddFriendshipGroupMemberResp;
import com.lld.im.service.friendship.model.resp.DeleteFriendshipGroupMemberResp;
import com.lld.im.service.friendship.service.ImFriendshipGroupMemberService;
import com.lld.im.service.friendship.service.ImFriendshipGroupService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @author Ypj
* @description 针对表【im_friendship_group_member(好友分组成员表)】的数据库操作Service实现
* @createDate 2026-03-18 21:07:09
*/
@Service
@RequiredArgsConstructor
public class ImFriendshipGroupMemberServiceImpl extends ServiceImpl<ImFriendshipGroupMemberMapper, ImFriendshipGroupMember>
    implements ImFriendshipGroupMemberService {

    private final ImFriendshipGroupMemberMapper imFriendshipGroupMemberMapper;

    @Lazy
    @Resource
    private ImFriendshipGroupService imFriendshipGroupService;

    @Override
    public AddFriendshipGroupMemberResp addGroupMember(AddFriendshipGroupMemberReq req) {

        ImFriendshipGroup imFriendshipGroup = imFriendshipGroupService.getFriendGroup(req.getGroupName()
                ,req.getFromId(),req.getAppId());
        if (imFriendshipGroup == null) {
            throw new ApplicationException(BaseCode.SYSTEM_ERROR);
        }

        List<String> success = new ArrayList<>();
        List<String> fail = new ArrayList<>();

        for(String id:req.getToIds()){
            try {
                ImFriendshipGroupMemberServiceImpl proxy = (ImFriendshipGroupMemberServiceImpl)AopContext.currentProxy();
                proxy.doAddGroupMember(imFriendshipGroup.getGroupId(),id);
                success.add(id);
            } catch (Exception e) {
                fail.add(id);
            }
        }
        AddFriendshipGroupMemberResp resp = new AddFriendshipGroupMemberResp();
        resp.setSuccess(success);
        resp.setFail(fail);

        return resp;
    }

    @Transactional(rollbackFor = Exception.class)
    public void doAddGroupMember(Long groupId, String toIds) {
        ImFriendshipGroupMember imFriendshipGroupMember = new ImFriendshipGroupMember();
        imFriendshipGroupMember.setGroupId(groupId);
        imFriendshipGroupMember.setToId(toIds);
        try {
            imFriendshipGroupMemberMapper.insert(imFriendshipGroupMember);
        } catch (Exception e) {
            throw new ApplicationException(BaseCode.SYSTEM_ERROR);
        }
    }

    @Override
    public DeleteFriendshipGroupMemberResp delGroupMember(DeleteFriendshipGroupMemberReq req) {
        ImFriendshipGroup imFriendshipGroup = imFriendshipGroupService.getFriendGroup(req.getGroupName()
                ,req.getFromId(),req.getAppId());
        if (imFriendshipGroup == null) {
            throw new ApplicationException(BaseCode.SYSTEM_ERROR);
        }

        List<String> success = new ArrayList<>();
        List<String> fail = new ArrayList<>();
        for(String id:req.getToIds()){
            try {
                ImFriendshipGroupMemberServiceImpl proxy = (ImFriendshipGroupMemberServiceImpl)AopContext.currentProxy();
                proxy.doDelGroupMember(imFriendshipGroup.getGroupId(),id);
                success.add(id);
            } catch (IllegalStateException e) {
                fail.add(id);
            }
        }
        DeleteFriendshipGroupMemberResp resp = new DeleteFriendshipGroupMemberResp();
        resp.setSuccess(success);
        resp.setFail(fail);
        return resp;
    }

    @Transactional
    public void doDelGroupMember(Long groupId, String toIds) {
        LambdaQueryWrapper<ImFriendshipGroupMember> eq = new LambdaQueryWrapper<ImFriendshipGroupMember>()
                .eq(ImFriendshipGroupMember::getGroupId, groupId)
                .eq(ImFriendshipGroupMember::getToId, toIds);
        imFriendshipGroupMemberMapper.delete(eq);
    }

    public void clearGroupMember(Long groupId) {
        LambdaQueryWrapper<ImFriendshipGroupMember> eq = new LambdaQueryWrapper<ImFriendshipGroupMember>()
                .eq(ImFriendshipGroupMember::getGroupId, groupId);
        imFriendshipGroupMemberMapper.delete(eq);
    }
}