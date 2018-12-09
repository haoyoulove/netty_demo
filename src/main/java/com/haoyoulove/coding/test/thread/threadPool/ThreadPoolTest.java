package com.haoyoulove.coding.test.thread.threadPool;

import java.util.concurrent.CountDownLatch;


public class ThreadPoolTest {
	
	static DefaultThreadPool<Job> pool = new DefaultThreadPool<>(5);
	// 保证所有ConnectionRunner能够同时开始
	static CountDownLatch start = new CountDownLatch(1);
	// main线程将会等待所有ConnectionRunner结束后才能继续执行
	static CountDownLatch end;
	
	public static void main(String[] args) throws Exception {
		int threadCount = 200;
		for (int i = 0; i < threadCount; i++) {
			pool.execute(new Job());
		}
		System.out.println("start->");
		start.countDown();
	}
	static class Job implements Runnable {

		public void run() {
			try {
				start.await();
			} catch (Exception ex) {
			}
			System.out.println("Job->"+Thread.currentThread().getName());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
