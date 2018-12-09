package com.pingan.traffic.netty3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yangjing
 */
@Configuration
public class ServerConfig {

	@Bean
	public WebSocketServer webSocketServer(){
		return new WebSocketServer(17888, new Listener());
	}
}
