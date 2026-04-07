package com.lld.im.service.friendship.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lld.im.codec.pack.friend.ApproverFriendRequestPack;
import com.lld.im.codec.pack.friend.ReadAllFriendRequestPack;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.friend.FriendshipEventCommand;
import com.lld.im.common.exception.ApplicationException;
import com.lld.im.service.friendship.dao.ImFriendshipRequest;
import com.lld.im.service.friendship.dao.mapper.ImFriendshipRequestMapper;
import com.lld.im.service.friendship.dto.FriendshipDTO;
import com.lld.im.service.friendship.enums.ApproveFriendRequestStatusEnum;
import com.lld.im.service.friendship.enums.FriendshipErrorCode;
import com.lld.im.service.friendship.model.req.ApproveFriendRequestReq;
import com.lld.im.service.friendship.model.req.GetFriendshipRequestReq;
import com.lld.im.service.friendship.model.req.ReadFriendshipRequestReq;
import com.lld.im.service.friendship.service.ImFriendshipRequestService;
import com.lld.im.service.utils.MessageProducer;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
* @author Ypj
* @description 针对表【im_friendship_request(好友申请表)】的数据库操作Service实现
* @createDate 2026-03-18 19:07:43
*/
@Service
@RequiredArgsConstructor
public class ImFriendshipRequestServiceImpl extends ServiceImpl<ImFriendshipRequestMapper, ImFriendshipRequest>
    implements ImFriendshipRequestService {

    private final ImFriendshipRequestMapper imFriendshipRequestMapper;

    private final MessageProducer messageProducer;

    @Lazy
    @Resource
    private ImFriendshipServiceImpl imFriendshipService;

    // 添加好友申请，A发送给B，目前通知B
    @Override
    public ResponseVO addFriendRequest(String fromId, FriendshipDTO toItem, Integer appId) {
        LambdaQueryWrapper<ImFriendshipRequest> eq = new LambdaQueryWrapper<ImFriendshipRequest>()
                .eq(ImFriendshipRequest::getFromId, fromId)
                .eq(ImFriendshipRequest::getAppId, appId)
                .eq(ImFriendshipRequest::getToId, toItem.getToId());

        ImFriendshipRequest imFriendshipRequest = imFriendshipRequestMapper.selectOne(eq);
        if (imFriendshipRequest == null) {
            ImFriendshipRequest request = BeanUtil.copyProperties(toItem, ImFriendshipRequest.class);
            request.setAppId(appId);
            request.setFromId(fromId);
            request.setCreateTime(DateTime.now().getTime());
            request.setUpdateTime(DateTime.now().getTime());
            request.setReadStatus(0);
            request.setApproveStatus(0);
            imFriendshipRequestMapper.insert(request);
            imFriendshipRequest=request;
        }else{
            ImFriendshipRequest request = new ImFriendshipRequest();
            if(toItem.getAddWording()!=null){
                request.setAddWording(toItem.getAddWording());
            }
            if(toItem.getAddSource()!=null){
                request.setAddSource(toItem.getAddSource());
            }
            if(toItem.getRemark()!=null){
                request.setRemark(toItem.getRemark());
            }
            request.setUpdateTime(DateTime.now().getTime());
            imFriendshipRequestMapper.update(request, eq);
        }

        // 通知tcp
        messageProducer.sendToUser(imFriendshipRequest.getToId(),
                FriendshipEventCommand.FRIEND_REQUEST,
                imFriendshipRequest,
                imFriendshipRequest.getAppId(),
                Constants.RocketConstants.FriendShip2Im);

        return ResponseVO.successResponse();
    }

    @Override
    @Transactional
    public ResponseVO approve(@Valid ApproveFriendRequestReq req) {
        ImFriendshipRequest request = imFriendshipRequestMapper.selectById(req.getId());
        if(request==null){
            throw new ApplicationException(FriendshipErrorCode.FRIEND_REQUEST_IS_NOT_EXIST);
        }

        if(!Objects.equals(req.getOperator(), request.getToId())){
            throw new ApplicationException(FriendshipErrorCode.NOT_APPROVER_OTHER_MAN_REQUEST);
        }

        ImFriendshipRequest update = new ImFriendshipRequest();
        update.setApproveStatus(request.getApproveStatus());
        update.setUpdateTime(DateTime.now().getTime());
        update.setId(req.getId());
        imFriendshipRequestMapper.updateById(update);

        if(req.getStatus()== ApproveFriendRequestStatusEnum.AGREE.getCode()){
            FriendshipDTO friendshipDTO = new FriendshipDTO();
            friendshipDTO.setToId(request.getToId());
            friendshipDTO.setRemark(request.getRemark());
            friendshipDTO.setAddSource(request.getAddSource());
            friendshipDTO.setAddWording(request.getAddWording());

            imFriendshipService.doAddFriend(req,request.getFromId(),friendshipDTO, req.getAppId());

        }

        // 通知tcp，实现多端同步
        ApproverFriendRequestPack pack = BeanUtil.copyProperties(req, ApproverFriendRequestPack.class);
        messageProducer.sendToUser(request.getToId(),
                req.getClientType(),
                req.getImei(),
                FriendshipEventCommand.FRIEND_REQUEST_APPROVER,
                pack,
                req.getAppId(),
                Constants.RocketConstants.FriendShip2Im);

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO read(ReadFriendshipRequestReq req) {
        LambdaUpdateWrapper<ImFriendshipRequest> eq = new LambdaUpdateWrapper<ImFriendshipRequest>()
                .eq(ImFriendshipRequest::getToId, req.getToId())
                .eq(ImFriendshipRequest::getAppId, req.getAppId());

        ImFriendshipRequest update = new ImFriendshipRequest();
        update.setReadStatus(1);

        imFriendshipRequestMapper.update(update, eq);

        // 通知
        ReadAllFriendRequestPack pack = new ReadAllFriendRequestPack();
        pack.setFromId(req.getToId());
        messageProducer.sendToUser(req.getToId(),
                FriendshipEventCommand.FRIEND_REQUEST_READ,
                pack,
                req.getAppId(),
                Constants.RocketConstants.FriendShip2Im);

        return ResponseVO.successResponse();
    }

    @Override
    public List<ImFriendshipRequest> getFriendshipRequest(@Valid GetFriendshipRequestReq req) {
        LambdaQueryWrapper<ImFriendshipRequest> eq = new LambdaQueryWrapper<ImFriendshipRequest>()
                .eq(ImFriendshipRequest::getToId, req.getToId())
                .eq(ImFriendshipRequest::getAppId, req.getAppId());

        return imFriendshipRequestMapper.selectList(eq);
    }
}




