package com.lld.im.codec;

import com.alibaba.fastjson.JSONObject;
import com.lld.im.codec.pack.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncode extends MessageToByteEncoder{
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof MessagePack messagePack){
            String jsonString = JSONObject.toJSONString(messagePack.getData());
            byte[] bytes = jsonString.getBytes();
            out.writeInt(messagePack.getCommand());
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }

}
