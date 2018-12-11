package com.pingan.traffic.nettywebscoket.handle;

import com.pingan.traffic.protocol.Protocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <h3>概要:</h3><p>TcpServerHandler</p>
 * <h3>功能:</h3><p>TcpServerHandler</p>
 * <h3>履历:</h3>
 * <li>2017年1月18日  v0.1 版本内容: 新建</li>
 * @author 粱桂钊
 * @since 0.1
 */
@Component
public class TcpServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(TcpServerHandler.class);

    /**
     * 客户端与服务端会话连接成功
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("客户端与服务端会话连接成功");
    }

    /**
     * 服务端接收到客户端消息
     */
    @Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);

        ctx.writeAndFlush(msg);
	}

    /**
     * 服务端监听到客户端异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("服务端监听到客户端异常");
    }
   
    /**
     * 客户端与服务端会话连接断开
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	super.channelInactive(ctx);
    	System.out.println("客户端与服务端会话连接断开");
    } 
}