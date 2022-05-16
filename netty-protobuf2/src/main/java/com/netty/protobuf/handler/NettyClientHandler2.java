package com.netty.protobuf.handler;

import com.netty.protobuf.proto.MyDataInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Random;

public class NettyClientHandler2 extends ChannelInboundHandlerAdapter {
    //当通道就绪就会触发该方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //随机的发送Student或worker对象
        int random = new Random().nextInt(3);
        MyDataInfo.MyMessage message = null;
        if (0 == random) {//发送一个student对象
            message = MyDataInfo.MyMessage.newBuilder().setDataType(
                            MyDataInfo.MyMessage.DataType.studentType)
                    .setStudent(MyDataInfo.Student.newBuilder().setId(5).setName("李四").build()).build();
        } else {//发送一个worker对象
            message = MyDataInfo.MyMessage.newBuilder().setDataType(
                            MyDataInfo.MyMessage.DataType.workerType)
                    .setWorker(MyDataInfo.Worker.newBuilder().setAge(20).setName("老王").build()).build();
        }
        ctx.writeAndFlush(message);
    }
}
