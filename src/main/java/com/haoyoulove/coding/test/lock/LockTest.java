package com.haoyoulove.coding.test.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 
 * @author lenovo
 * 验证独占式线程竞争
 */
public class LockTest  {

	public static void main(String[] args) {
		try {
			Lock lock = new ReentrantLock(true);
			System.out.println("线程1开始获取锁");
			Thread thread = new Thread(new LockRun(lock), "lock1");
			thread.start();
			
			Thread.sleep(1000);
			
			System.out.println("线程2开始获取锁");
			Thread thread2 = new Thread(new LockRun(lock), "lock2");
			thread2.start();
			
			System.out.println("结束");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	static class LockRun implements Runnable{
		final Lock lock;
		
		public LockRun(Lock lock) {
			this.lock = lock;  
		}
		@Override
		public void run() {
			String name = Thread.currentThread().getName();
			if(name.equals("lock2")){
				System.out.println("断点");
			}
			lock.lock();
			try {
				Thread.sleep(50000);
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				lock.unlock();
			}
			
			
		}
	}
}
