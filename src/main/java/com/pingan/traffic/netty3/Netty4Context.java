package com.pingan.traffic.netty3;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

public class Netty4Context extends GaoContext {
	private ChannelHandlerContext ctx;
	
	public Netty4Context() {
		super();
	}

	@Override
	public void writeToWeb(Object msg) {
		TextWebSocketFrame obj = new TextWebSocketFrame(msg.toString());
		Netty4Connetions.getInstance().getAllChannels().write(obj);
	}

	public Netty4Context(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void write(Object obj) {
		ctx.write(obj);
	}

	@Override
	public void writeAndFlush(Object obj) {
		ctx.writeAndFlush(obj);
	}

	@Override
	public void writeToAll(Object obj) {
		Netty4Connetions.getInstance().getAllChannels().writeAndFlush(obj);
	}

	@Override
	public void writeToList(List<Integer> uidList, Object obj) {
		for(Integer i : uidList){
			Netty4Connetions.getInstance().getUserChannel(i).writeAndFlush(obj);
		}
	}

	@Override
	public void writeToOne(int uid, Object obj) {
		Netty4Connetions.getInstance().getUserChannel(uid).writeAndFlush(obj);
	}

	@Override
	public void writeToRoom(int roomId, Object obj) {
		Netty4Connetions.getInstance().getRoomGroup(roomId).writeAndFlush(obj);
	}

	@Override
	public void writeToRoomSync(int roomId, Object obj) {
		Netty4Connetions.getInstance().getRoomGroup(roomId).writeAndFlush(obj).awaitUninterruptibly();
	}

	@Override
	public void writeToApp(int appId, Object obj) {
		Netty4Connetions.getInstance().getAppGroup(appId).writeAndFlush(obj);
	}

	@Override
	public void flush() {
		ctx.flush();
	}

	@Override
	public void close() {
		ctx.close();
	}
	
	@Override
	public boolean closeSync() throws InterruptedException {
		ChannelFuture future = ctx.close().sync().awaitUninterruptibly();
		return future.isSuccess();
	}

	@Override
	public void close(int uid) {
		Netty4Connetions.getInstance().getUserChannel(uid).close();
	}
	
	@Override
	public boolean closeSync(int uid) throws InterruptedException {
		if(Netty4Connetions.getInstance().getUserChannel(uid) != null){
			ChannelFuture future = Netty4Connetions.getInstance().getUserChannel(uid).close().sync().awaitUninterruptibly();
			return future.isSuccess();
		}
		return false;
	}



	@Override
	public InetSocketAddress getInetSocketAddress() {
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
                .remoteAddress();
		return insocket;
	}

	@Override
	public void writeAndFlush(List<Object> list) {
		int size = list.size();
		for(int i=0; i<size; i++){
			if((i+1) <size){
				write(list.get(i));
			}else{
				writeAndFlush(list.get(i));
			}
		}
	}

	@Override
	public GaoContext getContext(int uid) {
		AttributeKey<GaoContext> contextKey = AttributeKey.valueOf(NetKeys.GAOCONTEXT_KEY);
		return ctx.channel().attr(contextKey).get();
	}

	@Override
	public Set<GaoContext> getContexts() {
		return null;
	}

	@Override
	public Set<GaoContext> getContexts(int roomId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeToOneAndRoom(int uid, int roomId, Object obj) {
		// TODO Auto-generated method stub
		
	}

}
