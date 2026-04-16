package com.lld.im.service.user.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.lld.im.codec.pack.user.UserClientStatusChangeNotifyPack;
import com.lld.im.codec.pack.user.UserStatusChangeNotifyPack;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.command.user.UserEventCommand;
import com.lld.im.common.model.ClientInfo;
import com.lld.im.common.model.UserSession;
import com.lld.im.common.model.user.UserStatusModifyContent;
import com.lld.im.service.friendship.service.ImFriendshipService;
import com.lld.im.service.user.model.req.PullFriendOnlineStatueReq;
import com.lld.im.service.user.model.req.PullUserOnlineStatusReq;
import com.lld.im.service.user.model.req.SetUserClientStatusReq;
import com.lld.im.service.user.model.req.SubscribeUserOnlineReq;
import com.lld.im.service.user.model.resp.UserStatusResp;
import com.lld.im.service.user.service.ImUserStatueService;
import com.lld.im.service.utils.MessageProducer;
import com.lld.im.service.utils.UserSessionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImUserStatueServiceImpl implements ImUserStatueService {

    private final UserSessionUtil userSessionUtil;

    private final MessageProducer messageProducer;

    private final ImFriendshipService imFriendshipService;

    private final RedisTemplate redisTemplate;

    @Override
    public void processUserOnlineStatusNotify(UserStatusModifyContent content) {
        List<UserSession> userSession = userSessionUtil.getUserSession(content.getAppId(), content.getUserId());
        UserStatusChangeNotifyPack pack = new UserStatusChangeNotifyPack();
        pack.setAppId(content.getAppId());
        pack.setUserId(content.getUserId());
        pack.setStatus(content.getStatus());
        pack.setClient(userSession);

        // 同步给其他端
        syncSender(
                pack,
                new ClientInfo(content.getAppId(),content.getClientType(),content.getImei()),
                content.getUserId()
        );

        // 分发给在线好友和临时订阅自己的人
        dispatch(
                pack,
                content.getUserId(),
                content.getAppId()
        );
    }

    @Override
    public ResponseVO subscribeUserOnlineStatus(SubscribeUserOnlineReq req) {
        List<String> subIds = req.getSubId();
        Long expireTime = 0L;
        if(req.getSubTime() != null && req.getSubTime() >= 0){
            expireTime = DateTime.now().getTime()+req.getSubTime();
        }
        for(String subId : subIds){
            String key = req.getAppId()+Constants.RedisConstants.BeSubscribeUser+subId;
            redisTemplate.opsForHash().put(key, req.getOperator(), expireTime);
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO setUserClientStatus(SetUserClientStatusReq req) {
        UserClientStatusChangeNotifyPack pack = new UserClientStatusChangeNotifyPack();
        pack.setClientText(req.getClientText());
        pack.setClientStatus(req.getClientStatus());
        pack.setUserId(req.getUserId());

        String key = req.getAppId()+Constants.RedisConstants.ClientStatus+req.getUserId();
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(pack));

        syncSender(
                pack,
                new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()),
                req.getUserId()
        );

        dispatch(
                pack,
                req.getUserId(),
                req.getAppId()
        );
        return ResponseVO.successResponse();
    }

    @Override
    public Map<String, UserStatusResp> pullFriendOnlineStatus(PullFriendOnlineStatueReq req) {
        List<String> allFriendIds = imFriendshipService.getAllFriendIds(req.getOperator(), req.getAppId());
        return getUserStatus(allFriendIds,req.getAppId());
    }

    @Override
    public Map<String, UserStatusResp> pullUserOnlineStatue(PullUserOnlineStatusReq req) {
        return getUserStatus(req.getUserIds(),req.getAppId());
    }

    private Map<String, UserStatusResp> getUserStatus(List<String> userIds, Integer appId) {
        Map<String, UserStatusResp> map = new HashMap<>();
        for(String userId : userIds){
            UserStatusResp userStatusResp = new UserStatusResp();

            List<UserSession> userSession = userSessionUtil.getUserSession(appId, userId);

            userStatusResp.setUserSessions(userSession);

            String key = appId + Constants.RedisConstants.ClientStatus + userId;
            String o = (String)redisTemplate.opsForValue().get(key);
            if(StrUtil.isNotBlank(o)){
                JSONObject jsonObject = JSONObject.parseObject(o);
                String clientText = jsonObject.getString("clientText");
                Integer clientStatus = jsonObject.getInteger("clientStatus");

                userStatusResp.setClientText(clientText);
                userStatusResp.setClientStatus(clientStatus);
            }

            map.put(userId, userStatusResp);
        }
        return map;
    }

    private void dispatch(Object pack, String userId, Integer appId) {
        // 获取好友id
        List<String> ids = imFriendshipService.getAllFriendIds(userId, appId);
        for(String id : ids) {
            // 发送给好友的在线端
            messageProducer.sendToUser(
                    id,
                    UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY,
                    pack,
                    appId,
                    Constants.RocketConstants.UserService2Im
            );
        }

        // 发送给临时订阅自己的用户
        String key = appId+Constants.RedisConstants.BeSubscribeUser+userId;
        Set<String> beUserIds = redisTemplate.opsForHash().keys(key);
        for(String beUserId : beUserIds) {
            Long o = (Long) redisTemplate.opsForHash().get(key, beUserId);
            if(o!=0&&o>DateTime.now().getTime()){
                messageProducer.sendToUser(
                        beUserId,
                        UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY,
                        pack,
                        appId,
                        Constants.RocketConstants.UserService2Im
                );
            }else{
                redisTemplate.opsForHash().delete(key, beUserId);
            }
        }
    }

    private void syncSender(Object pack, ClientInfo clientInfo, String userId) {
        messageProducer.sendToUserExceptClient(
                userId,
                UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY_SYNC,
                pack,
                clientInfo,
                Constants.RocketConstants.UserService2Im
        );
    }
}
