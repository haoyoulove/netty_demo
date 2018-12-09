package com.haoyoulove.coding.test.thread.connectionPool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;
/**
 * 由于java.sql.Connection是一个接口，最终的实现是由数据库驱动提供方来实现的，考虑到
	只是个示例，我们通过动态代理构造了一个Connection，该Connection的代理实现仅仅是在
	commit()方法调用时休眠100毫秒
 * @author lenovo
 *
 */
public class ConnectionDriver {
	
	static class ConnectionHandler implements InvocationHandler{
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("commit")) {
				TimeUnit.MILLISECONDS.sleep(100);
			}
			return null;
		}
	}
	
	public static final Connection createConnection(){
		return (Connection) Proxy.newProxyInstance(ConnectionDriver.class.getClassLoader(),
				new Class<?>[] {Connection.class}, new ConnectionHandler());
	}
}
