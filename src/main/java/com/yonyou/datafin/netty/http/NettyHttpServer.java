package com.yonyou.datafin.netty.http;

import com.yonyou.datafin.exception.BaseRuntimeException;
import com.yonyou.datafin.framework.SpringPropertiesUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author: pizhihui
 * @datae: 2017-10-21
 */
@Component
public class NettyHttpServer implements ApplicationListener<ContextRefreshedEvent>,Ordered {

    Logger logger = LoggerFactory.getLogger(NettyHttpServer.class);

    public  void start() {
        int port = Integer.valueOf(SpringPropertiesUtil.getProperty("netty.server.port"));

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new NettyHttpServerChannlInitailizer());

            Channel ch = b.bind(port).sync().channel();

            logger.info("Open your web browser and navigate to " +
                    ("http") + "://127.0.0.1:" + port + '/');
            //System.err.println("Open your web browser and navigate to " + ("http") + "://127.0.0.1:" + port + '/');

            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new BaseRuntimeException("create netty server error: {}", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    @Override
    public int getOrder() {
        return 20;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        start();
    }

    private class NettyHttpServerChannlInitailizer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            //http解码，编码器
            ch.pipeline().addLast(new HttpServerCodec());
            ch.pipeline().addLast(new HttpObjectAggregator(1048576));
            ch.pipeline().addLast(new ChunkedWriteHandler());
            // 字节的http处理类
            ch.pipeline().addLast(new NettyHttpServerHandler());
        }
    }

}




