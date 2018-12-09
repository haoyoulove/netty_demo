package com.haoyoulove.coding.test.thread;

import java.util.concurrent.TimeUnit;

/**
 *  4.3.5　Thread.join()的使用
 * @author lenovo
 * 每个线程终止的前提是前驱线程的终止，每个线程等待前驱线程
	终止后，才从join()方法返回，这里涉及了等待/通知机制（等待前驱线程结束，接收前驱线程结
	束通知）。
 */
public class Join {
	
	public static void main(String[] args) throws InterruptedException {
		Thread previous = Thread.currentThread();
		for (int i = 0; i < 10; i++) {
		// 每个线程拥有前一个线程的引用，需要等待前一个线程终止，才能从等待中返回
		Thread thread = new Thread(new Domino(previous), String.valueOf(i));
		thread.start();
		previous = thread;
		}
		TimeUnit.SECONDS.sleep(5);
		System.out.println(Thread.currentThread().getName() + " 1terminate.");
	}
	static class Domino implements Runnable{
		private Thread thread;
		public Domino(Thread thread) {
			this.thread = thread;
		}
		@Override
		public void run() {
			try {
				//当前线程等待thread线程终止才从thread.join()返回
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + " terminate.");
			
		}
	}
}
