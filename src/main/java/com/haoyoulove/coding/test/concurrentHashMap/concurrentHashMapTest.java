package com.haoyoulove.coding.test.concurrentHashMap;

/**
 * @author yangjing
 */
public class concurrentHashMapTest {


	public static void main(String args[]){

//		int retries = -1;
//
//		while (true){
//			if(retries++ == 2){
//				System.out.println("s");
//				break;
//			}
//			System.out.println("w"+retries);
//		}
//0x7fffffff
//		System.out.println(Integer.valueOf("7fffffff",16).toString() );
//
//		System.out.println(Integer.toBinaryString(2147483647) );

		for (int i = 0, bound=0;;){
			if(--i >= bound){
				System.out.println(i);
				System.out.println(bound);
				break;
			}else{
				System.out.println("xxx");
				System.out.println(i);
				System.out.println(bound);
				break;
			}

		}


	}
}
