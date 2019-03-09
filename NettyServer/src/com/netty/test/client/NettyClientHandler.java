package com.netty.test.client;

import org.msgpack.type.ArrayValue;
import org.msgpack.type.IntegerValue;
import org.msgpack.type.Value;

import com.netty.test.coder.MessageConverter;
import com.netty.test.event.EventType;
import com.netty.test.event.TestEvent;
import com.netty.test.eventbus.EventBusFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理客户端的channel
 *
 * @author lucher
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<ArrayValue> {

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, ArrayValue value) throws Exception {
		// 获取EventType,BaseEvent的第一个字段是EventType，这是约定好的
		int typeIndex = ((IntegerValue) value.get(0)).intValue();
		EventType eventType = EventType.class.getEnumConstants()[typeIndex];
		switch (eventType) {
		case TEST_EVENT:
			TestEvent testEvent = MessageConverter.converter(value, TestEvent.class);
			System.out.println("Client接收到消息:" + testEvent);
			EventBusFactory.build().post(testEvent.toString());

			break;
		case OTHER_EVENT:
			System.out.println("Client接收到消息:其他事件，暂未处理");

			break;
		default:
			break;
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
		ctx.flush();
		// System.out.println("channelReadComplete:" + ctx + "\n");
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		System.out.println("Client channelRegistered:" + ctx);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client channelActive:" + ctx);
		EventBusFactory.build().post("建立连接："+ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		System.out.println("Client channelInactive:" + ctx);
		EventBusFactory.build().post("连接断开："+ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {// 断开链接事件
		super.channelUnregistered(ctx);
		System.out.println("Client channelUnregistered:" + ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("Client exception：" + cause.getMessage());
	}

}