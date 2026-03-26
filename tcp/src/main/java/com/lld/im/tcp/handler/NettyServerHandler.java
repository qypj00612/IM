package com.lld.im.tcp.handler;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lld.im.codec.pack.LoginPack;
import com.lld.im.codec.proto.Message;
import com.lld.im.codec.proto.MessageHeader;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.ImConnectStatusEnums;
import com.lld.im.common.enums.command.SystemCommand;
import com.lld.im.common.model.UserClientDto;
import com.lld.im.common.model.UserSession;
import com.lld.im.tcp.Redis.RedisManager;
import com.lld.im.tcp.publish.MqMessageProducer;
import com.lld.im.tcp.utils.SessionSocketHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.net.InetAddress;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    private final Integer brokerId;

    private final MqMessageProducer mqMessageProducer;

    public NettyServerHandler(Integer brokerId, MqMessageProducer mqMessageProducer) {
        this.brokerId = brokerId;
        this.mqMessageProducer = mqMessageProducer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        MessageHeader messageHeader = msg.getMessageHeader();
        Integer command = messageHeader.getCommand();

        if(command == SystemCommand.LOGIN.getCommand()){
            LoginPack loginPack = JSON.parseObject(JSON.toJSONString(msg.getMessagePack())
                    , new TypeReference<LoginPack>() {
                    }.getType());

            // 将会话信息存入 redis 中
            UserSession userSession = new UserSession();
            userSession.setUserId(loginPack.getUserId());
            userSession.setAppId(messageHeader.getAppId());
            userSession.setClientType(messageHeader.getClientType());
            userSession.setVersion(messageHeader.getVersion());
            userSession.setConnectState(ImConnectStatusEnums.ON_LINE.getCode());
            userSession.setImei(messageHeader.getImei());
            userSession.setBrokerId(brokerId);

            InetAddress localHost = InetAddress.getLocalHost();
            userSession.setBrokerHost(localHost.getHostAddress());

            RedissonClient redissonClient = RedisManager.getRedissonClient();
            RMap<String, String> map = redissonClient.getMap(
                    messageHeader.getAppId()
                            + Constants.RedisConstants.UserSessionConstant
                            + loginPack.getUserId());
            map.put(messageHeader.getClientType()+":"+messageHeader.getImei()
                    , JSONObject.toJSONString(userSession));

            // channel 绑定用户信息
            ctx.channel().attr(AttributeKey.valueOf(Constants.UserId)).set(loginPack.getUserId());
            ctx.channel().attr(AttributeKey.valueOf(Constants.AppId)).set(messageHeader.getAppId());
            ctx.channel().attr(AttributeKey.valueOf(Constants.ClientType)).set(messageHeader.getClientType());
            ctx.channel().attr(AttributeKey.valueOf(Constants.imei)).set(messageHeader.getImei());

            // 将channel 存入 session 管理
            SessionSocketHandler.put(messageHeader.getAppId()
                    ,loginPack.getUserId()
                    , messageHeader.getClientType()
                    , messageHeader.getImei()
                    , (NioSocketChannel) ctx.channel());

            // 广播用户登录
            UserClientDto userClientDto = new UserClientDto();
            userClientDto.setUserId(userSession.getUserId());
            userClientDto.setAppId(userSession.getAppId());
            userClientDto.setClientType(userSession.getClientType());
            userClientDto.setImei(userSession.getImei());
            mqMessageProducer.sendMessage(
                    Constants.RocketConstants.IM_BROADCAST,
                    Constants.RocketConstants.USER_LOGIN,
                    userClientDto
            );

        } else if (command == SystemCommand.LOGOUT.getCommand()) {
            // 将 channel 和会话信息从 session 管理和 redis 中移除
            SessionSocketHandler.loginOut((NioSocketChannel) ctx.channel());
        } else if (command == SystemCommand.PING.getCommand()) {
            long time = DateTime.now().getTime();
            ctx.channel().attr(AttributeKey.valueOf(Constants.ReadTime)).set(time);
        }
    }
}
