package com.netty.protobuf.client;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.netty.protobuf.handler.NettyClientHandler2;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        //设置main方法日志级别
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<Logger> loggerList = loggerContext.getLoggerList();
        loggerList.forEach(logger -> {
            logger.setLevel(Level.WARN);
        });
        //客户端需要一个事件循环组
        EventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            //创建客户端启动对象
            //注意客户端使用的不是serverBootStrap而是BootStrap
            Bootstrap bootstrap = new Bootstrap();
            //设置相关参数
            bootstrap
                    //设置线程组
                    .group(eventExecutors)
                    //客户端通道的实现类(反射)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //加入ProtobufEncoder编码器
                            pipeline.addLast("encoder", new ProtobufEncoder());
                            //加入自己的处理器
                            pipeline.addLast(new NettyClientHandler2());
                        }
                    });
            System.out.println("客户端...ok...");
            //启动客户端去连接服务端
            //关于ChannelFuture后面会分析，设计netty的异步模型
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();
            //给关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }
}
