package com.haoyoulove.coding.test.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * 一个独占锁的示例,类似实现了一个ReentrantLock的独占实现方法例子
 * @author lenovo
 */
public class MutexLock implements Lock8{
	
	// 静态内部类，自定义同步器
	private static class Sync extends AbstractQueuedSynchronizer8{
		private static final long serialVersionUID = 1L;

		// 是否处于占用状态
		protected boolean isHeldExclusively(){
			return getState() == 1;
		}
		// 当状态为0的时候获取锁,这个重写方法不会阻塞,阻塞的是同步器里面的acquire
		public boolean tryAcquire(int acq){
			// 当前state==0的时候获取并且更新值， CAS更新.不会阻塞
			if(compareAndSetState(0, 1)){
				setExclusiveOwnerThread(Thread.currentThread());
				return true;
			}
			return false;
		}
		//释放锁，将状态设置为0
		protected boolean tryRelease(int releases){
			if(getState() == 0){
				throw new IllegalMonitorStateException();
			}
			/**
			 * 这里是线程安全的，因为只可能只有一个当前获得锁的线程在处理
			 * 这里是被同步器里面的模板方法调用的，调用完之后，成功释放，那么模板方法release里面会唤醒同步队列里面的第一个线程
			 */
			setExclusiveOwnerThread(null);
			setState(0);
			return true;
		}
		
		Condition newCondition(){
			return new ConditionObject();
		}
	}
	
	// 仅仅需要将操作代理到sync上即可
	private final Sync sync = new Sync();
	
	@Override
	public void lock() {
		sync.acquire(1);
	}
	@Override
	public void lockInterruptibly() throws InterruptedException {
		sync.acquireInterruptibly(1);
	}
	// 这个是直接调用获取锁,不会阻塞
	@Override
	public boolean tryLock() {
		return sync.tryAcquire(1);
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return sync.tryAcquireNanos(1, unit.toNanos(time));
	}

	@Override
	public void unlock() {
		sync.release(1);
	}

	@Override
	public Condition8 newCondition() {
//		return sync.newCondition();
		return null;
	}
	
}
