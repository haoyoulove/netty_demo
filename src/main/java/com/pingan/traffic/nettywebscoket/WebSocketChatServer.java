package com.pingan.traffic.nettywebscoket;

import com.pingan.traffic.nettywebscoket.constant.Constants;
import com.pingan.traffic.nettywebscoket.initializer.WebSocketChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息聊天WEBSOCKET服务端
 * @author 粱桂钊
 * @since 
 * <p>更新时间: 2016年8月29日  v0.1</p><p>版本内容: 创建</p>
 */

@Component
public class WebSocketChatServer {

    private Logger logger = LoggerFactory.getLogger(WebSocketChatServer.class);


    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workGroup = new NioEventLoopGroup();

    @Autowired
    private WebSocketChannelInitializer webSocketChannelInitializer;

    /**
     * 开启	     
     * @throws Exception
     */
    public void start() throws Exception {
        try {
        	logger.info("starting tcp server ... Port: " + Constants.WEBSOCKET_PORT);
        	
        	// 创建ServerBootstrap对象，它是Netty用于启动NIO服务端的辅助启动类， 目的是降低服务端的开发复杂度
        	ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            // 处理业务
            bootstrap.handler(new LoggingHandler(LogLevel.INFO));
            // 绑定I/O事件的处理类
            bootstrap.childHandler(webSocketChannelInitializer);
            // 绑定端口，同步等待成功
            bootstrap.bind(Constants.WEBSOCKET_PORT).sync();

        }  catch (Exception e) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}