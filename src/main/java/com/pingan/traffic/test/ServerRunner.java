package com.pingan.traffic.test;

import com.pingan.traffic.netty.proto.WebSocketProtosServer;
import com.pingan.traffic.nettywebscoket.WebSocketChatServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author yangjing
 */

@Component
public class ServerRunner implements CommandLineRunner {

//	private WebSocketServer server;

//	private WebSocketProtosServer server;

	@Autowired
	private WebSocketChatServer server;


	@Override
	public void run(String... args) throws Exception {
		System.out.println("服务启动。。。。");
		server.start();
	}
}
