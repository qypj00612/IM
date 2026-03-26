package com.lld.im.tcp.utils;

import com.lld.im.codec.config.BootstrapConfig;
import com.lld.im.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

@Slf4j
public class MqFactory {

    private static volatile DefaultMQProducer producer;

    public static DefaultMQProducer getProducer(BootstrapConfig.TcpConfig config) {
        if(producer == null) {
            synchronized (MqFactory.class) {
                if(producer == null) {
                    DefaultMQProducer defaultMQProducer = new DefaultMQProducer(Constants.RocketConstants.IM_TO_SERVICE);
                    defaultMQProducer.setNamesrvAddr(config.getRocketmq().getAddress());
                    producer = defaultMQProducer;
                    try {
                        producer.start();
                    } catch (MQClientException e) {
                        e.printStackTrace();
                        log.error("生产者启动失败");
                    }
                }
            }
        }
        return producer;
    }

    public static DefaultMQPushConsumer createConsumer(BootstrapConfig config,String group) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
        consumer.setNamesrvAddr(config.getIm().getRocketmq().getAddress());
        consumer.setConsumeThreadMin(5);
        consumer.setConsumeThreadMax(10);
        return consumer;
    }
}
