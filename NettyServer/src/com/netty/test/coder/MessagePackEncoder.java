package com.netty.test.coder;


import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * MessagePack实现的消息编码器
 * 
 * @author lucher
 *
 */
public class MessagePackEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object obj, ByteBuf buf) throws Exception {
		MessagePack msgPack = new MessagePack();
		//序列化操作
		byte[] bytes = msgPack.write(obj);
		//netty操作,将对象序列化数组传入ByteBuf
		buf.writeBytes(bytes);
	}
}
