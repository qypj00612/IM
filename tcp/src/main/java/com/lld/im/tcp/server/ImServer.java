package com.lld.im.tcp.server;

import com.lld.im.codec.MessageDecode;
import com.lld.im.codec.MessageEncode;
import com.lld.im.codec.config.BootstrapConfig;
import com.lld.im.tcp.handler.HearBeatHandler;
import com.lld.im.tcp.handler.NettyServerHandler;
import com.lld.im.tcp.publish.MqMessageProducer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImServer {
    private final int port;

    private final ServerBootstrap server;

    // 生产者
    private final MqMessageProducer producer;

    public ImServer(BootstrapConfig.TcpConfig config) {
        this.port = config.getTcpPort();
        this.producer = new MqMessageProducer(config);

        NioEventLoopGroup boss = new NioEventLoopGroup(config.getBossThreadSize());
        NioEventLoopGroup worker = new NioEventLoopGroup(config.getWorkerThreadSize());
        server = new ServerBootstrap();

        server.group(boss, worker);
        server.channel(NioServerSocketChannel.class);

        server.option(ChannelOption.SO_BACKLOG, 10240) // 服务端可连接队列大小
                .option(ChannelOption.SO_REUSEADDR, true) // 参数表示允许重复使用本地地址和端口
                .childOption(ChannelOption.TCP_NODELAY, true) // 是否禁用Nagle算法 简单点说是否批量发送数据 true关闭 false开启。 开启的话可以减少一定的网络开销，但影响消息实时性
                .childOption(ChannelOption.SO_KEEPALIVE, true);// 保活开关2h没有数据服务端会发送心跳包

        server.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new MessageDecode());
                ch.pipeline().addLast(new MessageEncode());
                //ch.pipeline().addLast(new IdleStateHandler(0,0,1));
                ch.pipeline().addLast(new HearBeatHandler(config.getHearBeatTime()));
                ch.pipeline().addLast(new NettyServerHandler(config.getBrokerId(),producer));
            }
        });

    }

    public void start() {
        server.bind(port).syncUninterruptibly();
    }

}
