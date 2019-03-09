package com.netty.test.event;

import org.msgpack.annotation.Index;
import org.msgpack.annotation.MessagePackOrdinalEnum;

/**
 * 事件类型枚举
 * 
 * @author lucher
 *
 */
@MessagePackOrdinalEnum // 使用MessagePack传输的枚举类需要加入该注解
public enum EventType {

	/**
	 * 测试类事件
	 */
	@Index(0)
	TEST_EVENT,
	/**
	 * 其他事件
	 */
	@Index(1)
	OTHER_EVENT
}
