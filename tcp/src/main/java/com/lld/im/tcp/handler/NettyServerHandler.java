package com.lld.im.tcp.handler;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lld.im.codec.pack.LoginAckPack;
import com.lld.im.codec.pack.LoginPack;
import com.lld.im.codec.pack.MessagePack;
import com.lld.im.codec.pack.message.ChatMessageAck;
import com.lld.im.codec.pack.user.UserStatusChangeNotifyPack;
import com.lld.im.codec.proto.Message;
import com.lld.im.codec.proto.MessageHeader;
import com.lld.im.common.ResponseVO;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.ImConnectStatusEnums;
import com.lld.im.common.enums.command.MessageCommand;
import com.lld.im.common.enums.command.SystemCommand;
import com.lld.im.common.enums.command.group.GroupEventCommand;
import com.lld.im.common.enums.command.user.UserEventCommand;
import com.lld.im.common.model.UserClientDto;
import com.lld.im.common.model.UserSession;
import com.lld.im.common.model.message.req.CheckSendMessageReq;
import com.lld.im.tcp.Redis.RedisManager;
import com.lld.im.tcp.feign.FeignMessageService;
import com.lld.im.tcp.publish.MqMessageProducer;
import com.lld.im.tcp.utils.SessionSocketHandler;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
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

    private final FeignMessageService feignMessageService;

    public NettyServerHandler(Integer brokerId, MqMessageProducer mqMessageProducer, String url) {
        this.brokerId = brokerId;
        this.mqMessageProducer = mqMessageProducer;

        feignMessageService = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .options(new Request.Options(1000,3500))
                .target(FeignMessageService.class, url);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        MessageHeader messageHeader = msg.getMessageHeader();
        Integer command = messageHeader.getCommand();
        log.info("接收到消息，指令为:{}", command);

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
            Message message = new Message();
            message.setMessagePack(userClientDto);
            mqMessageProducer.sendMessage(
                    Constants.RocketConstants.IM_BROADCAST,
                    Constants.RocketConstants.USER_LOGIN,
                    message
            );

            // 通知 逻辑服务层 用户登录
            UserStatusChangeNotifyPack pack = new UserStatusChangeNotifyPack();
            pack.setAppId(userSession.getAppId());
            pack.setUserId(userSession.getUserId());
            pack.setStatus(ImConnectStatusEnums.ON_LINE.getCode());

            messageHeader.setCommand(UserEventCommand.USER_ONLINE_STATUS_CHANGE.getCommand());

            mqMessageProducer.sendMessage(
                    Constants.RocketConstants.IM_TO_SERVICE,
                    Constants.RocketConstants.Im2UserService,
                    messageHeader,
                    pack
            );

            // 回 ack 给客户端表示登陆成功
            LoginAckPack loginAckPack = new LoginAckPack();
            loginAckPack.setUserId(userSession.getUserId());

            MessagePack<LoginAckPack> ack = new MessagePack<>();
            ack.setUserId(userSession.getUserId());
            ack.setAppId(userSession.getAppId());
            ack.setToId(userSession.getUserId());
            ack.setClientType(userSession.getClientType());
            ack.setImei(userSession.getImei());
            ack.setCommand(SystemCommand.LOGIN_ACK.getCommand());
            ack.setData(loginAckPack);

            ctx.channel().writeAndFlush(ack);

            log.info("用户登录: {}", userSession);

        } else if (command == SystemCommand.LOGOUT.getCommand()) {
            // 将 channel 和会话信息从 session 管理和 redis 中移除
            SessionSocketHandler.loginOut((NioSocketChannel) ctx.channel(), mqMessageProducer);
        } else if (command == SystemCommand.PING.getCommand()) {
            long time = DateTime.now().getTime();
            ctx.channel().attr(AttributeKey.valueOf(Constants.ReadTime)).set(time);
        } else if (command == GroupEventCommand.MSG_GROUP.getCommand()) {

            CheckSendMessageReq checkSendMessageReq = new CheckSendMessageReq();
            JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()));
            String fromId = jsonObject.getString("fromId");
            String groupId = jsonObject.getString("groupId");

            checkSendMessageReq.setFromId(fromId);
            checkSendMessageReq.setToId(groupId);
            checkSendMessageReq.setAppId(messageHeader.getAppId());
            checkSendMessageReq.setCommand(command);

            ResponseVO responseVO = feignMessageService.checkGroupSendMessage(checkSendMessageReq);
            if(responseVO.isOk()){
                mqMessageProducer.sendMessage(
                        Constants.RocketConstants.IM_TO_SERVICE,
                        Constants.RocketConstants.Im2GroupService,
                        msg
                );
            }else{
                ChatMessageAck chatMessageAck = new ChatMessageAck(jsonObject.getString("messageId"));
                responseVO.setData(chatMessageAck);
                MessagePack messagePack = new MessagePack();
                messagePack.setData(responseVO);
                messagePack.setCommand(GroupEventCommand.GROUP_MSG_ACK.getCommand());
                ctx.channel().writeAndFlush(messagePack);
            }

        } else if(command == MessageCommand.MSG_P2P.getCommand()){
            CheckSendMessageReq checkSendMessageReq = new CheckSendMessageReq();
            JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()));
            String fromId = jsonObject.getString("fromId");
            String toId = jsonObject.getString("toId");

            checkSendMessageReq.setFromId(fromId);
            checkSendMessageReq.setToId(toId);
            checkSendMessageReq.setAppId(messageHeader.getAppId());
            checkSendMessageReq.setCommand(command);

            // 调用 校验接口发送方 的接口
            ResponseVO responseVO = feignMessageService.checkP2pSendMessage(checkSendMessageReq);

            if(responseVO.isOk()){
                mqMessageProducer.sendMessage(
                        Constants.RocketConstants.IM_TO_SERVICE,
                        Constants.RocketConstants.Im2MessageService,
                        msg
                );
            }else{
                ChatMessageAck chatMessageAck = new ChatMessageAck(jsonObject.getString("messageId"));
                responseVO.setData(chatMessageAck);
                MessagePack ack = new MessagePack();
                ack.setData(chatMessageAck);
                ack.setCommand(MessageCommand.MSG_ACK.getCommand());
                ctx.channel().writeAndFlush(ack);
            }
        }else if (command == MessageCommand.MSG_RECIVE_ACK.getCommand()){
            mqMessageProducer.sendMessage(
                    Constants.RocketConstants.IM_TO_SERVICE,
                    Constants.RocketConstants.Im2MessageService,
                    msg
            );
        } else if (command == GroupEventCommand.MSG_GROUP_READED.getCommand()) {
            // 群聊消息已读
            mqMessageProducer.sendMessage(
                    Constants.RocketConstants.IM_TO_SERVICE,
                    Constants.RocketConstants.Im2GroupService,
                    msg
            );
        } else {
            mqMessageProducer.sendMessage(
                    Constants.RocketConstants.IM_TO_SERVICE,
                    Constants.RocketConstants.Im2MessageService,
                    msg
            );
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("用户{}下线", ctx.channel().attr(AttributeKey.valueOf(Constants.UserId)).get());
        SessionSocketHandler.offLineUserSession((NioSocketChannel) ctx.channel(), mqMessageProducer);
        ctx.close();
    }
}
