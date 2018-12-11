package com.pingan.traffic.netty.proto;

import com.pingan.traffic.netty3.GaoContext;
import com.pingan.traffic.netty3.GaoListener;

public class ProtosListener implements GaoListener {

	@Override
	public void channelRead(GaoContext context, Object msg) {
//		try {
//			CcRequest request = (CcRequest) msg;
//			Header header = request.getHeader();
//			System.out.println("header->"+header);
//
////			TextWebSocketFrame obj = new TextWebSocketFrame(msg
////					+ " , welocme to："
////					+ new Date().toString());
////			context.write(obj);
//
//			Header.Builder beatHeader =Header.newBuilder();
//			beatHeader.setId(100);
//
//			Protocol.BeatResponse.Builder res = Protocol.BeatResponse.newBuilder();
//			res.setResMsg(msg + " , welocme to：" + new Date().toString());
//			Protocol.CcResponse response = ProtocolUtil.buildSuccessResponse(header.toBuilder(), res.build().toByteString());
//			context.write(response);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
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
