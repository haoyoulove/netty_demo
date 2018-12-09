package com.haoyoulove.coding.test.HashMap;

import java.util.Map;

/**
 * @author yangjing
 */
public class EntryHashMap<K,V> {


	 Entry<K,V>[] table;



	 class Entry<K,V> implements Map.Entry<K,V> {
		K key;
		V value;
		Entry<K,V> next;
		int hash;

		/**
		 * Creates new entry.
		 */
		Entry(int h, K k, V v, Entry<K,V> n) {
			value = v;
			next = n;
			key = k;
			hash = h;
		}

		@Override
		public final K getKey() {
			return key;
		}
		@Override
		public final V getValue() {
			return value;
		}
		@Override
		public final V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}
		@Override
		public final String toString() {
			return getKey() + "=" + getValue();
		}

	}

	public void put(K k,V v, K k1, V v1){
		table = new Entry[10];
		Entry<K,V> e = table[0];
		table[0] = new Entry<K, V>(1, k, v, e);
		Entry<K,V> e1 = table[0];
		table[0] = new Entry<K, V>(1, k1, v1, e1);
		test();
	}

	public void test(){
		Entry[] newTable = new Entry[10];
		for (Entry<K,V> e : table) {
			while(null != e) {
				Entry<K,V> next = e.next;
				int i = 8;
				e.next = newTable[i];
				newTable[i] = e;
				e = next;
			}
		}
	}
}
