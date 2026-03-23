package com.lld.im.tcp.server;


import com.lld.im.codec.MessageDecode;
import com.lld.im.codec.config.BootstrapConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.http.WebSocket;

@Slf4j
public class ImWebServer {

    private final int port;
    private final ServerBootstrap server;

    public ImWebServer(BootstrapConfig.TcpConfig config) {
        this.port = config.getWebSocketPort();

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
                ChannelPipeline pipeline = ch.pipeline();
                // websocket   基于http协议，所以要有http编解码器
                pipeline.addLast("http-codec", new HttpServerCodec());
                // 对写大数据流的支持
                pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                // 几乎在netty中的编程，都会使用到此handler
                pipeline.addLast("aggregator", new HttpObjectAggregator(65535));

                /**
                 * WebSocketServerProtocolHandler 是 Netty 提供的 WebSocket 协议核心处理器，
                 * 用于监听 /ws 路径的连接请求，自动完成 WebSocket 协议握手、心跳、连接关闭等底层操作，
                 * 将 HTTP 协议升级为 WebSocket 长连接，为实时对话模块提供稳定的双向实时通信能力。
                 */
                pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

                pipeline.addLast(new MessageDecode());
            }
        });

    }

    public void start(){
        server.bind(port).syncUninterruptibly();
        log.info("im web server started on port {}", port);
    }
}
