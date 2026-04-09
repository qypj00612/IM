package com.lld.im.tcp.receiver;

import com.alibaba.fastjson.JSONObject;
import com.lld.im.codec.pack.MessagePack;
import com.lld.im.common.constant.Constants;
import com.lld.im.common.enums.ClientType;
import com.lld.im.common.enums.DeviceMultiLoginEnum;
import com.lld.im.common.enums.command.SystemCommand;
import com.lld.im.common.model.UserClientDto;
import com.lld.im.tcp.utils.SessionSocketHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/***
 * 多端登录
 * 1 单端登录
 *      一端在线，踢掉除了本 clientType + imei 的设备
 * 2 双端登录
 *      允许 pc/mobile 其中一端登录 + web端 踢掉除了本 clientType + imei 以外的 非 web端 设备
 * 3 三段登录
 *      允许 手机 + pc + web，踢同端的其他 imei 除了 web
 * 4 不做处理
 */
@Slf4j
public class LoginMessageReceiver implements MessageListenerConcurrently {

    private final Integer loginModel;

    public LoginMessageReceiver(Integer loginModel) {
        this.loginModel = loginModel;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for(MessageExt msg : msgs){
            String s = new String(msg.getBody());
            JSONObject jsonObject = JSONObject.parseObject(s);
            UserClientDto userClientDto = jsonObject.toJavaObject(UserClientDto.class);
    //        UserClientDto userClientDto = new UserClientDto();
//            userClientDto.setUserId(jsonObject.getString("userId"));
//            userClientDto.setAppId(jsonObject.getInteger("appId"));
//            userClientDto.setClientType(jsonObject.getInteger("clientType"));
//            userClientDto.setImei(jsonObject.getString("imei"));

//            MessagePack messagePack = JSONObject.parseObject(s, MessagePack.class);
//            UserClientDto userClientDto = JSONObject.parseObject(
//                    (String) messagePack.getData(),
//                    UserClientDto.class);
            //UserClientDto userClientDto = JSONObject.parseObject(s, UserClientDto.class);
            log.info("用户{} 上线",userClientDto.getUserId());

            // 获取 AppId 和 userId 相等的 channel
            List<NioSocketChannel> channels = SessionSocketHandler.get(userClientDto.getAppId(), userClientDto.getUserId());
            for(NioSocketChannel channel : channels){
                if(loginModel == DeviceMultiLoginEnum.ONE.getLoginMode()){
                    Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
                    String imei = (String) channel.attr(AttributeKey.valueOf(Constants.imei)).get();

                    if(!( userClientDto.getImei().equals(imei)
                            && userClientDto.getClientType().equals(clientType) )
                    ){
                        // 踢掉 告诉客户端其他段登录
                        MessagePack<Object> pack = new MessagePack<>();
                        pack.setToId((String) channel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                        pack.setUserId((String) channel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                        pack.setCommand(SystemCommand.MULTIPLE_LOGIN.getCommand());
                        channel.writeAndFlush(pack);
                    }


                }else if(loginModel == DeviceMultiLoginEnum.TWO.getLoginMode()){
                    Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
                    String imei = (String) channel.attr(AttributeKey.valueOf(Constants.imei)).get();

                    if(clientType == ClientType.WEB.getCode() ||
                            userClientDto.getClientType() == ClientType.WEB.getCode()){
                        continue;
                    }

                    if(!( userClientDto.getImei().equals(imei)
                            && userClientDto.getClientType().equals(clientType) )
                    ){
                        // 踢掉
                        MessagePack<Object> pack = new MessagePack<>();
                        pack.setToId((String) channel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                        pack.setUserId((String) channel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                        pack.setCommand(SystemCommand.MULTIPLE_LOGIN.getCommand());
                        channel.writeAndFlush(pack);
                    }


                }else if(loginModel == DeviceMultiLoginEnum.THREE.getLoginMode()){
                    Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.ClientType)).get();
                    String imei = (String) channel.attr(AttributeKey.valueOf(Constants.imei)).get();

                    if(clientType == ClientType.WEB.getCode() ||
                            userClientDto.getClientType() == ClientType.WEB.getCode()){
                        continue;
                    }

                    boolean isSameClient = false;

                    // 判断是否都是手机端
                    if( ( userClientDto.getClientType() == ClientType.IOS.getCode()
                            ||userClientDto.getClientType()==ClientType.ANDROID.getCode() )
                            &&( clientType == ClientType.ANDROID.getCode()
                                            ||clientType == ClientType.IOS.getCode() )
                    ){
                        isSameClient = true;
                    }

                    // 判断是否都是 pc端
                    if( ( userClientDto.getClientType() == ClientType.WINDOWS.getCode()
                            ||userClientDto.getClientType()==ClientType.MAC.getCode() )
                            &&( clientType == ClientType.WINDOWS.getCode()
                            ||clientType == ClientType.MAC.getCode() )

                    ){
                        isSameClient = true;
                    }

                    // 同端 且 imei号 和 clientType 不一样
                    if( isSameClient &&
                            !( userClientDto.getImei().equals(imei)
                            && userClientDto.getClientType().equals(clientType) )
                    ){
                        // 踢掉
                        MessagePack<Object> pack = new MessagePack<>();
                        pack.setToId((String) channel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                        pack.setUserId((String) channel.attr(AttributeKey.valueOf(Constants.UserId)).get());
                        pack.setCommand(SystemCommand.MULTIPLE_LOGIN.getCommand());
                        channel.writeAndFlush(pack);
                    }

                }
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
