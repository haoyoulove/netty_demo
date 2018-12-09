package com.pingan.traffic.netty3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;

/**
 * @author yangjing
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

	private WebSocketServerHandshaker handshaker;
	private GaoListener gaoListener;
	public WebSocketServerHandler(GaoListener gaoListener){
		this.gaoListener = gaoListener;
		ApplicationContext applicationContext = new Netty4ApplicationContext();
		ApplicationContext.setContext(applicationContext);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		// WebSocket接入
		if (msg instanceof WebSocketFrame) {
			System.out.println("request meesage body:"+msg);
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}else if(msg instanceof FullHttpRequest){
			System.out.println("server receiver message is:"+msg);
			handleHttpRequest(ctx, (FullHttpRequest) msg);

		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
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

	private void handleWebSocketFrame(ChannelHandlerContext ctx,
									  WebSocketFrame frame) {

		// 判断是否是关闭链路的指令
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(),
					(CloseWebSocketFrame) frame.retain());
			return;
		}
		// 判断是否是Ping消息
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(
					new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		// 本例程仅支持文本消息，不支持二进制消息
		if (!(frame instanceof TextWebSocketFrame)) {
			throw new UnsupportedOperationException(String.format(
					"%s frame types not supported", frame.getClass().getName()));
		}

		// 返回应答消息
		String request = ((TextWebSocketFrame) frame).text();
		System.out.println("server receiver message is:"+request);

		AttributeKey<Integer> key = AttributeKey.valueOf(NetKeys.READIDLE);
		ctx.channel().attr(key).set(0);
		AttributeKey<GaoContext> contextKey = AttributeKey.valueOf(NetKeys.GAOCONTEXT_KEY);
		gaoListener.channelRead(ctx.channel().attr(contextKey).get(), request);


//		ctx.channel().write(
//				new TextWebSocketFrame(request
//						+ " , welocme to："
//						+ new java.util.Date().toString()));
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 连接成功之后存储连接的context
		super.channelActive(ctx);
		AttributeKey<GaoContext> contextKey = AttributeKey.valueOf(NetKeys.GAOCONTEXT_KEY);
		ctx.channel().attr(contextKey).set(new Netty4Context(ctx));
		gaoListener.channelActive(ctx.channel().attr(contextKey).get());

		Netty4Connetions.getInstance().addChannel(ctx.channel());
		System.out.println("channelActive============");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}


}
