package com.pingan.traffic.netty.proto;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.pingan.traffic.netty3.GaoListener;
import com.pingan.traffic.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * @author yangjing
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

	private GaoListener gaoListener;

	public ServerInitializer(GaoListener gaoListener){
		this.gaoListener = gaoListener;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {

//		ChannelPipeline pipeline = ch.pipeline();
//		// HTTP请求的解码和编码
//		pipeline.addLast(new HttpServerCodec());
//		// 把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse，
//		// 原因是HTTP解码器会在每个HTTP消息中生成多个消息对象HttpRequest/HttpResponse,HttpContent,LastHttpContent
//		pipeline.addLast(new HttpObjectAggregator(64 * 1024));
//		// 主要用于处理大数据流，比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的; 增加之后就不用考虑这个问题了
//		pipeline.addLast(new ChunkedWriteHandler());
//		// WebSocket数据压缩
//		pipeline.addLast(new WebSocketServerCompressionHandler());
//		// 协议包长度限制
//		pipeline.addLast(new WebSocketServerProtocolHandler("/websocket", null, true));
//		// 协议包解码
//		pipeline.addLast(new MessageToMessageDecoder<WebSocketFrame>() {
//			@Override
//			protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> objs) throws Exception {
//				ByteBuf buf = ((BinaryWebSocketFrame) frame).content();
//				objs.add(buf);
//				buf.retain();
//			}
//		});
//
//		// 协议包解码时指定Protobuf字节数实例化为CommonProtocol类型
//		pipeline.addLast(new ProtobufDecoder(Protocol.CcRequest.getDefaultInstance()));
//		// 业务处理器
//		pipeline.addLast(new WebSocketProtosServerHandler(gaoListener));
//
//		// 协议包编码
//		pipeline.addLast(new MessageToMessageEncoder<MessageLiteOrBuilder>() {
//			@Override
//			protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
//				ByteBuf result = null;
//				if (msg instanceof MessageLite) {
//					result = wrappedBuffer(((MessageLite) msg).toByteArray());
//				}
//				if (msg instanceof MessageLite.Builder) {
//					result = wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray());
//				}
//
//				// ==== 上面代码片段是拷贝自TCP ProtobufEncoder 源码 ====
//				// 然后下面再转成websocket二进制流，因为客户端不能直接解析protobuf编码生成的
//
//				WebSocketFrame frame = new BinaryWebSocketFrame(result);
//				out.add(frame);
//			}
//		});

////		ch.pipeline().addLast("readTimeout", new ReadTimeoutHandler(45)); // 长时间不写会断
//		ch.pipeline().addLast("HttpServerCodec", new HttpServerCodec());
////		ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(64*1024, 0, 4, 0, 4));
//		ch.pipeline().addLast("ChunkedWriter", new ChunkedWriteHandler());
//		ch.pipeline().addLast("HttpAggregator", new HttpObjectAggregator(65535));
//		ch.pipeline().addLast("WsProtocolDecoder",
//				new WebSocketServerProtocolHandler("/websocket", null, true));
//		ch.pipeline().addLast("WsBinaryDecoder", new WebSocketFrameDecoder()); // ws解码成字节
//		ch.pipeline().addLast("WsEncoder", new WebSocketFramePrepender()); // 字节编码成ws
//		ch.pipeline().addLast("protoBufDecoder", new ProtobufDecoder(Protocol.CcRequest.getDefaultInstance()));
//		ch.pipeline().addLast("protoBufEncoder", new ProtobufEncoder()); // bp编码成字节
//		ch.pipeline().addLast(new WebSocketProtosServerHandler(gaoListener));

	}
}
