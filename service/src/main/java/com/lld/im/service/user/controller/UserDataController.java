package com.lld.im.service.user.controller;


import com.lld.im.common.ResponseVO;
import com.lld.im.service.user.dao.ImUserData;
import com.lld.im.common.enums.user.UserErrorCode;
import com.lld.im.service.user.model.req.GetSingleUserInfoReq;
import com.lld.im.service.user.model.req.GetUserInfoReq;
import com.lld.im.service.user.model.req.ModifyUserInfoReq;
import com.lld.im.service.user.model.resp.GetUserInfoResp;
import com.lld.im.service.user.service.ImUserDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/user/data")
@RequiredArgsConstructor
public class UserDataController {

    private final ImUserDataService userService;

    @RequestMapping("/getStringUserInfo")
    public ResponseVO getStringUserInfo(@RequestBody @Valid GetSingleUserInfoReq userInfo) {
        ImUserData singleUserInfo = userService.getSingleUserInfo(userInfo.getUserId(),userInfo.getAppId());
        if(singleUserInfo != null) {
            return ResponseVO.successResponse(singleUserInfo);
        }
        return ResponseVO.errorResponse(UserErrorCode.USER_IS_NOT_EXIST);
    }

    @RequestMapping("/getUserInfo")
    public ResponseVO<GetUserInfoResp> getUserInfo(@RequestBody @Validated GetUserInfoReq req) {
        GetUserInfoResp userInfo = userService.getUserInfo(req);
        return ResponseVO.successResponse(userInfo);
    }

    @RequestMapping("/modifyUserInfo")
    public ResponseVO modifyUserInfo(@RequestBody @Validated ModifyUserInfoReq req) {
        return userService.modifyUserInfo(req);
    }

}
;