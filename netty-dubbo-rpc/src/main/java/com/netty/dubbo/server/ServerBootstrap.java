package com.netty.dubbo.server;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import java.util.List;

//ServerBootstrap 会启动一个服务提供者，就是 NettyServer
public class ServerBootstrap {
    public static void main(String[] args) {
        //设置main方法日志级别
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<Logger> loggerList = loggerContext.getLoggerList();
        loggerList.forEach(logger -> {
            logger.setLevel(Level.WARN);
        });

        //代码代填..
        NettyServer.startServer("127.0.0.1", 7000);
    }
}
