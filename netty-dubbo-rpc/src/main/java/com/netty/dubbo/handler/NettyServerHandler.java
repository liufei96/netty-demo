package com.netty.dubbo.handler;

import com.netty.dubbo.service.impl.HelloServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * https://blog.csdn.net/weixin_42412601/article/details/106985879
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //获取客户端发送的消息，并调用服务
        System.out.println("msg=" + msg);
        //客户端在调用服务器的api 时，我们需要定义一个协议
        //比如我们要求 每次发消息是都必须以某个字符串开头 "HelloService#hello#你好"
//        if(msg.toString().startsWith(ClientBootstrap.providerName)) {
        if (msg.toString().startsWith("HelloService#hello#")) {
            String result = new HelloServiceImpl().hello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
            ctx.writeAndFlush(result);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
