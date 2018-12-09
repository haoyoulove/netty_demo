package com.pingan.traffic.netty3;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 当前连接才拥有
 * @author lenovo
 *
 */
public class Netty4Connetions {
	private static final Netty4Connetions CONNETIONS = new Netty4Connetions();
	
	private ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private ConcurrentMap<Integer, ChannelGroup> appChannels = new ConcurrentHashMap<Integer, ChannelGroup>();
	private ConcurrentMap<Integer, ChannelGroup> roomChannels = new ConcurrentHashMap<Integer, ChannelGroup>();
	private ConcurrentMap<Integer, Channel> userChannels = new ConcurrentHashMap<>();
	
	private Netty4Connetions(){	
	}

	public void addChannel(Channel channel){
		allChannels.add(channel);
	}
	
	public static Netty4Connetions getInstance(){
		return CONNETIONS;
	}

	public ChannelGroup getAllChannels() {
		return allChannels;
	}
	

	public Channel getUserChannel(int uid){
		return userChannels.get(uid);
	}
	
	public ChannelGroup getAppGroup(int appId){
		return appChannels.get(appId);
	}
	
	public ChannelGroup getRoomGroup(int roomId){
		return roomChannels.get(roomId);
	}
	
}
