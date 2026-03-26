package com.lld.im.service.user.controller;

import com.lld.im.common.ResponseVO;
import com.lld.im.service.user.model.req.DeleteUserReq;
import com.lld.im.service.user.model.req.ImportUserReq;
import com.lld.im.service.user.model.req.LoginReq;
import com.lld.im.service.user.model.resp.DeleteUserResp;
import com.lld.im.service.user.model.resp.ImportUserResp;
import com.lld.im.service.user.service.ImUserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final ImUserDataService imUserDataService;

    @PostMapping("/import")
    public ResponseVO<ImportUserResp> importUser(@RequestBody ImportUserReq userData) {
        //userData.setAppId(appId);
        ImportUserResp importUserResp=imUserDataService.importUser(userData);
        return ResponseVO.successResponse(importUserResp);
    }

    @RequestMapping("/deleteUser")
    public ResponseVO<DeleteUserResp> deleteUser(@RequestBody @Validated DeleteUserReq req) {
        DeleteUserResp deleteUserResp = imUserDataService.deleteUser(req);
        return ResponseVO.successResponse(deleteUserResp);
    }

    @RequestMapping("login")
    public ResponseVO login(@RequestBody @Validated LoginReq req) {
        return imUserDataService.login(req);
    }
    
    @GetMapping("hello")
    public String hello() {
        return "hello";
    }
}
