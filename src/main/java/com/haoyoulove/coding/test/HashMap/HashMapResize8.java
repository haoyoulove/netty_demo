package com.haoyoulove.coding.test.HashMap;

import java.util.Map;
import java.util.Objects;

/**
 * @author yangjing
 */
public class HashMapResize8<K, V> {

	public void resize(Node<K,V>[] oldTab){

		Node<K,V> e = oldTab[0];
		int oldCap =  oldTab.length;
		System.out.println("oldCap->"+oldCap);
		Node<K,V> loHead = null, loTail = null;
		Node<K,V> hiHead = null, hiTail = null;
		Node<K,V> next;
		do {
			next = e.next;
			if ((e.hash & oldCap) == 0) {
				if (loTail == null) {
					loHead = e;
				} else {
					loTail.next = e;
				}
				loTail = e;
			}
			else {
				if (hiTail == null) {
					hiHead = e;
				} else {
					hiTail.next = e;
				}
				hiTail = e;
			}
		} while ((e = next) != null);

		if (loTail != null) {
			loTail.next = null;
//			newTab[j] = loHead;

			System.out.println("loHead->"+loHead);
			System.out.println("loTail->"+loTail);
		}
		System.out.println("--------------------");

		if (hiTail != null) {
			hiTail.next = null;
			System.out.println("hiHead->"+hiHead);
			System.out.println("hiTail->"+hiTail);
		}

//		System.out.println("loHead->"+loHead);
//		System.out.println("loTail->"+loTail);
//
//		System.out.println("--------------------");
//
//		System.out.println("hiHead->"+hiHead);
//		System.out.println("hiTail->"+hiTail);
	}

//	static class Node<K,V> implements Map.Entry<K,V> {
//		final int hash;
//		final K key;
//		V value;
//		Node<K,V> next;
//
//		Node(int hash, K key, V value, Node<K,V> next) {
//			this.hash = hash;
//			this.key = key;
//			this.value = value;
//			this.next = next;
//		}
//
//		@Override
//		public final K getKey()        { return key; }
//		@Override
//		public final V getValue()      { return value; }
//
//
//		@Override
//		public final int hashCode() {
//			return Objects.hashCode(key) ^ Objects.hashCode(value);
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
//			if (o == this) {
//				return true;
//			}
//			if (o instanceof Map.Entry) {
//				Map.Entry<?,?> e = (Map.Entry<?,?>)o;
//				if (Objects.equals(key, e.getKey()) &&
//						Objects.equals(value, e.getValue())) {
//					return true;
//				}
//			}
//			return false;
//		}
//
//		@Override
//		public String toString() {
//			return "Node{" +
//					"hash=" + hash +
//					", key=" + key +
//					", value=" + value +
//					", next=" + next +
//					'}';
//		}
//	}

	static class Node<K,V> implements Map.Entry<K,V> {
		final int hash;
		final K key;
		V value;
		Node<K,V> next;

		Node(int hash, K key, V value, Node<K,V> next) {
			this.hash = hash;
			this.key = key;
			this.value = value;
			this.next = next;
		}

		public final K getKey()        { return key; }
		public final V getValue()      { return value; }
		public final String toString() { return key + "=" + value; }

		public final int hashCode() {
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}

		public final V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}

		public final boolean equals(Object o) {
			if (o == this)
				return true;
			if (o instanceof Map.Entry) {
				Map.Entry<?,?> e = (Map.Entry<?,?>)o;
				if (Objects.equals(key, e.getKey()) &&
						Objects.equals(value, e.getValue()))
					return true;
			}
			return false;
		}
	}
}
