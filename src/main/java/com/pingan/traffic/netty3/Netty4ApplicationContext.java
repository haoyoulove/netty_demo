package com.pingan.traffic.netty3;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;

import java.util.*;


public class Netty4ApplicationContext extends ApplicationContext {


	@Override
	public void writeToWeb(Object msg) {
		System.out.println("writeToWeb");

		TextWebSocketFrame frame = new TextWebSocketFrame((String)msg);
		String request = frame.text();

		System.out.println("writeToWeb message is:"+request);
		TextWebSocketFrame obj = new TextWebSocketFrame(request);
		Netty4Connetions.getInstance().getAllChannels().writeAndFlush(obj);
	}

	@Override
	public void writeToAll(Object obj) {

		Netty4Connetions.getInstance().getAllChannels().writeAndFlush(obj);
	}

	@Override
	public void writeToList(List<Integer> uidList, Object obj) {
		for(Integer i : uidList){
			writeToOne(i, obj);
		}
	}

	@Override
	public void writeToOne(int uid, Object obj) {
		Channel channel = Netty4Connetions.getInstance().getUserChannel(uid);
		if(channel != null){
			channel.writeAndFlush(obj);
		}
	}


	@Override
	public void writeToRoom(int roomId, Object obj) {
		ChannelGroup group =  Netty4Connetions.getInstance().getRoomGroup(roomId);
		if(group != null){
			group.writeAndFlush(obj);
		}
	}

	@Override
	public void writeToRoomSync(int roomId, Object obj) {
		Netty4Connetions.getInstance().getRoomGroup(roomId).writeAndFlush(obj).awaitUninterruptibly();
	}

	@Override
	public void writeToApp(int appId, Object obj) {
		ChannelGroup group = Netty4Connetions.getInstance().getAppGroup(appId);
		if(group != null){
			group.writeAndFlush(obj);
		}
	}

	@Override
	public void close(int uid) {
		Channel channel = Netty4Connetions.getInstance().getUserChannel(uid);
		if(channel != null){
			channel.flush();
			channel.close();
		}
	}
	
	@Override
	public boolean closeSync(int uid) throws InterruptedException {
		Channel channel = Netty4Connetions.getInstance().getUserChannel(uid);
		if(channel != null){
			channel.flush();
			ChannelFuture future = channel.close().awaitUninterruptibly();
			return future.isSuccess();
		}
		return false;
	}

	@Override
	public GaoContext getContext(int uid) {
		Channel channel = Netty4Connetions.getInstance().getUserChannel(uid);
		if(channel != null){
			AttributeKey<GaoContext> contextKey = AttributeKey.valueOf(NetKeys.GAOCONTEXT_KEY);
			return channel.attr(contextKey).get();
		}
		return null;
	}

	@Override
	public Set<GaoContext> getContexts() {
		ChannelGroup channelGroup = Netty4Connetions.getInstance().getAllChannels();
		if(channelGroup == null){
			return null;
		}
		Set<GaoContext> set = new HashSet<GaoContext>();
		AttributeKey<GaoContext> contextKey = AttributeKey.valueOf(NetKeys.GAOCONTEXT_KEY);
		Iterator<Channel> it = channelGroup.iterator();
		while (it.hasNext()) {
			Channel channel = it.next();
			set.add(channel.attr(contextKey).get());
		}
		return set;
	}

	@Override
	public Set<GaoContext> getContexts(int roomId) {
		ChannelGroup channelGroup = Netty4Connetions.getInstance().getRoomGroup(roomId);
		if(channelGroup == null){
			return null;
		}
		Set<GaoContext> set = new HashSet<GaoContext>();
		AttributeKey<GaoContext> contextKey = AttributeKey.valueOf(NetKeys.GAOCONTEXT_KEY);
		Iterator<Channel> it = channelGroup.iterator();
		while (it.hasNext()) {
			Channel channel = it.next();
			set.add(channel.attr(contextKey).get());
		}
		return set;
	}

	@Override
	public void writeToOneAndRoom(int uid, int roomId, Object obj) {

	}
}
