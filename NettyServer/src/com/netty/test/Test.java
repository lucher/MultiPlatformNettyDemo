package com.netty.test;

import java.io.IOException;

import org.msgpack.MessagePack;

import com.alibaba.fastjson.JSON;
import com.netty.test.event.EventType;
import com.netty.test.event.TestEvent;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 测试类
 * 
 * @author lucher
 *
 */
public class Test {

	public static void main(String[] args) throws IOException {

		// 使用MessagePack对TestEvent进行编解码测试
		TestEvent testEvent = new TestEvent(EventType.TEST_EVENT, "test", "bdfas");
		System.out.println("编解码之前：" + testEvent);
		System.out.println(JSON.toJSONString(testEvent));

		MessagePack msgPack = new MessagePack();
		// 序列化
		byte[] bytes = msgPack.write(testEvent);
		// 编码
		ByteBuf buf = Unpooled.buffer(128);
		// netty操作,将对象序列化数组传入ByteBuf
		buf.writeBytes(bytes);

		// 解码
		final int length = buf.readableBytes();
		bytes = new byte[length];
		// 从数据包buf中获取要操作的byte数组
		buf.getBytes(buf.readerIndex(), bytes, 0, length);
		// 反序列化，将bytes反序列化成对象
		testEvent = msgPack.read(bytes, TestEvent.class);

		System.out.println("编解码之后：" + testEvent);
	}
}
