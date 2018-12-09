package com.pingan.traffic.netty3;

public interface GaoListener {
	public void serverStart(int port, String host);
	
	public void serverShutdown(int port, String host);
	
	public void channelRead(GaoContext context, Object msg);
	
	public void close(GaoContext context);
	
	public void channelActive(GaoContext context);
	
	public void writeIdle(GaoContext context);
}
