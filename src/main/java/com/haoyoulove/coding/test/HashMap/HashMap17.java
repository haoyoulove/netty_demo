//package com.haoyoulove.coding.test.HashMap;
//
//import java.io.IOException;
//import java.io.InvalidObjectException;
//import java.io.Serializable;
//import java.util.AbstractCollection;
//import java.util.AbstractMap;
//import java.util.AbstractSet;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.ConcurrentModificationException;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.NoSuchElementException;
//import java.util.Objects;
//import java.util.Set;
//
//
//public class HashMap17<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable {
//
//	/*
//	    HashMap17 的实例有两个参数影响其性能：初始容量 和加载因子。
//	    容量是哈希表中桶的数量，初始容量只是哈希表在创建时的容量。
//	    加载因子是哈希表在其容量自动增加之前可以达到多满的一种尺度。
//	    当哈希表中的条目数超出了加载因子与当前容量的乘积时，
//	    则要对该哈希表进行 rehash 操作（即重建内部数据结构），
//	    从而哈希表将具有大约两倍的桶数。
//	    加载因子默认值为0.75，默认哈希表容量为16
//	*/
//
//	// The default initial capacity - MUST be a power of two.
//	// 默认HashMap的容量，初始化容量16 HashMap的容量必须是2的指数倍        【Hashtable是11】
//	static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
//
//	// 最大容量2的30次方 。
//	// HashMap17 Bucket数组的长度,int 是4个字节存储，
//	// 去掉其符号位数为31位，再考虑到这里其实是定义HashMap Bucket数组的长度，考虑到Java堆存储空间的限制，定位30位，其大小为107****24。
//	static final int MAXIMUM_CAPACITY = 1 << 30;
//
//	// The load factor used when none specified in constructor.
//	// 默认加载因子默认的平衡因子为0.75，权衡了时间复杂度与空间复杂度之后的最好取值（JDK说是最好的），
//	// 过高的因子会降低存储空间但是查找（lookup，包括HashMap中的put与get方法）的时间就会增加。
//	static final float DEFAULT_LOAD_FACTOR = 0.75f;
//
//	// An empty table instance to share when the table is not inflated.
//	// 空的Entry的二维数组,用来存储键值对的Entry数组,用于设置刚刚初始化的HashMap对象,用来减少存储空间
//	static final Entry<?,?>[] EMPTY_TABLE = {};
//
//
//	// transient 表明该数据不参与序列化？为什么用transient
//	// 1.transient 首先是表明该数据不参与序列化。假设HashMap 中的存储数据的数组还有很多的空间没有被使用，
//	// 没有被使用到的空间被序列化没有意义。所以下文会有手动使用 writeObject() 方法，只序列化实际存储元素的数组。
//	// 2. 不同的虚拟机对于相同 hashCode 产生的 Code 值可能是不一样的，如果使用默认序列化，则反序列化后，元素的位置和之前的是保持一致的，
//	// 可是由于 hashCode 的值不一样了，那么后续看到的定位函数 indexOf（）返回的元素下标就会不同，其结果会出差错。
//
//	// The table, resized as necessary. Length MUST Always be a power of two.
//	// **HashMap底层Bucket数组
//	transient Entry<K,V>[] table = (Entry<K,V>[]) EMPTY_TABLE;
//
//	/**
//	 * The number of key-value mappings contained in this map.
//	 */
//	// 当前HashMap键值对K/V数据的大小
//	transient int size;
//
//
//
//	// The next size value at which to resize (capacity * load factor).
//	// If table == EMPTY_TABLE then this is the initial capacity at which the
//	// table will be created when inflated.
//	// 阀值（threshold = capacity  * loadFactor ），当size超过threshold时，table将会扩容.
//	int threshold;
//
//	// The load factor for the hash table.
//	// 负载因子
//	final float loadFactor;
//
//	// Fail-Fast机制：java.util.HashMap非线程安全，如果在使用迭代器的过程中有其他线程修改了map，会抛出ConcurrentModificationException
//	// 记录HashMap修改次数，如增、删元素或rehash。这个字段被用来当迭代器的fail-fast检查线程是否同步
//	transient int modCount;
//
//	//默认的阀值
//	static final int ALTERNATIVE_HASHING_THRESHOLD_DEFAULT = Integer.MAX_VALUE;
//
//	// Holder是为了加载获取threshold的配置参数。
//	private static class Holder {
//		/**
//		 * Table capacity above which to switch to use alternative hashing.
//		 */
//		static final int ALTERNATIVE_HASHING_THRESHOLD;
//		static {
//			// JDK 1.7新加，针对字符串的key的hash算法会提供更好的hashcode分布减少冲突；
//			// 如果想启用此特性，需设置jdk.map.althashing.threshold系统属性的值为一个非负数（默认是-1）这个值代表了一个集合大小的threshold，
//			// 超过这个值，就会使用新的hash算法。需要注意的一点，只有当re-hash的时候，新的hash算法才会起作用。
//			String altThreshold = java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("jdk.map.althashing.threshold"));
//			int threshold;
//			try {
//				threshold = (null != altThreshold) ? Integer.parseInt(altThreshold) : ALTERNATIVE_HASHING_THRESHOLD_DEFAULT;
//				// disable alternative hashing if -1
//				if (threshold == -1) {
//					threshold = Integer.MAX_VALUE;
//				}
//				if (threshold < 0) {
//					throw new IllegalArgumentException("value must be positive integer.");
//				}
//			} catch(IllegalArgumentException failed) {
//				throw new Error("Illegal value for 'jdk.map.althashing.threshold'", failed);
//			}
//			ALTERNATIVE_HASHING_THRESHOLD = threshold;
//		}
//	}
//
//	// If 0 then alternative hashing is disabled.
//	// 用于hash的种子
//	transient int hashSeed = 0;
//
//	//构造函数：使用初始化容量和加载因子初始化HashMap
//	public HashMap17(int initialCapacity, float loadFactor) {
//		if (initialCapacity < 0) {
//			throw new IllegalArgumentException("Illegal initial capacity: " +
//					initialCapacity);
//		}
//		if (initialCapacity > MAXIMUM_CAPACITY) {
//			initialCapacity = MAXIMUM_CAPACITY;
//		}
//		if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
//			throw new IllegalArgumentException("Illegal load factor: " +
//					loadFactor);
//		}
//
//		this.loadFactor = loadFactor;
//		threshold = initialCapacity;
//
//		// init() hook because : HashMap是可序列化的，而反序列化方法（readObject()）是一个跟构造器性质相似、但却不是构造器的奇怪的东西.
//		// 为了让子类能方便规整地实现构造初始化与反序列初始化的功能，HashMap就在构造器末尾和反序列化方法末尾都埋了这个init()钩子，
//		// 这样子类就不用为这两种不同的初始化需求而重复头疼了。
//		// jdk8 改名为reinitialize(),
//		// LinkedHashMap要维持插入顺序，为此它会把所有插入的节点（键值对）用双向链表串在一起。而在它的init()实现里，它就创建并初始化了该双向链表的头节点。
//		init();
//	}
//
//	// Constructs ,指定负载因子
//	public HashMap17(int initialCapacity) {
//		this(initialCapacity, DEFAULT_LOAD_FACTOR);
//	}
//
//	/**
//	 * Constructs 使用默认容量和加载因子初始化HashMap
//	 */
//	public HashMap17() {
//		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
//	}
//
//	/**
//	 * Constructs a new <tt>HashMap17</tt> with the same mappings as the
//	 * specified <tt>Map</tt>.  The <tt>HashMap17</tt> is created with
//	 * default load factor (0.75) and an initial capacity sufficient to
//	 * hold the mappings in the specified <tt>Map</tt>.
//	 *
//	 * @param   m the map whose mappings are to be placed in this map
//	 * @throws  NullPointerException if the specified map is null
//	 */
//	// 从一个已有的Map创建一个新的HashMap
//	public HashMap17(Map<? extends K, ? extends V> m) {
//		this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
//		inflateTable(threshold);
//		putAllForCreate(m);
//	}
//
//	// 将number扩展成2的倍数 ,返回比入参初始容量大的最小的2的幂数
//	private static int roundUpToPowerOf2(int number) {
//		// assert number >= 0 : "number must be non-negative";
//		int rounded = number >= MAXIMUM_CAPACITY ? MAXIMUM_CAPACITY : (rounded = Integer.highestOneBit(number)) != 0 ? (Integer.bitCount(number) > 1) ? rounded << 1 : rounded : 1;
//		return rounded;
//	}
//
//	/**
//	 * 【JDK 1.7新加】Inflates the table. 扩充 HASHMAP 容量
//	 */
//	private void inflateTable(int toSize) {
//		// Find a power of 2 >= toSize
//		int capacity = roundUpToPowerOf2(toSize);
//		//重新设置阀值
//		threshold = (int) Math.min(capacity * loadFactor, MAXIMUM_CAPACITY + 1);
//		table = new Entry[capacity];
//		initHashSeedAsNeeded(capacity);
//	}
//
//	/**
//	 * Initialization hook for subclasses. This method is called
//	 * in all constructors and pseudo-constructors (clone, readObject)
//	 * after HashMap17 has been initialized but before any entries have
//	 * been inserted.  (In the absence of this method, readObject would
//	 * require explicit knowledge of subclasses.)
//	 */
//	// 内部使用,可作为一个钩子来被子类使用，它已经作为模版模式被所有的构造器，clone等调用。
//	void init() {
//	}
//
//	// Initialize the hashing mask value. We defer initialization until we really need it.
//	final boolean initHashSeedAsNeeded(int capacity) {
//		boolean currentAltHashing = hashSeed != 0;
//		//根据系统函数得到一个hash
//		boolean useAltHashing = sun.misc.VM.isBooted() &&
//				(capacity >= Holder.ALTERNATIVE_HASHING_THRESHOLD);
//		boolean switching = currentAltHashing ^ useAltHashing;
//		//如果hashSeed初始化为0则跳过switching
//		//否则使用系统函数得到新的hashSeed
//		if (switching) {
//			hashSeed = useAltHashing
//					? sun.misc.Hashing.randomHashSeed(this)
//					: 0;
//		}
//		return switching;
//	}
//
//	/**
//	 * Retrieve object hash code and applies a supplemental hash function to the
//	 * result hash, which defends against poor quality hash functions.  This is
//	 * critical because HashMap17 uses power-of-two length hash tables, that
//	 * otherwise encounter collisions for hashCodes that do not differ
//	 * in lower bits. Note: Null keys always map to hash 0, thus index 0.
//	 */
//	// 哈希算法的核心：哈希函数 ,用来计算对象的hash值
//	final int hash(Object k) {
//		int h = hashSeed;
//		//通过hashSeed初始化的值的不同来选择不同的hash方式
//		if (0 != h && k instanceof String) {
//			//String类采用不同的hash函数
//			return sun.misc.Hashing.stringHash32((String) k);
//		}
//
//		h ^= k.hashCode();
//
//		// This function ensures that hashCodes that differ only by
//		// constant multiples at each bit position have a bounded
//		// number of collisions (approximately 8 at default load factor).
//		h ^= (h >>> 20) ^ (h >>> 12);
//		return h ^ (h >>> 7) ^ (h >>> 4);
//	}
//
//	// Returns index for hash code h.
//	// 通过得到的hash值来确定返回hash code对应的length中的下标
//	static int indexFor(int h, int length) {
//		// assert Integer.bitCount(length) == 1 : "length must be a non-zero power of 2";
//		return h & (length-1);
//	}
//
//	// 当前HashMap键值对K/V数据的大小
//	@Override
//	public int size() {
//		return size;
//	}
//
//	// Returns <tt>true</tt> if this map contains no key-value mappings.
//	@Override
//	public boolean isEmpty() {
//		return size == 0;
//	}
//
//	@Override
//	public V get(Object key) {
//		if (key == null) {
//			return getForNullKey();
//		}
//		Entry<K,V> entry = getEntry(key);
//
//		return null == entry ? null : entry.getValue();
//	}
//
//	private V getForNullKey() {
//		if (size == 0) {
//			return null;
//		}
//		for (Entry<K,V> e = table[0]; e != null; e = e.next) {
//			if (e.key == null) {
//				return e.value;
//			}
//		}
//		return null;
//	}
//
//	@Override
//	public boolean containsKey(Object key) {
//		return getEntry(key) != null;
//	}
//
//	//
//	final Entry<K,V> getEntry(Object key) {
//		if (size == 0) {
//			return null;
//		}
//		//通过key的hash值确定table下标（null对应下标0）
//		int hash = (key == null) ? 0 : hash(key);
//		// 遍历table中的 entry数组
//		for (Entry<K,V> e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
//			Object k;
//			// 因为存在不同的key对应相同的hash值，还要判读key是否一样
//			if ( e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))) ) {
//				return e;
//			}
//		}
//		return null;
//	}
//
//	//
//	// 通过key的hash值确定table下标，如果key已经存在则更新，不存在则调用addEntry方法
//	@Override
//	public V put(K key, V value) {
//		if (table == EMPTY_TABLE) {
//			inflateTable(threshold);
//		}
//		if (key == null) {
//			return putForNullKey(value);
//		}
//		int hash = hash(key);
//		int i = indexFor(hash, table.length);
//		for (Entry<K,V> e = table[i]; e != null; e = e.next) {
//			Object k;
//			// 因为存在不同的key对应相同的hash值，还要判读key是否一样
//			if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
//				V oldValue = e.value;
//				e.value = value;
//				e.recordAccess(this);
//				return oldValue; // 如果已经存在，更新值
//			}
//		}
//
//		// //上面的循环结束表示当前的key不存在与表中，需要另外增加
//		modCount++;
//		addEntry(hash, key, value, i);
//		return null;
//	}
//
//	/**
//	 * Offloaded version of put for null keys
//	 */
//	private V putForNullKey(V value) {
//		for (Entry<K,V> e = table[0]; e != null; e = e.next) {
//			if (e.key == null) {
//				V oldValue = e.value;
//				e.value = value;
//				e.recordAccess(this);
//				return oldValue;
//			}
//		}
//		modCount++;
//		addEntry(0, null, value, 0);
//		return null;
//	}
//
//	/**
//	 * This method is used instead of put by constructors and
//	 * pseudoconstructors (clone, readObject).  It does not resize the table,
//	 * check for comodification, etc.  It calls createEntry rather than
//	 * addEntry.
//	 */
//	private void putForCreate(K key, V value) {
//		int hash = null == key ? 0 : hash(key);
//		int i = indexFor(hash, table.length);
//
//		/**
//		 * Look for preexisting entry for key.  This will never happen for
//		 * clone or deserialize.  It will only happen for construction if the
//		 * input Map is a sorted map whose ordering is inconsistent w/ equals.
//		 */
//		for (Entry<K,V> e = table[i]; e != null; e = e.next) {
//			Object k;
//			if (e.hash == hash &&
//					((k = e.key) == key || (key != null && key.equals(k)))) {
//				e.value = value;
//				return;
//			}
//		}
//
//		createEntry(hash, key, value, i);
//	}
//
//	private void putAllForCreate(Map<? extends K, ? extends V> m) {
//		for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
//			putForCreate(e.getKey(), e.getValue());
//		}
//	}
//
//	void resize(int newCapacity) {  //传入新的容量
//		Entry[] oldTable = table;   //引用扩容前的Entry数组
//		int oldCapacity = oldTable.length;
//		if (oldCapacity == MAXIMUM_CAPACITY) {    //扩容前的数组大小如果已经达到最大(2^30)了
//			threshold = Integer.MAX_VALUE;  //修改阈值为int的最大值(2^31-1)，这样以后就不会扩容了
//			return;
//		}
//
//		Entry[] newTable = new Entry[newCapacity]; //初始化一个新的Entry数组
//		transfer(newTable, initHashSeedAsNeeded(newCapacity)); //！！将数据转移到新的Entry数组里
//		table = newTable; //HashMap的table属性引用新的Entry数组
//		threshold = (int)Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1); //修改阈值
//	}
//
//	/**
//	 * Transfers all entries from current table to newTable.
//	 */
//	// 复制目前HashMap 使用了单链表的头插入方式。引用重新指向
//	void transfer(Entry[] newTable, boolean rehash) {
//		int newCapacity = newTable.length;
//		for (Entry<K,V> e : table) { //遍历旧的Entry数组
//			while(null != e) {
//				Entry<K,V> next = e.next; // next变量获取e.next的引用值
//				//是否重新进行hash计算
//				if (rehash) {
//					e.hash = null == e.key ? 0 : hash(e.key);
//				}
//				int i = indexFor(e.hash, newCapacity); //！！重新计算每个元素在数组中的位置
//				e.next = newTable[i]; //标记[1]
//				newTable[i] = e; //将元素放在数组上
//				e = next; //访问下一个Entry链上的元素
//			}
//		}
//	}
//
//	/**
//	 * Copies all of the mappings from the specified map to this map.
//	 * These mappings will replace any mappings that this map had for
//	 * any of the keys currently in the specified map.
//	 *
//	 * @param m mappings to be stored in this map
//	 * @throws NullPointerException if the specified map is null
//	 */
//	@Override
//	public void putAll(Map<? extends K, ? extends V> m) {
//		int numKeysToBeAdded = m.size();
//		if (numKeysToBeAdded == 0) {
//			return;
//		}
//
//		if (table == EMPTY_TABLE) {
//			inflateTable((int) Math.max(numKeysToBeAdded * loadFactor, threshold));
//		}
//
//        /*
//         * Expand the map if the map if the number of mappings to be added
//         * is greater than or equal to threshold.  This is conservative; the
//         * obvious condition is (m.size() + size) >= threshold, but this
//         * condition could result in a map with twice the appropriate capacity,
//         * if the keys to be added overlap with the keys already in this map.
//         * By using the conservative calculation, we subject ourself
//         * to at most one extra resize.
//         */
//		if (numKeysToBeAdded > threshold) {
//			int targetCapacity = (int)(numKeysToBeAdded / loadFactor + 1);
//			if (targetCapacity > MAXIMUM_CAPACITY) {
//				targetCapacity = MAXIMUM_CAPACITY;
//			}
//			int newCapacity = table.length;
//			while (newCapacity < targetCapacity) {
//				newCapacity <<= 1;
//			}
//			if (newCapacity > table.length) {
//				resize(newCapacity);
//			}
//		}
//
//		for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
//			put(e.getKey(), e.getValue());
//		}
//	}
//
//	/**
//	 * Removes the mapping for the specified key from this map if present.
//	 *
//	 * @param  key key whose mapping is to be removed from the map
//	 * @return the previous value associated with <tt>key</tt>, or
//	 *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
//	 *         (A <tt>null</tt> return can also indicate that the map
//	 *         previously associated <tt>null</tt> with <tt>key</tt>.)
//	 */
//	@Override
//	public V remove(Object key) {
//		Entry<K,V> e = removeEntryForKey(key);
//		return (e == null ? null : e.value);
//	}
//
//	/**
//	 * Removes and returns the entry associated with the specified key
//	 * in the HashMap17.  Returns null if the HashMap17 contains no mapping
//	 * for this key.
//	 */
//	final Entry<K,V> removeEntryForKey(Object key) {
//		if (size == 0) {
//			return null;
//		}
//		int hash = (key == null) ? 0 : hash(key);
//		int i = indexFor(hash, table.length);
//		Entry<K,V> prev = table[i];
//		Entry<K,V> e = prev;
//
//		while (e != null) {
//			Entry<K,V> next = e.next;
//			Object k;
//			if (e.hash == hash &&
//					((k = e.key) == key || (key != null && key.equals(k)))) {
//				modCount++;
//				size--;
//				if (prev == e) {
//					table[i] = next;
//				} else {
//					prev.next = next;
//				}
//				e.recordRemoval(this);
//				return e;
//			}
//			prev = e;
//			e = next;
//		}
//
//		return e;
//	}
//
//	/**
//	 * Special version of remove for EntrySet using {@code Map.Entry.equals()}
//	 * for matching.
//	 */
//	final Entry<K,V> removeMapping(Object o) {
//		if (size == 0 || !(o instanceof Map.Entry)) {
//			return null;
//		}
//
//		Map.Entry<K,V> entry = (Map.Entry<K,V>) o;
//		Object key = entry.getKey();
//		int hash = (key == null) ? 0 : hash(key);
//		int i = indexFor(hash, table.length);
//		Entry<K,V> prev = table[i];
//		Entry<K,V> e = prev;
//
//		while (e != null) {
//			Entry<K,V> next = e.next;
//			if (e.hash == hash && e.equals(entry)) {
//				modCount++;
//				size--;
//				if (prev == e) {
//					table[i] = next;
//				} else {
//					prev.next = next;
//				}
//				e.recordRemoval(this);
//				return e;
//			}
//			prev = e;
//			e = next;
//		}
//
//		return e;
//	}
//
//	/**
//	 * Removes all of the mappings from this map.
//	 * The map will be empty after this call returns.
//	 */
//	@Override
//	public void clear() {
//		modCount++;
//		Arrays.fill(table, null);
//		size = 0;
//	}
//
//	/**
//	 * Returns <tt>true</tt> if this map maps one or more keys to the
//	 * specified value.
//	 *
//	 * @param value value whose presence in this map is to be tested
//	 * @return <tt>true</tt> if this map maps one or more keys to the
//	 *         specified value
//	 */
//	@Override
//	public boolean containsValue(Object value) {
//		if (value == null) {
//			return containsNullValue();
//		}
//
//		Entry[] tab = table;
//		for (int i = 0; i < tab.length ; i++) {
//			for (Entry e = tab[i]; e != null; e = e.next) {
//				if (value.equals(e.value)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * Special-case code for containsValue with null argument
//	 */
//	private boolean containsNullValue() {
//		Entry[] tab = table;
//		for (int i = 0; i < tab.length ; i++) {
//			for (Entry e = tab[i]; e != null; e = e.next) {
//				if (e.value == null) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * Returns a shallow copy of this <tt>HashMap17</tt> instance: the keys and
//	 * values themselves are not cloned.
//	 *
//	 * @return a shallow copy of this map
//	 */
//	@Override
//	public Object clone() {
//		HashMap17<K,V> result = null;
//		try {
//			result = (HashMap17<K,V>)super.clone();
//		} catch (CloneNotSupportedException e) {
//			// assert false;
//		}
//		if (result.table != EMPTY_TABLE) {
//			result.inflateTable(Math.min(
//					(int) Math.min(
//							size * Math.min(1 / loadFactor, 4.0f),
//							// we have limits...
//							HashMap17.MAXIMUM_CAPACITY),
//					table.length));
//		}
//		result.entrySet = null;
//		result.modCount = 0;
//		result.size = 0;
//		result.init();
//		result.putAllForCreate(this);
//
//		return result;
//	}
//
//	// 核心数据结构,数组+（单项）链表，LinkHashMap 是双向链表
//	static class Entry<K,V> implements Map.Entry<K,V> {
//		final K key;
//		V value;
//		Entry<K,V> next;
//		int hash;
//
//		/**
//		 * Creates new entry.
//		 */
//		Entry(int h, K k, V v, Entry<K,V> n) {
//			value = v;
//			next = n;
//			key = k;
//			hash = h;
//		}
//
//		@Override
//		public final K getKey() {
//			return key;
//		}
//
//		@Override
//		public final V getValue() {
//			return value;
//		}
//
//		@Override
//		public final V setValue(V newValue) {
//			V oldValue = value;
//			value = newValue;
//			return oldValue;
//		}
//
//		@Override
//		public final boolean equals(Object o) {
//			if (!(o instanceof Map.Entry)) {
//				return false;
//			}
//			Map.Entry e = (Map.Entry)o;
//			Object k1 = getKey();
//			Object k2 = e.getKey();
//			if (k1 == k2 || (k1 != null && k1.equals(k2))) {
//				Object v1 = getValue();
//				Object v2 = e.getValue();
//				if (v1 == v2 || (v1 != null && v1.equals(v2))) {
//					return true;
//				}
//			}
//			return false;
//		}
//
//		@Override
//		public final int hashCode() {
//			return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
//		}
//
//		@Override
//		public final String toString() {
//			return getKey() + "=" + getValue();
//		}
//
//		// LinkedHashMap 有实现
//		void recordAccess(HashMap17<K,V> m) {
//		}
//
//		// This method is invoked whenever the entry is removed from the table.
//		void recordRemoval(HashMap17<K,V> m) {}
//	}
//
//	//
//	void addEntry(int hash, K key, V value, int bucketIndex) {
//		if ((size >= threshold) && (null != table[bucketIndex])) {
//			resize(2 * table.length);
//			hash = (null != key) ? hash(key) : 0;
//			bucketIndex = indexFor(hash, table.length);
//		}
//		createEntry(hash, key, value, bucketIndex);
//	}
//
//	// 它把新建的Entry节点node作为头部,链上之前的链表
//	void createEntry(int hash, K key, V value, int bucketIndex) {
//		Entry<K,V> e = table[bucketIndex];
//		table[bucketIndex] = new Entry<K, V>(hash, key, value, e);
//		size++;
//	}
//
//	// 类似于Entry数组的迭代器，主要是对table进行操作
//	private abstract class HashIterator<E> implements Iterator<E> {
//		Entry<K,V> next;        // next entry to return
//		int expectedModCount;   // For fast-fail
//		int index;              // current slot
//		Entry<K,V> current;     // current entry
//
//		HashIterator() {
//			expectedModCount = modCount;
//			if (size > 0) { // advance to first entry
//				Entry[] t = table;
//				while (index < t.length && (next = t[index++]) == null) {
//					;
//				}
//			}
//		}
//
//		@Override
//		public final boolean hasNext() {
//			return next != null;
//		}
//
//		final Entry<K,V> nextEntry() {
//			if (modCount != expectedModCount) {
//				throw new ConcurrentModificationException();
//			}
//			Entry<K,V> e = next;
//			if (e == null) {
//				throw new NoSuchElementException();
//			}
//
//			if ((next = e.next) == null) {
//				Entry[] t = table;
//				while (index < t.length && (next = t[index++]) == null) {
//					;
//				}
//			}
//			current = e;
//			return e;
//		}
//
//		@Override
//		public void remove() {
//			if (current == null) {
//				throw new IllegalStateException();
//			}
//			if (modCount != expectedModCount) {
//				throw new ConcurrentModificationException();
//			}
//			Object k = current.key;
//			current = null;
//			HashMap17.this.removeEntryForKey(k);
//			expectedModCount = modCount;
//		}
//	}
//
//	//-------------------
//	private final class ValueIterator extends HashIterator<V> {
//		@Override
//		public V next() {
//			return nextEntry().value;
//		}
//	}
//
//	private final class KeyIterator extends HashIterator<K> {
//		@Override
//		public K next() {
//			return nextEntry().getKey();
//		}
//	}
//
//	private final class EntryIterator extends HashIterator<Map.Entry<K,V>> {
//		@Override
//		public Map.Entry<K,V> next() {
//			return nextEntry();
//		}
//	}
//
//	// Subclass overrides these to alter behavior of views' iterator() method
//	Iterator<K> newKeyIterator()   {
//		return new KeyIterator();
//	}
//	Iterator<V> newValueIterator()   {
//		return new ValueIterator();
//	}
//	Iterator<Map.Entry<K,V>> newEntryIterator()   {
//		return new EntryIterator();
//	}
//
//	private transient Set<Map.Entry<K,V>> entrySet = null;
//
////    public Set<K> keySet() {
////        Set<K> ks = keySet;
////        return (ks != null ? ks : (keySet = new KeySet()));
////    }
//
//	private final class KeySet extends AbstractSet<K> {
//		@Override
//		public Iterator<K> iterator() {
//			return newKeyIterator();
//		}
//		@Override
//		public int size() {
//			return size;
//		}
//		@Override
//		public boolean contains(Object o) {
//			return containsKey(o);
//		}
//		@Override
//		public boolean remove(Object o) {
//			return HashMap17.this.removeEntryForKey(o) != null;
//		}
//		@Override
//		public void clear() {
//			HashMap17.this.clear();
//		}
//	}
//
//	@Override
//	public Collection<V> values() {
//		Collection<V> vs = values;
//		return (vs != null ? vs : (values = new Values()));
//	}
//
//	private final class Values extends AbstractCollection<V> {
//		@Override
//		public Iterator<V> iterator() {
//			return newValueIterator();
//		}
//		@Override
//		public int size() {
//			return size;
//		}
//		@Override
//		public boolean contains(Object o) {
//			return containsValue(o);
//		}
//		@Override
//		public void clear() {
//			HashMap17.this.clear();
//		}
//	}
//
//	@Override
//	public Set<Map.Entry<K,V>> entrySet() {
//		return entrySet0();
//	}
//
//	private Set<Map.Entry<K,V>> entrySet0() {
//		Set<Map.Entry<K,V>> es = entrySet;
//		return es != null ? es : (entrySet = new EntrySet());
//	}
//
//	private final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
//		@Override
//		public Iterator<Map.Entry<K,V>> iterator() {
//			return newEntryIterator();
//		}
//		@Override
//		public boolean contains(Object o) {
//			if (!(o instanceof Map.Entry)) {
//				return false;
//			}
//			Map.Entry<K,V> e = (Map.Entry<K,V>) o;
//			Entry<K,V> candidate = getEntry(e.getKey());
//			return candidate != null && candidate.equals(e);
//		}
//		@Override
//		public boolean remove(Object o) {
//			return removeMapping(o) != null;
//		}
//		@Override
//		public int size() {
//			return size;
//		}
//		@Override
//		public void clear() {
//			HashMap17.this.clear();
//		}
//	}
//
//	private void writeObject(java.io.ObjectOutputStream s) throws IOException {
//		// Write out the threshold, loadfactor, and any hidden stuff
//		s.defaultWriteObject();
//
//		// Write out number of buckets
//		if (table==EMPTY_TABLE) {
//			s.writeInt(roundUpToPowerOf2(threshold));
//		} else {
//			s.writeInt(table.length);
//		}
//
//		// Write out size (number of Mappings)
//		s.writeInt(size);
//
//		// Write out keys and values (alternating)
//		if (size > 0) {
//			for(Map.Entry<K,V> e : entrySet0()) {
//				s.writeObject(e.getKey());
//				s.writeObject(e.getValue());
//			}
//		}
//	}
//
//	private static final long serialVersionUID = 362498820763181265L;
//
//	private void readObject(java.io.ObjectInputStream s)
//			throws IOException, ClassNotFoundException
//	{
//		// Read in the threshold (ignored), loadfactor, and any hidden stuff
//		s.defaultReadObject();
//		if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
//			throw new InvalidObjectException("Illegal load factor: " +
//					loadFactor);
//		}
//
//		// set other fields that need values
//		table = (Entry<K,V>[]) EMPTY_TABLE;
//
//		// Read in number of buckets
//		s.readInt(); // ignored.
//
//		// Read number of mappings
//		int mappings = s.readInt();
//		if (mappings < 0) {
//			throw new InvalidObjectException("Illegal mappings count: " +
//					mappings);
//		}
//
//		// capacity chosen by number of mappings and desired load (if >= 0.25)
//		int capacity = (int) Math.min(
//				mappings * Math.min(1 / loadFactor, 4.0f),
//				// we have limits...
//				HashMap17.MAXIMUM_CAPACITY);
//
//		// allocate the bucket array;
//		if (mappings > 0) {
//			inflateTable(capacity);
//		} else {
//			threshold = capacity;
//		}
//
//		init();  // Give subclass a chance to do its thing.
//
//		// Read the keys and values, and put the mappings in the HashMap17
//		for (int i = 0; i < mappings; i++) {
//			K key = (K) s.readObject();
//			V value = (V) s.readObject();
//			putForCreate(key, value);
//		}
//	}
//
//	// These methods are used when serializing HashSets
//	int   capacity()     { return table.length; }
//	float loadFactor()   { return loadFactor;   }
//}
