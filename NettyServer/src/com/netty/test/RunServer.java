package com.netty.test;

import com.netty.test.server.NettyServer;

/**
 * 服务端启动入口
 * @author lucher
 *
 */
public class RunServer {
	
	// Netty服务端监听端口号
	static final int PORT = Integer.parseInt(System.getProperty("port", "8888"));

	public static void main(String[] args) throws Exception {
		new NettyServer(PORT).start();
	}
}
