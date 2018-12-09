package com.pingan.traffic.netty3;

import java.util.List;
import java.util.Set;

/**
 * 全局上下文
 * @author ryan
 *
 */
public abstract class ApplicationContext {
	private static ApplicationContext applicationContext;
	
	public static ApplicationContext getContext() {
		return applicationContext;
	}
	public static void setContext(ApplicationContext applicationContext) {
		if(ApplicationContext.applicationContext == null){
			ApplicationContext.applicationContext = applicationContext;
		}
	}

	public abstract void writeToWeb(Object obj);

	/**
     * 发送到列表指定的用户
     * @param uidList
     * @param obj
     */
    public abstract void writeToList(List<Integer> uidList, Object obj);
    /**
     * 发送给指定唯一用户
     * @param uid
     * @param obj
     */
    public abstract void writeToOne(int uid, Object obj);
    /**
     * 发送给指定房间号和唯一用户
     * @param uid
     * @param obj
     */
    public abstract void writeToOneAndRoom(int uid,int roomId, Object obj);
    /**
     * 发送给指定房间的所有用户
     * @param roomId
     * @param obj
     */
    public abstract void writeToRoom(int roomId, Object obj);
	/**
	 * 发送给指定房间的所有用户
	 * @param roomId
	 * @param obj
	 */
	public abstract void writeToRoomSync(int roomId, Object obj);
    /**
     * 发送给指定应用的所有用户
     * @param appId
     * @param obj
     */
    public abstract void writeToApp(int appId, Object obj);
    /**
     * 发送当前服务器所有用户
     * @param obj
     */
    public abstract void writeToAll(Object obj);
    /**
     * 关闭某个用户(默认异步)
     * @param uid
     */
    public abstract void close(int uid);
    /**
     * 关闭某个用户
     * @param uid
     */
    public abstract boolean closeSync(int uid) throws InterruptedException;
    /**
     * 获取指定用户上下文
     * @param uid
     * @return
     */
    public abstract GaoContext getContext(int uid);
    /**
     * 获取所有的上下文
     * @param appId a
     * @return
     */
    public abstract Set<GaoContext> getContexts();
    
    /**
     * 获取房间里所有人的上下文
     * @param appId a
     * @return
     */
    public abstract Set<GaoContext> getContexts(int roomId);
}
