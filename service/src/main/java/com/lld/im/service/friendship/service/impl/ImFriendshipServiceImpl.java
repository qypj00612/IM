package com.lld.im.service.friendship.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.exception.ApplicationException;
import com.lld.im.service.friendship.dao.ImFriendship;
import com.lld.im.service.friendship.dao.mapper.ImFriendshipMapper;
import com.lld.im.service.friendship.dto.FriendshipDTO;
import com.lld.im.service.friendship.dto.ImportFriendDTO;
import com.lld.im.service.friendship.enums.AllowFriendTypeEnum;
import com.lld.im.service.friendship.enums.CheckFriendShipTypeEnum;
import com.lld.im.service.friendship.enums.FriendshipErrorCode;
import com.lld.im.service.friendship.enums.FriendshipStatusEnum;
import com.lld.im.service.friendship.model.req.*;
import com.lld.im.service.friendship.model.resp.CheckFriendShipResp;
import com.lld.im.service.friendship.model.resp.ImportFriendshipResp;
import com.lld.im.service.friendship.service.ImFriendshipRequestService;
import com.lld.im.service.friendship.service.ImFriendshipService;
import com.lld.im.service.user.dao.ImUserData;
import com.lld.im.common.enums.user.UserErrorCode;
import com.lld.im.service.user.service.ImUserDataService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
* @author Ypj
* @description 针对表【im_friendship(IM好友关系表)】的数据库操作Service实现
* @createDate 2026-03-15 22:22:41
*/
@Service
@RequiredArgsConstructor
public class ImFriendshipServiceImpl extends ServiceImpl<ImFriendshipMapper, ImFriendship>
    implements ImFriendshipService {

    private final ImFriendshipMapper imFriendshipMapper;

    private final ImUserDataService imUserDataService;

    @Lazy
    @Resource
    private ImFriendshipRequestService imFriendshipRequestService;

    @Override
    public ImportFriendshipResp importFriendship(ImportFriendshipReq importFriendshipReq) {

        if (importFriendshipReq.getFriendItem().size()>100) {
            throw new ApplicationException(FriendshipErrorCode.IMPORT_SIZE_BEYOND);
        }

        List<ImportFriendDTO> friendItem = importFriendshipReq.getFriendItem();

        ArrayList<String> success = new ArrayList<>();
        ArrayList<String> fail = new ArrayList<>();

        for (ImportFriendDTO importFriendDTO : friendItem) {
            ImFriendship imFriendship = new ImFriendship();
            BeanUtil.copyProperties(importFriendDTO, imFriendship);
            imFriendship.setFromId(importFriendshipReq.getFromId());
            imFriendship.setAppId(importFriendshipReq.getAppId());

            try {
                int insert = imFriendshipMapper.insert(imFriendship);
                if (insert > 0) {
                    success.add(importFriendDTO.getToId());
                }else{
                    fail.add(importFriendDTO.getToId());
                }
            } catch (Exception e) {
                fail.add(importFriendDTO.getToId());
            }
        }

        ImportFriendshipResp importFriendshipResp = new ImportFriendshipResp();
        importFriendshipResp.setSuccessIds(success);
        importFriendshipResp.setErrorIds(fail);
        return importFriendshipResp;

    }

    @Override
    public ResponseVO addFriend(AddFriendReq addFriendReq) {
        // 校验双方是否存在
        ImUserData fromInfo = imUserDataService.getSingleUserInfo(addFriendReq.getFromId(), addFriendReq.getAppId());
        if (fromInfo == null) {
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }
        ImUserData toInfo = imUserDataService.getSingleUserInfo(addFriendReq.getToItem().getToId(), addFriendReq.getAppId());
        if(toInfo == null){
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        if(toInfo.getFriendAllowType()!=null&&toInfo.getFriendAllowType()== AllowFriendTypeEnum.NOT_NEED.getCode()){
            // 不需要申请
            ImFriendshipServiceImpl proxy = (ImFriendshipServiceImpl)AopContext.currentProxy();
            return proxy.doAddFriend(addFriendReq.getFromId(),addFriendReq.getToItem(),addFriendReq.getAppId());
        }else{
            // 需要申请
            return imFriendshipRequestService.addFriendRequest(addFriendReq.getFromId()
                    ,addFriendReq.getToItem()
                    ,addFriendReq.getAppId());
        }


    }

    @Override
    public ResponseVO updateFriend(UpdateFriendReq updateFriendReq) {
        ImUserData fromInfo = imUserDataService.getSingleUserInfo(updateFriendReq.getFromId(), updateFriendReq.getAppId());
        if (fromInfo == null) {
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }
        ImUserData toInfo = imUserDataService.getSingleUserInfo(updateFriendReq.getToItem().getToId(), updateFriendReq.getAppId());
        if(toInfo == null){
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        ImFriendshipServiceImpl proxy = (ImFriendshipServiceImpl)AopContext.currentProxy();
        return proxy.doUpdateFriend(updateFriendReq.getFromId(),updateFriendReq.getToItem(),updateFriendReq.getAppId());
    }

    @Override
    public ResponseVO deleteFriend(DeleteFriendReq deleteFriendReq) {
        LambdaQueryWrapper<ImFriendship> eq = new LambdaQueryWrapper<ImFriendship>()
                .eq(ImFriendship::getFromId, deleteFriendReq.getFromId())
                .eq(ImFriendship::getToId, deleteFriendReq.getToId())
                .eq(ImFriendship::getAppId, deleteFriendReq.getAppId());
        ImFriendship imFriendship = imFriendshipMapper.selectOne(eq);
        if (imFriendship == null) {
            throw new ApplicationException(FriendshipErrorCode.TO_IS_NOT_YOUR_FRIEND);
        }

        if(imFriendship.getStatus() == FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode()){
            ImFriendship update = new ImFriendship();
            update.setStatus(FriendshipStatusEnum.FRIEND_STATUS_DELETE.getCode());
            imFriendshipMapper.update(update, eq);
            return ResponseVO.successResponse();
        }else{
            throw new ApplicationException(FriendshipErrorCode.TO_IS_NOT_YOUR_FRIEND);
        }
    }

    @Override
    public ResponseVO deleteAllFriend(DeleteFriendReq deleteFriendReq) {
        LambdaUpdateWrapper<ImFriendship> eq = new LambdaUpdateWrapper<ImFriendship>()
                .eq(ImFriendship::getFromId, deleteFriendReq.getFromId())
                .eq(ImFriendship::getAppId, deleteFriendReq.getAppId())
                .eq(ImFriendship::getStatus, FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        ImFriendship imFriendship = new ImFriendship();
        imFriendship.setStatus(FriendshipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        imFriendshipMapper.update(imFriendship, eq);
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getAllFriend(GetAllFriendshipReq getAllFriendReq) {
        LambdaQueryWrapper<ImFriendship> eq = new LambdaQueryWrapper<ImFriendship>()
                .eq(ImFriendship::getFromId, getAllFriendReq.getFromId())
                .eq(ImFriendship::getAppId, getAllFriendReq.getAppId())
                .eq(ImFriendship::getStatus, FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
        return ResponseVO.successResponse(imFriendshipMapper.selectList(eq));
    }

    @Override
    public ResponseVO getRelationShip(GetRelationshipReq getRelationshipReq) {
        LambdaQueryWrapper<ImFriendship> eq = new LambdaQueryWrapper<ImFriendship>()
                .eq(ImFriendship::getFromId, getRelationshipReq.getFromId())
                .eq(ImFriendship::getToId, getRelationshipReq.getToId())
                .eq(ImFriendship::getAppId, getRelationshipReq.getAppId())
                .eq(ImFriendship::getStatus, FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        ImFriendship imFriendship = imFriendshipMapper.selectOne(eq);
        if (imFriendship == null) {
            throw new ApplicationException(FriendshipErrorCode.REPEATSHIP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(imFriendship);
    }

    @Override
    public List<CheckFriendShipResp> checkFriendShip(CheckFriendShipReq req) {

        Map<String,Integer> result
                = req.getToIds().stream()
                .collect(Collectors.toMap(Function.identity() , s -> 0));

        List<CheckFriendShipResp> resp;

        if(req.getCheckType()== CheckFriendShipTypeEnum.SINGLE.getType()){
            resp = imFriendshipMapper.singleCheck(req);
        }else{
            resp = imFriendshipMapper.bothCheck(req);
        }

        Map<String , Integer> collect = resp.stream()
                .collect(Collectors.toMap(CheckFriendShipResp::getToId , CheckFriendShipResp::getStatus));

        for(String toId : result.keySet()){
            if(!collect.containsKey(toId)){
                CheckFriendShipResp checkFriendShipResp = new CheckFriendShipResp();
                checkFriendShipResp.setFromId(req.getFromId());
                checkFriendShipResp.setToId(toId);
                checkFriendShipResp.setStatus(result.get(toId));
                resp.add(checkFriendShipResp);

            }
        }

        return resp;
    }

    @Override
    public ResponseVO addBlack(AddBlackReq addBlackReq) {
        ImUserData fromInfo = imUserDataService.getSingleUserInfo(addBlackReq.getFromId(),addBlackReq.getAppId());
        if(fromInfo == null){
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }
        ImUserData toInfo = imUserDataService.getSingleUserInfo(addBlackReq.getToId(), addBlackReq.getAppId());
        if(toInfo == null){
            throw new ApplicationException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        LambdaQueryWrapper<ImFriendship> eq = new LambdaQueryWrapper<ImFriendship>()
                .eq(ImFriendship::getFromId, addBlackReq.getFromId())
                .eq(ImFriendship::getToId, addBlackReq.getToId())
                .eq(ImFriendship::getAppId, addBlackReq.getAppId());
        ImFriendship imFriendship = imFriendshipMapper.selectOne(eq);
        if(imFriendship == null){
            ImFriendship insert = new ImFriendship();

            insert.setAppId(addBlackReq.getAppId());
            insert.setFromId(addBlackReq.getFromId());
            insert.setToId(addBlackReq.getToId());
            insert.setStatus(FriendshipStatusEnum.FRIEND_STATUS_NO_FRIEND.getCode());
            insert.setBlack(FriendshipStatusEnum.BLACK_STATUS_BLACKED.getCode());
            int count = imFriendshipMapper.insert(insert);

            if(count == 0){
                throw new ApplicationException(FriendshipErrorCode.ADD_BLACK_ERROR);
            }
        }else{
            if(imFriendship.getBlack()==null
            ||imFriendship.getBlack() == FriendshipStatusEnum.BLACK_STATUS_NORMAL.getCode()){
                ImFriendship update = new ImFriendship();
                update.setBlack(FriendshipStatusEnum.BLACK_STATUS_BLACKED.getCode());

                int count = imFriendshipMapper.update(update, eq);
                if(count == 0){
                    throw new ApplicationException(FriendshipErrorCode.ADD_BLACK_ERROR);
                }
            }else{
                throw new ApplicationException(FriendshipErrorCode.FRIEND_IS_BLACK);
            }
        }
        return ResponseVO.successResponse();
    }


    @Override
    public ResponseVO deleteBlack(DeleteBlackReq deleteBlackReq) {
        LambdaUpdateWrapper<ImFriendship> eq = new LambdaUpdateWrapper<ImFriendship>()
                .eq(ImFriendship::getFromId, deleteBlackReq.getFromId())
                .eq(ImFriendship::getToId, deleteBlackReq.getToId())
                .eq(ImFriendship::getAppId, deleteBlackReq.getAppId());

        ImFriendship imFriendship = new ImFriendship();
        imFriendship.setBlack(FriendshipStatusEnum.BLACK_STATUS_NORMAL.getCode());
        imFriendshipMapper.update(imFriendship, eq);
        return ResponseVO.successResponse();
    }

    @Override
    public List<CheckFriendShipResp> checkBlack(CheckFriendShipReq req) {
        Map<String, Integer> result = req.getToIds().stream()
                .collect(Collectors.toMap(Function.identity(), s -> 0));

        List<CheckFriendShipResp> resp;

        if(req.getCheckType()== CheckFriendShipTypeEnum.SINGLE.getType()){
            resp = imFriendshipMapper.singleBlack(req);
        }else{
            resp = imFriendshipMapper.bothBlack(req);
        }

        Map<String, Integer> collect = resp.stream().collect(
                Collectors.toMap(CheckFriendShipResp::getToId, CheckFriendShipResp::getStatus));

        for(String toId : result.keySet()){
            if(!collect.containsKey(toId)){
                CheckFriendShipResp checkFriendShipResp = new CheckFriendShipResp();
                checkFriendShipResp.setFromId(req.getFromId());
                checkFriendShipResp.setToId(toId);
                checkFriendShipResp.setStatus(result.get(toId));
                resp.add(checkFriendShipResp);
            }
        }

        return resp;
    }

    @Transactional
    public ResponseVO doUpdateFriend(String fromId, FriendshipDTO dto, Integer appId) {
        LambdaQueryWrapper<ImFriendship> eq = new LambdaQueryWrapper<ImFriendship>()
                .eq(ImFriendship::getFromId, fromId)
                .eq(ImFriendship::getAppId, appId)
                .eq(ImFriendship::getToId, dto.getToId());

        ImFriendship imFriendship = BeanUtil.copyProperties(dto, ImFriendship.class);
        imFriendship.setFromId(fromId);
        imFriendship.setAppId(appId);

        imFriendshipMapper.update(imFriendship, eq);

        return ResponseVO.successResponse();
    }

    @Transactional
    public ResponseVO doAddFriend(String fromId, FriendshipDTO dto, Integer appId) {

        // 添加 A 与 B 是好友的记录
        LambdaQueryWrapper<ImFriendship> eq = new LambdaQueryWrapper<ImFriendship>()
                .eq(ImFriendship::getFromId, fromId)
                .eq(ImFriendship::getAppId, appId)
                .eq(ImFriendship::getToId, dto.getToId());
        ImFriendship imFriendship = imFriendshipMapper.selectOne(eq);

        if (imFriendship == null) {
            // 没有则添加
            ImFriendship insert = BeanUtil.copyProperties(dto, ImFriendship.class);
            insert.setAppId(appId);
            insert.setFromId(fromId);
            insert.setCreateTime(DateTime.now().getTime());
            insert.setStatus(FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

            int count = imFriendshipMapper.insert(insert);
            if(count!=1){
                throw new ApplicationException(FriendshipErrorCode.ADD_FRIEND_ERROR);
            }
        }else{
            if (imFriendship.getStatus() == FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                throw new ApplicationException(FriendshipErrorCode.TO_IS_YOUR_FRIEND);
            }else{
                ImFriendship update = new ImFriendship();
                if(StrUtil.isNotBlank(dto.getRemark())){
                    update.setRemark(dto.getRemark());
                }
                if(StrUtil.isNotBlank(dto.getAddSource())){
                    update.setAddSource(dto.getAddSource());
                }
                if(StrUtil.isNotBlank(dto.getExtra())){
                    update.setExtra(dto.getExtra());
                }
                update.setStatus(FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
                update.setCreateTime(DateTime.now().getTime());

                int count = imFriendshipMapper.update(update, eq);
                if(count!=1){
                    throw new ApplicationException(FriendshipErrorCode.ADD_FRIEND_ERROR);
                }
            }
        }

        // 添加 B 与 A 是好友的记录
        LambdaQueryWrapper<ImFriendship> toEq = new LambdaQueryWrapper<ImFriendship>()
                .eq(ImFriendship::getFromId, dto.getToId())
                .eq(ImFriendship::getAppId, appId)
                .eq(ImFriendship::getToId, fromId);

        ImFriendship toFriendShip = imFriendshipMapper.selectOne(toEq);
        if(toFriendShip == null){
            toFriendShip = BeanUtil.copyProperties(dto, ImFriendship.class);
            toFriendShip.setAppId(appId);
            toFriendShip.setFromId(dto.getToId());
            toFriendShip.setToId(fromId);
            toFriendShip.setCreateTime(DateTime.now().getTime());
            toFriendShip.setStatus(FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            int count = imFriendshipMapper.insert(toFriendShip);
            if(count!=1){
                throw new ApplicationException(FriendshipErrorCode.ADD_FRIEND_ERROR);
            }
        }else{
            if(toFriendShip.getStatus() == FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode()){
                throw new ApplicationException(FriendshipErrorCode.ADD_FRIEND_ERROR);
            }else{
                ImFriendship update = new ImFriendship();
                if(StrUtil.isNotBlank(dto.getRemark())){
                    update.setRemark(dto.getRemark());
                }
                if(StrUtil.isNotBlank(dto.getAddSource())){
                    update.setAddSource(dto.getAddSource());
                }
                if(StrUtil.isNotBlank(dto.getExtra())){
                    update.setExtra(dto.getExtra());
                }
                update.setStatus(FriendshipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
                int count = imFriendshipMapper.update(update, eq);
                if(count!=1){
                    throw new ApplicationException(FriendshipErrorCode.ADD_FRIEND_ERROR);
                }
            }
        }

        return ResponseVO.successResponse();
    }
}




