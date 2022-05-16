package com.netty.protobuf.handler;

import com.netty.protobuf.proto.StudentPOJO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 读取数据事件(可以读取客户端发送的消息)
     * 1.ChannelHandlerContext：上下文对象，含有管道pipeline，通道channel，地址。
     * 管道和通道区别：管道里面是处理器(处理数据)，通道里面是buffer写入的数据(传输数据)
     * 2.msg: 客户端发送的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //读取从客户端发送的StudentPOJO.Student
        StudentPOJO.Student student = (StudentPOJO.Student) msg;
        System.out.println("客户端发送的数据：id=" + student.getId() + " name=" + student.getName());
    }
}
