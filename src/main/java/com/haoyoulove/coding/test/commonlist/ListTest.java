package com.haoyoulove.coding.test.commonlist;

import java.util.*;

/**
 * @author yangjing
 */
public class ListTest {

	public static void main(String args[]){

//		List<String> list = new ArrayList8<>();
//		list.add("123");
//		list.add("456");
//		list.add("456");
//		list.add("456");
//		list.add("456");


//		String[] a = new String[20];
//		a[0]="1233";
//		String[] strings = list.toArray(a);
//		for (String s : strings) {
//			System.out.println(s);
//		}


//		Object[] obj = list.toArray();
//		for (Object o : obj) {
//			System.out.println(o);
//		}

		// 这个取的是list有多少个String对象
//		for (String s : list) {
//			System.out.println(s);
//		}


//		Object[] arr = {};
//		arr = Arrays.copyOf(arr, 10);
//		System.out.println("arr.length->"+arr.length);
//		int i = 0;
//		arr[i++] = "123";
//
//		System.out.println("i->"+i);
//		for (Object s : arr) {
//			System.out.println(s);
//		}
//		System.out.println(arr.toString());


//		String[] str = new String[10];
//		str[0] ="0";
//		str[1] ="1";
//		str[2] ="2";
//		str[3] ="3";
//		System.out.println(str.length);
//
//		for (String s : str) {
//			System.out.println(s);
//		}
//		String[] str1 = new String[10];
//		System.arraycopy(str,0,str1,0,str.length);
//		System.out.println("====================");
//		for (String s : str1) {
//			System.out.println(s);
//		}
//		str1[0]= "xxxx";
//		System.out.println("====================11111111111");
//		for (String s : str) {
//			System.out.println(s);
//		}
//		System.out.println("====================222222222222222");
//		for (String s : str1) {
//			System.out.println(s);
//		}


//		LinkedList8<String> test = new LinkedList8<>();
//		test.add("123");
//		test.add("1231");
//		test.add("1223");
//		test.add("1234");
//		test.add("1235");
//
//		ListIterator<String> it = test.listIterator(test.size);
//		while (it.hasPrevious()){
//			String previous = it.previous();
//			System.out.println(previous);
//		}

//		while (it.hasNext()){
//			String next = it.next();
//			System.out.println(next);
//		}


		int a = 0;
		int b = 0;

		if(a++ > 0 && b++ > 0){
			System.out.println(b++);
		}

		System.out.println(b);

		if(a == 1 && b++ == 0){
			b++;
		}
		System.out.println(b);
	}
}


































