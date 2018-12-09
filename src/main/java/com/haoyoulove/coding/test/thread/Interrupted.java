package com.haoyoulove.coding.test.thread;

import java.util.concurrent.TimeUnit;

/**
 * 4.2.3 理解中断
 * @author lenovo
 *
 */
public class Interrupted {

	public static void main(String[] args) throws InterruptedException {
		// SleepRunner 不停的尝试睡眠
		Thread sleepThread = new Thread(new SleepRunner(), "SleeepRunner");
		sleepThread.setDaemon(true);
		// busyThread不停的运行
		Thread busyThread = new Thread(new BusyRunner(), "BusyThread");
		busyThread.setDaemon(true);
		sleepThread.start();
		busyThread.start();
		// 休眠5秒，让sleepThread和busyThread充分运行
		TimeUnit.SECONDS.sleep(5);
		sleepThread.interrupt();
		busyThread.interrupt();
		System.out.println("SleepThread interrupted is " + sleepThread.isInterrupted());
		System.out.println("BusyThread interrupted is " + busyThread.isInterrupted());
		
		// 防止sleepThread和busyThread立刻退出
		SleepUtils.second(2);
		
	}
	
	static class SleepRunner implements Runnable{
		@Override
		public void run() {
			while (true) {
				SleepUtils.second(10);
			}
		}
		
	}
	static class BusyRunner implements Runnable{
		@Override
		public void run() {
			while (true) {
				
			}
		}
	}
}

















