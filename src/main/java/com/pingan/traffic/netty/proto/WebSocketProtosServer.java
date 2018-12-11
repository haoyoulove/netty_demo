package com.pingan.traffic.netty.proto;

import com.pingan.traffic.netty3.GaoListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author yangjing
 */
public class WebSocketProtosServer {

	private int port;
	private GaoListener gaoListener;

	public WebSocketProtosServer(int port, GaoListener gaoListener) {
		this.port = port;
		this.gaoListener = gaoListener;
	}

	public void run(){

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ServerInitializer(gaoListener))
					.option(ChannelOption.SO_BACKLOG, 256)         // (5)
					.childOption(ChannelOption.SO_RCVBUF, 131072) // (6)
					.childOption(ChannelOption.SO_SNDBUF, 131072)
					.childOption(ChannelOption.SO_KEEPALIVE, true);


			Channel ch = b.bind(port).sync().channel();
			System.out.println("Web socket Server started at port "+ port);

			ch.closeFuture().sync();

		} catch (Exception e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}
