package com.netty.demo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 说明：
 * 1.自定义一个Handler,需要继承netty规定好的某个Handler适配器
 * 2.这时我们自定义一个handler，才能称为一个handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 读取数据事件(可以读取客户端发送的消息)
     * 1.ChannelHandlerContext：上下文对象，含有管道pipeline，通道channel，地址。
     * 管道和通道区别：管道里面是处理器(处理数据)，通道里面是buffer写入的数据(传输数据)
     * 2.msg: 客户端发送的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server ctx=" + ctx);
        //将msg转成一个ByteBuf
        //这个ByteBuf是netty提供的，不是nio的ByteBuffer
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发送的消息是：" + byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }

    /**
     * 数据读取完毕
     * 发送消息给客户端
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //writeAndFlush是write+flush
        //将数据写入到缓存，并刷新
        //一般来讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端~", CharsetUtil.UTF_8));
    }

    /**
     * 异常处理，一般需要关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        //也可以
//        ctx.channel().close();
    }
}
