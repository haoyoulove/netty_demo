package com.haoyoulove.coding.test.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public interface Lock8 {

    /**
     * Acquires the lock. 获取锁
     * 如果锁定不可用，则当前线程因为线程调度目的而被禁用，并处于休眠状态，直到获取锁定。
     * 实现可能能够检测锁的错误使用，例如会导致死锁的调用，并且可能在这种情况下抛出（未检查的）异常。 该情况和异常类型必须由该实现记录。
     */
    void lock();

    /**
     * 中断获取锁，如果锁定不能获取，则当前线程禁用处于休眠状态，直到获取锁或者被中断
     */
    void lockInterruptibly() throws InterruptedException;

    /**
     * 尝试非阻塞的获取锁，调用该方法后立刻返回，如果能够获取则返回true,否则返回false
     */
    boolean tryLock();

    /**
     * 超时的获取锁
     * 1、当前线程超时时间内获得了锁 返回true
     * 2、当前线程超时时间内被中断
     * 3、超时时间结束、返回false
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    /**
     * Releases the lock.
     *	释放锁
     */
    void unlock();

    /**
     * 获取等待通知组件，该组件和当前锁绑定，当前线程只有获取了锁，才能调用该组件的wait()方法，而调用后，当前线程将释放锁。
     */
    Condition8 newCondition();
}
