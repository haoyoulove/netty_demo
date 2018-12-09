package com.haoyoulove.coding.test.HashMap;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangjing
 */
public class MapTest {


	public static void main(String[] agrs ){



//		Map<String, Object> map = new HashMap17<>(4);
//		Object put = map.put("ad", "name");
//		Object put1 = map.put("ad1", "name1");
//
//		Map<String, Object> map2 = new HashMap17<>(4);
//		map2.put("ad", "namexxx");
//
//		map.put("ad2", null);
//
//		map.replace("ad2", null, "as");

//		System.out.println(map.putIfAbsent("ad3", "xxxx"));

//		System.out.println(map);

//		map.putAll(map2);

//		System.out.println(map.getOrDefault("ad3", "defalut"));

//		System.out.println(map.values());

//		System.out.println(map.remove("ad"));
//		System.out.println(map);






		Map<String, Object> map3 = new HashMap<>(4);
		map3.put("ad3", "name3");
		map3.put("ad2", "name2");
		map3.put("ad", "name");
		map3.put("ad1", "name1");

		// 1.8 1
//		System.out.println(map3.getOrDefault("ad3", "defalut"));
//		System.out.println(map3);


//		// 1.8 2  map ->forEach + Lambda表达式 增强
//		List<String> list3 = new ArrayList8<>();
//		map3.forEach((s, o) ->{
//					list3.add(s);
//					System.out.println("k: "+s +", o: "+ o );
//				});
//
//		System.out.println(list3);
//
//		// 1.8  list -> forEach + Lambda表达式 增强
//		Consumer<String> con = s -> {
//			s += "1";
//			System.out.println(s);
//		};
//		list3.forEach(con);


		//1.8 replaceAll  替换map里面所有的value值为key和value计算出来的值
//		map3.replaceAll((s, o) -> s+"_"+o );
//		System.out.println(map3);

		//1.8 putIfAbsent 为了设置key不存在的值，key如果存在，直接返回对应value
//		System.out.println(map3.putIfAbsent("ad5","absent"));
//		System.out.println(map3);

		//1.8 remove 删除key和value都匹配上的值
//		System.out.println(map3.remove("ad2","name2"));
//		System.out.println(map3);

		//1.8 replace 替换 指定的key和value都一致 的value值
//		map3.replace("ad2","name2","rep");
//		System.out.println(map3);

		//1.8 替换指定的key,只要key存在，key的value替换成为新值
//		map3.replace("ad2", "namexxx");
//		System.out.println(map3);


		//1.8 设置一个key不存在的值，这个值需要通过传入key计算得到
//		map3.computeIfAbsent("ad25", new Function<String, Object>() {
//			@Override
//			public Object apply(String s) {
//
//				return "xxxtttt";
//			}
//		});
//		map3.computeIfAbsent("ad6", s -> s+"_xxrrtccc");
//
//		System.out.println(map3);

		// 1.8 传入key值，有存在的key和对应的value值，key和value计算得到新值更改,并且返回新值
		//如果传入的key对应value为null，不做处理直接返回null
		//如果key和value计算得到的新值为null,删除这个key，返回null.
//		map3.computeIfPresent("ad2",(s, o) -> {
//			String test = "xxxx";
//			return s+test+o;
//		});

//		map3.computeIfPresent("ad2",(s, o) -> null);
//		System.out.println(map3);


		//传入一个key,得到value值，value可能为null，也就是这个key可能不存在，计算key和value得到新值newValue
		//newValue不为null,设置key的value为新值，newValue为null，如果oldValue也会null，
		// 直接返回null,如果oldValue不为null。需要删除这个key
//		map3.compute("ad5",(s, o) -> s+o);
//		System.out.println(map3);
//
//		map3.compute("ad2",(s, o) -> null);
//		System.out.println(map3);


		//传入key和一个值，如果key对应的oldValue为null,将传入值设置为newValue,
		//如果oldValue不为null,计算oldValue和传入值的计算结果，作为newValue
		//如果得到的新值newValue为null，直接删除这个key.
//		map3.merge("ad2","tnhy788877",(o, o2) -> o+"__**___"+o2);
//		System.out.println(map3);
//		map3.merge("ad5","tnhy788877",(o, o2) -> o+"__**___"+o2);
//		System.out.println(map3);


		Map<Integer, Object> map4 = new HashMap<>(4);
		map4.put(2, "name3");
		map4.put(3, "name2");
		map4.put(4, "name");
		map4.put(1, "name1");

		Comparator<Map.Entry<Integer, Object>> entryComparator = Map.Entry.comparingByKey();

		map4.entrySet().stream()
				.sorted(entryComparator)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));

		System.out.println(map4);

//		Comparator<Map.Entry<Integer, String>> com = Comparator.comparing(Map.Entry::getValue);


	}



}





































