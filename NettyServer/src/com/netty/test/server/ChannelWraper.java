package com.netty.test.server;

import com.alibaba.fastjson.JSON;

import io.netty.channel.Channel;

/**
 * 管道包装类，为管道添加协议类别，针对不同的协议发送不同的消息
 * 
 * @author lucher
 *
 */
public class ChannelWraper {

	/**
	 * tcp协议
	 */
	public static final String PROTOCOL_TCP = "TCP";
	/**
	 * websocket协议
	 */
	public static final String PROTOCOL_WS = "WS";

	// 通信管道
	private Channel channel;
	// 通信协议，取值为
	private String protocol;

	public ChannelWraper(Channel channel) {
		setChannel(channel);
		setProtocol(PROTOCOL_TCP);
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
