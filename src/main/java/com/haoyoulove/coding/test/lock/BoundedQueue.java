package com.haoyoulove.coding.test.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition示例 有界队列是一种特殊的队列，当队列为空时，队列的获取操作
 * 将会阻塞获取线程，直到队列中有新增元素，当队列已满时，队列的插入操作将会阻塞插入线 程，直到队列出现“空位”
 * 
 * @author lenovo
 *
 */
public class BoundedQueue<T> {

	private Object[] items;

	// 添加的下标，删除的下标和数组当前数量
	private int addIndex, removeIndex, count;

	private Lock lock = new ReentrantLock();
	// 队列空了获取阻塞
	private Condition notEmpty = lock.newCondition();
	// 队列满了插入阻塞
	private Condition notFull = lock.newCondition();

	public BoundedQueue(int size) {
		items = new Object[size];
	}

	// 添加一个元素，如果数组满，则添加线程进入等待状态，直到有"空位"
	// 都有点问题，addIndex=0 不太理解，只理解思想即可。
	public void add(T t) throws InterruptedException {
		lock.lock();
		try {
			while (count == items.length){
				//	释放锁进入等待状态
				notFull.await();
			}

			items[addIndex] = t;
			if (++addIndex == items.length)
				addIndex = 0;
			++count;
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}

	// 由头部删除一个元素，如果数组空，则删除线程进入等待状态，直到有新添加元素
	// 这里删除有点问题
	@SuppressWarnings("unchecked")
	public T remove() throws InterruptedException {
		lock.lock();
		try {
			while (count == 0)
				notEmpty.await();
			
			Object x = items[removeIndex];
			// 先加在比较
			if (++removeIndex == items.length)
				removeIndex = 0;
			--count;
			notFull.signal();
			return (T) x;
		} finally {
			lock.unlock();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		BoundedQueue<Integer> bQueue = new BoundedQueue<>(2);
		bQueue.add(1);
		bQueue.add(2);
		
		bQueue.remove();
		bQueue.remove();
		bQueue.remove();
	}

}









