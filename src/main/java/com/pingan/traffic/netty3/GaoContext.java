package com.pingan.traffic.netty3;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 单用户通信上下文
 * @author ryan
 *
 */
public abstract class GaoContext extends ApplicationContext{
	/**
	 * 原路发送到缓存
	 * @param obj
	 */
	public abstract void write(Object obj);
	
	public abstract void writeAndFlush(List<Object> list);
	/**
	 * 发送缓存的数据
	 */
	public abstract void flush();
	/**
	 * 原路立刻发送
	 * @param obj
	 */
    public abstract void writeAndFlush(Object obj);
    /**
     * 关闭连接(默认异步)
     */
    public abstract void close();
    
    /**
     * 关闭连接(同步)
     */
    public abstract boolean closeSync() throws InterruptedException;
    
    public abstract InetSocketAddress getInetSocketAddress();
    

}
