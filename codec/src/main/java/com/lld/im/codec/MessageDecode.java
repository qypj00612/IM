package com.lld.im.codec;

import com.alibaba.fastjson.JSONObject;
import com.lld.im.codec.proto.Message;
import com.lld.im.codec.proto.MessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecode extends ByteToMessageDecoder {

    //请求头（指令
    // 版本
    // clientType
    // 消息解析类型
    // appId
    // imei长度
    // bodyLen）+ imei 号 + 请求体

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes()<28){
            return;
        }

        int command = in.readInt();
        int version = in.readInt();
        int clientType = in.readInt();
        int messageType = in.readInt();
        int appId = in.readInt();
        int imeiLen = in.readInt();
        int bodyLen = in.readInt();

        if(in.readableBytes()<imeiLen+bodyLen){
            in.resetReaderIndex();
            return;
        }

        byte[] imeiData = new byte[imeiLen];
        in.readBytes(imeiData);
        String imei = new String(imeiData);

        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setCommand(command);
        messageHeader.setVersion(version);
        messageHeader.setClientType(clientType);
        messageHeader.setAppId(appId);
        messageHeader.setMessageType(messageType);
        messageHeader.setImeiLength(imeiLen);
        messageHeader.setLength(bodyLen);
        messageHeader.setImei(imei);

        byte[] bodyData = new byte[bodyLen];
        in.readBytes(bodyData);
        String body = new String(bodyData);

        Message message = new Message();
        message.setMessageHeader(messageHeader);

        if(messageType==0x0){
            JSONObject json = (JSONObject)JSONObject.parse(body);
            message.setMessagePack(json);
        }

        in.markReaderIndex();
        out.add(message);
    }
}
