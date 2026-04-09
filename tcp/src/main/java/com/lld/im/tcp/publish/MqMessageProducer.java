package com.lld.im.tcp.publish;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lld.im.codec.config.BootstrapConfig;
import com.lld.im.tcp.utils.MqFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

@Slf4j
public class MqMessageProducer {

    private final DefaultMQProducer producer;

    public MqMessageProducer(BootstrapConfig.TcpConfig config) {
        producer = MqFactory.getProducer(config);
    }

    public void sendMessage(String topic, String tag, com.lld.im.codec.proto.Message message){
        //DefaultMQProducer producer = MqFactory.getProducer(config);
        JSONObject jsonObject = (JSONObject)JSON.toJSON(message.getMessagePack());
        if(message.getMessageHeader()!=null) {
            jsonObject.put("command", message.getMessageHeader().getCommand());
            jsonObject.put("clientType", message.getMessageHeader().getClientType());
            jsonObject.put("appId", message.getMessageHeader().getAppId());
            jsonObject.put("imei", message.getMessageHeader().getImei());
        }
        Message msg = new Message(
                topic,
                tag,
                JSONObject.toJSONString(jsonObject).getBytes()
        );
        try {
            producer.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("消息发送失败");
        }
    }
}
