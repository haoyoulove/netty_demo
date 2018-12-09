package com.haoyoulove.coding.test.observers;

import ch.qos.logback.core.net.SyslogOutputStream;

import java.util.concurrent.CountDownLatch;

/**
 * @author yangjing
 */
public class TestDemo {

	static CountDownLatch start = new CountDownLatch(1);

	public static void  main(String args[]){

		ServerManager sm = new ServerManager();
		sm.addObserver(new AObserver());


		for (int i = 1; i <= 10; i++){
			Thread thread = new Thread(new servers(sm, i), "thread"+i);
			thread.start();
		}

		System.out.println("start->");
		start.countDown();

	}


	static class servers implements Runnable{

		private ServerManager serverManger;
		private int i;

		servers(ServerManager sm, int i){
			this.serverManger = sm;
			this.i = i;

		}

		@Override
		public void run() {
			try {
				start.await();
			} catch (Exception ex) {
			}
			System.out.println("run->"+Thread.currentThread().getName());
			serverManger.setData(i);
			System.out.println("run end->"+Thread.currentThread().getName());
		}
	}
}
