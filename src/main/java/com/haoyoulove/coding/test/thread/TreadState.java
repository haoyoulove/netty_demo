package com.haoyoulove.coding.test.thread;

/**
 * 4.1.4　线程的状态
 * @author lenovo
 *
 */
public class TreadState {

	public static void main(String[] args) {
		new Thread(new TimeWaiting(), "TimeWaitingThread").start();
		new Thread(new Waiting(), "WaitingThread").start();
		// 使用两个Blocked线程，一个获取锁成功，另一个被阻塞
		new Thread(new Blocked(), "BlockedThread-1").start();
		new Thread(new Blocked(), "BlockedThread-2").start();
		
	}
	// 该线程不断进行睡眠
	static class TimeWaiting implements Runnable{
		@Override
		public void run() {
			while (true) {
				SleepUtils.second(100);
			}
		}
	}
	// 该线程在Waiting.class实例上等待
	static class Waiting implements Runnable{
		@Override
		public void run() {
			while (true) {
				synchronized (Waiting.class) {
					try {
						Waiting.class.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	// 该线程在Blocked.clas实例上加锁后，不会释放锁
	static class Blocked implements Runnable{
		@Override
		public void run() {
			synchronized (Blocked.class) {
				while (true) {
					SleepUtils.second(100);
				}
			}
		}
	}
	
	
	
}
/**
 * 在cmd执行使用jps 查看类的pid，然后执行jstack pid 得到下面快照
 */
/**
2018-04-07 12:27:29
Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.151-b12 mixed mode):

"DestroyJavaVM" #14 prio=5 os_prio=0 tid=0x0000000005058000 nid=0x1a50 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"BlockedThread-2" #13 prio=5 os_prio=0 tid=0x0000000017a3b800 nid=0x2248 waiting on condition [0x00000000187bf000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
        at java.lang.Thread.sleep(Native Method)
        at java.lang.Thread.sleep(Thread.java:340)
        at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
        at com.haoyoulove.coding.test.thread.SleepUtils.second(SleepUtils.java:9)
        at com.haoyoulove.coding.test.thread.TreadState$Blocked.run(TreadState.java:43)
        - locked <0x00000000f56aad18> (a java.lang.Class for com.haoyoulove.coding.test.thread.TreadState$Blocked)
        at java.lang.Thread.run(Thread.java:748)

"BlockedThread-1" #12 prio=5 os_prio=0 tid=0x0000000017a3a800 nid=0x30e8 waiting for monitor entry [0x00000000186bf000]
   java.lang.Thread.State: BLOCKED (on object monitor)
        at com.haoyoulove.coding.test.thread.TreadState$Blocked.run(TreadState.java:43)
        - waiting to lock <0x00000000f56aad18> (a java.lang.Class for com.haoyoulove.coding.test.thread.TreadState$Blocked)
        at java.lang.Thread.run(Thread.java:748)

"WaitingThread" #11 prio=5 os_prio=0 tid=0x0000000017a32800 nid=0x2e04 in Object.wait() [0x00000000185bf000]
   java.lang.Thread.State: WAITING (on object monitor)
        at java.lang.Object.wait(Native Method)
        - waiting on <0x00000000f56a9040> (a java.lang.Class for com.haoyoulove.coding.test.thread.TreadState$Waiting)
        at java.lang.Object.wait(Object.java:502)
        at com.haoyoulove.coding.test.thread.TreadState$Waiting.run(TreadState.java:29)
        - locked <0x00000000f56a9040> (a java.lang.Class for com.haoyoulove.coding.test.thread.TreadState$Waiting)
        at java.lang.Thread.run(Thread.java:748)

"TimeWaitingThread" #10 prio=5 os_prio=0 tid=0x0000000017a31800 nid=0x2d74 waiting on condition [0x00000000184be000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
        at java.lang.Thread.sleep(Native Method)
        at java.lang.Thread.sleep(Thread.java:340)
        at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
        at com.haoyoulove.coding.test.thread.SleepUtils.second(SleepUtils.java:9)
        at com.haoyoulove.coding.test.thread.TreadState$TimeWaiting.run(TreadState.java:18)
        at java.lang.Thread.run(Thread.java:748)

"Service Thread" #9 daemon prio=9 os_prio=0 tid=0x000000001796e000 nid=0x2a10 runnable [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C1 CompilerThread2" #8 daemon prio=9 os_prio=2 tid=0x00000000165a2800 nid=0x24d8 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread1" #7 daemon prio=9 os_prio=2 tid=0x000000001659e800 nid=0xd8c waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"C2 CompilerThread0" #6 daemon prio=9 os_prio=2 tid=0x000000001659a800 nid=0x329c waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Attach Listener" #5 daemon prio=5 os_prio=2 tid=0x0000000017903800 nid=0x1d90 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Signal Dispatcher" #4 daemon prio=9 os_prio=2 tid=0x0000000016585800 nid=0xea0 runnable [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"Finalizer" #3 daemon prio=8 os_prio=1 tid=0x000000000514d800 nid=0x38e4 in Object.wait() [0x00000000178bf000]
   java.lang.Thread.State: WAITING (on object monitor)
        at java.lang.Object.wait(Native Method)
        - waiting on <0x00000000f5588ec8> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:143)
        - locked <0x00000000f5588ec8> (a java.lang.ref.ReferenceQueue$Lock)
        at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:164)
        at java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:209)

"Reference Handler" #2 daemon prio=10 os_prio=2 tid=0x0000000005148000 nid=0x2b8c in Object.wait() [0x00000000177bf000]
   java.lang.Thread.State: WAITING (on object monitor)
        at java.lang.Object.wait(Native Method)
        - waiting on <0x00000000f5586b68> (a java.lang.ref.Reference$Lock)
        at java.lang.Object.wait(Object.java:502)
        at java.lang.ref.Reference.tryHandlePending(Reference.java:191)
        - locked <0x00000000f5586b68> (a java.lang.ref.Reference$Lock)
        at java.lang.ref.Reference$ReferenceHandler.run(Reference.java:153)

"VM Thread" os_prio=2 tid=0x0000000016537000 nid=0x14a0 runnable

"GC task thread#0 (ParallelGC)" os_prio=0 tid=0x000000000506e000 nid=0x2dd4 runnable

"GC task thread#1 (ParallelGC)" os_prio=0 tid=0x000000000506f800 nid=0x2888 runnable

"GC task thread#2 (ParallelGC)" os_prio=0 tid=0x0000000005071000 nid=0xe78 runnable

"GC task thread#3 (ParallelGC)" os_prio=0 tid=0x0000000005073000 nid=0x2004 runnable

"VM Periodic Task Thread" os_prio=2 tid=0x0000000017a22800 nid=0x33cc waiting on condition

JNI global references: 6
*/

















