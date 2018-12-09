package com.pingan.traffic.netty3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author yangjing
 */
public class WebSocketServer {

	private int port;
	private GaoListener gaoListener;

	public WebSocketServer(int port, GaoListener gaoListener) {
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
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast("http-codec", new HttpServerCodec());
							pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
							pipeline.addLast("http-chunked", new ChunkedWriteHandler());
							pipeline.addLast("handler", new WebSocketServerHandler(gaoListener));

						}
					});
			Channel ch = b.bind(port).sync().channel();
			System.out.println("Web socket Server started at port "+ port);

			ch.closeFuture().sync();

		} catch (Exception e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}



}


















