package com.netty.test.server;

import java.util.HashMap;

import com.netty.test.coder.MessagePackDecoder;
import com.netty.test.coder.MessagePackEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Netty服务端
 * 
 * @author lucher
 *
 */
public class NettyServer extends Thread {

	// Netty服务端监听端口号
	private int port;

	/*
	 * NioEventLoopGroup实际上就是个线程池,
	 * NioEventLoopGroup在后台启动了n个NioEventLoop来处理Channel事件,
	 * 每一个NioEventLoop负责处理m个Channel,
	 * NioEventLoopGroup从NioEventLoop数组里挨个取出NioEventLoop来处理Channel
	 */
	private final static EventLoopGroup bossGroup = new NioEventLoopGroup(1);// 如果系统只有一个服务端端口需要监听，则建议bossGroup线程组线程数设置为1。
	private final static EventLoopGroup workerGroup = new NioEventLoopGroup();

	/**
	 * 简单实现保存所有在线的终端
	 */
	public static HashMap<String, ChannelWraper> CLIENTS = new HashMap<>();

	public NettyServer(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开启服务
	 * 
	 * @throws Exception
	 */
	private void startServer() throws Exception {

		try {
			// NIO服务端启动辅助类，降低服务端开发复杂度
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					// 当执行channelfactory的newChannel方法时,会创建NioServerSocketChannel实例
					.channel(NioServerSocketChannel.class)
					// ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数，函数listen(int
					// socketfd,int backlog)用来初始化服务端可连接队列，
					// 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
					.option(ChannelOption.SO_BACKLOG, 128)
					// 日志处理器
					// .handler(new LoggingHandler(LogLevel.INFO))
					// handler()和childHandler()的主要区别是，handler()是发生在初始化的时候，childHandler()是发生在客户端连接之后
					.childHandler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							// 协议解码处理器，判断是什么协议（WebSocket还是TcpSocket）,然后动态修改编解码器
							pipeline.addLast("protocolHandler", new ProtocolDecoder());

							/** TcpSocket协议需要使用的编解码器 */
							// Tcp粘包处理，添加一个LengthFieldBasedFrameDecoder解码器，它会在解码时按照消息头的长度来进行解码。
							pipeline.addLast("tcpFrameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4));
							// MessagePack解码器，消息进来后先由frameDecoder处理，再给msgPackDecoder处理
							pipeline.addLast("tcpMsgPackDecoder", new MessagePackDecoder());
							// Tcp粘包处理，添加一个
							// LengthFieldPrepender编码器，它会在ByteBuf之前增加4个字节的字段，用于记录消息长度。
							pipeline.addLast("tcpFrameEncoder", new LengthFieldPrepender(4));
							// MessagePack编码器，消息发出之前先由frameEncoder处理，再给msgPackEncoder处理
							pipeline.addLast("tcpMsgPackEncoder", new MessagePackEncoder());

							/** WebSocket协议需要使用的编解码器 */
							// websocket协议本身是基于http协议的，所以这边也要使用http解编码器
							pipeline.addLast("httpCodec", new HttpServerCodec());
							// netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
							pipeline.addLast("httpAggregator", new HttpObjectAggregator(65536));
							// 用于向客户端发送Html5文件，主要用于支持浏览器和服务端进行WebSocket通信
							pipeline.addLast("httpChunked", new ChunkedWriteHandler());

							// 管道消息处理
							pipeline.addLast("channelHandler", new ServerChannelHandler());
						}
					});
			// 开启服务，绑定端口，同步等待成功
			ChannelFuture f = bootstrap.bind(port).sync();
			System.out.println("服务端已启动,随时欢迎骚扰\n");
			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} finally {
			shutdown();
		}
	}

	/**
	 * 优雅退出，释放线程池资源
	 */
	protected static void shutdown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}
}
