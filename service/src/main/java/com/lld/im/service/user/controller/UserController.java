package com.lld.im.service.user.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.model.req.SyncReq;
import com.lld.im.common.route.RouteHandle;
import com.lld.im.common.route.RouteInfo;
import com.lld.im.service.message.service.MessageReceiveAckService;
import com.lld.im.service.user.model.req.*;
import com.lld.im.service.user.model.resp.DeleteUserResp;
import com.lld.im.service.user.model.resp.ImportUserResp;
import com.lld.im.service.user.model.resp.UserStatusResp;
import com.lld.im.service.user.service.ImUserDataService;
import com.lld.im.service.user.service.ImUserStatueService;
import com.lld.im.service.utils.NacosUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final ImUserDataService imUserDataService;

    private final RouteHandle routeHandle;

    private final MessageReceiveAckService messageReceiveAckService;

    private final ImUserStatueService imUserStatueService;

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
        ResponseVO login = imUserDataService.login(req);
        if(login.isOk()){
            List<Instance> im = NacosUtil.getAllImNode("tcp");
            List<RouteInfo> routeInfos = BeanUtil.copyToList(im, RouteInfo.class);
            RouteInfo routeInfo = routeHandle.routeServer(routeInfos,req.getUserId());
            return ResponseVO.successResponse(routeInfo);
        }

        return login;
    }

    @RequestMapping("getUserSeq")
    public ResponseVO getUserSeq(@RequestBody @Validated GetUserSeqReq req) {
        return imUserDataService.getUserSeq(req);
    }

    @RequestMapping("syncOfflineMessage")
    public ResponseVO syncOfflineMessage(@RequestBody @Validated SyncReq req) {
        return messageReceiveAckService.syncOfflineMessage(req);
    }

    @RequestMapping("subscribeUserOnlineStatus")
    public ResponseVO subscribeUserOnlineStatus(@RequestBody @Validated SubscribeUserOnlineReq req) {
        return imUserStatueService.subscribeUserOnlineStatus(req);
    }

    @RequestMapping("setUserClientStatus")
    public ResponseVO setUserClientStatus(@RequestBody @Validated SetUserClientStatusReq req) {
        return imUserStatueService.setUserClientStatus(req);
    }

    @RequestMapping("pullUserOnlineStatue")
    public ResponseVO pullUserOnlineStatue(@RequestBody @Validated PullUserOnlineStatusReq req) {
        Map<String, UserStatusResp> resp = imUserStatueService.pullUserOnlineStatue(req);
        return ResponseVO.successResponse(resp);
    }

    @RequestMapping("pullFriendOnlineStatus")
    public ResponseVO pullFriendOnlineStatus(@RequestBody @Validated PullFriendOnlineStatueReq req) {
        Map<String, UserStatusResp> resp = imUserStatueService.pullFriendOnlineStatus(req);
        return ResponseVO.successResponse(resp);
    }

    @GetMapping("hello")
    public String hello() {
        return "hello";
    }
}
