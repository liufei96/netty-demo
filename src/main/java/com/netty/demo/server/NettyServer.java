package com.netty.demo.server;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.netty.demo.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //2.创建服务器端的启动对象，配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //3.使用链式编程进行设置
            serverBootstrap
                    //设置两个线程组
                    .group(bossGroup,workerGroup)
                    //使用NioServerSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    //设置线程队列等待连接个数
                    .option(ChannelOption.SO_BACKLOG,128)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    //给我们的workerGroup的EventLoopGroup对应的管道设置处理器Handler
                    .childHandler(
                            //创建一个通道初始化对象(匿名对象)
                            new ChannelInitializer<SocketChannel>() {
                                //给管道设置处理器
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                   socketChannel.pipeline().addLast(new NettyServerHandler());
                                }
                    });
            System.out.println("...服务器 is ready ...");
            //4.绑定一个端口并且同步，生成一个ChannelFuture对象
            //启动服务器并绑定端口
            ChannelFuture sync = serverBootstrap.bind(6668).sync();
            //5.对关闭通道进行监听
            sync.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
