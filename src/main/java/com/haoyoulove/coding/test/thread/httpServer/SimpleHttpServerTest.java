package com.haoyoulove.coding.test.thread.httpServer;

public class SimpleHttpServerTest {
	
	public static void main(String[] args) throws Exception {
		SimpleHttpServer.setPort(8081);
		
		SimpleHttpServer.setBasePath("E:\\workspace\\coding\\src\\main\\webapp");
		SimpleHttpServer.start();
	}
	 
	
	
}
