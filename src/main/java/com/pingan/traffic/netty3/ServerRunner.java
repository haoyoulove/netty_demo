package com.pingan.traffic.netty3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author yangjing
 */

@Component
public class ServerRunner implements CommandLineRunner {

	private  WebSocketServer server;

	@Autowired
	public ServerRunner(WebSocketServer server) {
		this.server = server;
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("服务启动。。。。");
		server.run();
	}
}
