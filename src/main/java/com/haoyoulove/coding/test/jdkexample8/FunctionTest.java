package com.haoyoulove.coding.test.jdkexample8;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author yangjing
 */
public class FunctionTest {

	public static void main(String args[]){

//		Predicate<Object> pre = o ->{
//			System.out.println("pre o->"+o);
//			return false;
//		};
//		// 直接指向pre函数获取boolean返回值
//		System.out.println(pre.test("asd"));
//
//		Predicate<Object> pre1 = o ->{
//			System.out.println("pre1 o->"+o);
//			return false;
//		};
//		// pre和pre1两个函数绑定为且的关系，并且返回函数preAnd
//		Predicate<Object> preAnd = pre1.and(pre);
//
//		System.out.println(preAnd.test("tesss"));



		Predicate<Object> pre2 = o ->{
			System.out.println("pre2 o->"+o);
			return false;
		};
		// pre2是第一个pre函数，和isEqual组成组合or函数。
		//通过or来调用test比较，传入“asd”，都会在pre2和Predicate.isEqual("asd")使用计算
		Predicate<Object> or = pre2.or(Predicate.isEqual("asd"));
		System.out.println(or.test("asd"));


		/**
		 *
		 * Predicate -- 传入一个参数，返回一个bool结果， 方法为boolean test(T t)
		 Consumer -- 传入一个参数，无返回值，纯消费。 方法为void accept(T t)
		 Function -- 传入一个参数，返回一个结果，方法为R apply(T t)
		 Supplier -- 无参数传入，返回一个结果，方法为T get()
		 UnaryOperator -- 一元操作符， 继承Function,传入参数的类型和返回类型相同。
		 BinaryOperator -- 二元操作符， 传入的两个参数的类型和返回类型相同， 继承BiFunction

		 相互之间可以搭配使用
		 */

		Supplier<Object> sup = () -> {
			System.out.println("sup");
			return 1;
		};

		System.out.println(sup.get());

		// 可能是在func调用之后传入的Obj改变了，然后需要拿参数使用吧.
		Function<Object, Object> identity = Function.identity();
		Function<Object, Object> fuc = o -> {
			System.out.println("fuc o->"+o);
			return 1;
		};

		Function<Object, Object> fucObj = fuc.andThen(identity);
		fucObj.apply("funObje");
	}
}
