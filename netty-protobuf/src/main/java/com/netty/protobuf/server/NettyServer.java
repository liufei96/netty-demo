package com.netty.protobuf.server;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.netty.protobuf.handler.NettyServerHandler2;
import com.netty.protobuf.proto.StudentPOJO;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        //设置main方法日志级别
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<Logger> loggerList = loggerContext.getLoggerList();
        loggerList.forEach(logger -> {
            logger.setLevel(Level.WARN);
        });
        //1.创建BossGroup 和 WorkerGroup
        //说明：
        //创建两个线程组 bossGroup和workerGroup
        //bossGroup只是处理连接请求
        //workerGroup真正的和客户端进行业务处理
        //两个都是无限循环
        //默认bossGroup和workerGroup含有的子线程(NioEventLoop)的个数=2*CPU核数
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(3);

        try {
            //2.创建服务器端的启动对象，配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //3.使用链式编程进行设置
            serverBootstrap
                    //设置两个线程组
                    .group(bossGroup, workerGroup)
                    //使用NioServerSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    //当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。
                    // 如果未设置或所设置的值小于1，Java将使用默认值50。
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动连接状态， 是否启用心跳保活机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //给我们的workerGroup的EventLoopGroup对应的管道设置处理器Handler
                    .childHandler(
                            //创建一个通道测试对象(匿名对象)
                            new ChannelInitializer<SocketChannel>() {
                                //给管道设置处理器
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    ChannelPipeline pipeline = socketChannel.pipeline();
                                    //ProtoBuf核心代码start-------------------------
                                    //在管道中加入protobuf解码器，指定对哪种对象进行解码
                                    pipeline.addLast("decoder", new ProtobufDecoder(StudentPOJO.Student.getDefaultInstance()));
                                    pipeline.addLast(new NettyServerHandler2());
                                    //ProtoBuf核心代码end-------------------------
                                }
                            });
            System.out.println("...服务器 is ready ...");
            //4.绑定一个端口并且同步，生成一个ChannelFuture对象
            //启动服务器并绑定端口
            ChannelFuture sync = serverBootstrap.bind(6668).sync();

            sync.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    System.out.println("绑定完成");
                } else {
                    System.out.println("绑定失败");
                }
            });
            //5.对关闭通道进行监听
            sync.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
