package com.lld.im.service.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lld.im.common.ResponseVO;
import com.lld.im.service.user.dao.ImUserData;
import com.lld.im.service.user.model.req.DeleteUserReq;
import com.lld.im.service.user.model.req.GetUserInfoReq;
import com.lld.im.service.user.model.req.ImportUserReq;
import com.lld.im.service.user.model.req.ModifyUserInfoReq;
import com.lld.im.service.user.model.resp.DeleteUserResp;
import com.lld.im.service.user.model.resp.GetUserInfoResp;
import com.lld.im.service.user.model.resp.ImportUserResp;

/**
* @author Ypj
* @description 针对表【im_user_data(IM用户基础信息表)】的数据库操作Service
* @createDate 2026-03-12 21:30:19
*/
public interface ImUserDataService extends IService<ImUserData> {

    ImportUserResp importUser(ImportUserReq userData);

    GetUserInfoResp getUserInfo(GetUserInfoReq req);

    ImUserData getSingleUserInfo(String userId , Integer appId);

    DeleteUserResp deleteUser(DeleteUserReq req);

    ResponseVO modifyUserInfo(ModifyUserInfoReq req);
}
