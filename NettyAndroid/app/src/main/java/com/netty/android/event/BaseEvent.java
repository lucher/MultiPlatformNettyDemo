package com.netty.android.event;

import org.msgpack.annotation.Index;
import org.msgpack.annotation.Message;

/**
 * 事件基类
 *
 * @author lucher
 */
@Message // 使用MessagePack传输的实体类需要加入该注解
public abstract class BaseEvent {

	@Index(0)
	//事件类别
	protected EventType eventType;
	// 消息来源，如server，android etc
	@Index(1)
	protected String from;
	// 发送时间
	@Index(2)
	protected String time;

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
