package com.haoyoulove.coding.test.thread;

/**
 * 4.3.1  volatile和synchronized关键字
 * @author lenovo
 *
 */
public class Synchronized {

	public static void main(String[] args) {
		// 对Synchronized Class对象进行加锁
		synchronized (Synchronized.class) {
		}
		// 静态同步方法，对Synchronized Class对象进行加锁
		m();
	}

	public static synchronized void m() {
	}
}
