package com.haoyoulove.coding.test.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class QueueTest {
	
	public static void main(String[] args) {
		
		BlockingQueue<String> bq = new ArrayBlockingQueue<>(3);
		bq.offer("1");
		bq.offer("2");
		bq.offer("3");
		bq.poll();
		bq.offer("4");
		
		
	}

}
