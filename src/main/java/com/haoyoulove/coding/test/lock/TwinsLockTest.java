package com.haoyoulove.coding.test.lock;

import java.util.concurrent.locks.Lock;

import com.haoyoulove.coding.test.thread.SleepUtils;

public class TwinsLockTest {
	

	public static void  main(String args[]){
		final Lock lock = new TwinsLock();
		// 启动10个线程
		for (int i = 0; i < 10; i++) {
			Worker w = new Worker(lock);
//			w.setDaemon(true);
			w.start();
		}
		// 每隔1秒换行(可以使用daemon(main)来让下面这个执行完之后其他设置为daemon线程也执行完毕了)
//		for (int i = 0; i < 10; i++) {
//			SleepUtils.second(1);
//			System.out.println();
//		}
	}
	
	static class Worker extends Thread {
		final Lock lock;
		public Worker(Lock lock) {
			this.lock = lock;
		}
		public void run() {
			while (true) {
				lock.lock();
				try {
					SleepUtils.second(1);
					System.out.println(Thread.currentThread().getName());
					SleepUtils.second(1);
				} finally {
					lock.unlock();
				}
			}
		}
	}
}
