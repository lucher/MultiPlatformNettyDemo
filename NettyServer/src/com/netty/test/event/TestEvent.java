package com.netty.test.event;

import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;

import com.netty.test.util.TimeUtil;

/**
 * 测试事件
 * 
 * @author lucher
 */
@Message // 使用MessagePack传输的实体类需要加入该注解
public class TestEvent extends BaseEvent {

	// 需要有默认构造，否则会出现错误：Caused by: compile error: no such constructor:
	// com.netty.test.event.TestEvent
	public TestEvent() {
	}

	public TestEvent(EventType eventType, String from, String content) {
		setEventType(eventType);
		setFrom(from);
		setContent(content);
		setTime(TimeUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
	}

	// 消息内容
	@Index(3)
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return String.format("【%s,%s】", time, content);
	}
}
