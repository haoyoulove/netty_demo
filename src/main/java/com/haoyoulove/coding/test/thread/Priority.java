package com.haoyoulove.coding.test.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 4.1.3  线程优先级
 * @author lenovo
 *
 */
public class Priority {
	private static volatile boolean notStart = true;
	private static volatile boolean notEnd = true;
	
	public static void main(String[] args) throws InterruptedException {
		List<Job> jobs = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			int priority = i < 5  ? Thread.MIN_PRIORITY : Thread.MAX_PRIORITY;
			Job job = new Job(priority);
			jobs.add(job);
			Thread thread = new Thread(job, "Thread:" + i);
			thread.setPriority(priority);
			thread.start();
		}
		notStart = false;
		TimeUnit.SECONDS.sleep(10);
		notEnd = false;
		for (Job job : jobs) {
		System.out.println("Job Priority : " + job.priority + ",Count : " + job.jobCount);
		}
		
		
	}

	static class Job implements Runnable{
		private int priority;
		private long jobCount;
		
		public Job(int priority) {
			this.priority = priority;
		}
		
		@Override
		public void run() {
			while(notStart){
				//使当前线程从执行状态（运行状态）变为可执行态（就绪状态）。cpu会从众多的可执行态里选择
				//该线程就会把CPU时间让掉，让其他或者自己的线程执行（也就是谁先抢到谁执行）
				Thread.yield();
			}
			while (notEnd) {
				Thread.yield();
				jobCount++;
			}
			
		}
	}
}
// win10 执行结果
//Job Priority : 1,Count : 595155
//Job Priority : 1,Count : 595414
//Job Priority : 1,Count : 595146
//Job Priority : 1,Count : 595194
//Job Priority : 1,Count : 595225
//Job Priority : 10,Count : 3967482
//Job Priority : 10,Count : 3976683
//Job Priority : 10,Count : 3977499
//Job Priority : 10,Count : 3975377
//Job Priority : 10,Count : 3978247
