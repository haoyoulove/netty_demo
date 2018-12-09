package com.haoyoulove.coding.test.lock;

public interface ReadWriteLock8 {
    /**
     * Returns the lock used for reading.
     *
     * @return the lock used for reading
     */
    Lock8 readLock();

    /**
     * Returns the lock used for writing.
     *
     * @return the lock used for writing
     */
    Lock8 writeLock();
}
