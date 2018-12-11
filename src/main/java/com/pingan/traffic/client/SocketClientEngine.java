package com.pingan.traffic.client;

import java.net.URISyntaxException;

/**
 * @author yangjing
 */
public class SocketClientEngine {

	public static void main(String[] args) {
		try {
			WebClientEnum.CLIENT.initClient(new MsgWebSocketClient("ws://localhost:17888"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
