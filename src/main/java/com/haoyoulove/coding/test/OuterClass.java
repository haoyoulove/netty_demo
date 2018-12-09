package com.haoyoulove.coding.test;

public class OuterClass {

	String name = "ourerClass";
	static String title = "ourerClass";

	static class StaticInnerClass{

		public static void print(){
			System.out.println(title);
		}
	}

	public static void main(String[] args) {


		OuterClass.StaticInnerClass sic =
				new OuterClass.StaticInnerClass();
		sic.print();

	}

}
