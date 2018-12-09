package com.haoyoulove.coding.test.HashMap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yangjing
 */
public class HashMapTest2 {

	public static void main(String[] args) {

//		System.out.println("断点");
//		Map<String, Object> map = new HashMap<>(64);
//		map.put("ads","testssss");

//		String[] oldCap = {"1111","22222","33333"};
//
//		for (int j = 0; j < oldCap.length; ++j) {
//			System.out.println(j);
//			System.out.println(oldCap[j]);
//		}


//		HashMapResize8.Node<String,String>[] oldTab = (HashMapResize8.Node<String,String>[])new HashMapResize8.Node[2];
//
//		HashMapResize8.Node node1 = new HashMapResize8.Node<>(1,"key1","value1",null);
//
//		HashMapResize8.Node node2 = new HashMapResize8.Node(3,"key2","Value2",node1);
//
//		HashMapResize8.Node node3 = new HashMapResize8.Node(4,"key3","Value3",node2);
//
//		HashMapResize8.Node node4 = new HashMapResize8.Node(5,"key4","Value4",node3);
//
//		System.out.println(node4);
//		System.out.println("-------------------");
//
//		oldTab[0] = node4;
//
//
//		HashMapResize8 hmR = new HashMapResize8();
//		hmR.resize(oldTab);
//
//		System.out.println(1 & 2);
//		System.out.println(2 & 2);
//		System.out.println(3 & 2);
//		System.out.println(4 & 2);
//		System.out.println(5 & 2);
//		System.out.println(6 & 2);
//		System.out.println(7 & 2);
//		System.out.println(8 & 2);

		LinkedHashMap<String,Integer> lmap = new LinkedHashMap<>();
		lmap.put("语文", 1);
		lmap.put("数学", 2);
		lmap.put("英语", 3);
		lmap.put("历史", 4);
		lmap.put("政治", 5);
		lmap.put("地理", 6);
		lmap.put("生物", 7);
		lmap.put("化学", 8);
		for(Map.Entry<String, Integer> entry : lmap.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

	}
}
