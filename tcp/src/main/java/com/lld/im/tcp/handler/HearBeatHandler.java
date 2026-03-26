package com.lld.im.tcp.handler;

import cn.hutool.core.date.DateTime;
import com.lld.im.common.constant.Constants;
import com.lld.im.tcp.utils.SessionSocketHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HearBeatHandler extends ChannelInboundHandlerAdapter {

    private final Long headBeatTime;

    public HearBeatHandler(Long headBeatTime) {
        this.headBeatTime = headBeatTime;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent event){
            if(event.state()== IdleState.READER_IDLE){
                log.info("读空闲");
            }else if(event.state()== IdleState.WRITER_IDLE){
                log.info("写空闲");
            }else if(event.state()== IdleState.ALL_IDLE){
                long now = DateTime.now().getTime();
                Long time = (Long) ctx.channel().attr(AttributeKey.valueOf(Constants.ReadTime)).get();
                if(time != null&&now-time>headBeatTime){
                    SessionSocketHandler.offLineUserSession((NioSocketChannel) ctx.channel());
                }
            }
        }
    }
}
