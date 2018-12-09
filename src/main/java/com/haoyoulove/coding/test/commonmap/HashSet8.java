//package com.haoyoulove.coding.test.commonmap;
//
//import java.util.*;
//
//import java.io.InvalidObjectException;
//import sun.misc.SharedSecrets;
//
//
//public class HashSet8<E>
//		extends AbstractSet<E>
//		implements Set<E>, Cloneable, java.io.Serializable
//{
//	static final long serialVersionUID = -5024744406713321676L;
//
//	// 基于hashMap 做一次封装
//	private transient HashMap<E,Object> map;
//
//	// Dummy value to associate with an Object in the backing Map
//	// 虚构的present,用于存放value的位置
//	private static final Object PRESENT = new Object();
//
//	/**
//	 * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
//	 * default initial capacity (16) and load factor (0.75).
//	 */
//	// 默认创建
//	public HashSet8() {
//		map = new HashMap<>();
//	}
//
//	/**
//	 * Constructs a new set containing the elements in the specified
//	 * collection.  The <tt>HashMap</tt> is created with default load factor
//	 * (0.75) and an initial capacity sufficient to contain the elements in
//	 * the specified collection.
//	 *
//	 * @param c the collection whose elements are to be placed into this set
//	 * @throws NullPointerException if the specified collection is null
//	 */
//	public HashSet8(Collection<? extends E> c) {
//		map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
//		addAll(c);
//	}
//
//	/**
//	 * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
//	 * the specified initial capacity and the specified load factor.
//	 *
//	 * @param      initialCapacity   the initial capacity of the hash map
//	 * @param      loadFactor        the load factor of the hash map
//	 * @throws     IllegalArgumentException if the initial capacity is less
//	 *             than zero, or if the load factor is nonpositive
//	 */
//	// 创建都只是为了创建当前一个实体map的实例
//	public HashSet8(int initialCapacity, float loadFactor) {
//		map = new HashMap<>(initialCapacity, loadFactor);
//	}
//
//	/**
//	 * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
//	 * the specified initial capacity and default load factor (0.75).
//	 *
//	 * @param      initialCapacity   the initial capacity of the hash table
//	 * @throws     IllegalArgumentException if the initial capacity is less
//	 *             than zero
//	 */
//	public HashSet8(int initialCapacity) {
//		map = new HashMap<>(initialCapacity);
//	}
//
//	/**
//	 * Constructs a new, empty linked hash set.  (This package private
//	 * constructor is only used by LinkedHashSet8.) The backing
//	 * HashMap instance is a LinkedHashMap with the specified initial
//	 * capacity and the specified load factor.
//	 *
//	 * @param      initialCapacity   the initial capacity of the hash map
//	 * @param      loadFactor        the load factor of the hash map
//	 * @param      dummy             ignored (distinguishes this constructor from other int, float constructor.)
//	 * @throws     IllegalArgumentException if the initial capacity is less
//	 *             than zero, or if the load factor is nonpositive
//	 */
//	// 多一个参数只是为了区分构造的是否是LinedHashMap.其实也就是有序的LinkedHashSet初始化而已
//	HashSet8(int initialCapacity, float loadFactor, boolean dummy) {
//		map = new LinkedHashMap<>(initialCapacity, loadFactor);
//	}
//
//	/**
//	 * Returns an iterator over the elements in this set.  The elements
//	 * are returned in no particular order.
//	 *
//	 * @return an Iterator over the elements in this set
//	 * @see ConcurrentModificationException
//	 */
//	// map的迭代器
//	@Override
//	public Iterator<E> iterator() {
//		return map.keySet().iterator();
//	}
//
//	/**
//	 * Returns the number of elements in this set (its cardinality).
//	 *
//	 * @return the number of elements in this set (its cardinality)
//	 */
//	@Override
//	// map的大小
//	public int size() {
//		return map.size();
//	}
//
//	/**
//	 * Returns <tt>true</tt> if this set contains no elements.
//	 *
//	 * @return <tt>true</tt> if this set contains no elements
//	 */
//	@Override
//
//	public boolean isEmpty() {
//		return map.isEmpty();
//	}
//
//	/**
//	 * Returns <tt>true</tt> if this set contains the specified element.
//	 * More formally, returns <tt>true</tt> if and only if this set
//	 * contains an element <tt>e</tt> such that
//	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
//	 *
//	 * @param o element whose presence in this set is to be tested
//	 * @return <tt>true</tt> if this set contains the specified element
//	 */
//	@Override
//	//判断map是否包含这个key
//	public boolean contains(Object o) {
//		return map.containsKey(o);
//	}
//
//	/**
//	 * Adds the specified element to this set if it is not already present.
//	 * More formally, adds the specified element <tt>e</tt> to this set if his set contains no element <tt>e2</tt> such that
//	 * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>.
//	 * If this set already contains the element, the call leaves the set unchanged and returns <tt>false</tt>.
//	 *
//	 * @param e element to be added to this set
//	 * @return <tt>true</tt> if this set did not already contain the specified
//	 * element
//	 */
//	@Override
//	// 其实也就是调用map.put，如果key不存在，直接插入，hashmap会返回空，说明插入成功。
//	// 如果不返回空，就说明之前map的key已经存在了.这里添加返回false即可.
//	// 由于hashMap支持key和value都为null,所以hashset也支持。
//	public boolean add(E e) {
//		return map.put(e, PRESENT)==null;
//	}
//
//	/**
//	 * Removes the specified element from this set if it is present.
//	 * More formally, removes an element <tt>e</tt> such that
//	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>,
//	 * if this set contains such an element.  Returns <tt>true</tt> if
//	 * this set contained the element (or equivalently, if this set
//	 * changed as a result of the call).  (This set will not contain the
//	 * element once the call returns.)
//	 *
//	 * @param o object to be removed from this set, if present
//	 * @return <tt>true</tt> if the set contained the specified element
//	 */
//	@Override
//	// 同理走的hashMap的删除，如果map中包含这个元素，并且删了它，才返回true
//	public boolean remove(Object o) {
//		return map.remove(o)==PRESENT;
//	}
//
//	/**
//	 * Removes all of the elements from this set.
//	 * The set will be empty after this call returns.
//	 */
//	@Override
//	public void clear() {
//		map.clear();
//	}
//
//	/**
//	 * Returns a shallow copy of this <tt>HashSet8</tt> instance: the elements
//	 * themselves are not cloned.
//	 *
//	 * @return a shallow copy of this set
//	 */
//	@Override
//	@SuppressWarnings("unchecked")
//	public Object clone() {
//		try {
//			HashSet8<E> newSet = (HashSet8<E>) super.clone();
//			newSet.map = (HashMap<E, Object>) map.clone();
//			return newSet;
//		} catch (CloneNotSupportedException e) {
//			throw new InternalError(e);
//		}
//	}
//
//	/**
//	 * Save the state of this <tt>HashSet</tt> instance to a stream (that is,
//	 * serialize it).
//	 *
//	 * @serialData The capacity of the backing <tt>HashMap</tt> instance
//	 *             (int), and its load factor (float) are emitted, followed by
//	 *             the size of the set (the number of elements it contains)
//	 *             (int), followed by all of its elements (each an Object) in
//	 *             no particular order.
//	 */
//	//	// transient 表明该数据不参与序列化？为什么用transient
////	// 1.transient 首先是表明该数据不参与序列化。假设HashMap 中的存储数据的数组还有很多的空间没有被使用，
////	// 没有被使用到的空间被序列化没有意义。所以下文会有手动使用 writeObject() 方法，只序列化实际存储元素的数组。
////	// 2. 不同的虚拟机对于相同 hashCode 产生的 Code 值可能是不一样的，如果使用默认序列化，则反序列化后，元素的位置和之前的是保持一致的，
////	// 可是由于 hashCode 的值不一样了，那么后续看到的定位函数 indexOf（）返回的元素下标就会不同，其结果会出差错。
//	// 好像map都有上面这个原因，后面学习transient的时候看看先
//	private void writeObject(java.io.ObjectOutputStream s)
//			throws java.io.IOException {
//		// Write out any hidden serialization magic
//		s.defaultWriteObject();
//
//		// Write out HashMap capacity and load factor
//		s.writeInt(map.capacity());
//		s.writeFloat(map.loadFactor());
//
//		// Write out size
//		s.writeInt(map.size());
//
//		// Write out all elements in the proper order.
//		for (E e : map.keySet()) {
//			s.writeObject(e);
//		}
//	}
//
//	/**
//	 * Reconstitute the <tt>HashSet</tt> instance from a stream (that is,
//	 * deserialize it).
//	 */
//	private void readObject(java.io.ObjectInputStream s)
//			throws java.io.IOException, ClassNotFoundException {
//		// Read in any hidden serialization magic
//		s.defaultReadObject();
//
//		// Read capacity and verify non-negative.
//		int capacity = s.readInt();
//		if (capacity < 0) {
//			throw new InvalidObjectException("Illegal capacity: " +
//					capacity);
//		}
//
//		// Read load factor and verify positive and non NaN.
//		float loadFactor = s.readFloat();
//		if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
//			throw new InvalidObjectException("Illegal load factor: " +
//					loadFactor);
//		}
//
//		// Read size and verify non-negative.
//		int size = s.readInt();
//		if (size < 0) {
//			throw new InvalidObjectException("Illegal size: " +
//					size);
//		}
//		// Set the capacity according to the size and load factor ensuring that
//		// the HashMap is at least 25% full but clamping to maximum capacity.
//		capacity = (int) Math.min(size * Math.min(1 / loadFactor, 4.0f),
//				HashMap.MAXIMUM_CAPACITY);
//
//		// Constructing the backing map will lazily create an array when the first element is
//		// added, so check it before construction. Call HashMap.tableSizeFor to compute the
//		// actual allocation size. Check Map.Entry[].class since it's the nearest public type to
//		// what is actually created.
//
//		SharedSecrets.getJavaOISAccess()
//				.checkArray(s, Map.Entry[].class, HashMap.tableSizeFor(capacity));
//
//		// Create backing HashMap
//		map = (((HashSet8<?>)this) instanceof LinkedHashSet ? new LinkedHashMap<E,Object>(capacity, loadFactor) : new HashMap<E,Object>(capacity, loadFactor));
//
//		// Read in all elements in the proper order.
//		for (int i=0; i<size; i++) {
//			@SuppressWarnings("unchecked")
//			E e = (E) s.readObject();
//			map.put(e, PRESENT);
//		}
//	}
//
//	/**
//	 * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
//	 * and <em>fail-fast</em> {@link Spliterator} over the elements in this
//	 * set.
//	 *
//	 * <p>The {@code Spliterator} reports {@link Spliterator#SIZED} and
//	 * {@link Spliterator#DISTINCT}.  Overriding implementations should document
//	 * the reporting of additional characteristic values.
//	 *
//	 * @return a {@code Spliterator} over the elements in this set
//	 * @since 1.8
//	 */
//	@Override
//	public Spliterator<E> spliterator() {
//		return new HashMap.KeySpliterator<E,Object>(map, 0, -1, 0, 0);
//	}
//}
