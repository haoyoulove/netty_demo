package com.pingan.traffic.netty.proto;

import com.pingan.traffic.netty3.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;

/**
 * @author yangjing
 */
public class WebSocketProtosServerHandler extends ChannelInboundHandlerAdapter {

	private WebSocketServerHandshaker handshaker;
	private GaoListener gaoListener;
	public WebSocketProtosServerHandler(GaoListener gaoListener){
		this.gaoListener = gaoListener;
		ApplicationContext applicationContext = new Netty4ApplicationContext();
		ApplicationContext.setContext(applicationContext);
	}


	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelReadComplete");
		ctx.flush();
	}


	private void handleHttpRequest(ChannelHandlerContext ctx,
								   FullHttpRequest req) throws Exception {



		// 构造握手响应返回，本机测试
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				"ws://localhost:17888/websocket", null, false);
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		if (msg instanceof WebSocketFrame) {
			System.out.println("request meesage body:"+msg);

		}else if(msg instanceof FullHttpRequest){
			System.out.println("server receiver message is:"+msg);
			handleHttpRequest(ctx, (FullHttpRequest) msg);

		}else{
			AttributeKey<Integer> key = AttributeKey.valueOf(NetKeys.READIDLE);
			ctx.channel().attr(key).set(0);
			System.out.println("channelRead0 protos============");

			AttributeKey<GaoContext> contextKey = AttributeKey.valueOf(NetKeys.GAOCONTEXT_KEY);
			gaoListener.channelRead(ctx.channel().attr(contextKey).get(), msg);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 连接成功之后存储连接的context
		super.channelActive(ctx);
		System.out.println("channelActive protos============");
		AttributeKey<GaoContext> contextKey = AttributeKey.valueOf(NetKeys.GAOCONTEXT_KEY);
		ctx.channel().attr(contextKey).set(new Netty4Context(ctx));
		gaoListener.channelActive(ctx.channel().attr(contextKey).get());

		Netty4Connetions.getInstance().addChannel(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("exceptionCaught");
		ctx.close();
	}


}
