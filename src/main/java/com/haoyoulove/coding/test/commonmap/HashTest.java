package com.haoyoulove.coding.test.commonmap;

import java.util.*;

/**
 * @author yangjing
 */
public class HashTest {
	static final int SHARED_SHIFT   = 16;
    // 0000 0000 0000 0001 0000 0000 0000 0000
    static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
    // 0000 0000 0000 0000 1000 0000 0000 0000
    static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
    // 0000 0000 0000 0000 1000 0000 0000 0000
    static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

	public static void main(String args[]){
		
        
		Map<String, Object> ht = new Hashtable<>();

//		ht.put(null,"123");
//
//		System.out.println(ht);

		//0x7fffffff
//		System.out.println(Integer.toBinaryString(Integer.valueOf("0000FFFF",16)) );

		System.out.println(Integer.toBinaryString((1 << 16) - 1));
		
		System.out.println(2 & EXCLUSIVE_MASK);
		System.out.println(Integer.valueOf("1111111111111111", 2).toString());
		
		
//		Object key = "test";
//
//		System.out.println(Integer.toBinaryString(key.hashCode()));
//		int hash = key.hashCode();
//		System.out.println((hash & 0x7FFFFFFF) % 11);

//		int j = 0;
//		for (int i = 0; i < 100; i++) {
//			j = ++j;
//		}
//		System.out.println(j);
//
//		Map<String, Object> map = new HashMap<>();
//		map.put(null,null);
//		System.out.println(map);


//		TreeSet<String> set = new TreeSet<>();
//		set.add("123");
//		set.add("1231");
//		set.add("1232");
//		set.add("1233");
//		set.add("789");
//		set.add("456");
//		set.add("4562");
//		set.add("4563");
//		set.add("45634");
//		set.add("123");
//		System.out.println(set);
//		SortedSet<String> sortedSet = set.headSet("456");
//		SortedSet<String> endSet = set.tailSet("456");
//		System.out.println(sortedSet);
//		System.out.println(endSet);

	}
}

