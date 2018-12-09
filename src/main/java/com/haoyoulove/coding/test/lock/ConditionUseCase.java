package com.haoyoulove.coding.test.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.haoyoulove.coding.test.lock.LockTest.LockRun;

/**
 * Condition使用示例
 * @author lenovo
 *
 */
public class ConditionUseCase {

	public static void main(String[] args) throws InterruptedException {
		Lock lock = new ReentrantLock();
		Condition condition = lock.newCondition();
		
		
		System.out.println("awatiLock开始");
		Thread thread = new Thread(new ConditionClass1(lock, condition), "awatiLock");
		thread.start();
		
		Thread.sleep(1000);
		
		System.out.println("signalLock 开始");
		Thread thread2 = new Thread(new  ConditionClass2(lock, condition), "signalLock");
		thread2.start();
		
		System.out.println("结束");

	}

	static class ConditionClass1 implements Runnable{
		
		Lock lock;
		Condition condition;
		
		ConditionClass1(Lock lock, Condition condition){
			this.lock = lock;
			this.condition = condition;
		}
		
		@Override
		public void run() {
			lock.lock();
			try {
				System.out.println("condition awati start======");
				condition.await();
				System.out.println("condition awati end======");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
	}
	
	static class ConditionClass2 implements Runnable{
		
		Lock lock;
		Condition condition;
		
		ConditionClass2(Lock lock, Condition condition){
			this.lock = lock;
			this.condition = condition;
		}
		@Override
		public void run() {
			lock.lock();
			try {
				System.out.println("condition signal start======");
				condition.signal();
				System.out.println("condition signal end======");
			} finally {
				lock.unlock();
			}
		}
	}
	
	
	
}
