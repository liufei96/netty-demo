package com.netty.protobuf.handler;

import com.netty.protobuf.proto.MyDataInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyServerHandler2 extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MyDataInfo.MyMessage message = (MyDataInfo.MyMessage) msg;
        //根据DataType来显示不同的信息
        MyDataInfo.MyMessage.DataType dataType = message.getDataType();
        if (dataType == MyDataInfo.MyMessage.DataType.studentType) {
            MyDataInfo.Student student = message.getStudent();
            System.out.println("student：id=" + student.getId() + " name=" + student.getName());
        } else if (dataType == MyDataInfo.MyMessage.DataType.workerType) {
            MyDataInfo.Worker worker = message.getWorker();
            System.out.println("worker：id=" + worker.getAge() + " name=" + worker.getName());
        } else {
            System.out.println("传输了类型不正确");
        }
    }
}
