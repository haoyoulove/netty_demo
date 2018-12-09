package com.pingan.traffic.netty3;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Date;

public class Listener implements GaoListener {

	@Override
	public void channelRead(GaoContext context, Object msg) {
		try {
//			CcRequest request = (CcRequest) msg;
//			Header header = request.getHeader();
//			if(header.getId() != 100){
//				System.out.println("header->"+header);
//			}
//			ActionInvoker actionInvoker = SpringContextHolder.getInvoker(header.getId());
//			if(actionInvoker == null){
//				throw new ActionException(header.getId()+"");
//			}
//		    Object result = actionInvoker.execute(context,request);
//		    if(result!=null && MessageLite.class.isAssignableFrom(result.getClass())){
//		    	context.writeAndFlush(result);
//		    }
			TextWebSocketFrame obj = new TextWebSocketFrame(msg
					+ " , welocme to："
					+ new Date().toString());
			context.write(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close(GaoContext context) {
		System.out.println("gao context close");
	}

	@Override
	public void channelActive(GaoContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serverStart(int port, String host) {
	}

	@Override
	public void serverShutdown(int port, String host) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeIdle(GaoContext context) {

	}
}
