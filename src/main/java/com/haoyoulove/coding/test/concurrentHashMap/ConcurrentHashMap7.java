
package com.haoyoulove.coding.test.concurrentHashMap;
import com.gaoinvest.common.util.UUIDUtil;
import com.haoyoulove.coding.test.lock.ReentrantLock7;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentHashMap7<K, V> extends AbstractMap<K, V>
        implements ConcurrentMap<K, V>, Serializable {
    private static final long serialVersionUID = 7249069246763182397L;


    /* ---------------- Constants -------------- */

    /**
     * The default initial capacity for this table,
     * used when not otherwise specified in a constructor.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The default load factor for this table, used when not
     * otherwise specified in a constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The default concurrency level for this table, used when not
     * otherwise specified in a constructor.
     */
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly
     * specified by either of the constructors with arguments.  MUST
     * be a power of two <= 1<<30 to ensure that entries are indexable
     * using ints.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The minimum capacity for per-segment tables.  Must be a power
     * of two, at least two to avoid immediate resizing on next use
     * after lazy construction.
     */
    static final int MIN_SEGMENT_TABLE_CAPACITY = 2;

    /**
     * The maximum number of segments to allow; used to bound
     * constructor arguments. Must be power of two less than 1 << 24.
     */
    static final int MAX_SEGMENTS = 1 << 16; // slightly conservative

    /**
     * Number of unsynchronized retries in size and containsValue
     * methods before resorting to locking. This is used to avoid
     * unbounded retries if tables undergo continuous modification
     * which would make it impossible to obtain an accurate result.
     */
    static final int RETRIES_BEFORE_LOCK = 2;

    /* ---------------- Fields -------------- */

    /**
     * holds values which can't be initialized until after VM is booted.
     */
    private static class Holder {

        /**
        * Enable alternative hashing of String keys?
        *
        * <p>Unlike the other hash map implementations we do not implement a
        * threshold for regulating whether alternative hashing is used for
        * String keys. Alternative hashing is either enabled for all instances
        * or disabled for all instances.
        */
        static final boolean ALTERNATIVE_HASHING;

        static {
            // Use the "threshold" system property even though our threshold
            // behaviour is "ON" or "OFF".
            String altThreshold = java.security.AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction(
                    "jdk.map.althashing.threshold"));

            int threshold;
            try {
                threshold = (null != altThreshold)
                        ? Integer.parseInt(altThreshold)
                        : Integer.MAX_VALUE;

                // disable alternative hashing if -1
                if (threshold == -1) {
                    threshold = Integer.MAX_VALUE;
                }

                if (threshold < 0) {
                    throw new IllegalArgumentException("value must be positive integer.");
                }
            } catch(IllegalArgumentException failed) {
                throw new Error("Illegal value for 'jdk.map.althashing.threshold'", failed);
            }
            ALTERNATIVE_HASHING = threshold <= MAXIMUM_CAPACITY;
        }
    }

    /**
     * A randomizing value associated with this instance that is applied to
     * hash code of keys to make hash collisions harder to find.
     */
    private transient final int hashSeed = randomHashSeed(this);

    private static int randomHashSeed(ConcurrentHashMap7 instance) {
        if (sun.misc.VM.isBooted() && Holder.ALTERNATIVE_HASHING) {
//            return sun.misc.Hashing.randomHashSeed(instance);
			return Integer.valueOf(UUIDUtil.getRandomNum(10));
        }

        return 0;
    }

    /**
     * Mask value for indexing into segments. The upper bits of a
     * key's hash code are used to choose the segment.
     *  索引分段的掩码值
     */
    final int segmentMask;

    /**
     * Shift value for indexing within segments.
     * 片段中索引的偏移量
     */
    final int segmentShift;

    /**
     * The segments, each of which is a specialized hash table.
     */
    final Segment<K,V>[] segments;

    transient Set<K> keySet;
    transient Set<Entry<K,V>> entrySet;
    transient Collection<V> values;

    /**
     * ConcurrentHashMap7 list entry. Note that this is never exported
     * out as a user-visible Map.Entry.
     *
     * 不对外引用的HashEntry. 基本都设置为final是为了防止链表被破坏
     */
    static final class HashEntry<K,V> {
        final int hash;
        final K key;
        volatile V value;
        volatile HashEntry<K,V> next;

        HashEntry(int hash, K key, V value, HashEntry<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        /**
         * Sets next field with volatile write semantics.  (See above
         * about use of putOrderedObject.)
         */
        final void setNext(HashEntry<K,V> n) {
            UNSAFE.putOrderedObject(this, nextOffset, n);
        }

        // Unsafe mechanics
        static final sun.misc.Unsafe UNSAFE;
        static final long nextOffset;
        static {
            try {
                UNSAFE = sun.misc.Unsafe.getUnsafe();
                Class k = HashEntry.class;
                nextOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("next"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    /**
     * Gets the ith element of given table (if nonnull) with volatile
     * read semantics. Note: This is manually integrated into a few
     * performance-sensitive methods to reduce call overhead.
     */
    @SuppressWarnings("unchecked")
    static final <K,V> HashEntry<K,V> entryAt(HashEntry<K,V>[] tab, int i) {
        return (tab == null) ? null :
            (HashEntry<K,V>) UNSAFE.getObjectVolatile
            (tab, ((long)i << TSHIFT) + TBASE);
    }

    /**
     * Sets the ith element of given table, with volatile write
     * semantics. (See above about use of putOrderedObject.)
     */
    static final <K,V> void setEntryAt(HashEntry<K,V>[] tab, int i,
                                       HashEntry<K,V> e) {
        UNSAFE.putOrderedObject(tab, ((long)i << TSHIFT) + TBASE, e);
    }

    /**
     * Applies a supplemental hash function to a given hashCode, which
     * defends against poor quality hash functions.  This is critical
     * because ConcurrentHashMap7 uses power-of-two length hash tables,
     * that otherwise encounter collisions for hashCodes that do not
     * differ in lower or upper bits.
     */
    private int hash(Object k) {
        int h = hashSeed;

        if ((0 != h) && (k instanceof String)) {
//            return sun.misc.Hashing.stringHash32((String) k);
			return Integer.valueOf(UUIDUtil.getRandomNum(10));
        }

        h ^= k.hashCode();

        // Spread bits to regularize both segment and index locations,
        // using variant of single-word Wang/Jenkins hash.
        h += (h <<  15) ^ 0xffffcd7d;
        h ^= (h >>> 10);
        h += (h <<   3);
        h ^= (h >>>  6);
        h += (h <<   2) + (h << 14);
        return h ^ (h >>> 16);
    }

    /**
     * Segments are specialized versions of hash tables.  This
     * subclasses from ReentrantLock opportunistically, just to
     * simplify some locking and avoid separate construction.
	 *
	 * segment片段，内部数据结构，继承ReentrantLock
     */
    static final class Segment<K,V> extends ReentrantLock7 implements Serializable {

        private static final long serialVersionUID = 2249069246763182397L;

        /**
		 *
		 * 尝试获取锁的最大次数
         */
        static final int MAX_SCAN_RETRIES =
            Runtime.getRuntime().availableProcessors() > 1 ? 64 : 1;

        /**
         * The per-segment table. Elements are accessed via
         * entryAt/setEntryAt providing volatile semantics.
		 *
		 * 每个片段对应的table数组
         */
        transient volatile HashEntry<K,V>[] table;

        /**
         * The number of elements. Accessed only either within locks
         * or among other volatile reads that maintain visibility.
		 *
		 * 每个片段存在多少个HashEntry  table数组大小中拥有多少个元素
         */
        transient int count;

        /**
         * The total number of mutative operations in this segment.
         * Even though this may overflows 32 bits, it provides
         * sufficient accuracy for stability checks in CHM isEmpty()
         * and size() methods.  Accessed only either within locks or
         * among other volatile reads that maintain visibility.
		 *
		 * 这个片段中的修改次数
         */
        transient int modCount;

        /**
         * The table is rehashed when its size exceeds this threshold.
         * (The value of this field is always <tt>(int)(capacity *
         * loadFactor)</tt>.)
		 *
		 * 当前片段使用的阀值,segment元素超过这个值会扩容
         */
        transient int threshold;

        /**
         * The load factor for the hash table.  Even though this value
         * is same for all segments, it is replicated to avoid needing
         * links to outer object.
         * @serial
		 * 每个片段的负载因子
         */
        final float loadFactor;

        // 实例化 片段 构造函数
        Segment(float lf, int threshold, HashEntry<K,V>[] tab) {
            this.loadFactor = lf;
            this.threshold = threshold;
            this.table = tab;
        }

        //segment片段的put插入
        final V put(K key, int hash, V value, boolean onlyIfAbsent) {
            // 尝试去获取锁，tryLock=true 就设置node为null,如果拿不到锁，走scanAndLockForPut
            HashEntry<K,V> node = tryLock() ? null : scanAndLockForPut(key, hash, value);
            V oldValue;
            try {
                // segment片段的table数组
                HashEntry<K,V>[] tab = table;
                // 再利用 hash 值，求应该放置的数组下标,就类似hashmap对应的数组下标
                int index = (tab.length - 1) & hash;
                // 通过index 去获取table数组对应的tab元素entry,是通过硬件操作的java方法去调用的
                HashEntry<K,V> first = entryAt(tab, index);
                // 遍历链表，分链表是否为null的两种情况
                for (HashEntry<K,V> e = first;;) {
                    //链表的元素不为null
                    if (e != null) {
                        K k;
                        // 校验key相同,（这个好像就和hashmap有点不一样了，不过差别也不大）
                        //e元素不为空的时候，查询对应key值是否相同，替换旧值，跳出循环
                        if ((k = e.key) == key || (e.hash == hash && key.equals(k))) {
                            oldValue = e.value;
                            // 有些put只是为了去获取值而已，不需要更改替换
                            if (!onlyIfAbsent) {
                                e.value = value;
                                ++modCount;
                            }
                            break;
                        }
                        // 继续顺着链表走
                        e = e.next;
                    }
                    //遍历链表的元素为空，基本就是最后一个结点了
                    else {
                        // node 到底是不是 null，这个要看获取锁的过程，不过和这里都没有关系。
                        // 如果不为 null，那就直接将它设置为链表表头；如果是null，初始化并设置为链表表头。
                        // first 是null的
                        if (node != null) {
                            node.setNext(first);
                        } else {
                            node = new HashEntry<K, V>(hash, key, value, first);
                        }
                        int c = count + 1;
                        // 如果超过了该 segment 的阈值，这个 segment 需要扩容
                        if (c > threshold && tab.length < MAXIMUM_CAPACITY) {
                            //重置扩容
                            rehash(node);
                        } else {
                            // 硬件原子操作挂载在table数组上
                            // 没有达到阈值，将 node 放到数组 tab 的 index 位置，
                            // 其实就是将新的节点设置成原链表的表头
                            setEntryAt(tab, index, node);
                        }
                        ++modCount;
                        count = c;
                        oldValue = null;
                        break;
                    }
                }
            } finally {
                // 解锁
                unlock();
            }
            return oldValue;
        }

        /**
         * Doubles size of table and repacks entries, also adding the given node to new table
         *
         */
        @SuppressWarnings("unchecked")
        private void rehash(HashEntry<K,V> node) {
            /*
              Reclassify nodes in each list to new table.  Because we
              are using power-of-two expansion, the elements from
              each bin must either stay at same index, or move with a
              power of two offset. We eliminate unnecessary node
              creation by catching cases where old nodes can be
              reused because their next fields won't change.
              Statistically, at the default threshold, only about
              one-sixth of them need cloning when a table
              doubles. The nodes they replace will be garbage
              collectable as soon as they are no longer referenced by
              any reader thread that may be in the midst of
              concurrently traversing table. Entry accesses use plain
              array indexing because they are followed by volatile
              table write.
             */

            // 分配设值，创建新的table数组大小，重新设置阀值，遍历老table
            HashEntry<K,V>[] oldTable = table;
            int oldCapacity = oldTable.length;
            int newCapacity = oldCapacity << 1;
            threshold = (int)(newCapacity * loadFactor);
            HashEntry<K,V>[] newTable =
                (HashEntry<K,V>[]) new HashEntry[newCapacity];
            // 新的掩码，如从 16 扩容到 32，那么 sizeMask 为 31，对应二进制 ‘000...00011111’
            int sizeMask = newCapacity - 1;
            // 遍历原数组，老套路，将原数组位置 i 处的链表拆分到 新数组位置 i 和 i+oldCap 两个位置
            for (int i = 0; i < oldCapacity ; i++) {
                // 遍历 oldtable数组上的链表，为空的话就不用考虑，说明没用到。
                // e 是链表的第一个元素
                HashEntry<K,V> e = oldTable[i];
                if (e != null) {
                    HashEntry<K,V> next = e.next;
                    // 重新计算新table上的索引index,计算应该放置在新数组中的位置
                    // 假设原数组长度为 16，e 在 oldTable[3] 处，那么 idx 只可能是 3 或者是 3 + 16 = 19
                    /**
                     * 1、16-1 00001111 & hash(00000011/00010011) = index = 3  老数组
                     * 2、如果*2 00011111 &  hash(xxx00011/xxx10011) = index = 3/16  新数组
                     * 还有一种可能，由于1中 index等于3，那么0011这两位0一定是0，但是
                     * 在2中 0011的上一位可能为0 00011 也可能为1 10011 也就是说新的index可能是3，也可能是00010011 = 3+16 = 19
                     * 不可能是其他值了.
                     * 这就相当于，在原本链表结点的元素，要么就保留在原本结点上，要么就被转移到新增加的位置上，
                     * 不会占用原来数组的位置
                     */
                    int idx = e.hash & sizeMask;
                    if (next == null)   //  Single node on list
                    {
                        //说明链表上只有一个头结点，因为上面说了原本结点不会占用其他结点位置。
                        // 那么我们可以把只有一个头结点的e放在原本位置上(反正不会被占用)
                        newTable[idx] = e;
                    } else { // Reuse consecutive sequence at same slot
                        // e 是链表表头
                        HashEntry<K,V> lastRun = e;
                        // idx 是当前链表的头结点 e 的新位置
                        int lastIdx = idx;
                        // 下面这个 for 循环会找到一个 lastRun 节点，这个节点之后的所有元素是将要放到一起的
                        for (HashEntry<K,V> last = next; last != null; last = last.next) {
                            // 同一个链表中，也可能存在不同的hash值，比如3,5,7我们只需要把最后一个不相同的hash值后面那串结点
                            // 赋值给lastRun即可，也就是把最后一串不同hash值 & sizeMask得到相同k的拿出来
                            int k = last.hash & sizeMask;
                            if (k != lastIdx) {
                                lastIdx = k;
                                lastRun = last;
                            }
                        }
                        // 没有上面这个循环，直接把e设置到newTable[lastIdx] = lastRun = e;
                        // 将 lastRun 及其之后的所有节点组成的这个链表放到 lastIdx 这个位置
                        newTable[lastIdx] = lastRun;
                        // Clone remaining nodes
                        // 下面的操作是处理 lastRun 之前的节点，
                        //    这些节点可能分配在另一个链表中，也可能分配到上面的那个链表中
                        for (HashEntry<K,V> p = e; p != lastRun; p = p.next) {
                            V v = p.value;
                            int h = p.hash;
                            int k = h & sizeMask;
                            //把这个位置上的元素都放到next结点去，后面直接插入新节点。
                            //也就是所谓的头插法
                            HashEntry<K,V> n = newTable[k];
                            // 新分配的
                            newTable[k] = new HashEntry<K,V>(h, p.key, v, n);
                        }
                    }
                }
            }
            // 将新来的 node 放到新数组中刚刚的 两个链表之一 的 头部
            int nodeIndex = node.hash & sizeMask; // add the new node
            //把当前这个结点设置到新结点里面的next下
            node.setNext(newTable[nodeIndex]);
            //当前位置指向新节点
            newTable[nodeIndex] = node;
            table = newTable;
        }

        /**
         * Scans for a node containing given key while trying to
         * acquire lock, creating and returning one if not found. Upon
         * return, guarantees that lock is held. UNlike in most
         * methods, calls to method equals are not screened: Since
         * traversal speed doesn't matter, we might as well help warm
         * up the associated code and accesses as well.
         *
         * @return a new node if key not found, else null
         *
         * 在尝试获取锁定时扫描包含给定密钥的节点，如果找不到，
         * 则创建并返回一个节点。 返回后，保证锁定。
         * 不像大多数方法那样，对方法equals的调用没有被筛选：
         * 因为遍历速度并不重要，所以我们也可以帮助预热关联的代码和访问。
          
           @如果找不到密钥，则返回新节点，否则返回null

            这个方法也就是为了尝试去获取该segment的独占锁，如果需要的话顺便实例化了一下 node。
         */
        private HashEntry<K,V> scanAndLockForPut(K key, int hash, V value) {
            // 通过hash去拿到该片段上的数组table上的第一个链表结点
            HashEntry<K,V> first = entryForHash(this, hash);
            HashEntry<K,V> e = first;
            HashEntry<K,V> node = null;
            int retries = -1; // negative while locating node
            // 循环获取锁
            while (!tryLock()) {
                HashEntry<K,V> f; // to recheck first below
                if (retries < 0) {
                    if (e == null) {
                        if (node == null) // speculatively create node
                        {
                            // 进到这里说明数组该位置的链表是空的，没有任何元素
                            // 当然，进到这里的另一个原因是 tryLock() 失败，所以该槽存在并发，不一定是该位置
                            // 头插法，每次新结点必定是插在第一个链表头位置
                            node = new HashEntry<K, V>(hash, key, value, null);
                        }
                        retries = 0;
                    }
                    else if (key.equals(e.key)) {
                        retries = 0;
                    } else {
                        // 顺着链表往下走
                        e = e.next;
                    }
                }
                // 重试次数如果超过 MAX_SCAN_RETRIES（单核1多核64），那么不抢了，进入到阻塞队列等待锁
                //    lock() 是阻塞方法，直到获取锁后返回
                else if (++retries > MAX_SCAN_RETRIES) {
                    lock();
                    break;
                }
                else if ((retries & 1) == 0 &&
                        // 这个时候是有大问题了，那就是有新的元素进到了链表，成为了新的表头
                        //     所以这边的策略是，相当于重新走一遍这个 scanAndLockForPut 方法
                         (f = entryForHash(this, hash)) != first) {
                    e = first = f; // re-traverse if entry changed
                    retries = -1;
                }
            }
            return node;
        }

        /**
         * Scans for a node containing the given key while trying to
         * acquire lock for a remove or replace operation. Upon
         * return, guarantees that lock is held.  Note that we must
         * lock even if the key is not found, to ensure sequential
         * consistency of updates.
         */
        private void scanAndLock(Object key, int hash) {
            // similar to but simpler than scanAndLockForPut
            HashEntry<K,V> first = entryForHash(this, hash);
            HashEntry<K,V> e = first;
            int retries = -1;
            while (!tryLock()) {
                HashEntry<K,V> f;
                if (retries < 0) {
                    if (e == null || key.equals(e.key)) {
                        retries = 0;
                    } else {
                        e = e.next;
                    }
                }
                else if (++retries > MAX_SCAN_RETRIES) {
                    lock();
                    break;
                }
                else if ((retries & 1) == 0 &&
                         (f = entryForHash(this, hash)) != first) {
                    e = first = f;
                    retries = -1;
                }
            }
        }

        /**
         * Remove; match on key only if value null, else match both.
         */
        final V remove(Object key, int hash, Object value) {
            if (!tryLock()) {
                scanAndLock(key, hash);
            }
            V oldValue = null;
            try {
                HashEntry<K,V>[] tab = table;
                int index = (tab.length - 1) & hash;
                HashEntry<K,V> e = entryAt(tab, index);
                HashEntry<K,V> pred = null;
                while (e != null) {
                    K k;
                    HashEntry<K,V> next = e.next;
                    if ((k = e.key) == key ||
                        (e.hash == hash && key.equals(k))) {
                        V v = e.value;
                        if (value == null || value == v || value.equals(v)) {
                            if (pred == null) {
                                setEntryAt(tab, index, next);
                            } else {
                                pred.setNext(next);
                            }
                            ++modCount;
                            --count;
                            oldValue = v;
                        }
                        break;
                    }
                    pred = e;
                    e = next;
                }
            } finally {
                unlock();
            }
            return oldValue;
        }

        final boolean replace(K key, int hash, V oldValue, V newValue) {
            if (!tryLock()) {
                scanAndLock(key, hash);
            }
            boolean replaced = false;
            try {
                HashEntry<K,V> e;
                for (e = entryForHash(this, hash); e != null; e = e.next) {
                    K k;
                    if ((k = e.key) == key ||
                        (e.hash == hash && key.equals(k))) {
                        if (oldValue.equals(e.value)) {
                            e.value = newValue;
                            ++modCount;
                            replaced = true;
                        }
                        break;
                    }
                }
            } finally {
                unlock();
            }
            return replaced;
        }

        final V replace(K key, int hash, V value) {
            if (!tryLock()) {
                scanAndLock(key, hash);
            }
            V oldValue = null;
            try {
                HashEntry<K,V> e;
                for (e = entryForHash(this, hash); e != null; e = e.next) {
                    K k;
                    if ((k = e.key) == key ||
                        (e.hash == hash && key.equals(k))) {
                        oldValue = e.value;
                        e.value = value;
                        ++modCount;
                        break;
                    }
                }
            } finally {
                unlock();
            }
            return oldValue;
        }

        final void clear() {
            lock();
            try {
                HashEntry<K,V>[] tab = table;
                for (int i = 0; i < tab.length ; i++) {
                    setEntryAt(tab, i, null);
                }
                ++modCount;
                count = 0;
            } finally {
                unlock();
            }
        }
    }

    // Accessing segments

    /**
     * Gets the jth element of given segment array (if nonnull) with
     * volatile element access semantics via Unsafe. (The null check
     * can trigger harmlessly only during deserialization.) Note:
     * because each element of segments array is set only once (using
     * fully ordered writes), some performance-sensitive methods rely
     * on this method only as a recheck upon null reads.
     */
    @SuppressWarnings("unchecked")
    static final <K,V> Segment<K,V> segmentAt(Segment<K,V>[] ss, int j) {
        long u = (j << SSHIFT) + SBASE;
        return ss == null ? null :
            (Segment<K,V>) UNSAFE.getObjectVolatile(ss, u);
    }

    /**
     * Returns the segment for the given index,
       creating it and recording in segment table (via CAS) if not already present.
     * @param k the index
     * @return the segment
     *
     * 检查了片段不存在，也就是segment数组上这个位置是空的，创建一个segment片段在这个位置上。
     * 可能存在多个线程同时初始化这个槽(位置)的值，所以考虑并发，只要一个成功就可以了。
     */
    @SuppressWarnings("unchecked")
    private Segment<K,V> ensureSegment(int k) {
        final Segment<K,V>[] ss = this.segments;
        long u = (k << SSHIFT) + SBASE; // raw offset
        Segment<K,V> seg;
        //检查是否拿到的seg片段为null，每次检查只要不为空，都需要去插入，只要有一个线程设置成功。
        if ((seg = (Segment<K,V>)UNSAFE.getObjectVolatile(ss, u)) == null) {
            //通过第一个片段获取初始值
            // 这里看到为什么之前要初始化 segment[0] 了，
            // 使用当前 segment[0] 处的数组长度和负载因子来初始化 segment[k]
            // 为什么要用“当前”，因为 segment[0] 可能早就扩容过了
            Segment<K,V> proto = ss[0]; // use segment 0 as prototype
            int cap = proto.table.length;
            float lf = proto.loadFactor;
            int threshold = (int)(cap * lf);
            // 初始化 segment[k] 内部的数组
            HashEntry<K,V>[] tab = (HashEntry<K,V>[])new HashEntry[cap];
            // 再次检查seg片段是否为null
            if ((seg = (Segment<K,V>)UNSAFE.getObjectVolatile(ss, u))== null) { // recheck
                Segment<K,V> s = new Segment<K,V>(lf, threshold, tab);
                // CAS机制去修改，每次修改前比较，比较修改是原子操作。当前线程成功设值或其他线程成功设值后，退出
                // 我没搞懂这里为什么要搞一个 while 循环，CAS 失败不就代表有其他线程成功了吗，为什么要再进行判断？
                // CAS失败之后，继续循环是为了再次去拿seg的值吧。要不然当前线程的seg还是本身自己的线程内的seg
                while ((seg = (Segment<K,V>)UNSAFE.getObjectVolatile(ss, u)) == null) {
                    if (UNSAFE.compareAndSwapObject(ss, u, null, seg = s)) {
                        break;
                    }
                }
            }
        }
        return seg;
    }

    // Hash-based segment and entry accesses

    /**
     * Get the segment for the given hash
     */
    @SuppressWarnings("unchecked")
    private Segment<K,V> segmentForHash(int h) {
        long u = (((h >>> segmentShift) & segmentMask) << SSHIFT) + SBASE;
        return (Segment<K,V>) UNSAFE.getObjectVolatile(segments, u);
    }

    /**
     * Gets the table entry for the given segment and hash
     */
    @SuppressWarnings("unchecked")
    static final <K,V> HashEntry<K,V> entryForHash(Segment<K,V> seg, int h) {
        HashEntry<K,V>[] tab;
        return (seg == null || (tab = seg.table) == null) ? null :
            (HashEntry<K,V>) UNSAFE.getObjectVolatile
            (tab, ((long)(((tab.length - 1) & h)) << TSHIFT) + TBASE);
    }

    /* ---------------- Public operations -------------- */

    /**
     * Creates a new, empty map with the specified initial
     * capacity, load factor and concurrency level.
     *
     * @param initialCapacity the initial capacity. The implementation
     * performs internal sizing to accommodate this many elements.
     * 初始容量大小
     * @param loadFactor  the load factor threshold, used to control resizing.
     * Resizing may be performed when the average number of elements per
     * bin exceeds this threshold.
     * 负载因子
     *
     * @param concurrencyLevel the estimated number of concurrently
     * updating threads. The implementation performs internal sizing
     * to try to accommodate this many threads.
     * 最多支持线程并发写的大小，并行级别、并发数、Segment 数
     * @throws IllegalArgumentException if the initial capacity is
     * negative or the load factor or concurrencyLevel are
     * nonpositive.
     */
    @SuppressWarnings("unchecked")
    public ConcurrentHashMap7(int initialCapacity,
                             float loadFactor, int concurrencyLevel) {
        // 判断初始值条件
        if (!(loadFactor > 0) || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        // 线程等级最大值
        if (concurrencyLevel > MAX_SEGMENTS) {
            concurrencyLevel = MAX_SEGMENTS;
        }
        // Find power-of-two sizes best matching arguments
        /**
         * 查找比concurrencyLevel小的最大2次幂数
         * 例如concurrencyLevel=16 ssize=16
         * sshift是左移次数，也就是sshift=4
         * ssize segment的数组片段大小
         */

        int sshift = 0;
        int ssize = 1;
        // 计算并行级别 ssize，因为要保持并行级别是 2 的 n 次方
        while (ssize < concurrencyLevel) {
            ++sshift;
            ssize <<= 1;
        }
        // 我们这里先不要那么烧脑，用默认值，concurrencyLevel 为 16，sshift 为 4
        // 那么计算出 segmentShift 为 28，segmentMask 为 15，后面会用到这两个值

        // 片段中索引的偏移量
        this.segmentShift = 32 - sshift;
        // 索引分段的掩码值 掩码的二进制各个位的值都是1
        this.segmentMask = ssize - 1;
        // 初始值最大值
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }

        // c = 16/8 = 2
        // c * ssize = 16  c 没有++
        // 如果 concurrencyLevel = 16 ssize = 16 ,c = 16/16 = 1 c也没变化，除非initialCapacity不是2次幂
        // 例如 concurrencyLevel = 16 initialCapacity = 15, c = 15/16=0 0*8<15 c++ = 1 1*8<15 c++ = 2 2*8 > 15 c就等于2

        // 这里根据 initialCapacity 计算 Segment 数组中每个位置可以分到的大小
        // 如 initialCapacity 为 64，那么每个 Segment 或称之为"槽"可以分到 4 个
        int c = initialCapacity / ssize;
        if (c * ssize < initialCapacity) {
            ++c;
        }
        // segment 表格的最小容量,其实也算是table的数组空间大小
        // 默认 MIN_SEGMENT_TABLE_CAPACITY 是 2，这个值也是有讲究的，因为这样的话，对于具体的槽上，
        // 插入一个元素不至于扩容，插入第二个的时候才会扩容
        int cap = MIN_SEGMENT_TABLE_CAPACITY;
        while (cap < c) {
            cap <<= 1;
        }
        // create segments and segments[0]
        Segment<K,V> s0 =
            new Segment<K,V>(loadFactor, (int)(cap * loadFactor),
                             (HashEntry<K,V>[])new HashEntry[cap]);
        // 初始化片段的 segment的数组空间大小
        Segment<K,V>[] ss = (Segment<K,V>[])new Segment[ssize];

        // 往数组写入 segment[0] (硬件级别的原子操作)
        UNSAFE.putOrderedObject(ss, SBASE, s0); // ordered write of segments[0]

        this.segments = ss;
    }

    /**
     * Creates a new, empty map with the specified initial capacity
     * and load factor and with the default concurrencyLevel (16).
     *
     * @param initialCapacity The implementation performs internal
     * sizing to accommodate this many elements.
     * @param loadFactor  the load factor threshold, used to control resizing.
     * Resizing may be performed when the average number of elements per
     * bin exceeds this threshold.
     * @throws IllegalArgumentException if the initial capacity of
     * elements is negative or the load factor is nonpositive
     *
     * @since 1.6
     */
    public ConcurrentHashMap7(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, DEFAULT_CONCURRENCY_LEVEL);
    }

    /**
     * Creates a new, empty map with the specified initial capacity,
     * and with default load factor (0.75) and concurrencyLevel (16).
     *
     * @param initialCapacity the initial capacity. The implementation
     * performs internal sizing to accommodate this many elements.
     * @throws IllegalArgumentException if the initial capacity of
     * elements is negative.
     */
    public ConcurrentHashMap7(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
    }

    /**
     * Creates a new, empty map with a default initial capacity (16),
     * load factor (0.75) and concurrencyLevel (16).
     */
    public ConcurrentHashMap7() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
    }

    /**
     * Creates a new map with the same mappings as the given map.
     * The map is created with a capacity of 1.5 times the number
     * of mappings in the given map or 16 (whichever is greater),
     * and a default load factor (0.75) and concurrencyLevel (16).
     *
     * @param m the map
     */
    public ConcurrentHashMap7(Map<? extends K, ? extends V> m) {
        this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,
                      DEFAULT_INITIAL_CAPACITY),
             DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
        putAll(m);
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        /*
         * Sum per-segment modCounts to avoid mis-reporting when
         * elements are concurrently added and removed in one segment
         * while checking another, in which case the table was never
         * actually empty at any point. (The sum ensures accuracy up
         * through at least 1<<31 per-segment modifications before
         * recheck.)  Methods size() and containsValue() use similar
         * constructions for stability checks.
         */
        long sum = 0L;
        final Segment<K,V>[] segments = this.segments;
        for (int j = 0; j < segments.length; ++j) {
            Segment<K,V> seg = segmentAt(segments, j);
            if (seg != null) {
                if (seg.count != 0) {
                    return false;
                }
                sum += seg.modCount;
            }
        }
        if (sum != 0L) { // recheck unless no modifications
            for (int j = 0; j < segments.length; ++j) {
                Segment<K,V> seg = segmentAt(segments, j);
                if (seg != null) {
                    if (seg.count != 0) {
                        return false;
                    }
                    sum -= seg.modCount;
                }
            }
            if (sum != 0L) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the number of key-value mappings in this map.  If the
     * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of key-value mappings in this map
     * 这个遍历会做两次，如果两次计算中结构性变化的次数（modCount）相等，那么就直接完成返回。
     * 如果第一次遍历统计与第二次遍历统计直接发生了结构化操作（比如、put被另一个线程执行了），
     * 那么再尝试一次，如果第二次与第三次统计的结果依旧不相等，则动用Lock锁，
     * 锁住所有的segment（其他线程的put、clear等操作被阻塞）。
     * 接下来，再次统计两次，这次由于锁保持了互斥，所以不会被其他线程干扰，modCount不会改变，sum == last，返回统计值，并且要释放所有Segement锁。
     */
    @Override
    public int size() {
        // Try a few times to get accurate count. On failure due to
        // continuous async changes in table, resort to locking.
        final Segment<K,V>[] segments = this.segments;
        int size;
        boolean overflow; // true if size overflows 32 bits
        long sum;         // sum of modCounts
        long last = 0L;   // previous sum
        int retries = -1; // first iteration isn't retry
        try {
            for (;;) {
                //RETRIES_BEFORE_LOCK为不变常量2 尝试两次不锁住Segment的方式来统计每个Segment的大小,
                // 如果在统计的过程中Segment的count发生变化,这时候再加锁统计Segment的count
                //先比较了在++
                if (retries++ == RETRIES_BEFORE_LOCK) {
                    for (int j = 0; j < segments.length; ++j) {
                        ensureSegment(j).lock(); // force creation
                    }
                }
                sum = 0L;
                size = 0;
                overflow = false;
                for (int j = 0; j < segments.length; ++j) {
                    Segment<K,V> seg = segmentAt(segments, j);
                    if (seg != null) {
                        sum += seg.modCount;
                        int c = seg.count;
                        if (c < 0 || (size += c) < 0) {
                            overflow = true;
                        }
                    }
                }
                if (sum == last) {
                    break;
                }
                last = sum;
            }
        } finally {
            if (retries > RETRIES_BEFORE_LOCK) {
                for (int j = 0; j < segments.length; ++j) {
                    segmentAt(segments, j).unlock();
                }
            }
        }
        return overflow ? Integer.MAX_VALUE : size;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code key.equals(k)},
     * then this method returns {@code v}; otherwise it returns
     * {@code null}.  (There can be at most one such mapping.)
     *
     * @throws NullPointerException if the specified key is null
     *
     * 计算 hash 值，找到 segment 数组中的具体位置，或我们前面用的“槽”
        槽中也是一个数组，根据 hash 找到数组中具体的位置
        到这里是链表了，顺着链表进行查找即可
     */
    @Override
    public V get(Object key) {
        Segment<K,V> s; // manually integrate access methods to reduce overhead
        HashEntry<K,V>[] tab;
        // 1. hash 值
        int h = hash(key);
        // 2. 根据 hash 找到对应的 segment
        long u = (((h >>> segmentShift) & segmentMask) << SSHIFT) + SBASE;
        if ((s = (Segment<K,V>)UNSAFE.getObjectVolatile(segments, u)) != null && (tab = s.table) != null) {
            // 3. 找到segment 内部数组相应位置的链表，遍历
            for (HashEntry<K,V> e = (HashEntry<K,V>) UNSAFE.getObjectVolatile(tab, ((long)(((tab.length - 1) & h)) << TSHIFT) + TBASE);e != null; e = e.next) {
                K k;
                if ((k = e.key) == key || (e.hash == h && key.equals(k))) {
                    return e.value;
                }
            }
        }
        return null;
    }

    /**
     * Tests if the specified object is a key in this table.
     *
     * @param  key   possible key
     * @return <tt>true</tt> if and only if the specified object
     *         is a key in this table, as determined by the
     *         <tt>equals</tt> method; <tt>false</tt> otherwise.
     * @throws NullPointerException if the specified key is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        Segment<K,V> s; // same as get() except no need for volatile value read
        HashEntry<K,V>[] tab;
        int h = hash(key);
        long u = (((h >>> segmentShift) & segmentMask) << SSHIFT) + SBASE;
        if ((s = (Segment<K,V>)UNSAFE.getObjectVolatile(segments, u)) != null &&
            (tab = s.table) != null) {
            for (HashEntry<K,V> e = (HashEntry<K,V>) UNSAFE.getObjectVolatile
                     (tab, ((long)(((tab.length - 1) & h)) << TSHIFT) + TBASE);
                 e != null; e = e.next) {
                K k;
                if ((k = e.key) == key || (e.hash == h && key.equals(k))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value. Note: This method requires a full internal
     * traversal of the hash table, and so is much slower than
     * method <tt>containsKey</tt>.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value
     * @throws NullPointerException if the specified value is null
     */
    @Override
    public boolean containsValue(Object value) {
        // Same idea as size()
        if (value == null) {
            throw new NullPointerException();
        }
        final Segment<K,V>[] segments = this.segments;
        boolean found = false;
        long last = 0;
        int retries = -1;
        try {
            outer: for (;;) {
                if (retries++ == RETRIES_BEFORE_LOCK) {
                    for (int j = 0; j < segments.length; ++j) {
                        ensureSegment(j).lock(); // force creation
                    }
                }
                long hashSum = 0L;
                int sum = 0;
                for (int j = 0; j < segments.length; ++j) {
                    HashEntry<K,V>[] tab;
                    Segment<K,V> seg = segmentAt(segments, j);
                    if (seg != null && (tab = seg.table) != null) {
                        for (int i = 0 ; i < tab.length; i++) {
                            HashEntry<K,V> e;
                            for (e = entryAt(tab, i); e != null; e = e.next) {
                                V v = e.value;
                                if (v != null && value.equals(v)) {
                                    found = true;
                                    break outer;
                                }
                            }
                        }
                        sum += seg.modCount;
                    }
                }
                if (retries > 0 && sum == last) {
                    break;
                }
                last = sum;
            }
        } finally {
            if (retries > RETRIES_BEFORE_LOCK) {
                for (int j = 0; j < segments.length; ++j) {
                    segmentAt(segments, j).unlock();
                }
            }
        }
        return found;
    }

    /**
     * Legacy method testing if some key maps into the specified value
     * in this table.  This method is identical in functionality to
     * {@link #containsValue}, and exists solely to ensure
     * full compatibility with class {@link Hashtable},
     * which supported this method prior to introduction of the
     * Java Collections framework.

     * @param  value a value to search for
     * @return <tt>true</tt> if and only if some key maps to the
     *         <tt>value</tt> argument in this table as
     *         determined by the <tt>equals</tt> method;
     *         <tt>false</tt> otherwise
     * @throws NullPointerException if the specified value is null
     */
    public boolean contains(Object value) {
        return containsValue(value);
    }

    /**
     * Maps the specified key to the specified value in this table.
     * Neither the key nor the value can be null.
     *
     * <p> The value can be retrieved by calling the <tt>get</tt> method
     * with a key that is equal to the original key.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>
     * @throws NullPointerException if the specified key or value is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public V put(K key, V value) {
        Segment<K,V> s;
        // 不能设置null值 key和value都不能为空
        if (value == null) {
            throw new NullPointerException();
        }
        // 计算key的hash值，这个计算在hashmap有分析过，涉及到hashcode等.
        int hash = hash(key);
        /**
         * hash值无符号右移28位(在构造函数中初始化得到偏移量)
         * hash变量
         *
         * segmentMask 掩码值 15 这个值是通过segment大小-1得到。这个和hashMap里面的length-1类似
           就是为了让j均匀分布到segmentMask，并且不超过length大小。有点类似取模运算，可以通过hashmap的分析判断原因即可。

         * 按与操作，也就是获得hash的原本的高4位，右移之后的低4位
         * 这个值作为槽的数组下标
         */
        int j = (hash >>> segmentShift) & segmentMask;

        // 刚刚说了，初始化的时候初始化了 segment[0]，但是其他位置还是 null，
        // ensureSegment(j) 对 segment[j] 进行初始化
        // 利用得到的j作为去判断当前槽数组下标。得到这个segment片段是否为null
        if ((s = (Segment<K,V>)UNSAFE.getObject          // nonvolatile; recheck
             (segments, (j << SSHIFT) + SBASE)) == null) //  in ensureSegment
        {
            s = ensureSegment(j);
        }
        return s.put(key, hash, value, false);
    }

    /**
     * {@inheritDoc}
     *
     * @return the previous value associated with the specified key,
     *         or <tt>null</tt> if there was no mapping for the key
     * @throws NullPointerException if the specified key or value is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public V putIfAbsent(K key, V value) {
        Segment<K,V> s;
        if (value == null) {
            throw new NullPointerException();
        }
        int hash = hash(key);
        int j = (hash >>> segmentShift) & segmentMask;
        if ((s = (Segment<K,V>)UNSAFE.getObject
             (segments, (j << SSHIFT) + SBASE)) == null) {
            s = ensureSegment(j);
        }
        return s.put(key, hash, value, true);
    }

    /**
     * Copies all of the mappings from the specified map to this one.
     * These mappings replace any mappings that this map had for any of the
     * keys currently in the specified map.
     *
     * @param m mappings to be stored in this map
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Removes the key (and its corresponding value) from this map.
     * This method does nothing if the key is not in the map.
     *
     * @param  key the key that needs to be removed
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public V remove(Object key) {
        int hash = hash(key);
        Segment<K,V> s = segmentForHash(hash);
        return s == null ? null : s.remove(key, hash, null);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public boolean remove(Object key, Object value) {
        int hash = hash(key);
        Segment<K,V> s;
        return value != null && (s = segmentForHash(hash)) != null &&
            s.remove(key, hash, value) != null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if any of the arguments are null
     */
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        int hash = hash(key);
        if (oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        Segment<K,V> s = segmentForHash(hash);
        return s != null && s.replace(key, hash, oldValue, newValue);
    }

    /**
     * {@inheritDoc}
     *
     * @return the previous value associated with the specified key,
     *         or <tt>null</tt> if there was no mapping for the key
     * @throws NullPointerException if the specified key or value is null
     */
    @Override
    public V replace(K key, V value) {
        int hash = hash(key);
        if (value == null) {
            throw new NullPointerException();
        }
        Segment<K,V> s = segmentForHash(hash);
        return s == null ? null : s.replace(key, hash, value);
    }

    /**
     * Removes all of the mappings from this map.
     */
    @Override
    public void clear() {
        final Segment<K,V>[] segments = this.segments;
        for (int j = 0; j < segments.length; ++j) {
            Segment<K,V> s = segmentAt(segments, j);
            if (s != null) {
                s.clear();
            }
        }
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  The set supports element
     * removal, which removes the corresponding mapping from this map,
     * via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.
     *
     * <p>The view's <tt>iterator</tt> is a "weakly consistent" iterator
     * that will never throw {@link ConcurrentModificationException},
     * and guarantees to traverse elements as they existed upon
     * construction of the iterator, and may (but is not guaranteed to)
     * reflect any modifications subsequent to construction.
     */
    @Override
    public Set<K> keySet() {
        Set<K> ks = keySet;
        return (ks != null) ? ks : (keySet = new KeySet());
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  The collection
     * supports element removal, which removes the corresponding
     * mapping from this map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt>, and <tt>clear</tt> operations.  It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * <p>The view's <tt>iterator</tt> is a "weakly consistent" iterator
     * that will never throw {@link ConcurrentModificationException},
     * and guarantees to traverse elements as they existed upon
     * construction of the iterator, and may (but is not guaranteed to)
     * reflect any modifications subsequent to construction.
     */
    @Override
    public Collection<V> values() {
        Collection<V> vs = values;
        return (vs != null) ? vs : (values = new Values());
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  The set supports element
     * removal, which removes the corresponding mapping from the map,
     * via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.
     *
     * <p>The view's <tt>iterator</tt> is a "weakly consistent" iterator
     * that will never throw {@link ConcurrentModificationException},
     * and guarantees to traverse elements as they existed upon
     * construction of the iterator, and may (but is not guaranteed to)
     * reflect any modifications subsequent to construction.
     */
    @Override
    public Set<Entry<K,V>> entrySet() {
        Set<Entry<K,V>> es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
    }

    /**
     * Returns an enumeration of the keys in this table.
     *
     * @return an enumeration of the keys in this table
     * @see #keySet()
     */
    public Enumeration<K> keys() {
        return new KeyIterator();
    }

    /**
     * Returns an enumeration of the values in this table.
     *
     * @return an enumeration of the values in this table
     * @see #values()
     */
    public Enumeration<V> elements() {
        return new ValueIterator();
    }

    /* ---------------- Iterator Support -------------- */

    abstract class HashIterator {
        int nextSegmentIndex;
        int nextTableIndex;
        HashEntry<K,V>[] currentTable;
        HashEntry<K, V> nextEntry;
        HashEntry<K, V> lastReturned;

        HashIterator() {
            nextSegmentIndex = segments.length - 1;
            nextTableIndex = -1;
            advance();
        }

        /**
         * Set nextEntry to first node of next non-empty table
         * (in backwards order, to simplify checks).
         */
        final void advance() {
            for (;;) {
                if (nextTableIndex >= 0) {
                    if ((nextEntry = entryAt(currentTable,
                                             nextTableIndex--)) != null) {
                        break;
                    }
                }
                else if (nextSegmentIndex >= 0) {
                    Segment<K,V> seg = segmentAt(segments, nextSegmentIndex--);
                    if (seg != null && (currentTable = seg.table) != null) {
                        nextTableIndex = currentTable.length - 1;
                    }
                }
                else {
                    break;
                }
            }
        }

        final HashEntry<K,V> nextEntry() {
            HashEntry<K,V> e = nextEntry;
            if (e == null) {
                throw new NoSuchElementException();
            }
            lastReturned = e; // cannot assign until after null check
            if ((nextEntry = e.next) == null) {
                advance();
            }
            return e;
        }

        public final boolean hasNext() { return nextEntry != null; }
        public final boolean hasMoreElements() { return nextEntry != null; }

        public final void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            ConcurrentHashMap7.this.remove(lastReturned.key);
            lastReturned = null;
        }
    }

    final class KeyIterator
        extends HashIterator
        implements Iterator<K>, Enumeration<K>
    {
        @Override
        public final K next()        { return super.nextEntry().key; }
        @Override
        public final K nextElement() { return super.nextEntry().key; }
    }

    final class ValueIterator
        extends HashIterator
        implements Iterator<V>, Enumeration<V>
    {
        @Override
        public final V next()        { return super.nextEntry().value; }
        @Override
        public final V nextElement() { return super.nextEntry().value; }
    }

    /**
     * Custom Entry class used by EntryIterator.next(), that relays
     * setValue changes to the underlying map.
     */
    final class WriteThroughEntry
        extends SimpleEntry<K,V>
    {
        WriteThroughEntry(K k, V v) {
            super(k,v);
        }

        /**
         * Set our entry's value and write through to the map. The
         * value to return is somewhat arbitrary here. Since a
         * WriteThroughEntry does not necessarily track asynchronous
         * changes, the most recent "previous" value could be
         * different from what we return (or could even have been
         * removed in which case the put will re-establish). We do not
         * and cannot guarantee more.
         */
        @Override
        public V setValue(V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            V v = super.setValue(value);
            ConcurrentHashMap7.this.put(getKey(), value);
            return v;
        }
    }

    final class EntryIterator
        extends HashIterator
        implements Iterator<Entry<K,V>>
    {
        @Override
        public Entry<K,V> next() {
            HashEntry<K,V> e = super.nextEntry();
            return new WriteThroughEntry(e.key, e.value);
        }
    }

    final class KeySet extends AbstractSet<K> {
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        @Override
        public int size() {
            return ConcurrentHashMap7.this.size();
        }
        @Override
        public boolean isEmpty() {
            return ConcurrentHashMap7.this.isEmpty();
        }
        @Override
        public boolean contains(Object o) {
            return ConcurrentHashMap7.this.containsKey(o);
        }
        @Override
        public boolean remove(Object o) {
            return ConcurrentHashMap7.this.remove(o) != null;
        }
        @Override
        public void clear() {
            ConcurrentHashMap7.this.clear();
        }
    }

    final class Values extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        @Override
        public int size() {
            return ConcurrentHashMap7.this.size();
        }
        @Override
        public boolean isEmpty() {
            return ConcurrentHashMap7.this.isEmpty();
        }
        @Override
        public boolean contains(Object o) {
            return ConcurrentHashMap7.this.containsValue(o);
        }
        @Override
        public void clear() {
            ConcurrentHashMap7.this.clear();
        }
    }

    final class EntrySet extends AbstractSet<Entry<K,V>> {
        @Override
        public Iterator<Entry<K,V>> iterator() {
            return new EntryIterator();
        }
        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Entry<?,?> e = (Entry<?,?>)o;
            V v = ConcurrentHashMap7.this.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }
        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Entry<?,?> e = (Entry<?,?>)o;
            return ConcurrentHashMap7.this.remove(e.getKey(), e.getValue());
        }
        @Override
        public int size() {
            return ConcurrentHashMap7.this.size();
        }
        @Override
        public boolean isEmpty() {
            return ConcurrentHashMap7.this.isEmpty();
        }
        @Override
        public void clear() {
            ConcurrentHashMap7.this.clear();
        }
    }

    /* ---------------- Serialization Support -------------- */

    /**
     * Save the state of the <tt>ConcurrentHashMap7</tt> instance to a
     * stream (i.e., serialize it).
     * @param s the stream
     * @serialData
     * the key (Object) and value (Object)
     * for each key-value mapping, followed by a null pair.
     * The key-value mappings are emitted in no particular order.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        // force all segments for serialization compatibility
        for (int k = 0; k < segments.length; ++k) {
            ensureSegment(k);
        }
        s.defaultWriteObject();

        final Segment<K,V>[] segments = this.segments;
        for (int k = 0; k < segments.length; ++k) {
            Segment<K,V> seg = segmentAt(segments, k);
            seg.lock();
            try {
                HashEntry<K,V>[] tab = seg.table;
                for (int i = 0; i < tab.length; ++i) {
                    HashEntry<K,V> e;
                    for (e = entryAt(tab, i); e != null; e = e.next) {
                        s.writeObject(e.key);
                        s.writeObject(e.value);
                    }
                }
            } finally {
                seg.unlock();
            }
        }
        s.writeObject(null);
        s.writeObject(null);
    }

    /**
     * Reconstitute the <tt>ConcurrentHashMap7</tt> instance from a
     * stream (i.e., deserialize it).
     * @param s the stream
     */
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException {
        // Don't call defaultReadObject()
        ObjectInputStream.GetField oisFields = s.readFields();
        final Segment<K,V>[] oisSegments = (Segment<K,V>[])oisFields.get("segments", null);

        final int ssize = oisSegments.length;
        if (ssize < 1 || ssize > MAX_SEGMENTS
            || (ssize & (ssize-1)) != 0 )  // ssize not power of two
        {
            throw new java.io.InvalidObjectException("Bad number of segments:"
                    + ssize);
        }
        int sshift = 0, ssizeTmp = ssize;
        while (ssizeTmp > 1) {
            ++sshift;
            ssizeTmp >>>= 1;
        }
        UNSAFE.putIntVolatile(this, SEGSHIFT_OFFSET, 32 - sshift);
        UNSAFE.putIntVolatile(this, SEGMASK_OFFSET, ssize - 1);
        UNSAFE.putObjectVolatile(this, SEGMENTS_OFFSET, oisSegments);

        // set hashMask
        UNSAFE.putIntVolatile(this, HASHSEED_OFFSET, randomHashSeed(this));

        // Re-initialize segments to be minimally sized, and let grow.
        int cap = MIN_SEGMENT_TABLE_CAPACITY;
        final Segment<K,V>[] segments = this.segments;
        for (int k = 0; k < segments.length; ++k) {
            Segment<K,V> seg = segments[k];
            if (seg != null) {
                seg.threshold = (int)(cap * seg.loadFactor);
                seg.table = (HashEntry<K,V>[]) new HashEntry[cap];
            }
        }

        // Read the keys and values, and put the mappings in the table
        for (;;) {
            K key = (K) s.readObject();
            V value = (V) s.readObject();
            if (key == null) {
                break;
            }
            put(key, value);
        }
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe UNSAFE;
    private static final long SBASE;
    private static final int SSHIFT;
    private static final long TBASE;
    private static final int TSHIFT;
    private static final long HASHSEED_OFFSET;
    private static final long SEGSHIFT_OFFSET;
    private static final long SEGMASK_OFFSET;
    private static final long SEGMENTS_OFFSET;

    static {
        int ss, ts;
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class tc = HashEntry[].class;
            Class sc = Segment[].class;
            TBASE = UNSAFE.arrayBaseOffset(tc);
            SBASE = UNSAFE.arrayBaseOffset(sc);
            ts = UNSAFE.arrayIndexScale(tc);
            ss = UNSAFE.arrayIndexScale(sc);
            HASHSEED_OFFSET = UNSAFE.objectFieldOffset(
                ConcurrentHashMap7.class.getDeclaredField("hashSeed"));
            SEGSHIFT_OFFSET = UNSAFE.objectFieldOffset(
                ConcurrentHashMap7.class.getDeclaredField("segmentShift"));
            SEGMASK_OFFSET = UNSAFE.objectFieldOffset(
                ConcurrentHashMap7.class.getDeclaredField("segmentMask"));
            SEGMENTS_OFFSET = UNSAFE.objectFieldOffset(
                ConcurrentHashMap7.class.getDeclaredField("segments"));
        } catch (Exception e) {
            throw new Error(e);
        }
        if ((ss & (ss-1)) != 0 || (ts & (ts-1)) != 0) {
            throw new Error("data type scale not a power of two");
        }
        SSHIFT = 31 - Integer.numberOfLeadingZeros(ss);
        TSHIFT = 31 - Integer.numberOfLeadingZeros(ts);
    }

}
