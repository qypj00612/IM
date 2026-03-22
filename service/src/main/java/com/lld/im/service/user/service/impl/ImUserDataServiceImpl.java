package com.lld.im.service.user.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lld.im.common.ResponseVO;
import com.lld.im.service.user.dao.ImUserData;
import com.lld.im.service.user.dao.mapper.ImUserDataMapper;
import com.lld.im.service.user.enums.UserErrorCode;
import com.lld.im.service.user.model.req.DeleteUserReq;
import com.lld.im.service.user.model.req.GetUserInfoReq;
import com.lld.im.service.user.model.req.ImportUserReq;
import com.lld.im.service.user.model.req.ModifyUserInfoReq;
import com.lld.im.service.user.model.resp.DeleteUserResp;
import com.lld.im.service.user.model.resp.GetUserInfoResp;
import com.lld.im.service.user.model.resp.ImportUserResp;
import com.lld.im.service.user.service.ImUserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Ypj
* @description 针对表【im_user_data(IM用户基础信息表)】的数据库操作Service实现
* @createDate 2026-03-12 21:30:19
*/
@Service
@RequiredArgsConstructor
public class ImUserDataServiceImpl extends ServiceImpl<ImUserDataMapper, ImUserData>
    implements ImUserDataService {

    private final ImUserDataMapper imUserDataMapper;

    @Override
    public ImportUserResp importUser(ImportUserReq userData) {

        List<ImUserData> userDataList = userData.getUserData();

        if(userDataList.size() > 100){
            // TODO 插入人数过多

        }

        List<String> success = new ArrayList<>();
        List<String> error = new ArrayList<>();
        userDataList.forEach(e -> {
            e.setAppId(userData.getAppId());
            try {
                imUserDataMapper.insert(e);
                success.add(e.getUserId());
            } catch (Exception ex) {
                LambdaQueryWrapper<ImUserData> eq = new LambdaQueryWrapper<ImUserData>()
                        .eq(ImUserData::getUserId, e.getUserId())
                        .eq(ImUserData::getAppId, e.getAppId())
                        .eq(ImUserData::getDelFlag, 1);
                ImUserData imUserData = imUserDataMapper.selectOne(eq);;
                if(imUserData != null){
                    imUserData.setDelFlag(0);
                    imUserDataMapper.updateById(imUserData);
                    success.add(e.getUserId());
                }else{
                    error.add(e.getUserId());
                }
            }
        });
        ImportUserResp importUserResp = new ImportUserResp();

        importUserResp.setSuccess(success);
        importUserResp.setError(error);

        return importUserResp;
    }

    @Override
    public GetUserInfoResp getUserInfo(GetUserInfoReq req) {
        LambdaQueryWrapper<ImUserData> eq = new LambdaQueryWrapper<ImUserData>()
                .in(ImUserData::getUserId, req.getUserIds())
                .eq(ImUserData::getAppId, req.getAppId())
                .eq(ImUserData::getDelFlag, 0);

        List<ImUserData> imUserData = imUserDataMapper.selectList(eq);

        Map<String, ImUserData> collect = imUserData.stream().collect(Collectors.toMap(
                ImUserData::getUserId
                , e -> e
                , (oldValue, newValue) -> oldValue));

        List<String> fail = new ArrayList<>();
        req.getUserIds().forEach(e->{
            if(!collect.containsKey(e)){
                fail.add(e);
            }
        });

        GetUserInfoResp getUserInfoResp = new GetUserInfoResp();
        getUserInfoResp.setFailUser(fail);
        getUserInfoResp.setUserDataItem(new ArrayList<>(collect.values()));

        return getUserInfoResp;
    }

    @Override
    public ImUserData getSingleUserInfo(String userId, Integer appId) {
        LambdaQueryWrapper<ImUserData> eq = new LambdaQueryWrapper<ImUserData>()
                .eq(ImUserData::getUserId, userId)
                .eq(ImUserData::getAppId, appId)
                .eq(ImUserData::getDelFlag, 0);

        return imUserDataMapper.selectOne(eq);
    }

    @Override
    public DeleteUserResp deleteUser(DeleteUserReq req) {
        ArrayList<String> success = new ArrayList<>();
        ArrayList<String> fail = new ArrayList<>();
        for (String s : req.getUserId()) {
            LambdaUpdateWrapper<ImUserData> eq = new LambdaUpdateWrapper<ImUserData>()
                    .eq(ImUserData::getUserId, s)
                    .eq(ImUserData::getAppId,req.getAppId())
                    .eq(ImUserData::getDelFlag, 0);
            try {
                ImUserData upData = new ImUserData();
                upData.setDelFlag(1);
                int update = imUserDataMapper.update(upData,eq);
                if (update > 0) {
                    success.add(s);
                }else{
                    fail.add(s);
                }
            } catch (Exception e) {
                fail.add(s);
            }
        }

        DeleteUserResp deleteUserResp = new DeleteUserResp();
        deleteUserResp.setSuccess(success);
        deleteUserResp.setError(fail);

        return deleteUserResp;
    }

    @Override
    public ResponseVO modifyUserInfo(ModifyUserInfoReq req) {
        ImUserData update = BeanUtil.copyProperties(req, ImUserData.class);
        LambdaUpdateWrapper<ImUserData> eq = new LambdaUpdateWrapper<ImUserData>()
                .eq(ImUserData::getUserId, update.getUserId())
                .eq(ImUserData::getAppId, update.getAppId())
                .eq(ImUserData::getDelFlag, 0);

        int updated = imUserDataMapper.update(update, eq);
        if (updated > 0) {
            return ResponseVO.successResponse("修改成功");
        }
        return ResponseVO.errorResponse(UserErrorCode.MODIFY_USER_ERROR);
    }
}