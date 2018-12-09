package com.haoyoulove.coding.test.HashMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangjing
 */
public class HashMapTest {

//	public static final int test;
//
//	static {
//		test = 2;
//	}

	public static void main(String[] args){
		Map<String, Object> map = new HashMap<>(4);


//		map.put("test1", "name1");
//		map.put("test1", "name2");
//		map.put("test3", "name3");
//		map.put("test4", "name4");

//		System.out.println(map);

//		int i  = 4 << 2;
//		System.out.println(i);
//
//		int j  = 4 >> 2;
//		System.out.println(j);
//
//		int m = 4>>>2;
//		System.out.println(m);
//
//		int n = -4>>>2;
//		System.out.println(n);


//		System.out.println(highestOneBit(7));

		// 验证地址传递
//		EntryHashMap en = new EntryHashMap<Integer,Integer>();
//		en.put(1,11,2,22);
//
//		StringBuffer s= new StringBuffer("good");
//		StringBuffer s2=s;
//		s.append(" afternoon.");
//		System.out.println(s);
//		System.out.println(s2);


//		System.out.println(new Object().hashCode());

//		System.out.println(Integer.toBinaryString("asd".hashCode()));
//		System.out.println(Integer.toBinaryString(-1));
//		toBinary32("asd".hashCode());
//		System.out.println(toBinary32("asd".hashCode()));

		// 11111111111111111111000011101010
//		String s = "11111111111111111111000011101010";
//		System.out.println(s.length());
		System.out.println(Integer.valueOf("00000000000000000000000001100110",2).toString());

//		System.out.println(BinstrToStr(s));


//		for (int binCount = 0; ; ++binCount){
//			System.out.println(binCount);
//			if(binCount > (8 - 1)){
//				break;
//			}
//		}

	}

//	private static String BinstrToStr(String binStr) {
//		String[] tempStr = StrToStrArray(binStr);
//		char[] tempChar = new char[tempStr.length];
//		for (int i = 0; i < tempStr.length; i++) {
//			tempChar[i] = BinstrToChar(tempStr[i]);
//		}
//		return String.valueOf(tempChar);
//	}
//
//	// 将初始二进制字符串转换成字符串数组，以空格相隔
//	private static String[] StrToStrArray(String str) {
//		return str.split(" ");
//	}
//
//	// 将二进制字符串转换为char
//	private static char BinstrToChar(String binStr) {
//		int[] temp = BinstrToIntArray(binStr);
//		int sum = 0;
//		for (int i = 0; i < temp.length; i++) {
//			sum += temp[temp.length - 1 - i] << i;
//		}
//		return (char) sum;
//	}
//
//	// 将二进制字符串转换成int数组
//	private  static int[] BinstrToIntArray(String binStr) {
//		char[] temp = binStr.toCharArray();
//		int[] result = new int[temp.length];
//		for (int i = 0; i < temp.length; i++) {
//			result[i] = temp[i] - 48;
//		}
//		return result;
//	}
//
//
//
//
//	// 将字符串转换成二进制字符串，以空格相隔
//	private String StrToBinstr(String str) {
//		char[] strChar = str.toCharArray();
//		String result = "";
//		for (int i = 0; i < strChar.length; i++) {
//			result += Integer.toBinaryString(strChar[i]) + " ";
//		}
//		return result;
//	}
//
//
//
//
//
//	private static void toBinary32(int n){
//		for (int i = 31; i >= 0; i--) {
//			// 向左移动i位来和n的值按与比较。如果这个位置的值是1.那么就不等于0
//			if ((n & (1 << i)) != 0) {
//				System.out.print("1");
//			} else {
//				System.out.print("0");
//			}
//			if ((32 - i) % 8 == 0) {
//				System.out.print(" ");
//			}
//		}
//
//	}
//
//	public static int highestOneBit(int i) {
//		// HD, Figure 3-1
//		i |= (i >>  1);
//		System.out.println(i);
//		i |= (i >>  2);
//		i |= (i >>  4);
//		i |= (i >>  8);
//		i |= (i >> 16);
//		System.out.println(i);
//		System.out.println(i >>> 1);
//		return i - (i >>> 1);
//	}
}
