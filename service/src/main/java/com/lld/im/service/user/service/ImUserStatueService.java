package com.lld.im.service.user.service;

import com.lld.im.common.ResponseVO;
import com.lld.im.common.model.user.UserStatusModifyContent;
import com.lld.im.service.user.model.req.PullFriendOnlineStatueReq;
import com.lld.im.service.user.model.req.PullUserOnlineStatusReq;
import com.lld.im.service.user.model.req.SetUserClientStatusReq;
import com.lld.im.service.user.model.req.SubscribeUserOnlineReq;
import com.lld.im.service.user.model.resp.UserStatusResp;

import java.util.Map;

public interface ImUserStatueService {

    /**
     * 接收到用户在线信息改变之后的处理
     * @param content
     */
    void processUserOnlineStatusNotify(UserStatusModifyContent content);

    /**
     * 临时订阅用户的在线信息
     * @param req
     * @return
     */
    ResponseVO subscribeUserOnlineStatus(SubscribeUserOnlineReq req);

    /**
     * 自定义用户客户端状态
     * @param req
     * @return
     */
    ResponseVO setUserClientStatus(SetUserClientStatusReq req);

    /**
     * 获取指定用户状态
     * @param req
     * @return
     */
    Map<String, UserStatusResp> pullFriendOnlineStatus(PullFriendOnlineStatueReq req);

    /**
     * 获取好友状态
     * @param req
     * @return
     */
    Map<String, UserStatusResp> pullUserOnlineStatue(PullUserOnlineStatusReq req);
}
