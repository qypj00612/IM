package com.lld.im.tcp.receiver.process;

import com.lld.im.codec.pack.MessagePack;
import com.lld.im.tcp.utils.SessionSocketHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public abstract class BaseProcess {

    public abstract void processBefore();

    public void process(MessagePack pack){
        processBefore();
        NioSocketChannel nioSocketChannel = SessionSocketHandler.get(
                pack.getAppId(),
                pack.getUserId(),
                pack.getClientType(),
                pack.getImei()
        );
        if(nioSocketChannel!=null){
            nioSocketChannel.writeAndFlush(pack);
        }
        processAfter();
    }

    public abstract void processAfter();

}
