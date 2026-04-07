package com.lld.im.tcp.receiver;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.lld.im.codec.config.BootstrapConfig;
import com.lld.im.codec.pack.MessagePack;
import com.lld.im.common.constant.Constants;
import com.lld.im.tcp.receiver.process.BaseProcess;
import com.lld.im.tcp.receiver.process.ProcessFactory;
import com.lld.im.tcp.utils.MqFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

@Slf4j
public class MqMessageReceiver {
    private static DefaultMQPushConsumer broadConsumer;

    public static void startReceiver(BootstrapConfig config){
        DefaultMQPushConsumer consumer = null;
        try {
            consumer = MqFactory.createConsumer(config, Constants.RocketConstants.SERVICE_TO_IM);
            broadConsumer = MqFactory.createConsumer(config, Constants.RocketConstants.IM_BROADCAST);
            broadConsumer.setMessageModel(MessageModel.BROADCASTING);
        } catch (Exception e) {
            log.error("创建消费者失败");
        }

        try {
            if(consumer == null||broadConsumer == null){
                throw new Exception();
            }
            consumer.subscribe(
                    Constants.RocketConstants.SERVICE_TO_IM+"_"+config.getIm().getBrokerId(),  // 你只用一个TOPIC
                    Constants.RocketConstants.FriendShip2Im+" || "
                            +Constants.RocketConstants.GroupService2Im+" || "
                            +Constants.RocketConstants.MessageService2Im+" || "
                            +Constants.RocketConstants.UserService2Im
            );

            broadConsumer.subscribe(
                    Constants.RocketConstants.IM_BROADCAST,
                    Constants.RocketConstants.USER_LOGIN
            );

            consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                for (MessageExt msg : msgs) {
                    try {
                        String tag = msg.getTags();
                        String data = new String(msg.getBody());
                        MessagePack messagePack = JSONObject.parseObject(data, MessagePack.class);
//                    if(tag.equals(Constants.RocketConstants.GroupService2Im)){
//
//                    }else if(tag.equals(Constants.RocketConstants.FriendShip2Im)){
//
//                    }
                        BaseProcess process = ProcessFactory.getProcess(messagePack.getCommand());
                        process.process(messagePack);
                        log.info("收到消息 tag = {}, data = {}" ,tag ,messagePack);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("MQ接收消息异常");
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });

            // 广播消息
            broadConsumer.registerMessageListener(new LoginMessageReceiver(config.getIm().getLoginModel()));

            consumer.start();
            broadConsumer.start();

            log.info("rocketmq consumer start success");

        } catch (Exception e) {
            e.printStackTrace();
            log.error("消息接收异常");
        }

    }
}
