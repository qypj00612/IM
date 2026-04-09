package com.lld.im.service.friendship.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.enums.DelFlagEnum;
import com.lld.im.common.exception.ApplicationException;
import com.lld.im.service.friendship.dao.ImFriendshipGroup;
import com.lld.im.common.enums.friendship.FriendShipGroupErrorCode;
import com.lld.im.service.friendship.model.req.AddFriendshipGroupReq;
import com.lld.im.service.friendship.model.req.DeleteFriendshipGroupReq;
import com.lld.im.service.friendship.model.resp.AddFriendshipGroupResp;
import com.lld.im.service.friendship.service.ImFriendshipGroupService;
import com.lld.im.service.friendship.dao.mapper.ImFriendshipGroupMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @author Ypj
* @description 针对表【im_friendship_group(好友分组表)】的数据库操作Service实现
* @createDate 2026-03-18 21:11:07
*/
@Service
@RequiredArgsConstructor
public class ImFriendshipGroupServiceImpl extends ServiceImpl<ImFriendshipGroupMapper, ImFriendshipGroup>
    implements ImFriendshipGroupService{

    private final ImFriendshipGroupMapper imFriendshipGroupMapper;

    @Lazy
    @Resource
    private ImFriendshipGroupMemberServiceImpl imFriendshipGroupMemberService;

    @Override
    public ImFriendshipGroup getFriendGroup(String groupName, String fromId, Integer appId) {
        LambdaQueryWrapper<ImFriendshipGroup> eq = new LambdaQueryWrapper<ImFriendshipGroup>()
                .eq(ImFriendshipGroup::getGroupName, groupName)
                .eq(ImFriendshipGroup::getFromId, fromId)
                .eq(ImFriendshipGroup::getAppId, appId);
        return imFriendshipGroupMapper.selectOne(eq);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddFriendshipGroupResp addGroup(AddFriendshipGroupReq req) {
        ImFriendshipGroup group = getFriendGroup(req.getGroupName(), req.getFromId(), req.getAppId());
        List<String> success = new ArrayList<>();
        List<String> fail = new ArrayList<>();
        if (group == null) {
            group = new ImFriendshipGroup();
            group.setGroupId(IdUtil.getSnowflakeNextId());
            group.setFromId(req.getFromId());
            group.setAppId(req.getAppId());
            group.setGroupName(req.getGroupName());
            group.setCreateTime(DateTime.now().getTime());
            group.setUpdateTime(DateTime.now().getTime());
            group.setDelFlag(DelFlagEnum.NORMAL.getCode());
            try {
                imFriendshipGroupMapper.insert(group);
            } catch (Exception e) {
                throw new ApplicationException(FriendShipGroupErrorCode.CREAT_ERROR);
            }
            for(String id:req.getToIds()){
                try {
                    imFriendshipGroupMemberService.doAddGroupMember(group.getGroupId(), id);
                    success.add(id);
                } catch (Exception e) {
                    fail.add(id);
                }
            }
        }else{
            if(group.getDelFlag()== DelFlagEnum.DELETE.getCode()){
                ImFriendshipGroup update = new ImFriendshipGroup();
                update.setGroupId(group.getGroupId());
                update.setFromId(req.getFromId());
                update.setAppId(req.getAppId());
                update.setGroupName(req.getGroupName());
                update.setCreateTime(DateTime.now().getTime());
                update.setUpdateTime(DateTime.now().getTime());
                update.setDelFlag(DelFlagEnum.NORMAL.getCode());
                imFriendshipGroupMapper.updateById(update);
                for(String id:req.getToIds()){
                    try {
                        imFriendshipGroupMemberService.doAddGroupMember(update.getGroupId(), id);
                        success.add(id);
                    } catch (Exception e) {
                        fail.add(id);
                    }
                }
            }else{
                throw new ApplicationException(FriendShipGroupErrorCode.GROUP_EXIST);
            }
        }
        AddFriendshipGroupResp resp = new AddFriendshipGroupResp();
        resp.setSuccess(success);
        resp.setFail(fail);
        return resp;
    }

    @Override
    public ResponseVO delGroup(DeleteFriendshipGroupReq req) {
        for(String name:req.getGroupName()){
            ImFriendshipGroup group = getFriendGroup(name, req.getFromId(), req.getAppId());
            if(group != null&&group.getDelFlag()==DelFlagEnum.NORMAL.getCode()){
                imFriendshipGroupMemberService.clearGroupMember(group.getGroupId());

                ImFriendshipGroup imFriendshipGroup = new ImFriendshipGroup();
                imFriendshipGroup.setGroupId(group.getGroupId());
                imFriendshipGroup.setDelFlag(DelFlagEnum.DELETE.getCode());
                imFriendshipGroupMapper.updateById(imFriendshipGroup);
            }
        }
        return ResponseVO.successResponse();
    }

}




