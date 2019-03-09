package com.netty.test.server;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 协议解码处理器，用来判断使用的什么协议（本例是指WebSocket还是TcpSocket），从而动态修改编解码器
 * 
 * @author lucher
 *
 */
public class ProtocolDecoder extends ByteToMessageDecoder {

	/**
	 * 请求行信息的长度，ws为：GET /ws HTTP/1.1， Http为：GET / HTTP/1.1
	 */
	private static final int PROTOCOL_LENGTH = 16;
	/**
	 * WebSocket握手协议的前缀， 本例限定为：GET /ws ，在访问ws的时候，请求地址需要为如下格式 ws://ip:port/ws
	 */
	private static final String WEBSOCKET_PREFIX = "GET /ws";

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		String protocol = getBufStart(in);
//		System.out.println("ProtocolHandler protocol:" + protocol);
		if (protocol.startsWith(WEBSOCKET_PREFIX)) {// WebSocket协议处理,移除TcpSocket相关编解码器
			ctx.pipeline().remove("tcpFrameDecoder");
			ctx.pipeline().remove("tcpMsgPackDecoder");
			ctx.pipeline().remove("tcpFrameEncoder");
			ctx.pipeline().remove("tcpMsgPackEncoder");
			// 将对应的管道标记为ws协议
			ChannelWraper channelWraper = NettyServer.CLIENTS.get(ctx.channel().id().asLongText());
			if (channelWraper != null) {
				channelWraper.setProtocol(ChannelWraper.PROTOCOL_WS);
			}
		} else {// TcpSocket协议处理,移除WebSocket相关编解码器
			ctx.pipeline().remove("httpCodec");
			ctx.pipeline().remove("httpAggregator");
			ctx.pipeline().remove("httpChunked");
			// 将对应的管道标记为tcp协议
			ChannelWraper channelWraper = NettyServer.CLIENTS.get(ctx.channel().id().asLongText());
			if (channelWraper != null) {
				channelWraper.setProtocol(ChannelWraper.PROTOCOL_TCP);
			}
		}
		// 重置index标记位
		in.resetReaderIndex();
		// 移除该协议处理器，该channel后续的处理由对应协议安排好的编解码器处理
		ctx.pipeline().remove(this.getClass());
	}

	/**
	 * 获取buffer中指定长度的信息
	 * 
	 * @param in
	 * @return
	 */
	private String getBufStart(ByteBuf in) {
		int length = in.readableBytes();
		if (length > PROTOCOL_LENGTH) {
			length = PROTOCOL_LENGTH;
		}
		// 标记读取位置
		in.markReaderIndex();
		byte[] content = new byte[length];
		in.readBytes(content);
		return new String(content);
	}
}
