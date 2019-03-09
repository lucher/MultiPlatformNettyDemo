package com.netty.test.coder;


import java.util.List;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * MessagePack实现的消息解码器
 * 
 * @author lucher
 *
 */
public class MessagePackDecoder extends MessageToMessageDecoder<ByteBuf> {
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> objs) throws Exception {
		final byte[] bytes;
		final int length = buf.readableBytes();
		bytes = new byte[length];
		// 从数据包buf中获取要操作的byte数组
		buf.getBytes(buf.readerIndex(), bytes, 0, length);
		// 将bytes反序列化成对象,并添加到解码列表中
		MessagePack msgpack = new MessagePack();
		
		objs.add(msgpack.read(bytes));

	}
}